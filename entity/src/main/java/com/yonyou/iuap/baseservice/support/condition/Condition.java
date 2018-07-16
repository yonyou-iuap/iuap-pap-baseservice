package com.yonyou.iuap.baseservice.support.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition{

	public Match match() default Match.EQ;

	//区间判断：开始值属性name
	public String param1() default "";

	//区间判断：结束值属性name
	public String param2() default "";

}