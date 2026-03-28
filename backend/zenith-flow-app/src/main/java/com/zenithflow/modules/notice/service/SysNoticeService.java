/**
 * Copyright (c) 2019  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.notice.service;

import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.CrudService;
import com.zenithflow.modules.notice.dto.SysNoticeDTO;
import com.zenithflow.modules.notice.entity.SysNoticeEntity;

import java.util.Map;

/**
 * 通知管理
 *
 *
 */
public interface SysNoticeService extends CrudService<SysNoticeEntity, SysNoticeDTO> {

    /**
     * 获取被通知的用户
     */
    PageData<SysNoticeDTO> getNoticeUserPage(Map<String, Object> params);

    /**
     * 获取我的通知列表
     */
    PageData<SysNoticeDTO> getMyNoticePage(Map<String, Object> params);
}
