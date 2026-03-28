package com.zenithflow.modules.message.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.message.entity.SysMailLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件发送记录
 *
 *
 */
@Mapper
public interface SysMailLogDao extends BaseDao<SysMailLogEntity> {

}
