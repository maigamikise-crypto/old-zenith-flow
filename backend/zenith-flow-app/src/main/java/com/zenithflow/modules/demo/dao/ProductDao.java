package com.zenithflow.modules.demo.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.demo.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品管理
 *
 *
 */
@Mapper
public interface ProductDao extends BaseDao<ProductEntity> {

}
