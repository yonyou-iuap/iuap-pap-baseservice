package com.yonyou.iuap.baseservice.support.excel.convertor;

public class LongConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return long.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return 0l;
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
