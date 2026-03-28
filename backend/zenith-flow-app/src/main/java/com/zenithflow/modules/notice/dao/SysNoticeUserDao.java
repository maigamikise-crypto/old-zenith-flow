/**
 * Copyright (c) 2019  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.notice.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.notice.entity.SysNoticeUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 我的通知
*
*
*/
@Mapper
public interface SysNoticeUserDao extends BaseDao<SysNoticeUserEntity> {
    /**
     * 通知全部用户
     */
	void insertAllUser(SysNoticeUserEntity entity);

    /**
     * 未读的通知数
     * @param receiverId  接收者ID
     */
    int getUnReadNoticeCount(Long receiverId);
}
