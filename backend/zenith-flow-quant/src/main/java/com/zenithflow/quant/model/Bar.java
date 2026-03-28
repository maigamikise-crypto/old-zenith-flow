package com.zenithflow.quant.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Bar 实体类
 * 对应数据库表 market_data_bar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("market_data_bar")
public class Bar {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String symbol;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    // 价格字段 (MyBatis Plus 默认会将 camelCase 映射为 snake_case，如 high_price)
    // 但如果是标准字段如 open, high, low, close，通常数据库列名也是这些
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;

    private Long volume;
    private BigDecimal amount;

    // 该字段不存储在数据库中 (业务计算字段)
    public BigDecimal getChangePct() {
        if (open == null || open.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return close.subtract(open).divide(open, 4, BigDecimal.ROUND_HALF_UP);
    }
}
