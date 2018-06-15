package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.persistence.Column;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

public class MysqlInsertTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(MysqlInsertTemplate.class);

	public SqlCommandType getSQLType() {
		return SqlCommandType.INSERT;
	}
	
	@Override
	public String parseSQL(Method method) {
		Parameter[] params = method.getParameters();
		if(params.length == 1) {
			StringBuilder columnSql = new StringBuilder("INSERT INTO ");
			StringBuilder valuesSql = new StringBuilder(") VALUES (");
			boolean isFirst = true;
			for(Field field : ReflectUtil.getFields(params[0].getClass())) {
				if(FieldUtil.insertable(field)) {
	            	if(!isFirst) {
	            		columnSql.append(", ");
	            		valuesSql.append(", ");
	            	}
	            	this.build(field, columnSql, valuesSql);
                	isFirst = false;
				}
			}
			if(!isFirst) {
				return new StringBuilder("INSERT INTO ").append(EntityUtil.getTableName(params[0].getClass()))
									.append(" (").append(columnSql).append(") VALUES (")
									.append(valuesSql).append(")").toString();
			}else {
				throw new MapperException();
			}
		}else {
			throw new MapperException();
		}
	}
	
	private void build(Field field, StringBuilder columnSql, StringBuilder valuesSql) {
        Column column = field.getAnnotation(Column.class);
        if (StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
        	log.error("默认Column规则,待后续补充支持！");
            throw new RuntimeException("未定义");
        }
        columnSql.append(column.name());
        valuesSql.append("#{").append(field.getName()).append("}");
	}

}