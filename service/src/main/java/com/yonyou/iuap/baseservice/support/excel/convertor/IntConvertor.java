package com.yonyou.iuap.baseservice.support.excel.convertor;

import cn.hutool.poi.excel.cell.CellValue;

public class IntConvertor implements ValueConvertor{

	@Override
	public Class<?> getType() {
		return int.class;
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
		}else if(cellValue instanceof Long) {
			return ((Long) cellValue).intValue();
		}else if(cellValue instanceof String) {
			return Integer.parseInt((String)cellValue);
		}else {
			throw new RuntimeException("cellValue转换出错:"+cellValue+", 数据类型="+CellValue.class);
		}
	}

}