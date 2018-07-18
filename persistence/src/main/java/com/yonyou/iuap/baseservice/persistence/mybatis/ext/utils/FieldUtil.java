package com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import javax.persistence.Column;
import javax.persistence.Transient;
import cn.hutool.core.util.StrUtil;

import com.yonyou.iuap.baseservice.entity.annotation.ReferValue;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.converter.ConvertorHolder;
import com.yonyou.iuap.baseservice.support.condition.Condition;

public class FieldUtil {
	
	public static String getColumnName(Field field) {
		return ConvertorHolder.inst().getConvertor().field2Column(field);
	}
	
	public static String build4Mybatis(Field field) {
		String jdbcType = TypeMaping.getJdbcType(field.getType());
		if(jdbcType!=null) {
			return new StringBuilder("#{").append(field.getName())
					.append(", jdbcType=").append(jdbcType)
					.append("}").toString();
		}else {
			return new StringBuilder("#{").append(field.getName())
					.append("}").toString();
		}
	}
	
	public static String buildVersionField4Mybatis(Field field) {
		String jdbcType = TypeMaping.getJdbcType(field.getType());
		if(jdbcType!=null) {
			ReferValue newTs = field.getAnnotation(ReferValue.class);
			if(newTs == null) {
				return new StringBuilder("#{").append(field.getName())
						.append(", jdbcType=").append(jdbcType).append("}").toString();
			} else {
				return new StringBuilder("#{").append(newTs.value())
						.append(", jdbcType=").append(jdbcType).append("}").toString();
			}
		}else {
			ReferValue newTs = field.getAnnotation(ReferValue.class);
			if(newTs == null) {
				return new StringBuilder("#{").append(field.getName())
						.append("}").toString();
			} else {
				return new StringBuilder("#{").append(newTs.value())
						.append("}").toString();
			}
		}
	}
	
	public static String build4Mybatis(String prefix, Field field) {
		if(StrUtil.isBlank(prefix)) {
			return build4Mybatis(field);
		}else {
			String jdbcType = TypeMaping.getJdbcType(field.getType());
			if(jdbcType!=null) {
				return new StringBuilder("#{").append(prefix).append(".").append(field.getName())
						.append(", jdbcType=").append(jdbcType)
						.append("}").toString();
			}else {
				return new StringBuilder("#{").append(prefix).append(".").append(field.getName())
						.append("}").toString();
			}
		}
	}
	
	public static boolean isSupportedVariable(Field field) {
		return TypeMaping.getJdbcType(field.getType())!=null || field.getType()==Serializable.class;
	}

	/**
	 * 判断字段是否可作为条件
	 * @param field
	 * @return
	 */
	public static boolean isCondition(Field field) {
		return field.getAnnotation(Condition.class)!=null;
	}
	
	/**
	 * 判断字段是否可查询
	 * @param field
	 * @return
	 */
	public static boolean isSelectable(Field field) {
		if(field.getAnnotation(Transient.class) != null) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * 判断字段是否可新增
	 * @param field
	 * @return
	 */
	public static boolean insertable(Field field) {
		if(field.getAnnotation(Transient.class) != null) {
			return false;
		}else {
			Column column = field.getAnnotation(Column.class);
	        if(column != null) {
	        	return column.insertable();
	        }else {
	        	return FieldUtil.isSupportedVariable(field);
	        }
		}
	}
	
	/**
	 * 判断字段是否可更新
	 * @param field
	 * @return
	 */
	public static boolean updateable(Field field) {
		if(field.getAnnotation(Transient.class) != null) {
			return false;
		}else {
	        Column column = field.getAnnotation(Column.class);
	        if(column != null) {
	        	return column.updatable();
	        }else {
	        	return FieldUtil.isSupportedVariable(field);
	        }
		}
	}

}