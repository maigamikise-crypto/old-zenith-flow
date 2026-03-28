package com.zenithflow.quant.service;

import com.zenithflow.quant.client.AiEngineClient;
import com.zenithflow.quant.dto.AccountInfo;
import com.zenithflow.quant.dto.PositionInfo;
import com.zenithflow.quant.dto.TradeRequest;
import com.zenithflow.quant.dto.TradeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交易服务实现
 * 处理前端交易请求并调用 AI Engine 的交易接口
 */
@Slf4j
@Service
public class TradeService {

    @Autowired
    private AiEngineClient aiEngineClient;

    /**
     * 下单交易
     * @param request 交易请求
     * @return 交易响应
     */
    public TradeResponse placeOrder(TradeRequest request) {
        log.info("Processing trade request for symbol: {}, side: {}", request.getSymbol(), request.getAction());
        try {
            return aiEngineClient.placeOrder(request);
        } catch (Exception e) {
            log.error("Failed to place order via AI Engine", e);
            TradeResponse response = new TradeResponse();
            response.setStatus("FAILED");
            response.setMessage("Backend Service Error: " + e.getMessage());
            return response;
        }
    }

    /**
     * 撤销订单
     */
    public Boolean cancelOrder(String orderId, String accountId) {
        log.info("Cancelling order: {} for account: {}", orderId, accountId);
        try {
            return aiEngineClient.cancelOrder(orderId, accountId);
        } catch (Exception e) {
            log.error("Failed to cancel order", e);
            return false;
        }
    }

    /**
     * 查询账户信息
     */
    public AccountInfo getAccountInfo(String accountId) {
        try {
            return aiEngineClient.getAccountInfo(accountId);
        } catch (Exception e) {
            log.error("Failed to fetch account info", e);
            return null;
        }
    }

    /**
     * 查询持仓信息
     */
    public List<PositionInfo> getPositions(String accountId) {
        try {
            return aiEngineClient.getPositions(accountId);
        } catch (Exception e) {
            log.error("Failed to fetch positions", e);
            return List.of();
        }
    }
}
