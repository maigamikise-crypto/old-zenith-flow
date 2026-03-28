package com.zenithflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenithflow.entity.UserLogEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户日志
 *
 *
 */
@Mapper
public interface UserLogDao extends BaseMapper<UserLogEntity> {


}
