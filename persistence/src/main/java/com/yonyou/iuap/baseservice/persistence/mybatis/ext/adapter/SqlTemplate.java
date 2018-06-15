package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.SqlCommandType;

public interface SqlTemplate {
	
	public SqlCommandType getSQLType();

	public String parseSQL(Method method);

}
