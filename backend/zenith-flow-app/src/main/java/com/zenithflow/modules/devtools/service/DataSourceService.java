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
import com.zenithflow.modules.devtools.entity.DataSourceEntity;

import java.util.List;
import java.util.Map;

/**
 * 数据源管理
 *
 *
 */
public interface DataSourceService extends BaseService<DataSourceEntity> {

    PageData<DataSourceEntity> page(Map<String, Object> params);

    List<DataSourceEntity> list();
}
