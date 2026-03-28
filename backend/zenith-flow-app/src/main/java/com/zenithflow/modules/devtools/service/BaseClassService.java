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
import com.zenithflow.modules.devtools.entity.BaseClassEntity;

import java.util.List;
import java.util.Map;

/**
 * 基类管理
 *
 *
 */
public interface BaseClassService extends BaseService<BaseClassEntity> {

    PageData<BaseClassEntity> page(Map<String, Object> params);

    List<BaseClassEntity> list();
}
