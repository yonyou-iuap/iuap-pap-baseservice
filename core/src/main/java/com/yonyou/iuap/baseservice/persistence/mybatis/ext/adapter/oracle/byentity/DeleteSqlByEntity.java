package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle.byentity;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlGenerator;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;

public class DeleteSqlByEntity implements SqlGenerator{
	
	private Logger log = LoggerFactory.getLogger(DeleteSqlByEntity.class);

	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.DELETE;
	}

	/**
	 * 解析、构建SQL
	 */
	@Override
	public String parseSQL(Object entity) {
		Class<?> clazz = entity.getClass();
		StringBuilder deleteSql = new StringBuilder("DELETE FROM ").append(
				EntityUtil.getTableName(clazz));
		return deleteSql.append(this.buildWhere()).toString();
	}
	
	private String buildWhere() {
		return new StringBuffer().
				append("\r\n WHERE id=#{id} and ts=#{ts}").toString();
	}

}