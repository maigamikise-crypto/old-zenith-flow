/**
 * Copyright (c) 2018  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.flow.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 用户
 *
 *
 */
@Mapper
public interface FlowableUserDao {

    String getUserName(String userId);

    List<String> getRealNameList(List<Long> ids);

    List<String> getRoleNameList(List<Long> ids);

    List<Long> getUserIdListByRoleIdList(List<Long> ids);
}
