/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenithflow.modules.devtools.entity.FieldTypeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * 字段类型管理
 *
 *
 */
@Mapper
public interface FieldTypeDao extends BaseMapper<FieldTypeEntity> {

    /**
     * 根据tableId，获取包列表
     */
    Set<String> getPackageListByTableId(Long tableId);

    /**
     * 获取全部字段类型
     */
    Set<String> list();
}
