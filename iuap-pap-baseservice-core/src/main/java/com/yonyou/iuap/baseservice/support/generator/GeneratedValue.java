package com.yonyou.iuap.baseservice.support.generator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：数据生成策略
 * @author Aton
 * 2018年6月30日
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedValue {

	/**
	 * ID Generator策略
	 * 默认策略：UUID
	 * @return
	 */
	public Strategy strategy() default Strategy.UUID;
	
	public String module() default "";

	public String clazz() default "";			//自定义生成器,

}