package com.zenithflow.quant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * AI Engine 远程调用客户端
 * URL 配置在 application.yml 中: ai-engine.url
 */
@FeignClient(name = "ai-engine", url = "${ai-engine.url:http://localhost:8000/api/v1}")
public interface AiEngineClient {

    /**
     * 启动训练
     * POST /train/start
     */
    @PostMapping("/train/start")
    Map<String, Object> startTraining(@RequestBody Map<String, Object> request);

    /**
     * 获取指数成分股
     * GET /data/index-stocks
     */
    @GetMapping("/data/index-stocks")
    List<String> getIndexStocks(@RequestParam("index_code") String indexCode);

}

