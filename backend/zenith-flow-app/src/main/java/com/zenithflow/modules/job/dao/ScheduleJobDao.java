package com.zenithflow.modules.job.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.job.entity.ScheduleJobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 定时任务
 *
 *
 */
@Mapper
public interface ScheduleJobDao extends BaseDao<ScheduleJobEntity> {

	/**
	 * 批量更新状态
	 */
	int updateBatch(Map<String, Object> map);
}
