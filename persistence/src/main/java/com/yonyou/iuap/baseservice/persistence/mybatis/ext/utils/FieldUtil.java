package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation.Condition;

import cn.hutool.core.util.StrUtil;

public class FieldUtil {

	public static String getColumnName(Field field) {
		Column column = field.getAnnotation(Column.class);
        if (column==null || StrUtil.isEmpty(column.name())) {			//补充内容,比如驼峰规则
        	return field.getName();
        }else {
        	return column.name();
        }
	}
	
	public static boolean isCondition(Field field) {
		return field.getAnnotation(Condition.class)!=null;
	}
	
	public static boolean isSelectable(Field field) {
		return true;
	}
	
	public static boolean insertable(Field field) {
		if(field.getAnnotation(Transient.class) != null) {
			return false;
		}else {
			Column column = field.getAnnotation(Column.class);
			return column==null || column.insertable();
		}
	}
	
	public static boolean updateable(Field field) {
		if(field.getAnnotation(Transient.class) != null) {
			return false;
		}else {
	        Column column = field.getAnnotation(Column.class);
	        return column==null || column.updatable();
		}
	}

}