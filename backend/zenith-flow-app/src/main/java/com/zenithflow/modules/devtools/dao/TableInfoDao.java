/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenithflow.modules.devtools.entity.TableInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 列
 *
 *
 */
@Mapper
public interface TableInfoDao extends BaseMapper<TableInfoEntity> {
    TableInfoEntity getByTableName(String tableName);

    void deleteByTableName(String tableName);
}
