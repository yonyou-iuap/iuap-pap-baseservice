package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.MappedStatement;

public interface StatementBuilder {

	MappedStatement parseStatement(Method method);

}