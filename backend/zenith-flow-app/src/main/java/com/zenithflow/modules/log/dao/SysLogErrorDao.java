package com.zenithflow.modules.log.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.log.entity.SysLogErrorEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 异常日志
 *
 *
 */
@Mapper
public interface SysLogErrorDao extends BaseDao<SysLogErrorEntity> {

}
