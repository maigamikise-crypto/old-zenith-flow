package com.zenithflow.quant.controller;

import com.zenithflow.common.utils.Result;
import com.zenithflow.quant.client.AiEngineClient;
import com.zenithflow.quant.dto.TrainRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/train")
@RequiredArgsConstructor
public class TrainController {

    private static final Logger log = LoggerFactory.getLogger(TrainController.class);
    
    private final AiEngineClient aiEngineClient;

    @PostMapping("/start")
    public Result<String> startTraining(@RequestBody TrainRequest request) {
        log.info("Received training request: name={}, algo={}, symbols={}", request.getName(), request.getAlgo(), request.getSymbols());
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", request.getName());
            payload.put("algo", request.getAlgo());
            payload.put("symbols", request.getSymbols());
            
            // Call Python API via Feign Client
            Map<String, Object> response = aiEngineClient.startTraining(payload);
            log.info("AI Engine response: {}", response);
            
            return new Result<String>().ok("Training started successfully via AI Engine.");
        } catch (Exception e) {
            log.error("Failed to call AI Engine: {}", e.getMessage());
            return new Result<String>().error("Failed to start training: " + e.getMessage());
        }
    }
}
