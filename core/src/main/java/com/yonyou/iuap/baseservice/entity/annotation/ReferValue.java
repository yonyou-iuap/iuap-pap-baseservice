package com.yonyou.iuap.baseservice.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：用于定义字段、属性实际使用的Value
 * 如：乐观锁在更新时，用于产生新的乐观锁数值
 * @author Aton
 * 2018年7月18日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferValue {

	public String value();

}