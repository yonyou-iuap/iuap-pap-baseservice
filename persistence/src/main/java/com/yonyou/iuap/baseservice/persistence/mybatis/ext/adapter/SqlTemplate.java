package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.SqlCommandType;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public interface SqlTemplate {
	
	public Dialect getDialect();
	
	public SqlCommandType getSQLType();

	public String parseSQL(Method method, Class<?> entityClazz);

}
