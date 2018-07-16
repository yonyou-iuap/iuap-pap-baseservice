package com.yonyou.iuap.baseservice.support.excel.convertor;

public class DoubleObjectConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return Double.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return null;
		}
		if(cellValue.getClass() == double.class) {
			return cellValue;
		}else if(cellValue instanceof Double) {
			return ((Double) cellValue).doubleValue();
		}else if(cellValue instanceof String) {
			return Double.parseDouble((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
