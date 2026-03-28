package com.zenithflow.modules.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenithflow.common.exception.ErrorCode;
import com.zenithflow.common.exception.RenException;
import com.zenithflow.common.service.impl.CrudServiceImpl;
import com.zenithflow.common.utils.JsonUtils;
import com.zenithflow.modules.message.dao.SysMailTemplateDao;
import com.zenithflow.modules.message.dto.SysMailTemplateDTO;
import com.zenithflow.modules.message.email.EmailUtils;
import com.zenithflow.modules.message.entity.SysMailTemplateEntity;
import com.zenithflow.modules.message.service.SysMailTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysMailTemplateServiceImpl extends CrudServiceImpl<SysMailTemplateDao, SysMailTemplateEntity, SysMailTemplateDTO> implements SysMailTemplateService {
    @Autowired
    private EmailUtils emailUtils;

    @Override
    public QueryWrapper<SysMailTemplateEntity> getWrapper(Map<String, Object> params) {
        String name = (String)params.get("name");

        QueryWrapper<SysMailTemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return wrapper;
    }

    @Override
    public boolean sendMail(Long id, String mailTo, String mailCc, String params) throws Exception{
        Map<String, Object> map = null;
        try {
            if(StringUtils.isNotEmpty(params)){
                map = JsonUtils.parseObject(params, Map.class);
            }
        }catch (Exception e){
            throw new RenException(ErrorCode.JSON_FORMAT_ERROR);
        }
        String[] to = new String[]{mailTo};
        String[] cc = StringUtils.isBlank(mailCc) ? null : new String[]{mailCc};

        return emailUtils.sendMail(id, to, cc, map);
    }
}
