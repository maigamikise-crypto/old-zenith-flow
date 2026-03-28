"""
Qlib 数据加载器服务
将 TimescaleDB 中的市场数据转换为 Qlib 可用的格式
"""
import pandas as pd
import numpy as np
from datetime import datetime, timedelta
from typing import List, Optional, Dict
from sqlalchemy import create_engine, text
from loguru import logger
from pathlib import Path
import os
from zenith_ai.core.config import settings

try:
    import qlib
    from qlib.data import D
    from qlib.utils import exists_qlib_data, init_instance_by_config
    from qlib.data.dataset import Dataset
    from qlib.data.dataset.handler import DataHandlerLP
    QLIB_AVAILABLE = True
except ImportError:
    QLIB_AVAILABLE = False
    logger.warning("Qlib is not installed. Please install it with: pip install pyqlib")


class QlibDataLoader:
    """Qlib 数据加载器，负责从 TimescaleDB 加载数据并转换为 Qlib 格式"""
    
    def __init__(self):
        self.db_engine = create_engine(settings.DB_URL)
        self.qlib_initialized = False
        
    def init_qlib(self, provider_uri: Optional[str] = None, region: str = "cn"):
        """初始化 Qlib"""
        if not QLIB_AVAILABLE:
            raise ImportError("Qlib is not installed. Please install it with: pip install pyqlib")
        
        provider_uri = provider_uri or settings.QLIB_PROVIDER_URI
        mount_path = settings.QLIB_EXPERIMENTS_DIR
        
        # Resolve to absolute paths
        provider_uri = str(Path(provider_uri).expanduser().resolve())
        mount_path = str(Path(mount_path).expanduser().resolve())
        
        # Ensure directories exist
        if not os.path.exists(provider_uri):
             logger.warning(f"Provider URI does not exist: {provider_uri}")
        if not os.path.exists(mount_path):
             os.makedirs(mount_path, exist_ok=True)

        qlib.init(provider_uri=provider_uri, region=region, mount_path=mount_path)
        self.qlib_initialized = True
        logger.info(f"Qlib initialized with provider_uri: {provider_uri}, region: {region}, mount_path: {mount_path}")
    
    def load_market_data_from_db(
        self,
        symbols: Optional[List[str]] = None,
        start_date: str = None,
        end_date: str = None
    ) -> pd.DataFrame:
        """
        从 TimescaleDB 加载市场数据
        
        Args:
            symbols: 股票代码列表，如果为 None 则加载所有股票
            start_date: 开始日期，格式 'YYYY-MM-DD'
            end_date: 结束日期，格式 'YYYY-MM-DD'
        
        Returns:
            DataFrame with columns: symbol, date, open, high, low, close, volume, amount
        """
        if start_date is None:
            start_date = (datetime.now() - timedelta(days=365)).strftime("%Y-%m-%d")
        if end_date is None:
            end_date = datetime.now().strftime("%Y-%m-%d")
        
        query = """
            SELECT symbol, date, open, high, low, close, volume, amount
            FROM market_data_bar
            WHERE date >= :start_date AND date <= :end_date
        """
        params = {"start_date": start_date, "end_date": end_date}
        
        if symbols:
            symbol_list = "', '".join(symbols)
            query += f" AND symbol IN ('{symbol_list}')"
        
        query += " ORDER BY symbol, date"
        
        logger.info(f"Loading market data from DB: {len(symbols) if symbols else 'all'} symbols, "
                   f"{start_date} to {end_date}")
        
        df = pd.read_sql(query, self.db_engine, params=params)
        logger.info(f"Loaded {len(df)} records for {df['symbol'].nunique()} symbols")
        
        return df
    
    def convert_to_qlib_format(self, df: pd.DataFrame) -> pd.DataFrame:
        """
        将 TimescaleDB 格式的数据转换为 Qlib 格式
        
        Qlib 需要的格式：
        - 索引: MultiIndex (instrument, datetime)
        - 列名: 使用 Qlib 标准字段名 ($close, $open, $high, $low, $volume)
        
        Args:
            df: DataFrame with columns: symbol, date, open, high, low, close, volume, amount
        
        Returns:
            DataFrame in Qlib format
        """
        if df.empty:
            return df
        
        # 确保日期格式正确
        df['date'] = pd.to_datetime(df['date'])
        
        # 转换股票代码格式：Qlib 需要 '000001.SZ' 格式
        def format_symbol(symbol: str) -> str:
            """将股票代码转换为 Qlib 格式"""
            if '.' in symbol:
                return symbol  # 已经是正确格式
            # 判断是上海还是深圳
            if symbol.startswith('6'):
                return f"{symbol}.SH"
            elif symbol.startswith(('0', '3')):
                return f"{symbol}.SZ"
            else:
                return symbol
        
        df['symbol'] = df['symbol'].apply(format_symbol)
        
        # 创建 MultiIndex
        df = df.set_index(['symbol', 'date'])
        
        # 重命名列为 Qlib 标准字段名
        column_mapping = {
            'open': '$open',
            'high': '$high',
            'low': '$low',
            'close': '$close',
            'volume': '$volume',
            'amount': '$amount'
        }
        df = df.rename(columns=column_mapping)
        
        # 确保数据类型正确
        for col in df.columns:
            if col.startswith('$'):
                df[col] = pd.to_numeric(df[col], errors='coerce')
        
        # 删除包含 NaN 的行
        df = df.dropna()
        
        logger.info(f"Converted to Qlib format: {len(df)} records, {df.index.get_level_values(0).nunique()} instruments")
        
        return df
    
    def get_instruments(
        self,
        market: str = "csi300",
        start_date: Optional[str] = None,
        end_date: Optional[str] = None
    ) -> List[str]:
        """
        获取指定市场的股票列表
        
        Args:
            market: 市场名称，如 'csi300', 'csi500', 'all'
            start_date: 开始日期
            end_date: 结束日期
        
        Returns:
            股票代码列表
        """
        if not self.qlib_initialized:
            self.init_qlib()
        
        try:
            if market == "all":
                # 从数据库获取所有股票
                query = "SELECT DISTINCT symbol FROM market_data_bar ORDER BY symbol"
                df = pd.read_sql(query, self.db_engine)
                return df['symbol'].tolist()
            else:
                # 使用 Qlib 的内置市场数据
                instruments = D.instruments(market)
                return list(instruments)
        except Exception as e:
            logger.error(f"Failed to get instruments for market {market}: {e}")
            # 降级方案：从数据库获取
            query = "SELECT DISTINCT symbol FROM market_data_bar ORDER BY symbol LIMIT 300"
            df = pd.read_sql(query, self.db_engine)
            return df['symbol'].tolist()
    
    def load_features(
        self,
        instruments: List[str],
        fields: List[str],
        start_date: str,
        end_date: str
    ) -> pd.DataFrame:
        """
        使用 Qlib 加载特征数据
        
        Args:
            instruments: 股票代码列表
            fields: 字段列表，如 ['$close', '$volume', 'Ref($close, 1)']
            start_date: 开始日期
            end_date: 结束日期
        
        Returns:
            DataFrame with features
        """
        if not self.qlib_initialized:
            self.init_qlib()
        
        try:
            # 使用 Qlib 的 D.features 加载数据
            df = D.features(
                instruments=instruments,
                fields=fields,
                start_time=start_date,
                end_time=end_date
            )
            return df
        except Exception as e:
            logger.error(f"Failed to load features with Qlib: {e}")
            # 降级方案：从数据库加载基础数据
            logger.info("Falling back to database loading...")
            df = self.load_market_data_from_db(symbols=instruments, start_date=start_date, end_date=end_date)
            df = self.convert_to_qlib_format(df)
            return df

