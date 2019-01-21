package com.yonyou.iuap.baseservice.support.excel.convertor;

import java.util.Date;

import cn.hutool.core.util.StrUtil;

public class DateConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return Date.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return null;
		}
		if(cellValue instanceof Date) {
			return cellValue;
		}else {
			if(StrUtil.isBlankIfStr(cellValue)) {
				return null;				
			}
			throw new RuntimeException("cellValue转换出错:"+cellValue);
		}
	}

}
