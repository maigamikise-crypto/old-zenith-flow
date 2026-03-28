/**
 * Copyright (c) 2021  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.pay.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.pay.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* 订单
*
*
*/
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {

    /**
     * 支付成功
     */
    int paySuccess(@Param("orderId") Long orderId, @Param("status") Integer status, @Param("payAt") Date payAt);

    OrderEntity getByOrderId(Long orderId);
}
