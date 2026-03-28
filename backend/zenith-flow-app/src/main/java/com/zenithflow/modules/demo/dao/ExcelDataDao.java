package com.zenithflow.modules.demo.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.demo.entity.ExcelDataEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* Excel导入演示
*
*
*/
@Mapper
public interface ExcelDataDao extends BaseDao<ExcelDataEntity> {

}
