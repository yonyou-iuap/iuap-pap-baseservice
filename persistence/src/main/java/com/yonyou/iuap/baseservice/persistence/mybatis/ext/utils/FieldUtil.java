package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Transient;

public class FieldUtil {

	public static String getColumnName(Field field) {
		return field.getName();
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