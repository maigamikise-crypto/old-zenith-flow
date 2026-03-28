package com.zenithflow.modules.job.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.job.entity.ScheduleJobLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 *
 */
@Mapper
public interface ScheduleJobLogDao extends BaseDao<ScheduleJobLogEntity> {

}
