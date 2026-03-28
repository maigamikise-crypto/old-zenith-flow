package com.zenithflow.modules.oss.service;

import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.oss.entity.SysOssEntity;

import java.util.Map;

/**
 * 文件上传
 *
 *
 */
public interface SysOssService extends BaseService<SysOssEntity> {

	PageData<SysOssEntity> page(Map<String, Object> params);
}
