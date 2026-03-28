/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.devtools.entity;

import lombok.Data;

/**
 * 创建菜单
 *
 *
 */
@Data
public class MenuEntity {
    private Long pid;
    private String name;
    private String icon;
    private String moduleName;
    private String className;

}
