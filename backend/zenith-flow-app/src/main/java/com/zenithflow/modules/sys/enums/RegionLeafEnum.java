/**
 * Copyright (c) 2019  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.sys.enums;

/**
 * 叶子节点枚举
 *
 *
 */
public enum RegionLeafEnum {
    YES(1),
    NO(0);

    private int value;

    RegionLeafEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
