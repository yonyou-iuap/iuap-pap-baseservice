package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.support.generator.GeneratedValue;
import com.yonyou.iuap.baseservice.support.generator.Strategy;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 说明：
 * @author Aton
 * 2018年6月19日
 */
public class MysqlInsertSelectiveTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(MysqlInsertSelectiveTemplate.class);

	@Override
	public Dialect getDialect() {
		return Dialect.mysql;
	}
	
	public SqlCommandType getSQLType() {
		return SqlCommandType.INSERT;
	}
	
	@Override
	public String parseSQL(Method method, Class<?> entityClazz) {
        StringBuilder resultSql = new StringBuilder();
		StringBuilder columnSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		boolean isFirst = true;
		for(Field field : EntityUtil.getEntityFields(entityClazz)) {
			if(FieldUtil.insertable(field)) {
            	this.build(field, columnSql, valuesSql,isFirst);
               	isFirst = false;
			}
		}
		if(!isFirst) {
			return "<script>\r\n"+resultSql.append("INSERT INTO ").append(EntityUtil.getTableName(entityClazz))
								.append("  <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" > ").append(columnSql).append(" </trim> \r\n<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\" > ")
								.append(valuesSql).append("</trim>").toString()+"\r\n</script>";
		}else {
			log.error("无可插入字段:" + method.getName()+";\t"+entityClazz.getName());
			throw new MapperException("无可插入字段:" + method.getName()+";\t"+entityClazz.getName());
		}
	}
	
	private void build(Field field, StringBuilder columnSql, StringBuilder valuesSql,boolean isFirst) {
        Column column = field.getAnnotation(Column.class);
        columnSql.append("\r\n\t<if test=\""+field.getName()+" != null\">" );
        valuesSql.append("\r\n\t<if test=\""+field.getName()+" != null\">" );

        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
            columnSql.append(FieldUtil.getColumnName(field));
            valuesSql.append(FieldUtil.build4Mybatis(field));
        }else {
            columnSql.append(column.name());
            valuesSql.append(FieldUtil.build4Mybatis(field));
        }
        columnSql.append(",");
        valuesSql.append(",");
        columnSql.append("</if>" );
        valuesSql.append("</if>" );
	}

}