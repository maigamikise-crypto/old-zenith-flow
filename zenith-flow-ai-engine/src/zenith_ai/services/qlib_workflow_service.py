"""
Qlib Workflow 服务
实现完整的 Qlib 工作流：数据加载 -> 模型训练 -> 回测
"""
import os
import sys
import yaml
from datetime import datetime
from typing import Dict, Optional, List
from pathlib import Path
from loguru import logger

try:
    import qlib
    from qlib.workflow import R
    from qlib.workflow.record_temp import SignalRecord, PortAnaRecord
    from qlib.data.dataset import Dataset, DatasetH
    from qlib.contrib.model.gbdt import LGBModel
    from qlib.contrib.model.pytorch_lstm import LSTM
    from qlib.contrib.model.pytorch_alstm import ALSTM
    from qlib.contrib.strategy.signal_strategy import TopkDropoutStrategy
    from qlib.backtest import backtest
    from qlib.contrib.evaluate import risk_analysis
    QLIB_AVAILABLE = True
except Exception as e:
    QLIB_AVAILABLE = False
    logger.warning(f"Qlib is not installed. Detail: {e}")

from zenith_ai.core.config import settings
from zenith_ai.services.qlib_data_loader import QlibDataLoader


class QlibWorkflowService:
    """Qlib 工作流服务，封装完整的训练和回测流程"""
    
    def __init__(self):
        self.data_loader = QlibDataLoader()
        self.experiments_dir = Path(settings.QLIB_EXPERIMENTS_DIR)
        self.experiments_dir.mkdir(parents=True, exist_ok=True)
        
    def create_workflow_config(
        self,
        market: str = "csi300",
        model_type: str = "LGBModel",
        start_date: str = "2020-01-01",
        end_date: str = None,
        train_start: str = None,
        train_end: str = None,
        valid_start: str = None,
        valid_end: str = None,
        test_start: str = None,
        test_end: str = None,
        **kwargs
    ) -> Dict:
        """
        创建 Qlib Workflow 配置
        
        Args:
            market: 市场名称，如 'csi300'
            model_type: 模型类型，如 'LGBModel', 'LSTM', 'ALSTM'
            start_date: 数据开始日期
            end_date: 数据结束日期
            train_start/end: 训练集日期范围
            valid_start/end: 验证集日期范围
            test_start/end: 测试集日期范围
            **kwargs: 其他配置参数
        
        Returns:
            Workflow 配置字典
        """
        if end_date is None:
            end_date = datetime.now().strftime("%Y-%m-%d")
        
        # 默认日期分割：训练集 60%，验证集 20%，测试集 20%
        if train_start is None:
            train_start = start_date
        if train_end is None:
            # 计算训练集结束日期（60%）
            from datetime import datetime, timedelta
            start_dt = datetime.strptime(start_date, "%Y-%m-%d")
            end_dt = datetime.strptime(end_date, "%Y-%m-%d")
            total_days = (end_dt - start_dt).days
            train_end_dt = start_dt + timedelta(days=int(total_days * 0.6))
            train_end = train_end_dt.strftime("%Y-%m-%d")
        
        if valid_start is None:
            valid_start = train_end
        if valid_end is None:
            start_dt = datetime.strptime(start_date, "%Y-%m-%d")
            end_dt = datetime.strptime(end_date, "%Y-%m-%d")
            total_days = (end_dt - start_dt).days
            valid_end_dt = start_dt + timedelta(days=int(total_days * 0.8))
            valid_end = valid_end_dt.strftime("%Y-%m-%d")
        
        if test_start is None:
            test_start = valid_end
        if test_end is None:
            test_end = end_date
        
        # 模型配置
        model_config = {
            "LGBModel": {
                "class": "LGBModel",
                "module_path": "qlib.contrib.model.gbdt",
                "kwargs": {
                    "loss": "mse",
                    "colsample_bytree": 0.8879,
                    "learning_rate": 0.0421,
                    "subsample": 0.8789,
                    "lambda_l1": 205.6999,
                    "lambda_l2": 580.9768,
                    "max_depth": 8,
                    "num_leaves": 210,
                    "num_threads": 20
                }
            },
            "LSTM": {
                "class": "LSTM",
                "module_path": "qlib.contrib.model.pytorch_lstm",
                "kwargs": {
                    "d_feat": 158,
                    "hidden_size": 64,
                    "num_layers": 2,
                    "dropout": 0.0,
                    "n_epochs": 100,
                    "lr": 0.001,
                    "early_stop": 10,
                    "batch_size": 2000,
                    "metric": "loss",
                    "loss": "mse",
                    "optimizer": "adam"
                }
            },
            "ALSTM": {
                "class": "ALSTM",
                "module_path": "qlib.contrib.model.pytorch_alstm",
                "kwargs": {
                    "d_feat": 158,
                    "hidden_size": 64,
                    "num_layers": 2,
                    "dropout": 0.0,
                    "n_epochs": 100,
                    "lr": 0.001,
                    "early_stop": 10,
                    "batch_size": 2000,
                    "metric": "loss",
                    "loss": "mse",
                    "optimizer": "adam"
                }
            }
        }
        
        config = {
            "market": market,
            # DatasetH 配置结构
            "dataset": {
                "handler": {
                    "class": "Alpha158",
                    "module_path": "qlib.contrib.data.handler",
                    "kwargs": {
                        "start_time": start_date,
                        "end_time": end_date,
                        "fit_start_time": train_start,
                        "fit_end_time": train_end,
                        "instruments": market,
                        "infer_processors": [
                            {
                                "class": "RobustZScoreNorm",
                                "kwargs": {"fields_group": "feature", "clip_outlier": True}
                            },
                            {
                                "class": "Fillna",
                                "kwargs": {"fields_group": "feature"}
                            }
                        ],
                        "learn_processors": [
                            {
                                "class": "DropnaLabel"
                            },
                            {
                                "class": "CSRankNorm",
                                "kwargs": {"fields_group": "label"}
                            }
                        ]
                    }
                },
                "segments": {
                    "train": [train_start, train_end],
                    "valid": [valid_start, valid_end],
                    "test": [test_start, test_end]
                }
            },
            "port_analysis_config": {
                "executor": {
                    "class": "SimulatorExecutor",
                    "module_path": "qlib.backtest.executor",
                    "kwargs": {
                        "time_per_step": "day",
                        "generate_portfolio_metrics": True
                    }
                },
                "strategy": {
                    "class": "TopkDropoutStrategy",
                    "module_path": "qlib.contrib.strategy.signal_strategy",
                    "kwargs": {
                        "signal": "<PRED>",
                        "topk": 50,
                        "n_drop": 5
                    }
                },
                "backtest": {
                    "start_time": test_start,
                    "end_time": test_end,
                    "account": 1000000,
                    "benchmark": market,
                    "exchange_kwargs": {
                        "freq": "day",
                        "limit_threshold": 0.095,
                        "deal_price": "close",
                        "open_cost": 0.0005,
                        "close_cost": 0.0015,
                        "min_cost": 5
                    }
                }
            }
        }
        
        # 添加模型配置
        if model_type in model_config:
            config["model"] = model_config[model_type]
        else:
            config["model"] = model_config["LGBModel"]
            logger.warning(f"Unknown model type {model_type}, using LGBModel")
        
        return config
    
    def train_model(
        self,
        experiment_name: str,
        market: str = "csi300",
        model_type: str = "LGBModel",
        start_date: str = "2020-01-01",
        end_date: str = None,
        **kwargs
    ) -> Dict:
        """
        训练模型
        
        Args:
            experiment_name: 实验名称
            market: 市场名称
            model_type: 模型类型
            start_date: 开始日期
            end_date: 结束日期
            **kwargs: 其他配置参数
        
        Returns:
            训练结果字典，包含 experiment_id, model_path 等信息
        """
        if not QLIB_AVAILABLE:
            raise ImportError("Qlib is not installed")
        
        # 初始化 Qlib
        self.data_loader.init_qlib()
        
        # 创建配置
        config = self.create_workflow_config(
            market=market,
            model_type=model_type,
            start_date=start_date,
            end_date=end_date,
            **kwargs
        )
        
        # 保存配置
        config_path = self.experiments_dir / f"{experiment_name}_config.yaml"
        with open(config_path, 'w', encoding='utf-8') as f:
            yaml.dump(config, f, allow_unicode=True, default_flow_style=False)
        
        logger.info(f"Starting training experiment: {experiment_name}")
        print(f"DEBUG: Config saved to: {config_path}")
        
        try:
            print("DEBUG: Entering try block")
            # 使用 subprocess 调用外部 worker 脚本运行训练，避免 Windows 下的 multiprocessing 问题
            import subprocess
            import json
            print("DEBUG: Imports done")
            
            worker_script = Path(__file__).parent / "qlib_train_worker.py"
            print(f"DEBUG: Worker script: {worker_script}")
            
            cmd = [
                sys.executable,
                str(worker_script),
                "--config_path", str(config_path),
                "--experiment_name", experiment_name,
                "--provider_uri", settings.QLIB_PROVIDER_URI,
                "--mount_path", str(self.experiments_dir)
            ]
            print(f"DEBUG: CMD constructed: {cmd}")
            
            logger.info(f"Running worker command: {' '.join(cmd)}")
            print(f"DEBUG: Running worker command: {' '.join(cmd)}")
            print(f"DEBUG: Worker script exists? {worker_script.exists()}")
            
            result_proc = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                encoding='utf-8',
                cwd=str(Path(os.getcwd()))
            )
            print(f"DEBUG: Subprocess finished. Return code: {result_proc.returncode}")
            print(f"DEBUG: Stdout: {result_proc.stdout[:200]}...")
            print(f"DEBUG: Stderr: {result_proc.stderr[:200]}...")
            
            # Use print instead of logger to avoid loguru formatting issues with curly braces in Qlib output
            print(f"Worker stdout: {result_proc.stdout}")
            print(f"Worker stderr: {result_proc.stderr}")
            
            if result_proc.returncode != 0:
                raise RuntimeError(f"Worker failed with code {result_proc.returncode}: {result_proc.stderr}")
            
            # Parse result from stdout
            stdout = result_proc.stdout
            start_marker = "__RESULT_JSON_START__"
            end_marker = "__RESULT_JSON_END__"
            
            if start_marker in stdout and end_marker in stdout:
                json_str = stdout.split(start_marker)[1].split(end_marker)[0].strip()
                result_data = json.loads(json_str)
                
                if result_data.get("status") == "error":
                    raise RuntimeError(f"Worker reported error: {result_data.get('error')}")
                
                return {
                    "status": "success",
                    "experiment_name": experiment_name,
                    "experiment_id": result_data["experiment_id"],
                    "recorder_id": result_data["recorder_id"],
                    "pred_path": result_data["pred_path"],
                    "config_path": str(config_path),
                    "model_type": model_type,
                    "market": market
                }
            else:
                raise RuntimeError("Worker did not return valid JSON result")

        except Exception as e:
            logger.error(f"Training failed: {e}", exc_info=True)
            return {
                "status": "error",
                "error": str(e)
            }
    

    def run_backtest(
        self,
        experiment_name: str,
        recorder_id: Optional[str] = None,
        start_date: Optional[str] = None,
        end_date: Optional[str] = None,
        pred_path: Optional[str] = None,
        **kwargs
    ) -> Dict:
        """
        运行回测
        
        Args:
            experiment_name: 实验名称
            recorder_id: 记录器 ID
            start_date: 回测开始日期
            end_date: 回测结束日期
            pred_path: 预测结果文件路径 (Optional detour)
            **kwargs: 其他配置参数
        """
        if not QLIB_AVAILABLE:
            raise ImportError("Qlib is not installed")
        
        # 初始化 Qlib
        self.data_loader.init_qlib()
        
        try:
            # 加载预测结果
            if pred_path and os.path.exists(pred_path):
                logger.info(f"Loading predictions from explicit path: {pred_path}")
                import pandas as pd
                pred = pd.read_pickle(pred_path)
                # Mock recorder if needed or just skip
                recorder_id_used = recorder_id or "external_file"
            else:
                # 获取实验记录器
                experiment = R.get_exp(experiment_name=experiment_name)
                # ... (keep existing logic) ...
                if recorder_id:
                    recorder = experiment.get_recorder(recorder_id=recorder_id)
                else:
                    recorders = experiment.list_recorders()
                    if isinstance(recorders, dict):
                        recorders = list(recorders.values())
                    if not recorders:
                        raise ValueError(f"No recorders found for experiment {experiment_name}")
                    recorder = recorders[-1]
                
                logger.info(f"Loading object 'pred.pkl' from recorder {recorder.id}...")
                pred = recorder.load_object("pred.pkl")
                recorder_id_used = recorder.recorder_id
            
            logger.info(f"Loaded predictions: type={type(pred)}, shape={getattr(pred, 'shape', 'unknown')}")
            if hasattr(pred, 'head'):
                logger.info(f"Predictions head:\n{pred.head()}")
            
            # 简化回测 - 直接使用策略类而不是通过配置
            logger.info("Running simplified backtest...")
            
            # 回测时间范围
            bt_start = start_date or "2020-01-01"
            bt_end = end_date or datetime.now().strftime("%Y-%m-%d")
            
            # 创建策略 - 直接传入预测数据
            strategy = TopkDropoutStrategy(
                signal=pred,
                topk=50,
                n_drop=5
            )
            
            # 创建 executor 配置
            executor_config = {
                "class": "SimulatorExecutor",
                "module_path": "qlib.backtest.executor",
                "kwargs": {
                    "time_per_step": "day",
                    "generate_portfolio_metrics": True
                }
            }
            
            # exchange 配置
            exchange_kwargs = {
                "freq": "day",
                "limit_threshold": 0.095,
                "deal_price": "close",
                "open_cost": 0.0005,
                "close_cost": 0.0015,
                "min_cost": 5
            }
            
            logger.info(f"Backtest time range: {bt_start} to {bt_end}")
            
            # 运行回测 - 使用正确的函数签名
            portfolio_metric, indicator_metric = backtest(
                start_time=bt_start,
                end_time=bt_end,
                strategy=strategy,
                executor=executor_config,
                benchmark="SH000300",
                account=1000000,
                exchange_kwargs=exchange_kwargs
            )
            
            logger.info("Backtest completed, processing results...")
            
            # 风险分析 - portfolio_metric 是按频率分组的字典
            # 格式: {"day": PortfolioMetrics, "1min": PortfolioMetrics, ...}
            analysis_results = {}
            
            if isinstance(portfolio_metric, dict):
                # 获取日频数据（最常用）
                freq_key = "day" if "day" in portfolio_metric else list(portfolio_metric.keys())[0] if portfolio_metric else None
                
                if freq_key and portfolio_metric.get(freq_key):
                    pm = portfolio_metric[freq_key]
                    # 生成 portfolio metrics DataFrame
                    if hasattr(pm, 'generate_portfolio_metrics_dataframe'):
                        report_df = pm.generate_portfolio_metrics_dataframe()
                        # 计算超额收益
                        if 'return' in report_df.columns and 'bench' in report_df.columns:
                            excess_return = report_df['return'] - report_df['bench']
                            analysis_results['excess_return_without_cost'] = risk_analysis(excess_return)
                            
                            if 'cost' in report_df.columns:
                                excess_return_with_cost = excess_return - report_df['cost']
                                analysis_results['excess_return_with_cost'] = risk_analysis(excess_return_with_cost)
                        else:
                            analysis_results['note'] = "Missing return/bench columns"
                    else:
                        analysis_results['note'] = "PortfolioMetrics not available"
                else:
                    analysis_results['note'] = f"No frequency data found in portfolio_metric"
            else:
                analysis_results['note'] = f"Unexpected portfolio_metric type: {type(portfolio_metric)}"
            
            logger.info("Risk analysis completed")
            
            # 序列化结果
            def serialize_result(obj):
                if hasattr(obj, 'to_dict'):
                    return obj.to_dict()
                elif hasattr(obj, 'tolist'):
                    return obj.tolist()
                else:
                    return str(obj)
            
            return {
                "status": "success",
                "experiment_name": experiment_name,
                "recorder_id": recorder_id_used,
                "portfolio_metric": {k: serialize_result(v) for k, v in portfolio_metric.items()} if isinstance(portfolio_metric, dict) else str(portfolio_metric),
                "risk_analysis": {k: serialize_result(v) for k, v in analysis_results.items()}
            }
        except Exception as e:
            logger.error(f"Backtest failed: {e}", exc_info=True)
            return {
                "status": "error",
                "error": str(e)
            }

