package com.yonyou.iuap.baseservice.support.excel.convertor;

import cn.hutool.core.util.StrUtil;

public class FloatObjectConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return Float.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return null;
		}
		if(cellValue.getClass() == float.class) {
			return cellValue;
		}else if(cellValue instanceof Float) {
			return ((Float) cellValue).floatValue();
		}else if(cellValue instanceof String) {
			if(StrUtil.isBlankIfStr(cellValue)) {
				return null;
			}
			return Float.parseFloat((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
