package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.SqlCommandType;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.AutoMapperFactory;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public class MysqlMapperFactory implements AutoMapperFactory{

	@Override
	public Dialect getDialect() {
		return Dialect.mysql;
	}

	@Override
	public SqlTemplate getSqlTempalte(SqlCommandType sqlType) {
		return null;
	}

	@Override
	public String parseSQL4Insert(Method method) {
		return null;
	}

	@Override
	public String parseSQL4Update(Method method) {
		return null;
	}

	@Override
	public String parseSQL4Delete(Method method) {
		return null;
	}

	@Override
	public String parseSQL4Select(Method method) {
		// TODO Auto-generated method stub
		return null;
	}



	
	
}