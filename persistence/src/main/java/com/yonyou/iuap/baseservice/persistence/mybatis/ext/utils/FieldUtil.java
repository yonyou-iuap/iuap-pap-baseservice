package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation.Condition;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.converter.ConvertorHolder;

import cn.hutool.core.util.StrUtil;

public class FieldUtil {
	
	public static String getColumnName(Field field) {
		return ConvertorHolder.inst().getConvertor().field2Column(field);
	}
	
	public static String build4Mybatis(Field field) {
		return new StringBuilder().append("#{").append(field.getName())
				.append(", jdbcType=").append(TypeMaping.getJdbcType(field.getType()))
				.append("}").toString();
	}
	
	public static String build4Mybatis(String prefix, Field field) {
		if(StrUtil.isBlank(prefix)) {
			return new StringBuilder().append("#{").append(field.getName())
					.append(", jdbcType=").append(TypeMaping.getJdbcType(field.getType()))
					.append("}").toString();
		}else {
			return new StringBuilder().append("#{").append(prefix).append(".").append(field.getName())
					.append(", jdbcType=").append(TypeMaping.getJdbcType(field.getType()))
					.append("}").toString();
		}
	}
	
	public static boolean isBaseVariable(Field field) {
		return TypeMaping.getJdbcType(field.getType())!=null;
	}

	public static String getColumnName1(Field field) {
		Column column = field.getAnnotation(Column.class);
        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
        	return field.getName();
        }else {
        	return column.name();
        }
	}
	
	/**
	 * 判断字段是否可作为条件
	 * @param field
	 * @return
	 */
	public static boolean isCondition(Field field) {
		return field.getAnnotation(Condition.class)!=null;
	}
	
	/**
	 * 判断字段是否可查询
	 * @param field
	 * @return
	 */
	public static boolean isSelectable(Field field) {
		return true;
	}
	
	/**
	 * 判断字段是否可新增
	 * @param field
	 * @return
	 */
	public static boolean insertable(Field field) {
		if(field.getAnnotation(Transient.class) != null) {
			return false;
		}else {
			Column column = field.getAnnotation(Column.class);
	        if(column != null) {
	        	return column.insertable();
	        }else {
	        	return FieldUtil.isBaseVariable(field);
	        }
		}
	}
	
	/**
	 * 判断字段是否可更新
	 * @param field
	 * @return
	 */
	public static boolean updateable(Field field) {
		if(field.getAnnotation(Transient.class) != null) {
			return false;
		}else {
	        Column column = field.getAnnotation(Column.class);
	        if(column != null) {
	        	return column.updatable();
	        }else {
	        	return FieldUtil.isBaseVariable(field);
	        }
		}
	}

}