from fastapi import APIRouter
from typing import List
from zenith_ai.services.data_service import DataService

router = APIRouter()

@router.get("/index-stocks")
async def get_index_stocks(index_code: str) -> List[str]:
    """
    获取指数成分股
    """
    return DataService.get_index_stocks(index_code)
