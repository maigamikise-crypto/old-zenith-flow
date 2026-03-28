package com.zenithflow.quant.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("backtest_daily_result")
public class BacktestDailyResult {
    private Integer executionId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private BigDecimal totalAssets;
    private BigDecimal benchmarkValue;
    private BigDecimal holdingsValue;
    private BigDecimal cash;
    private BigDecimal dailyReturn;
}

