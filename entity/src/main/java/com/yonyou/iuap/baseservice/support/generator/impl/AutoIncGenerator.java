package com.yonyou.iuap.baseservice.support.generator.impl;


import com.yonyou.iuap.baseservice.support.generator.Generator;
import com.yonyou.iuap.baseservice.support.generator.Strategy;

import java.io.Serializable;

/**
 * 不填充id靠表自增特性自动生成
 *
 * @author leon
 * @date 2019/3/29
 */
public class AutoIncGenerator implements Generator {

    @Override
    public Strategy strategy() {
        return Strategy.AUTOINC;
    }

    @Override
    public String name() {
        return "auto";
    }

    @Override
    public Serializable generate(String module, Class<?> entityClazz) {
        return null;
    }
}
