package com.zenithflow.modules.demo.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.demo.entity.ProductParamsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品参数管理
 *
 *
 */
@Mapper
public interface ProductParamsDao extends BaseDao<ProductParamsEntity> {

    /**
     * 根据产品id，删除产品参数
     */
    void deleteByProductIds(Long[] productIds);
}
