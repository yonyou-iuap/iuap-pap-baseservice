package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.condition;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：字符串转为日期
 * @author Aton
 * 2018年7月18日
 */
public class Str2DateFormatter implements Formatter{

	@Override
	public String getName() {
		return "str2date";
	}

	@Override
	public String format(String paramName, String pattern) {
		if(StrUtil.isBlank(pattern)) {
			throw new RuntimeException("Entity字段条件格式定义不能为空:"+pattern);
		}
		return new StringBuilder("to_date(#{").append(paramName).append("}, '")
						.append(pattern).append("')").toString();
	}

}