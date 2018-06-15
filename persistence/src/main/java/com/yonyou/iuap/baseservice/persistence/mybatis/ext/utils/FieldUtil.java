package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.lang.reflect.Field;

import javax.persistence.Column;

public class FieldUtil {

	public static boolean insertable(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column==null || column.insertable();
	}
	
	public static boolean updateable(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column==null || column.updatable();
	}

}