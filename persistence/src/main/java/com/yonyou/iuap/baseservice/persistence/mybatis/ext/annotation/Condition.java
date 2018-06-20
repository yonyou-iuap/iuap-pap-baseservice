package com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Match;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition{

	public Match match() default Match.EQ;

}
