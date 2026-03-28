package com.zenithflow.quant.dto;

import lombok.Data;

@Data
public class BacktestRequest {
    private String symbol;
    private String index;
    private String strategy = "ai"; // default value
    private String model; // 指定使用的模型名称
}
