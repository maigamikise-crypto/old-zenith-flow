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
import com.zenithflow.modules.devtools.entity.FieldTypeEntity;

import java.util.Map;
import java.util.Set;

/**
 * 字段类型管理
 *
 *
 */
public interface FieldTypeService extends BaseService<FieldTypeEntity> {
    PageData<FieldTypeEntity> page(Map<String, Object> params);

    Map<String, FieldTypeEntity> getMap();

    /**
     * 根据tableId，获取包列表
     */
    Set<String> getPackageListByTableId(Long tableId);

    Set<String> list();
}
