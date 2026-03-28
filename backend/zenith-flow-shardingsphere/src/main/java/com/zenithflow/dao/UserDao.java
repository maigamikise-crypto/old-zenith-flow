package com.zenithflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.zenithflow.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户
 *
 *
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {


}
