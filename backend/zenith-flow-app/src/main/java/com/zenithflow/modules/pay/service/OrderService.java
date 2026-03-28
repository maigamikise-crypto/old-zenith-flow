/**
 * Copyright (c) 2021  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.pay.service;

import com.zenithflow.common.service.CrudService;
import com.zenithflow.modules.pay.dto.OrderDTO;
import com.zenithflow.modules.pay.entity.OrderEntity;

/**
 * 订单
 *
 *
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {


    OrderEntity getByOrderId(Long orderId);

    /**
     * 支付成功
     * @param order 订单
     */
    void paySuccess(OrderEntity order);
}
