import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType
import os
import requests
import logging
from datetime import datetime
from sqlalchemy import create_engine, text
from zenith_ai.core.config import settings

logger = logging.getLogger(__name__)

class TrainService:
    
    @staticmethod
    def calculate_rsi(series, period=14):
        delta = series.diff()
        gain = (delta.where(delta > 0, 0)).ewm(alpha=1/period, adjust=False).mean()
        loss = (-delta.where(delta < 0, 0)).ewm(alpha=1/period, adjust=False).mean()
        rs = gain / loss
        return 100 - (100 / (1 + rs))

    @staticmethod
    def calculate_macd(series, fast=12, slow=26, signal=9):
        exp1 = series.ewm(span=fast, adjust=False).mean()
        exp2 = series.ewm(span=slow, adjust=False).mean()
        macd = exp1 - exp2
        signal_line = macd.ewm(span=signal, adjust=False).mean()
        histogram = macd - signal_line
        return macd, signal_line, histogram

    @staticmethod
    def save_model_meta(model_name, algo, file_path, features):
        """保存模型元数据到数据库"""
        engine = create_engine(settings.DB_URL)
        try:
            with engine.connect() as conn:
                conn.execute(text(f"DELETE FROM ai_model WHERE model_name = '{model_name}'"))
                conn.commit()
                
                sql = text("""
                    INSERT INTO ai_model (model_name, algo_type, file_path, features, created_at)
                    VALUES (:name, :algo, :path, :feats, :now)
                """)
                
                rel_path = os.path.basename(file_path)
                
                conn.execute(sql, {
                    "name": model_name,
                    "algo": algo,
                    "path": rel_path,
                    "feats": features,
                    "now": datetime.now()
                })
                conn.commit()
                logger.info("模型元数据已保存到数据库")
        except Exception as e:
            logger.error(f"保存模型元数据失败: {e}")

    @staticmethod
    def notify_java_engine():
        logger.info("-" * 30)
        logger.info("正在通知 Java 引擎热重载模型...")
        try:
            response = requests.post(settings.RELOAD_API)
            if response.status_code == 200:
                logger.info(f"Java 引擎响应: {response.text}")
            else:
                logger.info(f"Java 引擎响应异常: {response.status_code} - {response.text}")
        except requests.exceptions.ConnectionError:
            logger.error("无法连接到 Java 引擎 (可能未启动?)")
        except Exception as e:
            logger.error(f"通知失败: {e}")

    @classmethod
    def train_and_export(cls, model_name="model", algo="rf", symbols=None):
        logger.info(f"开始训练任务: Name={model_name}, Algo={algo}")
        engine = create_engine(settings.DB_URL)
        
        query = "SELECT * FROM market_data_bar"
        if symbols:
            symbol_list = "', '".join(symbols)
            query += f" WHERE symbol IN ('{symbol_list}')"
        query += " ORDER BY symbol, date"
        
        logger.info("正在从数据库加载数据...")
        df = pd.read_sql(query, engine)
        logger.info(f"已加载 {len(df)} 条数据，包含 {df['symbol'].nunique()} 只股票")

        if df.empty:
            logger.warning("数据库为空或未找到指定股票数据。")
            return

        # 特征工程
        lag_window = 5
        
        def calculate_features(group):
            group['pct_change'] = group['close'].pct_change()
            for i in range(0, lag_window):
                group[f'ret_lag_{i}'] = group['pct_change'].shift(i)
                
            group['rsi_14'] = cls.calculate_rsi(group['close'], 14)
            _, _, group['macd_hist'] = cls.calculate_macd(group['close'])
            ma20 = group['close'].rolling(window=20).mean()
            group['ma20_bias'] = (group['close'] - ma20) / ma20
            group['volatility_20'] = group['pct_change'].rolling(window=20).std()
            group['target'] = (group['pct_change'].shift(-1) > 0).astype(int)
            return group

        logger.info("正在进行特征工程...")
        df = df.groupby('symbol', group_keys=False).apply(calculate_features)
        
        df.replace([np.inf, -np.inf], np.nan, inplace=True)
        df.dropna(inplace=True)
        
        feature_cols = [f'ret_lag_{i}' for i in range(lag_window)] + ['rsi_14', 'macd_hist', 'ma20_bias', 'volatility_20']
        
        X = df[feature_cols].values.astype(np.float32)
        y = df['target'].values
        
        logger.info(f"有效样本数: {len(X)}")
        if len(X) < 100:
            logger.warning("样本不足")
            return

        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        
        model = None
        if algo == 'rf':
            logger.info("正在训练随机森林 (Random Forest)...")
            model = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=42, n_jobs=-1)
        elif algo == 'lr':
            logger.info("正在训练逻辑回归 (Logistic Regression)...")
            model = LogisticRegression(random_state=42)
        else:
            logger.error(f"未知算法: {algo}")
            return
            
        model.fit(X_train, y_train)
        
        score = model.score(X_test, y_test)
        logger.info(f"模型测试集准确率: {score:.2%}")
        
        n_features = X.shape[1]
        initial_type = [('float_input', FloatTensorType([None, n_features]))]
        
        try:
            target_opset = 9 if algo == 'rf' else 12
            onnx_model = convert_sklearn(model, initial_types=initial_type, target_opset=target_opset)
        except Exception as e:
            logger.error(f"ONNX 转换失败: {e}")
            return
        
        os.makedirs(settings.MODELS_DIR, exist_ok=True)
        model_path = os.path.join(settings.MODELS_DIR, f"{model_name}.onnx")
        
        with open(model_path, "wb") as f:
            f.write(onnx_model.SerializeToString())
            
        logger.info(f"模型已保存至: {model_path}")
        
        features_str = ",".join(feature_cols)
        cls.save_model_meta(model_name, algo, model_path, features_str)
        
        cls.notify_java_engine()

