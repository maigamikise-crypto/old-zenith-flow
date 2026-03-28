from fastapi import APIRouter
from zenith_ai.api import train, backtest, data

router = APIRouter()

router.include_router(train.router, prefix="/train", tags=["training"])
router.include_router(backtest.router, prefix="/backtest", tags=["backtest"])
router.include_router(data.router, prefix="/data", tags=["data"])

