package com.yonyou.iuap.baseservice.entity.annotation;

import com.yonyou.iuap.baseservice.service.I18nEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在需要枚举反写的属性上
 * <br>例如 枚举编码的属性为 private String grade, 其对应的显示值属性为private String gradeEnumValue
 * 则需要在grate上打上本注解
 * <p>@I18nEnumCode(clazz=GradeEnum.class,target = "gradeEnumValue")</p>
 * <p>private String grade</p>
 *
 * @author leon
 * @date 2019/4/11
 * @since UCF1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nEnumCode {

    Class<? extends I18nEnum> clazz() default I18nEnum.class;

    String  target() default "";

}
