package com.zenithflow.modules.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.impl.BaseServiceImpl;
import com.zenithflow.modules.oss.dao.SysOssDao;
import com.zenithflow.modules.oss.entity.SysOssEntity;
import com.zenithflow.modules.oss.service.SysOssService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class SysOssServiceImpl extends BaseServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Override
	public PageData<SysOssEntity> page(Map<String, Object> params) {
		IPage<SysOssEntity> page = baseDao.selectPage(
			getPage(params, Constant.CREATE_DATE, false),
			new QueryWrapper<>()
		);
		return getPageData(page, SysOssEntity.class);
	}
}
