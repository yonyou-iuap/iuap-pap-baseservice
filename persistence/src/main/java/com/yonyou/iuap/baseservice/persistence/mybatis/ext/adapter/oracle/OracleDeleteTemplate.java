package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;

public class OracleDeleteTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(OracleDeleteTemplate.class);

	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.DELETE;
	}
	
	@Override
	public String parseSQL(Method method, Class<?> entityClazz) {
		StringBuilder deleteSql = new StringBuilder("DELETE FROM ").append(
							EntityUtil.getTableName(entityClazz));
		return deleteSql.append(this.buildWhere()).toString();
	}
	
	private String buildWhere() {
		StringBuffer where = new StringBuffer();
		where.append("\r\n WHERE id=#{id} and ts=#{ts}");
		return where.toString();
	}

}