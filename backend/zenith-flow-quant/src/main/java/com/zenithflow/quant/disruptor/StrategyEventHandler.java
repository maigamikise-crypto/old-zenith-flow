package com.zenithflow.quant.disruptor;

import com.lmax.disruptor.EventHandler;
import com.zenithflow.quant.model.Context;
import com.zenithflow.quant.service.Strategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消费者：处理市场数据事件
 * 实际上这里就是策略引擎的入口
 */
@Slf4j
@RequiredArgsConstructor
public class StrategyEventHandler implements EventHandler<MarketDataEvent> {

    private final Strategy strategy;
    private final Context context;

    @Override
    public void onEvent(MarketDataEvent event, long sequence, boolean endOfBatch) {
        try {
            // 调用策略核心逻辑
            strategy.onBar(context, event.getBar());
        } catch (Exception e) {
            log.error("Error processing event sequence: " + sequence, e);
        }
    }
}

