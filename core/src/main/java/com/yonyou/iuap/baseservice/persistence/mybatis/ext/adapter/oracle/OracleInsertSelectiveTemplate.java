package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 说明：
 * @author Aton
 * 2018年6月19日
 */
public class OracleInsertSelectiveTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(OracleInsertSelectiveTemplate.class);

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
            	this.build(field, columnSql, valuesSql,isFirst);
               	isFirst = false;
			}
		}
		if(!isFirst) {
			return "<script>\r\n"+new StringBuilder("INSERT INTO ").append(EntityUtil.getTableName(entityClazz))
								.append(" (").append(columnSql).append(") \r\nVALUES (")
								.append(valuesSql).append(")").toString()+"\r\n</script>";
		}else {
			log.error("无可插入字段:" + method.getName()+";\t"+entityClazz.getName());
			throw new MapperException("无可插入字段:" + method.getName()+";\t"+entityClazz.getName());
		}
	}
	
	private void build(Field field, StringBuilder columnSql, StringBuilder valuesSql,boolean isFirst) {
        Column column = field.getAnnotation(Column.class);
        columnSql.append("\r\n\t<if test=\""+field.getName()+" != null\">" );
        valuesSql.append("\r\n\t<if test=\""+field.getName()+" != null\">" );
        if(!isFirst) {
            columnSql.append(",");
            valuesSql.append(",");
        }
        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
            columnSql.append(FieldUtil.getColumnName(field));
            valuesSql.append(FieldUtil.build4Mybatis(field));
        }else {
            columnSql.append(column.name());

            if (StringUtils.isEmpty(column.columnDefinition())) {
                valuesSql.append(FieldUtil.build4Mybatis(field));
            } else {
                if ("DATE".equalsIgnoreCase(column.columnDefinition())
                        || "TIME".equalsIgnoreCase(column.columnDefinition())
                        || "DATETIME".equalsIgnoreCase(column.columnDefinition())
                ) {
                    valuesSql.append("TO_DATE(").append(FieldUtil.build4Mybatis(field)).append(",'yyyy-MM-dd HH24:mi:ss')");
                } else if ("TIMESTAMP".equalsIgnoreCase(column.columnDefinition())) {

                    valuesSql.append("TO_TIMESTAMP(").append(FieldUtil.build4Mybatis(field)).append(",'yyyy-MM-dd HH24:mi:ss ff')");
                } else if (field.getType().isAssignableFrom(Date.class) && ("VARCHAR".equalsIgnoreCase(column.columnDefinition())
                        || "CHAR".equalsIgnoreCase(column.columnDefinition()))
                ) {
                    valuesSql.append("TO_CHAR(").append(FieldUtil.build4Mybatis(field)).append(",'yyyy-MM-dd HH24:mi:ss')");
                } else {
                    valuesSql.append(FieldUtil.build4Mybatis(field));
                }
            }
        }
        columnSql.append("</if>" );
        valuesSql.append("</if>" );
	}

}