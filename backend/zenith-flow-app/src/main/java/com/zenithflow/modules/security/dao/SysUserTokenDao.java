package com.zenithflow.modules.security.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.security.entity.SysUserTokenEntity;
import com.zenithflow.modules.sys.entity.SysOnlineEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统用户Token
 *
 *
 */
@Mapper
public interface SysUserTokenDao extends BaseDao<SysUserTokenEntity> {

    SysUserTokenEntity getByToken(String token);

    SysUserTokenEntity getByUserId(Long userId);

    void logout(@Param("userId") Long userId, @Param("expireDate") Date expireDate);

    /**
     * 获取在线用户列表
     */
    List<SysOnlineEntity> getOnlineList(Map<String, Object> params);
}
