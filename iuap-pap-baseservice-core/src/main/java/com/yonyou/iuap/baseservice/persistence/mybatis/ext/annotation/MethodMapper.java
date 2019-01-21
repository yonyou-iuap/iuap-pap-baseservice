package com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.mapping.SqlCommandType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodMapper {

	 SqlCommandType type();

	 boolean isSelective() default false;
     boolean isBatch() default false;

}