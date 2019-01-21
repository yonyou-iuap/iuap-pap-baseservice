package com.yonyou.iuap.baseservice.support.excel.convertor;

import cn.hutool.core.util.StrUtil;

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
			if(StrUtil.isBlankIfStr(cellValue)) {
				return 0f;
			}
			return Float.parseFloat((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
