package com.yonyou.iuap.baseservice.support.excel.convertor;

public class StringConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return String.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return null;
		}
		if(cellValue instanceof String) {
			return cellValue;
		}else {
			return String.valueOf(cellValue);
		}
	}

}
