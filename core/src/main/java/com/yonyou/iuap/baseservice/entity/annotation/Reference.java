package com.yonyou.iuap.baseservice.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：启用参照的字段Annotation
 * @author 黄东东
 * 2018年7月7日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {
    // 参照路径
    String path() default "";
    // 参照编码
    String code() default "";

    // 参照回写源属性
    String[] srcProperties() default {};
    // 参照回写目的属性
    String[] desProperties() default {};
}
