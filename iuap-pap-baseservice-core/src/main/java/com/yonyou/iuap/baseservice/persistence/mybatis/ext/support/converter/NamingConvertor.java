package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.converter;

import java.lang.reflect.Field;

public interface NamingConvertor {
	
	public Convertor getType();
	
	public String field2Column(Field field);
	
	public String column2Field(String column);

}