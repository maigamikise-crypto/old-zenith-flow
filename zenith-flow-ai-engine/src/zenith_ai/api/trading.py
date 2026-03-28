from zenith_ai.api import train, backtest, data, trading
from zenith_ai.services.risk_service import RiskControlService, RiskCheckResult
from zenith_ai.services.trading_service import (
    trading_service, 
    OrderRequest, 
    OrderResponse, 
    AccountInfo, 
    PositionInfo, 
    BaseTradingService
)

router = APIRouter()

# Dependency for services access
def get_trading_service() -> BaseTradingService:
    return trading_service

def get_risk_service(service: BaseTradingService = Depends(get_trading_service)) -> RiskControlService:
    return RiskControlService(service)

@router.post("/order", response_model=OrderResponse)
async def place_order(
    request: OrderRequest, 
    trading: BaseTradingService = Depends(get_trading_service),
    risk: RiskControlService = Depends(get_risk_service)
):
    try:
        # Pre-trade Risk Check
        risk_result = await risk.check_pre_trade(request)
        if not risk_result.passed:
            return OrderResponse(order_id="", status="REJECTED_BY_RISK", message=risk_result.message)

        response = await trading.place_order(request)
        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/cancel/{order_id}", response_model=bool)
async def cancel_order(
    order_id: str, 
    account_id: str, 
    service: BaseTradingService = Depends(get_trading_service)
):
    try:
        success = await service.cancel_order(order_id, account_id)
        if not success:
            raise HTTPException(status_code=404, detail="Order not found or cannot be cancelled.")
        return success
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/account/{account_id}", response_model=AccountInfo)
async def get_account(
    account_id: str, 
    service: BaseTradingService = Depends(get_trading_service)
):
    try:
        info = await service.get_account_info(account_id)
        if not info:
             raise HTTPException(status_code=404, detail="Account not found.")
        return info
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/positions/{account_id}", response_model=List[PositionInfo])
async def get_positions(
    account_id: str, 
    service: BaseTradingService = Depends(get_trading_service)
):
    try:
        positions = await service.get_positions(account_id)
        return positions
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
