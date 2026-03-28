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
import com.zenithflow.modules.devtools.entity.TemplateEntity;

import java.util.List;
import java.util.Map;

/**
 * 模板管理
 *
 *
 */
public interface TemplateService extends BaseService<TemplateEntity> {

    PageData<TemplateEntity> page(Map<String, Object> params);

    List<TemplateEntity> list();

    void updateStatusBatch(Long[] ids, int status);

}
