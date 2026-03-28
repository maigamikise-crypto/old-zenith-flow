package com.zenithflow.modules.sys.service.impl;

import com.zenithflow.common.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import com.zenithflow.modules.sys.dao.SysLanguageDao;
import com.zenithflow.modules.sys.entity.SysLanguageEntity;
import com.zenithflow.modules.sys.service.SysLanguageService;

/**
 * 国际化
 *
 *
 */
@Service
public class SysLanguageServiceImpl extends BaseServiceImpl<SysLanguageDao, SysLanguageEntity> implements SysLanguageService {

    @Override
    public void saveOrUpdate(String tableName, Long tableId, String fieldName, String fieldValue, String language) {
        SysLanguageEntity entity = new SysLanguageEntity();
        entity.setTableName(tableName);
        entity.setTableId(tableId);
        entity.setFieldName(fieldName);
        entity.setFieldValue(fieldValue);
        entity.setLanguage(language);

        //判断是否有数据
        if(baseDao.getLanguage(entity) == null){
            baseDao.insert(entity);
        }else {
            baseDao.updateLanguage(entity);
        }
    }

    @Override
    public void deleteLanguage(String tableName, Long tableId) {
        baseDao.deleteLanguage(tableName, tableId);
    }
}
