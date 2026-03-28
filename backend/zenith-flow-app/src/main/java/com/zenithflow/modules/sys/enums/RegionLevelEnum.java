/**
 * Copyright (c) 2019  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.sys.enums;

/**
 * 行政区域  级别枚举
 *
 *
 */
public enum RegionLevelEnum {
    ONE(1),
    TWO(2),
    THREE(3);

    private int value;

    RegionLevelEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
