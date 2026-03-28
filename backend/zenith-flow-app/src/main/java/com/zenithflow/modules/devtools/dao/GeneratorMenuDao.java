/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 创建菜单
 *
 *
 */
@Mapper
public interface GeneratorMenuDao {

    void generatorMenu(Map<String, Object> params);

    void generatorMenuLanguage(Map<String, Object> params);
}
