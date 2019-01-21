package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle.byentity;

import java.lang.reflect.Field;
import javax.persistence.Column;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlGenerator;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

public class InsertSqlByEntity implements SqlGenerator{
	
	private Logger log = LoggerFactory.getLogger(InsertSqlByEntity.class);

	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.INSERT;
	}

	/**
	 * 解析、构建SQL
	 */
	@Override
	public String parseSQL(Object entity) {
		Class<?> clazz = entity.getClass();
		StringBuilder columnSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		boolean isFirst = true;
		for(Field field : ReflectUtil.getFields(clazz)) {
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
			return new StringBuilder("INSERT INTO ").append(EntityUtil.getTableName(clazz))
								.append(" (").append(columnSql).append(") VALUES (")
								.append(valuesSql).append(")").toString();
		}else {
			log.warn("未组装insert sql, entity类型非法:"+clazz.getName());
			throw new MapperException("未组装insert sql...");
		}
	}
	
	/**
	 * 补充column和value
	 * @param field
	 * @param columnSql
	 * @param valuesSql
	 */
	private void build(Field field, StringBuilder columnSql, StringBuilder valuesSql) {
        Column column = field.getAnnotation(Column.class);
        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
            columnSql.append(FieldUtil.getColumnName(field));
            valuesSql.append("#{").append(field.getName()).append("}");
        }else {
            columnSql.append(column.name());
            valuesSql.append("#{").append(field.getName()).append("}");
        }
	}

}