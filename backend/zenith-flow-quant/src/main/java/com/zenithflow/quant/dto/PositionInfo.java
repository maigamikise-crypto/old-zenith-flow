package com.zenithflow.quant.dto;

import lombok.Data;

/**
 * 账户持仓信息 DTO
 */
@Data
public class PositionInfo {
    private String symbol;
    private Integer volume;
    private Integer availableVolume;
    private Double avgPrice;
    private Double marketValue;
}
