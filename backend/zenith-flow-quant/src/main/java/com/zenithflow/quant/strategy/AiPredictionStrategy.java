package com.zenithflow.quant.strategy;

import com.zenithflow.quant.model.Bar;
import com.zenithflow.quant.model.Context;
import com.zenithflow.quant.service.AiPredictionService;
import com.zenithflow.quant.service.Strategy;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class AiPredictionStrategy implements Strategy {

    private final AiPredictionService predictionService;
    private final String modelName;
    
    // 历史收盘价 (用于计算 SMA, Volatility, Lag Returns)
    // 需要保留至少 21 天数据 (20 + 1) 来计算 20日 变动
    private final LinkedList<Double> closeHistory = new LinkedList<>();
    private final int MAX_HISTORY = 30; 

    // --- 增量计算的状态变量 ---
    private double ema12 = 0;
    private double ema26 = 0;
    private double dea9 = 0;
    
    private double avgGain = 0;
    private double avgLoss = 0;
    
    private boolean initialized = false;
    
    // 特征窗口
    private final int LAG_WINDOW = 5;

    public AiPredictionStrategy(AiPredictionService predictionService, String modelName) {
        this.predictionService = predictionService;
        this.modelName = modelName;
    }

    @Override
    public void onStart(Context context) {
        System.out.println("策略启动: AI 预测模型 (" + modelName + ")");
    }

    @Override
    public void onBar(Context context, Bar bar) {
        double close = bar.getClose().doubleValue();
        
        // 1. 增量更新指标状态
        updateIndicators(close);
        
        // 2. 维护历史数据
        closeHistory.add(close);
        if (closeHistory.size() > MAX_HISTORY) {
            closeHistory.removeFirst();
        }
        
        // 3. 数据预热
        // MACD 和 RSI 需要一定时间的预热才能稳定，这里简单判断历史长度
        if (closeHistory.size() < 25) {
            return;
        }
        
        // 4. 计算特征向量
        try {
            float[] features = buildFeatures(close);
            
            // 5. 预测 (使用指定模型)
            long prediction = predictionService.predict(modelName, features);
            
            // 6. 交易逻辑
            executeTrade(context, bar, prediction);
            
        } catch (Exception e) {
            // 数据不足或其他计算错误时跳过
            // System.err.println("特征计算失败: " + e.getMessage());
        }
    }
    
    private void updateIndicators(double close) {
        if (!initialized) {
            // 初始化
            ema12 = close;
            ema26 = close;
            dea9 = 0;
            avgGain = 0;
            avgLoss = 0;
            initialized = true;
            return;
        }
        
        // MACD Update
        // Pandas ewm(span=N, adjust=False) -> alpha = 2 / (N + 1)
        double alpha12 = 2.0 / 13.0;
        double alpha26 = 2.0 / 27.0;
        double alpha9 = 2.0 / 10.0;
        
        ema12 = close * alpha12 + ema12 * (1 - alpha12);
        ema26 = close * alpha26 + ema26 * (1 - alpha26);
        
        double diff = ema12 - ema26;
        dea9 = diff * alpha9 + dea9 * (1 - alpha9);
        
        // RSI Update
        // Pandas ewm(alpha=1/14, adjust=False)
        double lastClose = closeHistory.isEmpty() ? close : closeHistory.getLast();
        double delta = close - lastClose;
        
        double gain = Math.max(delta, 0);
        double loss = Math.max(-delta, 0);
        
        double alphaRsi = 1.0 / 14.0;
        
        avgGain = gain * alphaRsi + avgGain * (1 - alphaRsi);
        avgLoss = loss * alphaRsi + avgLoss * (1 - alphaRsi);
    }
    
    private float[] buildFeatures(double currentClose) {
        // 特征列表: [ret_lag_0...4, rsi_14, macd_hist, ma20_bias, volatility_20]
        // 共 5 + 4 = 9 个特征
        
        float[] feats = new float[9];
        int idx = 0;
        
        List<Double> prices = new ArrayList<>(closeHistory);
        int size = prices.size();
        
        // We need returns for T, T-1, ... T-4
        for (int i = 0; i < LAG_WINDOW; i++) {
            if (size - 2 - i < 0) throw new RuntimeException("Data not enough");
            
            double pNew = prices.get(size - 1 - i);
            double pOld = prices.get(size - 2 - i);
            feats[idx++] = (float) ((pNew - pOld) / pOld);
        }
        
        // --- B. RSI (1) ---
        double rs = (avgLoss == 0) ? 100 : avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + rs));
        feats[idx++] = (float) rsi;
        
        // --- C. MACD Hist (1) ---
        double diff = ema12 - ema26;
        double macdHist = diff - dea9;
        feats[idx++] = (float) macdHist;
        
        // --- D. MA20 Bias (1) ---
        double sum = 0;
        int count = 0;
        for (int i = 0; i < 20; i++) {
            if (size - 1 - i < 0) break;
            sum += prices.get(size - 1 - i);
            count++;
        }
        double ma20 = sum / count;
        feats[idx++] = (float) ((currentClose - ma20) / ma20);
        
        // --- E. Volatility 20 (1) ---
        List<Double> returns = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (size - 2 - i < 0) break;
            double pNew = prices.get(size - 1 - i);
            double pOld = prices.get(size - 2 - i);
            returns.add((pNew - pOld) / pOld);
        }
        
        double meanRet = returns.stream().mapToDouble(d -> d).average().orElse(0.0);
        double variance = returns.stream().mapToDouble(d -> Math.pow(d - meanRet, 2)).sum() / returns.size();
        double stdDev = Math.sqrt(variance);
        
        feats[idx++] = (float) stdDev;
        
        return feats;
    }

    private void executeTrade(Context context, Bar bar, long prediction) {
        int currentPos = context.getPositions().getOrDefault(bar.getSymbol(), 0);
        
        if (prediction == 1 && currentPos == 0) {
            // 买入
            int volume = context.getCash().divide(bar.getClose(), 0, BigDecimal.ROUND_DOWN).intValue();
            volume = (volume / 100) * 100;
            if (volume > 0) {
                context.buy(bar.getSymbol(), bar.getClose(), volume);
            }
        } else if (prediction == 0 && currentPos > 0) {
            // 卖出
            context.sell(bar.getSymbol(), bar.getClose(), currentPos);
        }
    }

    @Override
    public void onStop(Context context) {
        System.out.println("策略停止");
    }
}
