package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.SqlCommandType;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;

public class MysqlQueryTemplate implements SqlTemplate{

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.SELECT;
	}

	@Override
	public String parseSQL(Method method) {
		return null;
	}

}