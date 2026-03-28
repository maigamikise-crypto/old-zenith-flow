/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.service.impl;

import com.zenithflow.common.service.impl.BaseServiceImpl;
import com.zenithflow.modules.devtools.dao.TableFieldDao;
import com.zenithflow.modules.devtools.entity.TableFieldEntity;
import com.zenithflow.modules.devtools.service.TableFieldService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 表
 *
 *
 */
@Service
public class TableFieldServiceImpl extends BaseServiceImpl<TableFieldDao, TableFieldEntity> implements TableFieldService {

    @Override
    public List<TableFieldEntity> getByTableName(String tableName) {
        return baseDao.getByTableName(tableName);
    }

    @Override
    public void deleteByTableName(String tableName) {
        baseDao.deleteByTableName(tableName);
    }

    @Override
    public void deleteBatchTableIds(Long[] tableIds) {
        baseDao.deleteBatchTableIds(tableIds);
    }

}
