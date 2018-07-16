package com.yonyou.iuap.baseservice.support.excel.convertor;

import java.util.Date;

public class DateConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return Date.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return null;
		}
		if(cellValue instanceof Date) {
			return cellValue;
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
