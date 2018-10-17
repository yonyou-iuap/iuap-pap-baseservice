package com.yonyou.iuap.baseservice.entity.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明: 业务实体上国际化字段的标识,运行时动态解析,进行值替换
 * 被标识的业务实体属性应该对应多个数据库字段预留,以name字段为例:
 * <li>name  对应 简体中文</li>
 * <li>name2 对应 英文</li>
 * <li>name3 对应 繁体中文</li>
 * <li>name4 对应 法文</li>
 * <li>name5 对应 预留1</li>
 * <li>name6 对应 预留2</li>
 * 而本注解应标记在实体的name属性上
 * @author  leon
 * 2018年10月17日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18n {
}
