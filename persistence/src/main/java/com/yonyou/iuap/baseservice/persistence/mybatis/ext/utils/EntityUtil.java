package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.Model;

/**
 * 说明：Model/Entity工具类
 * @author houlf
 * 2018年6月12日
 */
@SuppressWarnings("all")
public class EntityUtil {
	
	private static Logger log = LoggerFactory.getLogger(EntityUtil.class);
	
	//Field缓存
	private static Map<Class<?>, Field[]> fieldCache = new HashMap<Class<?>, Field[]>();
	
	/**
	 * 根据JPA Annotable【Table】获取Table名称
	 * @param clazz
	 * @return
	 */
	public static String getTableName(Class<?> clazz) {
		return getTable(clazz).name();
	}
	
    public static Table getTable(Class<?> clazz) {  
        Table table = clazz.getAnnotation(Table.class);  
        if (table != null) {
        	return table;
        } else {
        	throw new RuntimeException();
        }
    }
    
    /**
     * 递归获取类属性
     * @param clazz
     * @return
     */
    public static Field[] getEntityFields(Class<?> clazz) {
    	Field[] cacheFields = fieldCache.get(clazz);
    	if(cacheFields == null || cacheFields.length==0) {
        	List<Field> listField = getFields(clazz);
        	Field[] validFields = new Field[listField.size()];
        	listField.toArray(validFields);
        	fieldCache.put(clazz, validFields);
        	return validFields;
    	}else {
    		return cacheFields;
    	}
    }
    
    
    /**
     * 递归获取类属性
     * @param clazz
     * @return
     */
    public static List<Field> getFields(Class<?> clazz) {
    	List<Field[]> listAllFields = new ArrayList<Field[]>();
    	recursiveFields(clazz, listAllFields);
    	Set<String> fieldSet = new HashSet<String>();
    	List<Field> listField = new ArrayList<Field>();
    	for(Field[] fields : listAllFields) {
    		if(fields != null) {
    			for(int i=0; i<fields.length; i++) {
    				if(fieldSet.contains(fields[i].getName().toLowerCase())) {
    					log.warn("Entity属性已存在，不重复加载父类属性:class=" + clazz.getName()
    								+ fields[i].getName());
    					continue;
    				}else {
    					listField.add(fields[i]);
    					fieldSet.add(fields[i].getName().toLowerCase());
    				}
    			}
    		}
    	}
    	return listField;
    }
    
    /**
     * 递归获取类及其父类的所有字段
     * @param clazz
     * @param allFields
     */
    public static void recursiveFields(Class<?> clazz, List<Field[]> allFields){
    	if(Model.class.isAssignableFrom(clazz)) {			//判断是否Model子类
            Field[] fields = clazz.getDeclaredFields();
            allFields.add(fields);							//添加当前类所有属性
            Class<?> superClazz = clazz.getSuperclass();
            if(superClazz != null) {
                recursiveFields(superClazz, allFields);
            }
    	}
    }

}