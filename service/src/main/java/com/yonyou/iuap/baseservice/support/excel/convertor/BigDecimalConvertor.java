package com.yonyou.iuap.baseservice.support.excel.convertor;

import java.math.BigDecimal;

public class BigDecimalConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return BigDecimal.class;
	}

	@Override
	public Object convert(Object cellValue) {
		if(cellValue == null) {
			return null;
		}
		if(cellValue.getClass() == BigDecimal.class) {
			return cellValue;
		}else {
			return new BigDecimal(String.valueOf(cellValue));
		}
	}

}
