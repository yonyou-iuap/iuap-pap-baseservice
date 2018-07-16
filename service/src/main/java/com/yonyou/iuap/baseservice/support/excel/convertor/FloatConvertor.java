package com.yonyou.iuap.baseservice.support.excel.convertor;

public class FloatConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return float.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return 0f;
		}
		if(cellValue.getClass() == float.class) {
			return cellValue;
		}else if(cellValue instanceof Double) {
			return ((Double)cellValue).floatValue();
		}else if(cellValue instanceof Float) {
			return ((Float) cellValue).floatValue();
		}else if(cellValue instanceof String) {
			return Float.parseFloat((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
