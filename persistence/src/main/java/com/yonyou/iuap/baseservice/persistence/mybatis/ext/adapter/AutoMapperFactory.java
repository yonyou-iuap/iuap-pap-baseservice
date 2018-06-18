package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.SqlCommandType;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public interface AutoMapperFactory {

	public Dialect getDialect();

	public SqlTemplate getSqlTempalte(SqlCommandType sqlType);
	
	public String parseSQL4Insert(Method method, Class<?> entityClazz);

	public String parseSQL4Update(Method method, Class<?> entityClazz);

	public String parseSQL4Delete(Method method, Class<?> entityClazz);

	public String parseSQL4Select(Method method, Class<?> entityClazz);

}