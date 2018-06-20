package com.yonyou.iuap.baseservice.persistence.mybatis.ext.template;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：MyBatis CRUD构建器
 * @author houlf
 * 2018年6月12日
 */
public class SimpleSqlProvider {
	
	private Logger log = LoggerFactory.getLogger(SimpleSqlProvider.class);
	
	/**
	 * 装配insert sql
	 * @param entity
	 * @return
	 */
	public String insert(Object entity) {
		Class<?> clazz = entity.getClass();
        Table table = EntityUtil.getTable(clazz);
        List<Field> listFields = EntityUtil.getFields(clazz);
        
        StringBuilder insertSql = new StringBuilder();
        StringBuilder valuesSql = new StringBuilder(") VALUES ( ");
        //装配insert 语句头
        insertSql.append("INSERT INTO ").append(table.name()).append("(");
        
        //装配insert 字段语句
        try {
        	boolean isFirst = true;										//是否insert 首字段
            for (Field curField : listFields) {
                Column column = curField.getAnnotation(Column.class);  
                if (column != null && column.insertable()) {
                    if (StrUtil.isEmpty(column.name())) {				//补充内容,比如驼峰规则
                    	log.error("默认Column规则,待后续补充支持！");
	                	throw new RuntimeException("未定义");
                    }
                	if(!isFirst) {
                		insertSql.append(", ");
                		valuesSql.append(", ");
                	}
                	insertSql.append(column.name());
                	valuesSql.append("#{").append(curField.getName()).append("}");
                	isFirst = false;
                }
            }
            
            //合并insertSql、valuesSql
            insertSql.append(valuesSql.toString()).append(")");
            log.info("Build insert sql:"+insertSql.toString());
            return insertSql.toString();
        } catch (Exception e) {  
        	throw new RuntimeException("Build insert sql is exceptoin:", e);  
        }

	}
	
	/**
	 * 装配update SQL
	 * @param entity
	 * @return
	 */
	public String update(Object entity) {
		if(entity instanceof Model) {
			Class<?> clazz = entity.getClass();
	        Table table = EntityUtil.getTable(clazz);
	        List<Field> listFields = EntityUtil.getFields(clazz);
	        
	        StringBuilder updateSql = new StringBuilder();
	        //装配UPDATE 语句
	        updateSql.append(" UPDATE ").append(table.name()).append(" SET ");

        	//装配Set 语句
	        try {
				boolean isFirst = true;							//是否第一个Set字段
	            for (Field curField : listFields) {
	                Column column = curField.getAnnotation(Column.class);
	                if (column!=null && column.updatable()) {
		                if (StrUtil.isEmpty(column.name())) {
		                	log.error("默认Column规则,待后续补充支持！");
		                	throw new RuntimeException("未定义");
		                }
		            	if(!isFirst) {
		            		updateSql.append(", \t");
		            	}
		                updateSql.append(column.name()).append("=#{").append(curField.getName()).append("}");
		                isFirst = false;
	                }
	            }  
	        } catch (Exception e) {  
	            new RuntimeException("Build update sql is exceptoin:" + e);  
	        }
	        
	        //装配Where 条件
	        updateSql.append(" WHERE id=#{id} and version=#{version}");
	        
            log.info("Build update sql:"+updateSql.toString());
	        return updateSql.toString();
		}else {
			log.error("装配updateSql出错，无效的Entity类型:"+entity.getClass()+"，应为Model实现类!");
			throw new RuntimeException("无效的Entity类型，应为Model实现类！");
		}
	}
	
	/**
	 * 装配delete SQL
	 * @param entity
	 * @return
	 */
	public String delete(Object entity) {
		if(entity instanceof Model) {
	        Class<?> clazz = entity.getClass();
	        Table table = EntityUtil.getTable(clazz);
			
	        StringBuilder deleteSql = new StringBuilder();
	        deleteSql.append(" DELETE FROM ").append(table.name())
	        		 .append(" WHERE id=#{id}");
	        
	        return deleteSql.toString();
		}else {
			log.error("装配deleteSql出错，无效的Entity类型:"+entity.getClass()+"，应为Model实现类!");
			throw new RuntimeException("无效的Entity类型，应为Model实现类！");
		}
	}
	
	/**
	 * 装配查询 SQL【非分页】
	 * @param entity
	 * @return
	 */
	public String queryList(Object entity) {
		//待补充
		return null;
	}

}