package com.zenithflow.modules.demo.service;

import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.demo.dto.ProductParamsDTO;
import com.zenithflow.modules.demo.entity.ProductParamsEntity;

import java.util.List;

/**
 * 产品参数管理
 *
 *
 */
public interface ProductParamsService extends BaseService<ProductParamsEntity> {

    void saveOrUpdate(Long productId, List<ProductParamsDTO> list);

    void deleteByProductIds(Long[] productIds);

    List<ProductParamsDTO> getList(Long productId);
}
