package com.zenithflow.modules.security.service;

import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.BaseService;
import com.zenithflow.common.utils.Result;
import com.zenithflow.modules.security.entity.SysUserTokenEntity;
import com.zenithflow.modules.sys.entity.SysOnlineEntity;

import java.util.Map;

/**
 * 用户Token
 *
 *
 */
public interface SysUserTokenService extends BaseService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param userId  用户ID
	 */
	Result createToken(Long userId);

	/**
	 * 退出
	 * @param userId  用户ID
	 */
	void logout(Long userId);

	/**
	 * 在线用户分页
	 */
	PageData<SysOnlineEntity> onlinePage(Map<String, Object> params);

}
