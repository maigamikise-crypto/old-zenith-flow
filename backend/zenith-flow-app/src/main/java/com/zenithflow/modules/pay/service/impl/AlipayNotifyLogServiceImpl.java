/**
 * Copyright (c) 2021  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenithflow.common.service.impl.CrudServiceImpl;
import com.zenithflow.modules.pay.dao.AlipayNotifyLogDao;
import com.zenithflow.modules.pay.dto.AlipayNotifyLogDTO;
import com.zenithflow.modules.pay.entity.AlipayNotifyLogEntity;
import com.zenithflow.modules.pay.service.AlipayNotifyLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付宝回调日志
 *
 *
 */
@Service
public class AlipayNotifyLogServiceImpl extends CrudServiceImpl<AlipayNotifyLogDao, AlipayNotifyLogEntity, AlipayNotifyLogDTO> implements AlipayNotifyLogService {

    @Override
    public QueryWrapper<AlipayNotifyLogEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<AlipayNotifyLogEntity> wrapper = new QueryWrapper<>();

        String outTradeNo = (String)params.get("outTradeNo");
        wrapper.eq(StringUtils.isNotBlank(outTradeNo), "out_trade_no", outTradeNo);

        String notifyId = (String)params.get("notifyId");
        wrapper.eq(StringUtils.isNotBlank(notifyId), "notify_id", notifyId);

        String tradeStatus = (String)params.get("tradeStatus");
        wrapper.eq(StringUtils.isNotBlank(tradeStatus), "trade_status", tradeStatus);

        return wrapper;
    }

}
