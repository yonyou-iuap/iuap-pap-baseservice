package com.yonyou.iuap.baseservice.statistics.support;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记字段是否为统计字段,是否进行统计分析查询
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StatisticsField {
    StatFunctions[]  functions() default StatFunctions.max;
}
