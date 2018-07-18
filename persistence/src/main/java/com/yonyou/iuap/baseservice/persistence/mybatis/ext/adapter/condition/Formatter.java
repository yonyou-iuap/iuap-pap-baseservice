package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.condition;

public interface Formatter {
	
	/**
	 * Formater名称：唯一
	 * @return
	 */
	public String getName();

	/**
	 * 格式化转换
	 * @return
	 */
	public String format(String paramName, String pattern);

}