package com.zenithflow.modules.message.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.message.entity.SysSmsLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信日志
 *
 *
 */
@Mapper
public interface SysSmsLogDao extends BaseDao<SysSmsLogEntity> {

}
