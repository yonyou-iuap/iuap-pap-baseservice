package com.yonyou.iuap.baseservice.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：主子表使用的注解,在主Entity上使用
 * @author 　leon
 * 2018年7月12日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Associative {


    // 子表外键对应的业务实体Field名字
    String fkName() default "";
    //是否进行级联删除的标识
    boolean deleteCascade() default true;
}
