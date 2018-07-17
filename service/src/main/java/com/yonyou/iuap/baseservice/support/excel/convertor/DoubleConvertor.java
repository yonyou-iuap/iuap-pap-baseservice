package com.yonyou.iuap.baseservice.support.excel.convertor;

import cn.hutool.core.util.StrUtil;

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
			if(StrUtil.isBlankIfStr(cellValue)) {
				return 0d;
			}
			return Double.parseDouble((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
