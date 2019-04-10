package com.yonyou.iuap.baseservice.support.condition;

import com.yonyou.iuap.ucf.dao.support.UcfSearchParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition{

	public UcfSearchParams.Match match() default UcfSearchParams.Match.EQ;

	//区间判断：开始值属性name
	public String param1() default "";

	//区间判断：结束值属性name
	public String param2() default "";
	
	//条件格式化工具
	public String format() default "";

}