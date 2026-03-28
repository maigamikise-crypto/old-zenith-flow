package com.zenithflow.modules.mp.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.mp.entity.MpMenuEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* 公众号自定义菜单
*
*
*/
@Mapper
public interface MpMenuDao extends BaseDao<MpMenuEntity> {

}
