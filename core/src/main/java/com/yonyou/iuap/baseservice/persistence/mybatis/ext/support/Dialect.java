package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support;

public enum Dialect {
	
	mysql("mysql"), oracle("oracle"),mssql("sqlserver");
	
	private String type;
	
	private Dialect(String dialect) {
		this.type = dialect;
	}
	
	public String getType() {
		return this.type;
	}

}