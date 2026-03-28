/**
 * Copyright (c) 2021  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.pay.service;

import com.zenithflow.common.service.CrudService;
import com.zenithflow.modules.pay.dto.AlipayNotifyLogDTO;
import com.zenithflow.modules.pay.entity.AlipayNotifyLogEntity;

/**
 * 支付宝回调日志
 *
 *
 */
public interface AlipayNotifyLogService extends CrudService<AlipayNotifyLogEntity, AlipayNotifyLogDTO> {

}
