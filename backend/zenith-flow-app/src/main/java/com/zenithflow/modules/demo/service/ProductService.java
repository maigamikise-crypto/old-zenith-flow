package com.zenithflow.modules.demo.service;

import com.zenithflow.common.service.CrudService;
import com.zenithflow.modules.demo.dto.ProductDTO;
import com.zenithflow.modules.demo.entity.ProductEntity;

/**
 * 产品管理
 *
 *
 */
public interface ProductService extends CrudService<ProductEntity, ProductDTO> {

}
