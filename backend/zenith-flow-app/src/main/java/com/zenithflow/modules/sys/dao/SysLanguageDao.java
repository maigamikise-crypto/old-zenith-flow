package com.zenithflow.modules.sys.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.sys.entity.SysLanguageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 国际化
 *
 *
 */
@Mapper
public interface SysLanguageDao extends BaseDao<SysLanguageEntity> {

    SysLanguageEntity getLanguage(SysLanguageEntity entity);

    void updateLanguage(SysLanguageEntity entity);

    /**
     * 删除国际化
     * @param tableName   表名
     * @param tableId     表主键
     */
    void deleteLanguage(@Param("tableName") String tableName, @Param("tableId") Long tableId);
}
