/**
 * Copyright (c) 2021  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.pay.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.pay.entity.AlipayNotifyLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 支付宝回调日志
*
*
*/
@Mapper
public interface AlipayNotifyLogDao extends BaseDao<AlipayNotifyLogEntity> {

}
