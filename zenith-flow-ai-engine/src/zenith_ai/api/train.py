from fastapi import APIRouter, BackgroundTasks
from pydantic import BaseModel
from typing import Optional, List
import logging
from loguru import logger
from zenith_ai.services.train_service import TrainService
from zenith_ai.services.qlib_workflow_service import QlibWorkflowService

router = APIRouter()

class TrainRequest(BaseModel):
    name: str
    algo: str
    symbols: Optional[str] = None
    
    class Config:
        schema_extra = {
            "example": {
                "name": "experiment_1",
                "algo": "rf",
                "symbols": "000001.SZ,600519.SH"
            }
        }

class QlibTrainRequest(BaseModel):
    """Qlib 训练请求"""
    experiment_name: str
    market: str = "csi300"
    model_type: str = "LGBModel"  # LGBModel, LSTM, ALSTM
    start_date: str = "2020-01-01"
    end_date: Optional[str] = None
    
    class Config:
        schema_extra = {
            "example": {
                "experiment_name": "qlib_experiment_1",
                "market": "csi300",
                "model_type": "LGBModel",
                "start_date": "2020-01-01",
                "end_date": "2023-12-31"
            }
        }

def run_training_task(request: TrainRequest):
    """
    后台执行训练任务（传统方式）
    """
    try:
        symbols_list = request.symbols.split(",") if request.symbols else None
        TrainService.train_and_export(
            model_name=request.name,
            algo=request.algo,
            symbols=symbols_list
        )
    except Exception as e:
        logger.error(f"Training task failed: {e}")

def run_qlib_training_task(request: QlibTrainRequest):
    """
    后台执行 Qlib 训练任务
    """
    try:
        workflow_service = QlibWorkflowService()
        result = workflow_service.train_model(
            experiment_name=request.experiment_name,
            market=request.market,
            model_type=request.model_type,
            start_date=request.start_date,
            end_date=request.end_date
        )
        logger.info(f"Qlib training completed: {result}")
        return result
    except Exception as e:
        logger.error(f"Qlib training task failed: {e}", exc_info=True)

@router.post("/start")
async def start_training(request: TrainRequest, background_tasks: BackgroundTasks):
    """
    启动模型训练 (异步) - 传统方式（sklearn）
    """
    background_tasks.add_task(run_training_task, request)
    return {"status": "accepted", "message": "Training started in background"}

@router.post("/qlib/start")
async def start_qlib_training(request: QlibTrainRequest, background_tasks: BackgroundTasks):
    """
    启动 Qlib 模型训练 (异步)
    
    支持的模型类型：
    - LGBModel: LightGBM（推荐，速度快）
    - LSTM: LSTM 神经网络
    - ALSTM: Attention-based LSTM
    """
    # background_tasks.add_task(run_qlib_training_task, request)
    result = run_qlib_training_task(request) # Run synchronously for debug
    
    response = {
        "status": "accepted",
        "message": "Qlib training started in background",
        "experiment_name": request.experiment_name
    }
    if result:
        response.update(result)
        
    return response

@router.get("/qlib/experiments")
async def list_qlib_experiments():
    """
    列出所有 Qlib 实验
    """
    try:
        from qlib.workflow import R
        from zenith_ai.services.qlib_data_loader import QlibDataLoader
        
        data_loader = QlibDataLoader()
        data_loader.init_qlib()
        
        # 获取所有实验
        experiments = R.list_experiments()
        if isinstance(experiments, dict):
            experiments = experiments.values()
            
        return {
            "status": "success",
            "experiments": [{"name": exp.name, "id": exp.id} for exp in experiments]
        }
    except Exception as e:
        logger.error(f"Failed to list experiments: {e}")
        return {
            "status": "error",
            "error": str(e)
        }
