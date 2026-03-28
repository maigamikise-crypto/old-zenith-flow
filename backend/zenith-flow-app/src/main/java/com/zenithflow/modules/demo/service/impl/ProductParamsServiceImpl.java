package com.zenithflow.modules.demo.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenithflow.common.service.impl.BaseServiceImpl;
import com.zenithflow.common.utils.ConvertUtils;
import com.zenithflow.modules.demo.dao.ProductParamsDao;
import com.zenithflow.modules.demo.dto.ProductParamsDTO;
import com.zenithflow.modules.demo.entity.ProductParamsEntity;
import com.zenithflow.modules.demo.service.ProductParamsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品参数管理
 *
 *
 */
@Service
public class ProductParamsServiceImpl extends BaseServiceImpl<ProductParamsDao, ProductParamsEntity> implements ProductParamsService {

    @Override
    public void saveOrUpdate(Long productId, List<ProductParamsDTO> list) {
        //先删除子表数据
        deleteByProductIds(new Long[]{productId});

        if(CollUtil.isEmpty(list)){
            return ;
        }

        //保存子表数据
        for(ProductParamsDTO dto : list){
            ProductParamsEntity entity = new ProductParamsEntity();
            entity.setProductId(productId);
            entity.setParamName(dto.getParamName());
            entity.setParamValue(dto.getParamValue());

            //保存
            insert(entity);
        }
    }

    @Override
    public void deleteByProductIds(Long[] productIds) {
        baseDao.deleteByProductIds(productIds);
    }

    @Override
    public List<ProductParamsDTO> getList(Long productId) {
        // 查询子表数据列表
        QueryWrapper<ProductParamsEntity> query = new QueryWrapper<>();
        query.eq("product_id", productId);
        List<ProductParamsEntity> list = baseDao.selectList(query);

        return ConvertUtils.sourceToTarget(list, ProductParamsDTO.class);
    }

}
