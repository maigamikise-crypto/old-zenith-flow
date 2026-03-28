package com.zenithflow.quant.dto;

import lombok.Data;

/**
 * 账户资产信息 DTO
 */
@Data
public class AccountInfo {
    private String accountId;
    private Double balance;
    private Double availableCash;
    private Double marketValue;
}
