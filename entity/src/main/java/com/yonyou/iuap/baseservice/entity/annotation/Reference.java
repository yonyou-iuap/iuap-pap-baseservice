package com.yonyou.iuap.baseservice.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：启用参照的字段Annotation
 * @author 黄东ongoing
 * 2018年7月7日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {
    // 参照路径
    public String path() default "";
    // 参照编码
    public String code() default "";
    // 参照类型
    public RefType type() default RefType.Single;
    // 参照回写源属性
    public String[] srcProperties() default {};
    // 参照回写目的属性
    public String[] desProperties() default {};
}
