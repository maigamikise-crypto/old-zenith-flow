package com.zenithflow.modules.log.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.log.entity.SysLogLoginEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志
 *
 *
 */
@Mapper
public interface SysLogLoginDao extends BaseDao<SysLogLoginEntity> {

}
