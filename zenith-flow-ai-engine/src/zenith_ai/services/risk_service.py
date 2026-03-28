import logging
from typing import Optional, List
from zenith_ai.core.config import settings
from zenith_ai.services.trading_service import OrderRequest, BaseTradingService, AccountInfo

logger = logging.getLogger(__name__)

class RiskCheckResult:
    def __init__(self, passed: bool, message: str = ""):
        self.passed = passed
        self.message = message

class RiskControlService:
    def __init__(self, trading_service: BaseTradingService):
        self.trading_service = trading_service

    async def check_pre_trade(self, request: OrderRequest) -> RiskCheckResult:
        """
        Perform pre-trade risk checks.
        """
        # 1. Max Position Ratio Check
        account_info = await self.trading_service.get_account_info(request.account_id)
        if not account_info:
            return RiskCheckResult(False, f"Risk Check Failed: Could not fetch account info for {request.account_id}")

        order_value = request.price * request.volume if request.price > 0 else 0
        # If market order, we might need to estimate price or have a different check
        if request.order_type == "MARKET":
            # Just a placeholder for market order estimation - in real QMT we'd query latest price
            order_value = 0 # Assume 0 for now or fetch price
            logger.warning(f"Market order risk check for {request.symbol} - estimating price as 0.")

        if order_value > account_info.available_cash * settings.RISK_MAX_POSITION_RATIO:
            msg = (f"Risk Breach: Order value {order_value} exceeds {settings.RISK_MAX_POSITION_RATIO * 100}% "
                   f"of available cash ({account_info.available_cash})")
            logger.warning(msg)
            return RiskCheckResult(False, msg)

        # 2. Daily Loss Limit (Mock check for now)
        # In a real system, we'd query cumulative PnL for the current day
        current_daily_loss_ratio = 0.02 # Mock 2% loss
        if current_daily_loss_ratio > settings.RISK_DAILY_LOSS_LIMIT:
            msg = f"Risk Breach: Daily loss limit reached ({current_daily_loss_ratio * 100}% > {settings.RISK_DAILY_LOSS_LIMIT * 100}%)"
            logger.warning(msg)
            return RiskCheckResult(False, msg)

        # 3. Blacklist Check
        blacklist = ["ST*"] # Mock blacklist
        if request.symbol in blacklist:
            msg = f"Risk Breach: Symbol {request.symbol} is in the blacklist."
            logger.warning(msg)
            return RiskCheckResult(False, msg)

        return RiskCheckResult(True, "Pre-trade risk checks passed.")

# Service instance
# Note: This will be initialized with the actual trading service in the API layer
