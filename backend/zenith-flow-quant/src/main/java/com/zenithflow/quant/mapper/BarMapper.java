package com.zenithflow.quant.mapper;

import com.zenithflow.commons.dynamic.datasource.annotation.DataSource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenithflow.quant.model.Bar;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper 接口
 * 继承 BaseMapper 后自动拥有 CRUD 能力
 */
@Mapper
@DataSource("quant")
public interface BarMapper extends BaseMapper<Bar> {
    // 如果需要手写复杂 SQL，可以在这里定义方法并在 XML 中实现
    // 目前基础功能 BaseMapper 已足够
}
