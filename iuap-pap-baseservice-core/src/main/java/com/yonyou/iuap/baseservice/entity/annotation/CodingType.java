package com.yonyou.iuap.baseservice.entity.annotation;

/**
 * 说明：编码类型
 * @author Aton
 * 2018年6月13日
 */
public enum CodingType {

	PRE("前编码"), POST("后编码");
	
	private String type;
	
	private CodingType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}
