/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.service;


import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.devtools.entity.TableFieldEntity;

import java.util.List;

/**
 * 列
 *
 *
 */
public interface TableFieldService extends BaseService<TableFieldEntity> {

    List<TableFieldEntity> getByTableName(String tableName);

    void deleteByTableName(String tableName);

    void deleteBatchTableIds(Long[] tableIds);
}
