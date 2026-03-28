package com.zenithflow.modules.log.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.log.entity.SysLogOperationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志
 *
 *
 */
@Mapper
public interface SysLogOperationDao extends BaseDao<SysLogOperationEntity> {

}
