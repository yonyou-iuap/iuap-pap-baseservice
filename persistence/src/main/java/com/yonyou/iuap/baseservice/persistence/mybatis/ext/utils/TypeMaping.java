package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.util.HashMap;
import java.util.Map;

public class TypeMaping {
	
	private static Map<String, String> javaTypeMap = new HashMap<String, String>();

	static {
		javaTypeMap.put("java.lang.Boolean", "BOOLEAN");
		javaTypeMap.put("java.lang.Byte", "TINYINT");
		javaTypeMap.put("java.lang.String", "VARCHAR");
		javaTypeMap.put("java.lang.Integer", "INTEGER");
		javaTypeMap.put("java.lang.Short", "SMALLINT");
		javaTypeMap.put("java.lang.Long", "BIGINT");
		javaTypeMap.put("java.lang.Float", "REAL");
		javaTypeMap.put("java.lang.Double", "DOUBLE");
		javaTypeMap.put("java.util.Date", "DATE");
		javaTypeMap.put("java.sql.Clob", "CLOB");
		javaTypeMap.put("java.sql.Blob", "BLOB");
		javaTypeMap.put("java.math.BigDecimal", "NUMERIC");
	}
	
	public static String getJdbcType(Class<?> fieldType) {
		String jdbcType = javaTypeMap.get(fieldType.getName());
		return jdbcType;
	}
	
	public static boolean isSupported(Class<?> fieldType) {
		return javaTypeMap.containsKey(fieldType.getName());
	}
	
}
