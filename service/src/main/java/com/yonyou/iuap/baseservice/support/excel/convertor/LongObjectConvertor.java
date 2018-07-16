package com.yonyou.iuap.baseservice.support.excel.convertor;

public class LongObjectConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return Long.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return null;
		}
		if(cellValue.getClass() == long.class) {
			return cellValue;
		}else if(cellValue instanceof Long) {
			return ((Long) cellValue).longValue();
		}else if(cellValue instanceof String) {
			return Long.parseLong((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
