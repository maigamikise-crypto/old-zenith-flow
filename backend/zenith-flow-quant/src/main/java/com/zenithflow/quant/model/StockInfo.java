package com.zenithflow.quant.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("stock_info")
public class StockInfo {
    @TableId
    private String symbol;
    private String name;
    private String market;
}

