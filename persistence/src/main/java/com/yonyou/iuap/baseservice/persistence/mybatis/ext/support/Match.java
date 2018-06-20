package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support;

public enum Match {
	
	EQ("="), GT(">"), LT("<"), GTEQ(">="), LTEQ("<="), LIKE("like"), LLIKE("llike"), RLIKE("rlike");
	
	private String type;
	
	private Match(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}