package com.zenithflow.quant.service;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.zenithflow.quant.disruptor.MarketDataEvent;
import com.zenithflow.quant.disruptor.MarketDataEventFactory;
import com.zenithflow.quant.disruptor.StrategyEventHandler;
import com.zenithflow.commons.dynamic.datasource.annotation.DataSource;
import com.zenithflow.quant.mapper.BacktestDailyResultMapper;
import com.zenithflow.quant.mapper.BacktestExecutionMapper;
import com.zenithflow.quant.mapper.StockInfoMapper;
import com.zenithflow.quant.model.BacktestDailyResult;
import com.zenithflow.quant.model.BacktestExecution;
import com.zenithflow.quant.model.Bar;
import com.zenithflow.quant.model.Context;
import com.zenithflow.quant.model.StockInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@DataSource("quant")
public class BacktestEngine {

    private final DataService dataService;
    private final BacktestExecutionMapper executionMapper;
    private final BacktestDailyResultMapper dailyResultMapper;
    private final StockInfoMapper stockInfoMapper;

    @Transactional
    public BacktestExecution runBacktest(String symbol, String csvPath, Strategy strategy) {
        // 1. 如果 csvPath 不为空，尝试导入（兼容旧逻辑）
        if (csvPath != null) {
            dataService.importCsvToDb(csvPath, symbol);
        }
        
        // 2. 从 DB 读取全部历史数据
        System.out.println("Loading data from DB for " + symbol);
        List<Bar> history = dataService.loadFromDb(symbol);
        System.out.println("Loaded " + history.size() + " bars from DB.");
        
        if (history.isEmpty()) {
            throw new RuntimeException("No data found for " + symbol + ". Please run Python fetch script first.");
        }

        // 3. 初始化上下文
        BigDecimal initialCash = new BigDecimal("1000000");
        Context context = new Context(initialCash);
        
        // 查询股票名称
        String stockName = "";
        StockInfo info = stockInfoMapper.selectById(symbol);
        if (info != null) {
            stockName = info.getName();
        }
        
        // 记录回测元数据
        BacktestExecution execution = BacktestExecution.builder()
                .strategyName(strategy.getClass().getSimpleName())
                .symbol(symbol)
                .stockName(stockName) // 设置股票名称
                .startDate(history.get(0).getDate())
                .endDate(history.get(history.size() - 1).getDate())
                .initialCash(initialCash)
                .createdAt(LocalDateTime.now())
                .build();
        
        executionMapper.insert(execution);
        Integer executionId = execution.getId();
        
        // 4. 配置 Disruptor (暂略)
        
        System.out.println("=== Backtest Start (Synchronous Mode for Accurate Recording) ===");
        strategy.onStart(context);
        
        List<BacktestDailyResult> dailyResults = new ArrayList<>();
        BigDecimal benchmarkStartPrice = history.get(0).getClose();
        
        for (Bar bar : history) {
            // 1. 策略处理
            strategy.onBar(context, bar);
            
            // 2. 计算当日资产
            BigDecimal currentPrice = bar.getClose();
            int pos = context.getPositions().getOrDefault(symbol, 0);
            BigDecimal holdingsValue = currentPrice.multiply(new BigDecimal(pos));
            BigDecimal totalAssets = context.getCash().add(holdingsValue);
            
            // 更新 context 中的总资产
            context.setTotalAssets(totalAssets);
            
            // 3. 计算基准表现 (Buy & Hold)
            // Benchmark Value = Initial Cash * (Current Price / Start Price)
            BigDecimal benchmarkValue = initialCash.multiply(currentPrice).divide(benchmarkStartPrice, 2, RoundingMode.HALF_UP);
            
            // 4. 记录每日结果
            BacktestDailyResult dailyResult = BacktestDailyResult.builder()
                    .executionId(executionId)
                    .date(bar.getDate())
                    .totalAssets(totalAssets)
                    .holdingsValue(holdingsValue)
                    .cash(context.getCash())
                    .benchmarkValue(benchmarkValue)
                    .dailyReturn(BigDecimal.ZERO) 
                    .build();
            
            dailyResults.add(dailyResult);
        }
        
        // 批量插入
        for (BacktestDailyResult result : dailyResults) {
            dailyResultMapper.insert(result);
        }
        
        strategy.onStop(context);
        
        // 更新回测主表的最终结果
        BigDecimal finalAssets = context.getTotalAssets();
        BigDecimal totalReturn = finalAssets.subtract(initialCash)
                .divide(initialCash, 4, RoundingMode.HALF_UP);
        
        execution.setFinalAssets(finalAssets);
        execution.setTotalReturn(totalReturn);
        executionMapper.updateById(execution);
        
        System.out.println("Final Assets: " + finalAssets);
        System.out.println("Total Return: " + totalReturn.multiply(new BigDecimal(100)) + "%");
        
        return execution;
    }

    public List<BacktestExecution> getBacktestHistory() {
        return executionMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BacktestExecution>()
                .orderByDesc("created_at"));
    }

    public List<BacktestDailyResult> getBacktestDailyResults(Integer executionId) {
        return dailyResultMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BacktestDailyResult>()
                .eq("execution_id", executionId)
                .orderByAsc("date"));
    }
}
