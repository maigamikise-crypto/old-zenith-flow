package com.zenithflow.modules.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenithflow.common.service.impl.CrudServiceImpl;
import com.zenithflow.modules.flow.dao.BpmFormDao;
import com.zenithflow.modules.flow.dto.BpmFormDTO;
import com.zenithflow.modules.flow.entity.BpmFormEntity;
import com.zenithflow.modules.flow.service.BpmFormService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 工作流表单
 *
 *
 */
@Service
public class BpmFormServiceImpl extends CrudServiceImpl<BpmFormDao, BpmFormEntity, BpmFormDTO> implements BpmFormService {

    @Override
    public QueryWrapper<BpmFormEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<BpmFormEntity> wrapper = new QueryWrapper<>();

        String name = (String) params.get("name");
        wrapper.like(StringUtils.isNotBlank(name), "name", name);
        wrapper.orderByDesc("create_date");
        return wrapper;
    }

}
