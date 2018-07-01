package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;

import cn.hutool.core.util.StrUtil;

public class MysqlUpdateTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(MysqlUpdateTemplate.class);

	@Override
	public Dialect getDialect() {
		return Dialect.mysql;
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
		for(Field field : EntityUtil.getEntityFields(entityClazz)) {
			if(FieldUtil.updateable(field)) {
            	if(!isFirst) {
            		updateSql.append(",\r\n\t");
            	}
            	this.build(field, updateSql);
               	isFirst = false;
			}
		}
		if(!isFirst) {
			return updateSql.append(this.buildWhere(entityClazz)).toString();
		}else {
			log.error("无可更新字段:" + method.getName()+";\t"+entityClazz.getName());
			throw new MapperException("无可更新字段:" + method.getName()+";\t"+entityClazz.getName());
		}
	}
	
	private void build(Field field, StringBuilder updateSql) {
        Column column = field.getAnnotation(Column.class);
        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
            updateSql.append(FieldUtil.getColumnName(field));
            updateSql.append("=").append(FieldUtil.build4Mybatis(field));
            //updateSql.append("=#{").append(field.getName()).append("}");
        }else {
            updateSql.append(column.name());
            updateSql.append("=").append(FieldUtil.build4Mybatis(field));
            //updateSql.append("=#{").append(field.getName()).append("}");
        }
	}
	
	private String buildWhere(Class<?> entityClazz){
		if(Model.class.isAssignableFrom(entityClazz)) {			
			Field idField = null, tsField = null;
			for (Field field : EntityUtil.getEntityFields(entityClazz)) {
				if (field.getAnnotation(Id.class) != null) {
					idField = field;
				}
				if (field.getAnnotation(Version.class) != null) {
					tsField = field;
				}
			}
			if (idField != null && tsField != null) {
				StringBuffer where = new StringBuffer("\r\n WHERE ");
				where.append(idField.getName()).append("=").append(FieldUtil.build4Mybatis(idField)).append(" and ")
						.append(tsField.getName()).append("=").append(FieldUtil.build4Mybatis(tsField));
				return where.toString();
			} else {
				log.error("无效的对象类型，class="+entityClazz.getName()+"\r\n未找到id、ts字段！");
				throw new MapperException("无效的对象类型，class="+entityClazz.getName());
			}
		}else {
			log.error("无效的对象类型，class="+entityClazz.getName());
			throw new MapperException("无效的对象类型，class="+entityClazz.getName());
		}
	}

}