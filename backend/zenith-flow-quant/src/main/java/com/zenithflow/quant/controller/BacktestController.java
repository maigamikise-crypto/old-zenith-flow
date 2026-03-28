package com.zenithflow.quant.controller;

import com.zenithflow.common.utils.Result;
import com.zenithflow.quant.client.AiEngineClient;
import com.zenithflow.quant.dto.BacktestRequest;
import com.zenithflow.quant.model.AiModel;
import com.zenithflow.quant.model.BacktestDailyResult;
import com.zenithflow.quant.model.BacktestExecution;
import com.zenithflow.quant.service.AiPredictionService;
import com.zenithflow.quant.service.BacktestEngine;
import com.zenithflow.quant.strategy.AiPredictionStrategy;
import com.zenithflow.quant.strategy.DualMovingAverageStrategy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/backtest")
@RequiredArgsConstructor
public class BacktestController {

    private static final Logger log = LoggerFactory.getLogger(BacktestController.class);

    private final BacktestEngine backtestEngine;
    private final AiPredictionService aiPredictionService;
    private final AiEngineClient aiEngineClient;

    /**
     * 获取所有可用模型列表 (包含详情)
     * GET /backtest/models
     */
    @GetMapping("/models")
    public Result<List<AiModel>> getAvailableModels() {
        List<AiModel> models = aiPredictionService.getAvailableModelDetails();
        return new Result<List<AiModel>>().ok(models);
    }

    /**
     * 热重载 AI 模型
     * POST /backtest/reload-model
     */
    @PostMapping("/reload-model")
    public Result<String> reloadModel() {
        try {
            aiPredictionService.reload();
            return new Result<String>().ok("Models reloaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<String>().error("Error reloading models: " + e.getMessage());
        }
    }

    /**
     * 启动回测
     * POST /backtest/start
     * Body: {"symbol": "000429,600519", "index": "sh.000300", "strategy": "ai", "model": "rf_v1"}
     */
    @PostMapping("/start")
    public Result<List<BacktestExecution>> startBacktest(@RequestBody(required = false) BacktestRequest request) {

        log.info("Received backtest request: symbol={}, index={}, strategy={}, model={}",
                request.getSymbol(), request.getIndex(), request.getStrategy(), request.getModel());

        List<String> targetSymbols = new ArrayList<>();

        try {
            // 1. Handle index via Python API (Feign)
            if (request.getIndex() != null && !request.getIndex().isEmpty()) {
                log.info("Fetching stocks for index: {}", request.getIndex());
                targetSymbols.addAll(fetchStocksFromIndex(request.getIndex()));
            }

            // 2. Handle manual symbols
            if (request.getSymbol() != null && !request.getSymbol().isEmpty()) {
                String[] parts = request.getSymbol().split(",");
                for (String s : parts) {
                    if (!s.trim().isEmpty()) {
                        targetSymbols.add(s.trim());
                    }
                }
            }

            if (targetSymbols.isEmpty()) {
                log.error("No symbols provided via 'symbol' or 'index' param.");
                return new Result<List<BacktestExecution>>().error("No symbols provided via 'symbol' or 'index' param.");
            }

            // Remove duplicates
            targetSymbols = targetSymbols.stream().distinct().collect(Collectors.toList());
            log.info("Target symbols: {}", targetSymbols);

            List<BacktestExecution> results = new ArrayList<>();
            for (String sym : targetSymbols) {
                try {
                    BacktestExecution result;
                    if ("ma".equalsIgnoreCase(request.getStrategy())) {
                        result = backtestEngine.runBacktest(sym, null, new DualMovingAverageStrategy());
                    } else {
                        // AI Strategy
                        // 确定使用的模型
                        String modelName = request.getModel();
                        if (modelName == null || modelName.isEmpty()) {
                            // 如果未指定，且只有一个模型，用默认的
                            // 否则用列表中第一个
                            List<AiModel> models = aiPredictionService.getAvailableModelDetails();
                            if (!models.isEmpty()) {
                                modelName = models.get(0).getModelName();
                            } else {
                                 throw new RuntimeException("No AI models available. Please train a model first.");
                            }
                        }

                        result = backtestEngine.runBacktest(sym, null, new AiPredictionStrategy(aiPredictionService, modelName));
                    }
                    results.add(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Log error but continue with other symbols
                    log.error("Failed to backtest {}: {}", sym, e.getMessage());
                }
            }
            return new Result<List<BacktestExecution>>().ok(results);
        } catch (Exception e) {
            return new Result<List<BacktestExecution>>().error("Backtest failed: " + e.getMessage());
        }
    }

    private List<String> fetchStocksFromIndex(String indexCode) {
        try {
            log.info("Calling AI Engine via Feign: index_code={}", indexCode);
            return aiEngineClient.getIndexStocks(indexCode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch index stocks from AI Engine: " + e.getMessage(), e);
        }
    }

    /**
     * 获取回测历史列表
     * GET /backtest/list
     */
    @GetMapping("/list")
    public Result<List<BacktestExecution>> getBacktestHistory() {
        List<BacktestExecution> list = backtestEngine.getBacktestHistory();
        return new Result<List<BacktestExecution>>().ok(list);
    }

    /**
     * 获取单次回测的每日详情（用于绘图）
     * GET /backtest/{id}/daily-results
     */
    @GetMapping("/{id}/daily-results")
    public Result<List<BacktestDailyResult>> getBacktestDailyResults(@PathVariable Integer id) {
        List<BacktestDailyResult> results = backtestEngine.getBacktestDailyResults(id);
        return new Result<List<BacktestDailyResult>>().ok(results);
    }
}
