/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenithflow.modules.devtools.entity.TemplateEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 模板管理
 *
 *
 */
@Mapper
public interface TemplateDao extends BaseMapper<TemplateEntity> {

    int updateStatusBatch(Map<String, Object> map);
}
