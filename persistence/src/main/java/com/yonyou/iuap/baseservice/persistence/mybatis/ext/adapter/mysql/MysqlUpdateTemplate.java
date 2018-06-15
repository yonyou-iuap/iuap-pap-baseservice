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

public class MysqlUpdateTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(MysqlUpdateTemplate.class);

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.UPDATE;
	}

	@Override
	public String parseSQL(Method method) {
		Parameter[] params = method.getParameters();
		if(params.length == 1) {
			boolean isFirst = true;
			StringBuilder updateSql = new StringBuilder("UPDATE ").append(EntityUtil.getTableName(params[0].getClass()))
												.append("\r\n SET ");
			for(Field field : ReflectUtil.getFields(params[0].getClass())) {
				if(FieldUtil.updateable(field)) {
	            	if(!isFirst) {
	            		updateSql.append(",\r\n");
	            	}
	            	this.build(field, updateSql);
                	isFirst = false;
				}
			}
			if(!isFirst) {
				return updateSql.append("\r\n").append(updateSql)
								.append(this.buildWhere()).toString();
			}else {
				throw new MapperException();
			}
		}else {
			throw new MapperException();
		}
	}
	
	private void build(Field field, StringBuilder updateSql) {
        Column column = field.getAnnotation(Column.class);
        if (StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
        	log.error("默认Column规则,待后续补充支持！");
            throw new RuntimeException("未定义");
        }
        updateSql.append(column.name());
        updateSql.append("=#{").append(field.getName()).append("}");
	}
	
	private String buildWhere(){
		StringBuffer where = new StringBuffer();
		where.append("\r\n WHERE id=#{id} and ts=#{ts}");
		return where.toString();
	}

}