package com.zenithflow.quant.controller;

import com.zenithflow.common.utils.Result;
import com.zenithflow.quant.dto.AccountInfo;
import com.zenithflow.quant.dto.PositionInfo;
import com.zenithflow.quant.dto.TradeRequest;
import com.zenithflow.quant.dto.TradeResponse;
import com.zenithflow.quant.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交易控制层
 * 提供给前端调用的实盘交易 API
 */
@Slf4j
@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    /**
     * 下单交易
     */
    @PostMapping("/order")
    public Result<TradeResponse> placeOrder(@RequestBody TradeRequest request) {
        log.info("REST request to place order: {}", request);
        TradeResponse response = tradeService.placeOrder(request);
        if ("FAILED".equals(response.getStatus())) {
            return new Result<TradeResponse>().error(response.getMessage());
        }
        return new Result<TradeResponse>().ok(response);
    }

    /**
     * 撤销订单
     */
    @PostMapping("/cancel")
    public Result<Boolean> cancelOrder(@RequestParam String orderId, @RequestParam String accountId) {
        log.info("REST request to cancel order: {} for account: {}", orderId, accountId);
        Boolean success = tradeService.cancelOrder(orderId, accountId);
        if (Boolean.TRUE.equals(success)) {
            return new Result<Boolean>().ok(true);
        }
        return new Result<Boolean>().error("Failed to cancel order.");
    }

    /**
     * 获取账户资产
     */
    @GetMapping("/account")
    public Result<AccountInfo> getAccountInfo(@RequestParam String accountId) {
        log.info("REST request to get account info: {}", accountId);
        AccountInfo info = tradeService.getAccountInfo(accountId);
        if (info == null) {
            return new Result<AccountInfo>().error("Failed to fetch account info.");
        }
        return new Result<AccountInfo>().ok(info);
    }

    /**
     * 获取账户持仓
     */
    @GetMapping("/positions")
    public Result<List<PositionInfo>> getPositions(@RequestParam String accountId) {
        log.info("REST request to get positions: {}", accountId);
        List<PositionInfo> positions = tradeService.getPositions(accountId);
        return new Result<List<PositionInfo>>().ok(positions);
    }
}
