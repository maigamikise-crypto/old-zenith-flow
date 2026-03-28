package com.zenithflow.service;

import com.zenithflow.common.service.BaseService;
import com.zenithflow.entity.UserEntity;
import com.zenithflow.dto.LoginDTO;

import java.util.Map;

/**
 * 用户
 *
 *
 */
public interface UserService extends BaseService<UserEntity> {

	UserEntity getByMobile(String mobile);

	UserEntity getUserByUserId(Long userId);

	/**
	 * 用户登录
	 * @param dto    登录表单
	 * @return        返回登录信息
	 */
	Map<String, Object> login(LoginDTO dto);
}
