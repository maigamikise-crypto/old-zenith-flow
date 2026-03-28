package com.zenithflow.service;

import com.zenithflow.common.service.BaseService;
import com.zenithflow.entity.TokenEntity;

/**
 * 用户Token
 *
 *
 */
public interface TokenService extends BaseService<TokenEntity> {

	TokenEntity getByToken(String token);

	/**
	 * 生成token
	 * @param userId  用户ID
	 * @return        返回token信息
	 */
	TokenEntity createToken(Long userId);

	/**
	 * 设置token过期
	 * @param userId 用户ID
	 */
	void expireToken(Long userId);

}
