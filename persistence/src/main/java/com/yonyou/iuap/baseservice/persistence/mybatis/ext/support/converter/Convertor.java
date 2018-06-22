package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.converter;

public enum Convertor {

	HUMP("hump"), UNANIMOUS("unanimous");

	private String type;
	
	private Convertor(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}