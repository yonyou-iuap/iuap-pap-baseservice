package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import com.yonyou.iuap.baseservice.model.Model;

/**
 * 说明：Model/Entity工具类
 * @author houlf
 * 2018年6月12日
 */
public class EntityUtil {

    public static Table getTable(Class<?> clazz) {  
        Table table = clazz.getAnnotation(Table.class);  
        if (table != null) {
        	return table;
        } else {
        	throw new RuntimeException();
        }
    }
    
    public static List<Field> getFields(Class<?> clazz) {
    	List<Field[]> allFields = new ArrayList<Field[]>();
    	recursiveFields(clazz, allFields);
    	Set<String> fieldSet = new HashSet<String>();
    	List<Field> listField = new ArrayList<Field>();
    	for(Field[] fieldArray : allFields) {
    		if(fieldArray != null) {
    			for(int i=0; i<fieldArray.length; i++) {
    				if(fieldSet.contains(fieldArray[i].getName().toLowerCase())) {
    					continue;
    				}else {
    					listField.add(fieldArray[i]);
    					fieldSet.add(fieldArray[i].getName().toLowerCase());
    				}
    			}
    		}
    	}
    	return listField;
    }
    
    public static void recursiveFields(Class<?> clazz, List<Field[]> allFields){
    	if(Model.class.isAssignableFrom(clazz)) {			//判断是否Model子类
            Field[] fields = clazz.getDeclaredFields();
            allFields.add(fields);

            Class<?> superClazz = clazz.getSuperclass();
            recursiveFields(superClazz, allFields);
    	}
    }
	

}