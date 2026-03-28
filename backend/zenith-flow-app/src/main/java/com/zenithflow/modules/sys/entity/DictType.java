/**
 * Copyright (c) 2019  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型
 *
 *
 */
@Data
public class DictType {
    @JsonIgnore
    private Long id;
    private String dictType;
    private List<DictData> dataList = new ArrayList<>();
}
