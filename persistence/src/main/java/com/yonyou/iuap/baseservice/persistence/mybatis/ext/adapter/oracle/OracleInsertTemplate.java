package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Column;
import org.apache.ibatis.mapping.SqlCommandType;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：
 * @author Aton
 * 2018年6月19日
 */
public class OracleInsertTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(OracleInsertTemplate.class);

	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}
	
	public SqlCommandType getSQLType() {
		return SqlCommandType.INSERT;
	}
	
	@Override
	public String parseSQL(Method method, Class<?> entityClazz) {
		StringBuilder columnSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		boolean isFirst = true;
		for(Field field : EntityUtil.getEntityFields(entityClazz)) {
			if(FieldUtil.insertable(field)) {
            	if(!isFirst) {
            		columnSql.append(", \r\n\t");
            		valuesSql.append(", \r\n\t");
            	}
            	this.build(field, columnSql, valuesSql);
               	isFirst = false;
			}
		}
		if(!isFirst) {
			return new StringBuilder("INSERT INTO ").append(EntityUtil.getTableName(entityClazz))
								.append(" (").append(columnSql).append(") \r\nVALUES (")
								.append(valuesSql).append(")").toString();
		}else {
			log.error("无可插入字段:" + method.getName()+";\t"+entityClazz.getName());
			throw new MapperException("无可插入字段:" + method.getName()+";\t"+entityClazz.getName());
		}
	}
	
	private void build(Field field, StringBuilder columnSql, StringBuilder valuesSql) {
        Column column = field.getAnnotation(Column.class);
        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
            columnSql.append(FieldUtil.getColumnName(field));
            valuesSql.append(FieldUtil.build4Mybatis(field));
        }else {
            columnSql.append(column.name());
            valuesSql.append(FieldUtil.build4Mybatis(field));
        }
	}

}