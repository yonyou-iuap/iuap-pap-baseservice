package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import java.lang.reflect.Method;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.SQLType;

public interface SqlTemplate {
	
	public SQLType getSQLType();

	public String parseSQL(Method method);

}
