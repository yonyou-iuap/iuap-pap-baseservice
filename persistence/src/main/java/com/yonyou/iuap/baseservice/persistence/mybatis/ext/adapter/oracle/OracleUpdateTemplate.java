package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Column;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

public class OracleUpdateTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(OracleUpdateTemplate.class);

	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}
	
	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.UPDATE;
	}

	@Override
	public String parseSQL(Method method, Class<?> entityClazz) {
		boolean isFirst = true;
		StringBuilder updateSql = new StringBuilder("UPDATE ").append(EntityUtil.getTableName(entityClazz))
											.append("\r\n SET ");
		for(Field field : ReflectUtil.getFields(entityClazz)) {
			if(FieldUtil.updateable(field)) {
            	if(!isFirst) {
            		updateSql.append(",\r\n");
            	}
            	this.build(field, updateSql);
               	isFirst = false;
			}
		}
		if(!isFirst) {
			return updateSql.append(this.buildWhere()).toString();
		}else {
			throw new MapperException("");
		}
	}
	
	private void build(Field field, StringBuilder updateSql) {
        Column column = field.getAnnotation(Column.class);
        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
            updateSql.append(FieldUtil.getColumnName(field));
            updateSql.append("=#{").append(field.getName()).append("}");
        }else {
            updateSql.append(column.name());
            updateSql.append("=#{").append(field.getName()).append("}");
        }
	}
	
	private String buildWhere(){
		StringBuffer where = new StringBuffer();
		where.append("\r\n WHERE id=#{id} and ts=#{ts}");
		return where.toString();
	}

}