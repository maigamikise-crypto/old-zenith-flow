/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.controller;

import com.google.gson.Gson;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.utils.Result;
import com.zenithflow.modules.devtools.entity.GenParam;
import com.zenithflow.modules.sys.service.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 代码生成参数配置
 *
 *
 */
@RestController
@RequestMapping("devtools/param")
public class GenParamController {
    @Autowired
    private SysParamsService sysParamsService;

    @GetMapping("info")
    public Result<GenParam> info(){
        GenParam param = sysParamsService.getValueObject(Constant.DEV_TOOLS_PARAM_KEY, GenParam.class);

        return new Result<GenParam>().ok(param);
    }

    @PostMapping
    public Result saveConfig(@RequestBody GenParam param){
        sysParamsService.updateValueByCode(Constant.DEV_TOOLS_PARAM_KEY, new Gson().toJson(param));

        return new Result();
    }
}
