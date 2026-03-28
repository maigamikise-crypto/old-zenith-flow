/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenithflow.modules.devtools.entity.TableFieldEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表
 *
 *
 */
@Mapper
public interface TableFieldDao extends BaseMapper<TableFieldEntity> {

    List<TableFieldEntity> getByTableName(String tableName);

    void deleteByTableName(String tableName);

    void deleteBatchTableIds(Long[] tableIds);
}
