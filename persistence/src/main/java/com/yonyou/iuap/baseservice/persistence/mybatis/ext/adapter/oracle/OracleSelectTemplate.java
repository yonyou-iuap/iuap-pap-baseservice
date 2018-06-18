package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.SqlCommandType;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public class OracleSelectTemplate implements SqlTemplate{

	@Override
	public Dialect getDialect() {
		return Dialect.mysql;
	}
	
	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.SELECT;
	}

	@Override
	public String parseSQL(Method method, Class<?> entityClazz) {
		return null;
	}

}