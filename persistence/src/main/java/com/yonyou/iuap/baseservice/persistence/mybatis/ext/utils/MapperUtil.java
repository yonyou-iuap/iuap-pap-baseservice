package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MapperUtil {
	
	/**
	 * 递归获取所有接口方法
	 * @param clazz
	 * @param listMethods
	 */
	public static Method[] getMethods4Interface(Class<?> clazz) {
		return clazz.getMethods();
	}
	
	/**
	 * 获取泛型类型
	 * @param clazz
	 * @return
	 */
	public static Class<?> getGenericClass(Class<?> clazz){
		Type[] genericType = clazz.getGenericInterfaces();
		if(genericType!=null && genericType.length==1) {
			Type[] actualType = ((ParameterizedType)genericType[0]).getActualTypeArguments();
			if(actualType!=null && actualType.length>0) {
				return ((Class<?>)actualType[0]);
			}
		}
		return null;
	}

}
