package com.zenithflow.modules.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenithflow.common.service.impl.CrudServiceImpl;
import com.zenithflow.common.utils.ConvertUtils;
import com.zenithflow.modules.mp.dao.MpMenuDao;
import com.zenithflow.modules.mp.dto.MpMenuDTO;
import com.zenithflow.modules.mp.entity.MpMenuEntity;
import com.zenithflow.modules.mp.service.MpMenuService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 公众号自定义菜单
 *
 *
 */
@Service
public class MpMenuServiceImpl extends CrudServiceImpl<MpMenuDao, MpMenuEntity, MpMenuDTO> implements MpMenuService {

    @Override
    public QueryWrapper<MpMenuEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<MpMenuEntity> wrapper = new QueryWrapper<>();

        return wrapper;
    }

    @Override
    public MpMenuDTO getByAppId(String appId) {
        MpMenuEntity entity = baseDao.selectOne(new QueryWrapper<MpMenuEntity>().eq("app_id", appId));
        return ConvertUtils.sourceToTarget(entity, MpMenuDTO.class);
    }

    @Override
    public void deleteByAppId(String appId) {
        baseDao.delete(new QueryWrapper<MpMenuEntity>().eq("app_id", appId));
    }
}
