package com.zenithflow.quant.disruptor;

import com.zenithflow.quant.model.Bar;
import lombok.Data;

/**
 * 市场数据事件
 * 这就是在 RingBuffer 中流转的载体
 */
@Data
public class MarketDataEvent {
    private Bar bar;
    
    // 可以在这里添加更多上下文信息，如订单簿快照等
}

