/**
 * 
 */
package com.yonyou.iuap.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author bill
 *
 */
	public class MethodUtils {

		static String GET_PREFIX = "get";
		static String SET_PREFIX = "set";
		private static Set<String> languagesSerial= new HashSet<>();
		static {
			languagesSerial.add("2");
			languagesSerial.add("3");
			languagesSerial.add("4");
			languagesSerial.add("5");
			languagesSerial.add("6");
		}

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodUtils.class);
	
	/**
	 * 通过实体的属性名以及属性序号获取值
	 * 
	 * @param obj
	 * @param prop
	 * @param serial
	 * @return
	 */
	public static String getDataBySerial(Object obj, String prop, String serial){

		if(!languagesSerial.contains(serial)){
			serial="";
		}

		Object value = getter(obj, prop + serial);

		if(value == null){
			value = getter(obj, prop);
		}

		if(value == null){
			return "";
		}else{
			return String.valueOf(value);
		}

	}
	
	
	/**
	 * 通过反射为对象设置值
	 * 暂时不用，为以后多语设值存留
	 * 
	 * @param obj
	 * @param prop
	 * @param value
	 * @param paraTypes
	 */
	public static void setter(Object obj, String prop, Object value, Class<?> paraTypes){
        Method target = null;
        try {
            target = obj.getClass().getMethod(setterMethodName(prop), paraTypes);
            target.invoke(obj, value);
        } catch (Exception e) {
			LOGGER.error("setter values failure",e);
        }
    }
	
	/**
	 * 获取对象的属性值
	 * 
	 * @param obj
	 * @param prop
	 * @return
	 */
    public static Object getter(Object obj, String prop){
        Method target = null;
        Object result = null;
        try {
            target = obj.getClass().getMethod(getterMethodName(prop));
            result = target.invoke(obj);
        } catch (Exception e) {
			LOGGER.error("get values failure",e);
        }
        return result;
    }
	
    
    public  static String getterMethodName(String prop){
		return GET_PREFIX+upperFirst(prop);
	}
	    
	public  static String setterMethodName(String prop){
		return SET_PREFIX+upperFirst(prop);
	}
	
	public static String upperFirst(String prop){
        return prop.substring(0, 1).toUpperCase()+ prop.substring(1);
    }
	
}
