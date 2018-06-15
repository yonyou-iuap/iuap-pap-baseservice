package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.ibatis.mapping.SqlCommandType;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;

public class MysqlDeleteTemplate implements SqlTemplate{

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.DELETE;
	}

	@Override
	public String parseSQL(Method method) {
		Parameter[] params = method.getParameters();
		if(params.length == 1) {
			StringBuilder deleteSql = new StringBuilder("DELETE FROM ").append(
								EntityUtil.getTableName(params[0].getClass()));
			return deleteSql.append(this.buildWhere()).toString();
		}else {
			throw new MapperException();
		}
	}
	
	private String buildWhere() {
		StringBuffer where = new StringBuffer();
		where.append("\r\n WHERE id=#{id} and ts=#{ts}");
		return where.toString();
	}
}