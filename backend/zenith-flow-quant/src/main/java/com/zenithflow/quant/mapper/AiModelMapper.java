package com.zenithflow.quant.mapper;

import com.zenithflow.commons.dynamic.datasource.annotation.DataSource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenithflow.quant.model.AiModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DataSource("quant")
public interface AiModelMapper extends BaseMapper<AiModel> {
}
