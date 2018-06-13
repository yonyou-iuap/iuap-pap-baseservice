package com.yonyou.iuap.baseservice.persistence.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class AnnotationUtil {
	
	public static List<Field> getFieldByAnnotation(Class<?> clazz, Class annotationClass) {
    	Field[] fields = clazz.getDeclaredFields();
    	List<Field> listFields = new ArrayList<Field>();
    	for(Field curField : fields) {
    		if(curField.getAnnotation(annotationClass) != null) {
    			listFields.add(curField);
    		}
    	}
    	return listFields;
    }
    
	public static Field getFirstFieldByAnnotation(Class<?> clazz, Class annotationClass) {
    	Field[] fields = clazz.getDeclaredFields();
    	for(Field curField : fields) {
    		if(curField.getAnnotation(annotationClass) != null) {
    			return curField;
    		}
    	}
    	return null;
    }

}