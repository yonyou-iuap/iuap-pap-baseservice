package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.data.domain.PageRequest;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher.Matcher;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher.MatcherFactory;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.mvc.type.SearchParams;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：
 * @author Aton
 * 2018年6月20日
 */
public class MysqlSelectTemplate implements SqlTemplate{

	@Override
	public Dialect getDialect() {
		return Dialect.mysql;
	}
	
	@Override
	public SqlCommandType getSQLType() {
		return SqlCommandType.SELECT;
	}

	@Override
	public String parseSQL(Method method, Class<?> entityClazz) {
		StringBuilder selectSql = new StringBuilder("SELECT ");
		StringBuilder whereSql = new StringBuilder(this.getBaseWhere(entityClazz));
		boolean isFirst4Select = true;
		for(Field field : EntityUtil.getEntityFields(entityClazz)) {
			if(FieldUtil.isSelectable(field)) {		//构建select
				if(!isFirst4Select) {
					selectSql.append(",\r\n");
				}
				this.build(field, selectSql);
				isFirst4Select = false;
			}
		}
		selectSql.append("\r\nFROM ").append(EntityUtil.getTableName(entityClazz));
		
		//构建Where语句
		Class<?>[] paramType = method.getParameterTypes();
		Annotation[][] paramAnnos = method.getParameterAnnotations();
		if(paramType!=null && paramType.length>0 && paramType.length<=2) {
			for(int i=0; i<paramType.length; i++) {
				Annotation[] curAnnoArray = paramAnnos[i];
				Param paramAnno = null;
				for(Annotation curAnno : curAnnoArray) {
					if(curAnno instanceof Param) {
						paramAnno = ((Param)curAnno);
						break;
					}
				}
				String prefix = paramAnno==null ? null:paramAnno.value();
				if(paramType[i].isAssignableFrom(PageRequest.class)) {
					//this.buildWhere4Page(prefix, PageRequest.class, whereSql);
				}else if(paramType[i].isAssignableFrom(SearchParams.class)) {
					this.buildWhere4SearchParams(prefix+".searchMap", entityClazz, whereSql);
				}else if(paramType[i].isAssignableFrom(Map.class)) {
					this.buildWhere4Map(prefix, entityClazz, whereSql);
				}
			}
		}
		return "<script>\r\n"+selectSql.append(whereSql).toString()+"\r\n</script>";
	}
	
	private void build(Field field, StringBuilder selectSql) {
		selectSql.append("\t").append(FieldUtil.getColumnName(field))
					.append(" as ").append(field.getName());
	}
	
	private void buildWhere4Page(String prefix, Class<PageRequest> clazz, StringBuilder whereSql) {
		//暂不需要
	}
	
	private void buildWhere4SearchParams(String prefix, Class<?> entityClazz, StringBuilder whereSql) {
		if(!StrUtil.isBlank(prefix)) {
			whereSql.append("\r\n<if test=\"" + prefix + "!= null\">");
		}
		Matcher matcher = null;
		for(Field field : EntityUtil.getEntityFields(entityClazz)) {
			if(FieldUtil.isCondition(field)) {					//构建select
				Condition condition = field.getAnnotation(Condition.class);
				matcher = MatcherFactory.getMatcher(condition.match());
				whereSql.append(matcher.buildCondition(field, prefix));
			}
		}
		whereSql.append("</if>");
	}
	
	private void buildWhere4Map(String prefix, Class<?> entityClazz, StringBuilder whereSql) {
		if(!StrUtil.isBlank(prefix)) {
			whereSql.append("\r\n<if test=\"" + prefix + "!= null\">");
		}
		Matcher matcher = null;
		for(Field field : EntityUtil.getEntityFields(entityClazz)) {
			if(FieldUtil.isCondition(field)) {					//构建select
				Condition condition = field.getAnnotation(Condition.class);
				matcher = MatcherFactory.getMatcher(condition.match());
				whereSql.append(matcher.buildCondition(field, prefix));
			}
		}
		if(!StrUtil.isBlank(prefix)) {
			whereSql.append("</if>");
		}
	}
	
	private String getBaseWhere(Class<?> entityClazz) {
		if(LogicDel.class.isAssignableFrom(entityClazz)) {
			return "\r\nWHERE 1=1 and dr=0";
		}else{
			return "\r\nWHERE 1=1 ";
		}
	}

}