package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import java.lang.reflect.Method;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.SQLType;

public class MysqlUpdateTemplate implements SqlTemplate{

	@Override
	public SQLType getSQLType() {
		return SQLType.UPDATE;
	}

	@Override
	public String parseSQL(Method method) {
		return null;
	}

}