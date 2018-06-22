package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.converter;

import java.lang.reflect.Field;
import javax.persistence.Column;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.NamingUtil;

import cn.hutool.core.util.StrUtil;

public class HumpConvertor implements NamingConvertor{
	
	@Override
	public Convertor getType() {
		return Convertor.HUMP;
	}

	@Override
	public String field2Column(Field field) {
		Column column = field.getAnnotation(Column.class);
		if(column!=null && !StrUtil.isBlank(column.name())) {
			return column.name();
		}else {
			return NamingUtil.humpToline(field.getName());
		}
	}

	@Override
	public String column2Field(String column) {
		throw new RuntimeException();
	}
	
}