package com.yonyou.iuap.baseservice.datapermission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：数据权限使用的注解,在Entity上使用
 * @author 　leon
 * 2018年8月21日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataAccess {
    String code() default "";
}
