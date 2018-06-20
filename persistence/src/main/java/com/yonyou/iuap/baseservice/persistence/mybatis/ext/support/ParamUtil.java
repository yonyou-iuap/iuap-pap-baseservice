package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support;

import cn.hutool.core.util.StrUtil;

public class ParamUtil {
	
	public static String contactParam(String prefix, String fieldName) {
		StringBuilder strb = new StringBuilder();
		if(!StrUtil.isBlank(prefix)) {
			strb.append(prefix).append(".");
		}
		return strb.append(fieldName).toString();
	}

}
