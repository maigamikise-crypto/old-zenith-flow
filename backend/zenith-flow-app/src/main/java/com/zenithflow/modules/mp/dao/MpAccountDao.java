package com.zenithflow.modules.mp.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.mp.entity.MpAccountEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 公众号账号管理
*
*
*/
@Mapper
public interface MpAccountDao extends BaseDao<MpAccountEntity> {

}
