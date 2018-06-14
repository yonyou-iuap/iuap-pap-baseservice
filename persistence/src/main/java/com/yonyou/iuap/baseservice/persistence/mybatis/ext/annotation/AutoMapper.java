package com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.SQLType;

public @interface AutoMapper {

	public SQLType type();

}