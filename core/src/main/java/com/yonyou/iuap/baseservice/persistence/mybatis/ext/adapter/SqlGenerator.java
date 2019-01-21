package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import org.apache.ibatis.mapping.SqlCommandType;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public interface SqlGenerator {
	
	public Dialect getDialect();
	
	public SqlCommandType getSQLType();

	public String parseSQL(Object entity);

}
