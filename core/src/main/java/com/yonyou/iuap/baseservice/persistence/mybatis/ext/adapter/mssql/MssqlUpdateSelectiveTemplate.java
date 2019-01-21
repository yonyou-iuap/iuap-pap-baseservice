package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mssql;

import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MssqlUpdateSelectiveTemplate extends AbsMssqlTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(MssqlUpdateSelectiveTemplate.class);

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
            	this.build(field, updateSql,isFirst);
               	isFirst = false;
			}
		}
		if(!isFirst) {
			return "<script>\r\n"+updateSql.append(this.buildWhere(entityClazz)).toString()+"\r\n</script>";
		}else {
			log.error("无可更新字段:" + method.getName()+";\t"+entityClazz.getName());
			throw new MapperException("无可更新字段:" + method.getName()+";\t"+entityClazz.getName());
		}
	}
	
	private void build(Field field, StringBuilder updateSql,boolean isFirst) {
        Column column = field.getAnnotation(Column.class);
        updateSql.append("\r\n\t<if test=\""+field.getName()+" != null\">");
        if(!isFirst) {
            updateSql.append(",");
        }
        if (column==null || StrUtil.isEmpty(column.name())) {
            updateSql.append(FieldUtil.getColumnName(field));
            if(field.getAnnotation(Version.class)==null) {				//非乐观锁字段
            	updateSql.append("=").append(FieldUtil.build4Mybatis(field));
            }else {														//乐观锁字段
                updateSql.append("=").append(FieldUtil.buildVersionField4Mybatis(field));
            }
        }else {
            updateSql.append(column.name());
            if(field.getAnnotation(Version.class)==null) {				//非乐观锁字段
                updateSql.append("=").append(FieldUtil.build4Mybatis(field));
            }else {														//乐观锁字段
                updateSql.append("=").append(FieldUtil.buildVersionField4Mybatis(field));
            }
        }
        updateSql.append("</if>");
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
			StringBuffer where = new StringBuffer("\r\n WHERE 1=1 ");
			if(idField != null) {
				where.append(" and ").append(FieldUtil.getColumnName(idField))
					 .append("=").append(FieldUtil.build4Mybatis(idField));
			}else {
				throw new MapperException("无效的对象类型,@Id Field必须存在，class="+entityClazz.getName());
			}
			
			if (tsField != null) {
				where.append(" and ").append(FieldUtil.getColumnName(tsField))
					 .append("=").append(FieldUtil.build4Mybatis(tsField));
				return where.toString();
			} else {
				log.warn("无效的对象类型,未找到@Version Field，class="+entityClazz.getName());
			}
			return where.toString();
		}else {
			log.error("无效的对象类型，class="+entityClazz.getName());
			throw new MapperException("无效的对象类型，class="+entityClazz.getName());
		}
	}

}