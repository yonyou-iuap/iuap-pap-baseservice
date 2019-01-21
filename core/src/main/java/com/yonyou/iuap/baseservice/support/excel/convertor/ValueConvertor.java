package com.yonyou.iuap.baseservice.support.excel.convertor;

public interface ValueConvertor {
	
	public Class<?> getType();
	
	public Object convert(Object cellValue);

}