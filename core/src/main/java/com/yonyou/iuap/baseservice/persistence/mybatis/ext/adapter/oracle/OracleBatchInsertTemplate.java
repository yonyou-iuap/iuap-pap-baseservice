package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.abs.AbsBatchInsertTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * description：add new oracle batch insert sql provider for performance optimizing
 * @author leon
 * 2018.12.14
 */
public class OracleBatchInsertTemplate  extends AbsBatchInsertTemplate {
    private Logger log = LoggerFactory.getLogger(OracleBatchInsertTemplate.class);

    @Override
    public Dialect getDialect() {
        return Dialect.oracle;
    }

    @Override
    public String parseSQL(Method method, Class<?> entityClazz) {
        StringBuilder columnSql = new StringBuilder();
        StringBuilder valuesSql = new StringBuilder();
        boolean isFirst = true;
        valuesSql.append("\r\n\t   <foreach collection =\"list\" item=\"item\" index= \"index\" separator =\" UNION ALL \"> \r\n\t  SELECT ");
        for (Field field : EntityUtil.getEntityFields(entityClazz)) {
            if (FieldUtil.insertable(field)) {
                this.build(field, columnSql, valuesSql, isFirst);
                isFirst = false;
            }
        }
        valuesSql.append(" FROM DUAL \r\n\t </foreach >");
        if (!isFirst) {
            return "<script>\r\n" + new StringBuilder("INSERT INTO ").append(EntityUtil.getTableName(entityClazz))
                    .append(" (").append(columnSql).append(") \r\n\t  ")
                    .append(valuesSql).append("\r\n</script>").toString();
        } else {
            log.error("无可插入字段:" + method.getName() + ";\t" + entityClazz.getName());
            throw new MapperException("无可插入字段:" + method.getName() + ";\t" + entityClazz.getName());
        }
    }

    private void build(Field field, StringBuilder columnSql, StringBuilder valuesSql, boolean isFirst) {
        Column column = field.getAnnotation(Column.class);
        if (!isFirst) {
            columnSql.append(",");
            valuesSql.append(",");
        }
        if (column == null || StrUtil.isEmpty(column.name())) {            //补充内容,比如驼峰规则
            columnSql.append(FieldUtil.getColumnName(field));
        } else {
            columnSql.append(column.name());
        }
        valuesSql.append(FieldUtil.build4Mybatis("item", field));
    }
}