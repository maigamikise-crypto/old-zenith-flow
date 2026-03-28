package com.zenithflow.modules.security.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.impl.BaseServiceImpl;
import com.zenithflow.common.utils.DateUtils;
import com.zenithflow.common.utils.Result;
import com.zenithflow.modules.security.dao.SysUserTokenDao;
import com.zenithflow.modules.security.entity.SysUserTokenEntity;
import com.zenithflow.modules.security.oauth2.TokenGenerator;
import com.zenithflow.modules.security.service.SysUserTokenService;
import com.zenithflow.modules.sys.entity.SysOnlineEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysUserTokenServiceImpl extends BaseServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
	/**
	 * 12小时后过期
	 */
	private final static int EXPIRE = 3600 * 12;

	@Override
	public Result createToken(Long userId) {
		//用户token
		String token;

		//当前时间
		Date now = new Date();
		//过期时间
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

		//判断是否生成过token
		SysUserTokenEntity tokenEntity = baseDao.getByUserId(userId);
		if(tokenEntity == null){
			//生成一个token
			token = TokenGenerator.generateValue();

			tokenEntity = new SysUserTokenEntity();
			tokenEntity.setUserId(userId);
			tokenEntity.setToken(token);
			tokenEntity.setUpdateDate(now);
			tokenEntity.setExpireDate(expireTime);

			//保存token
			this.insert(tokenEntity);
		}else{
			//判断token是否过期
			if(tokenEntity.getExpireDate().getTime() < System.currentTimeMillis()){
				//token过期，重新生成token
				token = TokenGenerator.generateValue();
			}else {
				token = tokenEntity.getToken();
			}

			tokenEntity.setToken(token);
			tokenEntity.setUpdateDate(now);
			tokenEntity.setExpireDate(expireTime);

			//更新token
			this.updateById(tokenEntity);
		}

		Map<String, Object> map = new HashMap<>(2);
		map.put(Constant.TOKEN_HEADER, token);
		map.put("expire", EXPIRE);
		return new Result().ok(map);
	}

	@Override
	public void logout(Long userId) {
		Date expireDate = DateUtils.addDateMinutes(new Date(), -1);
		baseDao.logout(userId, expireDate);
	}

	@Override
	public PageData<SysOnlineEntity> onlinePage(Map<String, Object> params) {
		//转换成like
		paramsToLike(params, "username");

		//分页
		IPage<?> page = getPage(params, "t1.update_date", false);

		//查询
		params.put("expireDate", new Date());
		List<SysOnlineEntity> list = baseDao.getOnlineList(params);

		return new PageData<>(list, page.getTotal());
	}
}
