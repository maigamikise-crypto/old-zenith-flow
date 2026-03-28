package com.zenithflow.quant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易报单请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequest {
    private String symbol;
    private String action;      // BUY, SELL
    private String orderType;   // MARKET, LIMIT
    private Double price;
    private Integer volume;
    private String accountId;
}
