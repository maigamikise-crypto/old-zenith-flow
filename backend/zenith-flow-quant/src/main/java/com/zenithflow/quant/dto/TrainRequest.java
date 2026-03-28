package com.zenithflow.quant.dto;

import lombok.Data;

@Data
public class TrainRequest {
    private String name;
    private String algo; // rf, lr
    private String symbols; // comma separated
}

