package com.yonyou.iuap.baseservice.support.excel.convertor;

import cn.hutool.core.util.StrUtil;

public class IntegerConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return Integer.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue==null) {
			return 0;
		}
		if(cellValue.getClass() == int.class) {
			return cellValue;
		}else if(cellValue instanceof Integer){
			return ((Integer) cellValue).intValue();
		}else if(cellValue instanceof String) {
			if(StrUtil.isBlankIfStr(cellValue)) {
				return null;
			}
			return Integer.parseInt((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
