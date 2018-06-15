package com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation;

import org.apache.ibatis.mapping.SqlCommandType;

public @interface MethodMapper {

	public SqlCommandType type();

}