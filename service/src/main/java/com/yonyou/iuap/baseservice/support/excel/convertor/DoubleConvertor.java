package com.yonyou.iuap.baseservice.support.excel.convertor;

public class DoubleConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return double.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return 0d;
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
