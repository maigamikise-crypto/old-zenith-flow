package com.zenithflow.quant.dto;

import lombok.Data;

/**
 * 交易报单响应 DTO
 */
@Data
public class TradeResponse {
    private String orderId;
    private String status;
    private String message;
}
