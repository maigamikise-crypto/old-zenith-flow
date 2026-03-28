/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.impl.BaseServiceImpl;
import com.zenithflow.modules.devtools.dao.TemplateDao;
import com.zenithflow.modules.devtools.entity.TemplateEntity;
import com.zenithflow.modules.devtools.service.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板管理
 *
 *
 */
@Service
public class TemplateServiceImpl extends BaseServiceImpl<TemplateDao, TemplateEntity> implements TemplateService {
    @Override
    public PageData<TemplateEntity> page(Map<String, Object> params) {
        IPage<TemplateEntity> page = baseDao.selectPage(
            getPage(params, Constant.CREATE_DATE, false),
            getWrapper(params)
        );
        return new PageData<>(page.getRecords(), page.getTotal());
    }

    private QueryWrapper<TemplateEntity> getWrapper(Map<String, Object> params){
        String name = (String)params.get("name");

        QueryWrapper<TemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), "name", name);

        return wrapper;
    }

    @Override
    public List<TemplateEntity> list() {
        QueryWrapper<TemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);

        return baseDao.selectList(wrapper);
    }

    @Override
    public void updateStatusBatch(Long[] ids, int status){
        Map<String, Object> map = new HashMap<>(2);
        map.put("ids", ids);
        map.put("status", status);
        baseDao.updateStatusBatch(map);
    }
}
