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
import com.zenithflow.modules.devtools.dao.DataSourceDao;
import com.zenithflow.modules.devtools.entity.DataSourceEntity;
import com.zenithflow.modules.devtools.service.DataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * 数据源管理
 *
 *
 */
@Service
public class DataSourceServiceImpl extends BaseServiceImpl<DataSourceDao, DataSourceEntity> implements DataSourceService {

    @Override
    public PageData<DataSourceEntity> page(Map<String, Object> params) {
        IPage<DataSourceEntity> page = baseDao.selectPage(
            getPage(params, Constant.CREATE_DATE, false),
            getWrapper(params)
        );
        return new PageData<>(page.getRecords(), page.getTotal());
    }

    private QueryWrapper<DataSourceEntity> getWrapper(Map<String, Object> params){
        String connName = (String)params.get("connName");
        String dbType = (String)params.get("dbType");

        QueryWrapper<DataSourceEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(connName), "conn_name", connName);
        wrapper.eq(StringUtils.isNotEmpty(dbType), "db_type", dbType);
        return wrapper;
    }

    @Override
    public List<DataSourceEntity> list() {
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);

        return baseDao.selectList(wrapper);
    }

}
