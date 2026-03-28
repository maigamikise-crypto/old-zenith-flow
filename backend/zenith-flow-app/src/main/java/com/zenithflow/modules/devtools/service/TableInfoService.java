/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.service;


import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.devtools.entity.TableInfoEntity;

import java.util.Map;

/**
 * 表
 *
 *
 */
public interface TableInfoService extends BaseService<TableInfoEntity> {

    PageData<TableInfoEntity> page(Map<String, Object> params);

    TableInfoEntity getByTableName(String tableName);

    void deleteByTableName(String tableName);

    void deleteBatchIds(Long[] ids);
}
