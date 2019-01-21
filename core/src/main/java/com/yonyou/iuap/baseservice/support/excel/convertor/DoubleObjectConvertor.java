package com.yonyou.iuap.baseservice.support.excel.convertor;

import cn.hutool.core.util.StrUtil;

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
			if(StrUtil.isBlankIfStr(cellValue)) {
				return null;
			}
			return Double.parseDouble((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
