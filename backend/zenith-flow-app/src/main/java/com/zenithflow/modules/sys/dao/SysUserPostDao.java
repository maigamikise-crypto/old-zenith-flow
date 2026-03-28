/**
 * Copyright (c) 2021  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.sys.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.sys.entity.SysUserPostEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* 用户岗位关系
*
*
*/
@Mapper
public interface SysUserPostDao extends BaseDao<SysUserPostEntity> {

    /**
     * 根据岗位ids，删除岗位用户关系
     * @param postIds 岗位ids
     */
    void deleteByPostIds(Long[] postIds);

    /**
     * 根据用户id，删除岗位用户关系
     * @param userIds 用户ids
     */
    void deleteByUserIds(Long[] userIds);

    /**
     * 岗位ID列表
     * @param userId  用户ID
     */
    List<Long> getPostIdList(Long userId);
}
