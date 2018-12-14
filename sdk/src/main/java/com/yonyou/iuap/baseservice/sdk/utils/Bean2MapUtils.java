package com.yonyou.iuap.baseservice.sdk.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jhb
 */
@SuppressWarnings("ALL")
public class Bean2MapUtils {

	public static Map<String, Object> bean2Map(Object obj) throws IllegalArgumentException {
		if (obj == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<>();

		Field[] declaredFields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : declaredFields) {
				field.setAccessible(true);
					map.put(field.getName(), field.get(obj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public static Map<String, String> bean2MapString(Object obj) throws IllegalArgumentException {
		if (obj == null) {
			return null;
		}

		Map<String, String> map = new HashMap<>();

		Field[] declaredFields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : declaredFields) {
				field.setAccessible(true);
				Object object=field.get(obj);
				if(object==null){
					continue;
				}else{
					map.put(field.getName(),object.toString() );
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}
}
