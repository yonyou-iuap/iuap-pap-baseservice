package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support;

import java.lang.reflect.Field;

import cn.hutool.core.util.StrUtil;

public class ParamUtil {
	
	/**
	 * 连接前缀 + 字段
	 * @param prefix
	 * @param fieldName
	 * @return
	 */
	public static String contactParam(String prefix, String fieldName) {
		StringBuilder strb = new StringBuilder();
		if(!StrUtil.isBlank(prefix)) {
			strb.append(prefix).append(".");
		}
		return strb.append(fieldName).toString();
	}
	
	/**
	 * 构建条件判断
	 * @param field
	 * @param fieldName
	 * @return
	 */
	public static String adjust4Condition(Field field, String fieldName) {
		if(field.getType().isAssignableFrom(String.class)) {
			return new StringBuffer(fieldName).append("!=null and ")
							.append(fieldName).append("!=''").toString();
		}else {
			return new StringBuffer(fieldName).append("!=null").toString();
		}
	}

}