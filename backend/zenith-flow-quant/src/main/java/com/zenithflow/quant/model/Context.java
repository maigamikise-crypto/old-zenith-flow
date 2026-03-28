package com.zenithflow.quant.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 交易上下文：保存当前账户状态
 */
@Data
public class Context {
    private BigDecimal cash; // 可用资金
    private BigDecimal totalAssets; // 总资产
    private Map<String, Integer> positions = new HashMap<>(); // 持仓: symbol -> volume
    
    public Context(BigDecimal initialCash) {
        this.cash = initialCash;
        this.totalAssets = initialCash;
    }
    
    // 简单的买入模拟 (不考虑滑点和手续费的简化版)
    public void buy(String symbol, BigDecimal price, int volume) {
        BigDecimal cost = price.multiply(new BigDecimal(volume));
        if (cash.compareTo(cost) >= 0) {
            cash = cash.subtract(cost);
            positions.merge(symbol, volume, Integer::sum);
            System.out.println("BUY: " + symbol + " @ " + price + " qty: " + volume);
        } else {
            System.out.println("Ignored BUY (Insufficient Cash): " + cost);
        }
    }

    // 简单的卖出模拟
    public void sell(String symbol, BigDecimal price, int volume) {
        int currentPos = positions.getOrDefault(symbol, 0);
        if (currentPos >= volume) {
            BigDecimal gain = price.multiply(new BigDecimal(volume));
            cash = cash.add(gain);
            positions.put(symbol, currentPos - volume);
            System.out.println("SELL: " + symbol + " @ " + price + " qty: " + volume);
        } else {
            System.out.println("Ignored SELL (Insufficient Position)");
        }
    }
}

