package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

public class NamingUtil {
	
	/***
	 * 下划线命名转为驼峰命名
	 * @param columnName		下划线命名的字符串
	 */
	public static String lineToHump(String columnName) {
		StringBuilder result = new StringBuilder();
		String a[] = columnName.split("_");
		for (String s : a) {
			if (result.length() == 0) {
				result.append(s.toLowerCase());
			} else {
				result.append(s.substring(0, 1).toUpperCase());
				result.append(s.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}
	
	/***
	 * 驼峰命名转为下划线命名
	 * @param fieldName		驼峰命名的字符串
	 */
	public static String humpToline(String fieldName) {
		StringBuilder sb = new StringBuilder(fieldName);
		int temp = 0;
		for (int i=0; i<fieldName.length(); i++) {
			if (Character.isUpperCase(fieldName.charAt(i))) {
				sb.insert(i + temp, "_");
				temp += 1;
			}
		}
		return sb.toString().toUpperCase();
	}

}
