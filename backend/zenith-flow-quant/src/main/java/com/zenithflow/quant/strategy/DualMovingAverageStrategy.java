package com.zenithflow.quant.strategy;

import com.zenithflow.quant.model.Bar;
import com.zenithflow.quant.model.Context;
import com.zenithflow.quant.service.Strategy;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.math.BigDecimal;

public class DualMovingAverageStrategy implements Strategy {

    private final int shortWindow = 5;
    private final int longWindow = 20;
    
    // 使用 Apache Commons Math 或简单的队列来维护历史价格窗口
    private DescriptiveStatistics shortMaStats = new DescriptiveStatistics(shortWindow);
    private DescriptiveStatistics longMaStats = new DescriptiveStatistics(longWindow);

    @Override
    public void onStart(Context context) {
        System.out.println("策略启动: 双均线 (MA" + shortWindow + " vs MA" + longWindow + ")");
    }

    @Override
    public void onBar(Context context, Bar bar) {
        double price = bar.getClose().doubleValue();
        
        // 更新均线计算窗口
        shortMaStats.addValue(price);
        longMaStats.addValue(price);
        
        // 数据不足时不操作
        if (longMaStats.getN() < longWindow) {
            return;
        }
        
        double ma5 = shortMaStats.getMean();
        double ma20 = longMaStats.getMean();
        
        int currentPos = context.getPositions().getOrDefault(bar.getSymbol(), 0);
        
        // 简单的金叉/死叉逻辑
        // 注意：实际策略需要判断上一时刻的状态以确认是“交叉”瞬间，这里简化为状态判断
        if (ma5 > ma20 && currentPos == 0) {
            // 全仓买入 (简化)
            int volume = context.getCash().divide(bar.getClose(), 0, BigDecimal.ROUND_DOWN).intValue();
            volume = (volume / 100) * 100; // A股必须是100股的整数倍
            if (volume > 0) {
                context.buy(bar.getSymbol(), bar.getClose(), volume);
            }
        } else if (ma5 < ma20 && currentPos > 0) {
            // 清仓卖出
            context.sell(bar.getSymbol(), bar.getClose(), currentPos);
        }
    }

    @Override
    public void onStop(Context context) {
        System.out.println("策略停止");
    }
}

