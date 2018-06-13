package com.yonyou.iuap.baseservice.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：启用编码规则的字段Annotation
 * @author houlf
 * 2018年6月13日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CodingField {
	
	public CodingType type() default CodingType.POST;

	/**
	 * 编码规则对象编码
	 * @return
	 */
	public String code() default "";

}