package com.zenithflow.quant.service;

import com.zenithflow.quant.model.Bar;
import com.zenithflow.quant.model.Context;

/**
 * 策略接口
 * 所有的量化策略都实现此接口
 */
public interface Strategy {
    
    // 策略初始化时调用
    void onStart(Context context);
    
    // 每一根K线到来时调用 (核心逻辑)
    void onBar(Context context, Bar bar);
    
    // 回测结束时调用
    void onStop(Context context);
}

