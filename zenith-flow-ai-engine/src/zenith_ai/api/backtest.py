from fastapi import APIRouter
from pydantic import BaseModel
from typing import List, Optional
from loguru import logger
from zenith_ai.services.qlib_workflow_service import QlibWorkflowService

router = APIRouter()

class BacktestRequest(BaseModel):
    """传统回测请求"""
    symbol: Optional[str] = None
    index: Optional[str] = None
    strategy: str
    model: Optional[str] = None

class QlibBacktestRequest(BaseModel):
    """Qlib 回测请求"""
    experiment_name: str
    recorder_id: Optional[str] = None  # 如果为 None，使用最新的记录器
    start_date: Optional[str] = None
    end_date: Optional[str] = None
    pred_path: Optional[str] = None  # Optional explicit path to prediction file for bypassing recorder loading logic
    
    class Config:
        schema_extra = {
            "example": {
                "experiment_name": "qlib_experiment_1",
                "recorder_id": None,
                "start_date": "2022-01-01",
                "end_date": "2023-12-31"
            }
        }

@router.post("/start")
async def start_backtest(request: BacktestRequest):
    """
    启动传统回测（TODO: 实现）
    """
    # TODO: 调用回测 Service
    return {"status": "ok", "result": []}

@router.post("/qlib/start")
async def start_qlib_backtest(request: QlibBacktestRequest):
    """
    启动 Qlib 回测
    
    使用指定实验的模型预测结果进行回测
    """
    try:
        workflow_service = QlibWorkflowService()
        result = workflow_service.run_backtest(
            experiment_name=request.experiment_name,
            recorder_id=request.recorder_id,
            start_date=request.start_date,
            end_date=request.end_date,
            pred_path=request.pred_path
        )
        return result
    except Exception as e:
        logger.error(f"Qlib backtest failed: {e}")
        return {
            "status": "error",
            "error": str(e)
        }

@router.get("/models")
async def get_models():
    """
    返回可用模型列表
    """
    # TODO: 从数据库查询模型列表
    return [
        {"modelName": "rf_v1", "algo": "RandomForest", "score": 0.85},
        {"modelName": "lgb_v1", "algo": "LightGBM", "score": 0.88}
    ]

@router.get("/qlib/experiments/{experiment_name}/recorders")
async def get_qlib_recorders(experiment_name: str):
    """
    获取指定实验的所有记录器
    """
    try:
        from qlib.workflow import R
        from zenith_ai.services.qlib_data_loader import QlibDataLoader
        
        data_loader = QlibDataLoader()
        data_loader.init_qlib()
        
        experiment = R.get_exp(experiment_name=experiment_name)
        recorders = experiment.list_recorders()
        
        return {
            "status": "success",
            "experiment_name": experiment_name,
            "recorders": [
                {
                    "recorder_id": rec.recorder_id,
                    "artifact_uri": rec.artifact_uri
                }
                for rec in recorders
            ]
        }
    except Exception as e:
        logger.error(f"Failed to get recorders: {e}")
        return {
            "status": "error",
            "error": str(e)
        }

