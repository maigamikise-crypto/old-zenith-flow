/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.controller;

import cn.hutool.core.io.IoUtil;
import com.zenithflow.modules.devtools.entity.ProjectEntity;
import com.zenithflow.modules.devtools.utils.ProjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 * 项目名修改
 *
 *
 */
@RestController
@RequestMapping("devtools/project")
public class ProjectController {

    @GetMapping
    public void project(ProjectEntity project, HttpServletResponse response) throws Exception {
        byte[] data = ProjectUtils.download(project);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + project.getNewProjectName() + ".zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), false, data);
    }
}
