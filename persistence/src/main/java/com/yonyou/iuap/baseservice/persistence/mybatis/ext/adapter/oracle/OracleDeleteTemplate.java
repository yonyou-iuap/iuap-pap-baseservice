package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Id;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;

/**
 * 说明：
 * @author Aton
 * 2018年6月24日
 */
public class OracleDeleteTemplate implements SqlTemplate{
	
	private Logger log = LoggerFactory.getLogger(OracleDeleteTemplate.class);

	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}

	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.DELETE;
	}
	
	@Override
	public String parseSQL(Method method, Class<?> entityClazz) {
		StringBuilder deleteSql = new StringBuilder("DELETE FROM ").append(
							EntityUtil.getTableName(entityClazz));
		//构建Where语句
		Class<?>[] paramType = method.getParameterTypes();
		Annotation[][] paramAnnos = method.getParameterAnnotations();
		if(paramType!=null && paramType.length>0 && paramType.length<=2) {
			for(int i=0; i<paramType.length; i++) {
				Annotation[] curAnnoArray = paramAnnos[i];
				Param curParamAnno = null;
				for(Annotation curAnno : curAnnoArray) {
					if(curAnno instanceof Param) {
						curParamAnno = ((Param)curAnno);
						break;
					}
				}
				String prefix = curParamAnno==null ? null:curParamAnno.value();
				return deleteSql.append(this.buildWhere(prefix, entityClazz)).toString();
			}
		}
		return deleteSql.append(this.buildWhere("", entityClazz)).toString();
	}
	
	private String buildWhere(String prefix, Class<?> entityClazz) {
		if(Model.class.isAssignableFrom(entityClazz)) {			
			Field idField = null;
			for (Field field : EntityUtil.getEntityFields(entityClazz)) {
				if (field.getAnnotation(Id.class) != null) {
					idField = field;
					break;
				}
			}
			if (idField != null) {
				StringBuffer where = new StringBuffer("\r\n WHERE 1=1 and ");
				where.append(FieldUtil.getColumnName(idField)).append("=")
					 .append(FieldUtil.build4Mybatis(prefix, idField));
				return where.toString();
			} else {
				log.error("无效的对象类型，class="+entityClazz.getName()+"\r\n未找到id字段！");
				throw new MapperException("无效的对象类型，class="+entityClazz.getName());
			}
		}else {
			log.error("无效的对象类型，class="+entityClazz.getName());
			throw new MapperException("无效的对象类型，class="+entityClazz.getName());
		}
	}

}