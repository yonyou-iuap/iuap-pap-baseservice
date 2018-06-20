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

public class SelectSqlByEntity implements SqlGenerator{
	
	private Logger log = LoggerFactory.getLogger(SelectSqlByEntity.class);

	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.UPDATE;
	}

	/**
	 * 解析、构建SQL
	 */
	@Override
	public String parseSQL(Object entity) {
		Class<?> clazz = entity.getClass();
		boolean isFirst = true;
		StringBuilder updateSql = new StringBuilder("UPDATE ").append(EntityUtil.getTableName(clazz))
											.append("\r\n SET ");
		for(Field field : ReflectUtil.getFields(clazz)) {
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
			log.warn("未组装update sql, entity类型非法:"+clazz.getName());
			throw new MapperException("未组装update sql...");
		}
	}
	
	/**
	 * 补充column和value
	 * @param field
	 * @param columnSql
	 * @param valuesSql
	 */
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
	
	/**
	 * 补充where条件
	 * @return
	 */
	private String buildWhere(){
		StringBuffer where = new StringBuffer();
		where.append("\r\n WHERE id=#{id} and ts=#{ts}");
		return where.toString();
	}

}