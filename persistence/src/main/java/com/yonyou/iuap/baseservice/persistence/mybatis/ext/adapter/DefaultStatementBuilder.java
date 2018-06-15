package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql.MysqlMapperFactory;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation.MethodMapper;

import cn.hutool.core.util.StrUtil;

public class DefaultStatementBuilder implements StatementBuilder{

	private Logger log = LoggerFactory.getLogger(DefaultStatementBuilder.class);

	private Configuration configuration;
	
	public DefaultStatementBuilder(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public MappedStatement parseStatement(Method method) {
		LanguageDriver driver = configuration.getDefaultScriptingLanuageInstance();
		MethodMapper methodMapper = method.getAnnotation(MethodMapper.class);
		if(methodMapper != null) {
			String sql = this.buildSql(method, methodMapper);
			if(!StrUtil.isBlank(sql)) {
				SqlSource sqlSource = driver.createSqlSource(configuration, sql, Object.class);
				String statementId = method.getDeclaringClass().getName() + "." + method.getName();
				Builder builder = new Builder(configuration, statementId, sqlSource, methodMapper.type());
				String resource = this.getResource(method);
				builder.resource(resource).lang(driver).statementType(StatementType.PREPARED);
				return builder.build();
			}
		}
		return null;
	}
	
	private String buildSql(Method method, MethodMapper methodMapper) {
		if(methodMapper != null) {
			if(methodMapper.type() == SqlCommandType.SELECT) {
				this.getMapperFactory().parseSQL4Select(method);
			}else if(methodMapper.type() == SqlCommandType.UPDATE) {
				this.getMapperFactory().parseSQL4Update(method);
			}else if(methodMapper.type() == SqlCommandType.INSERT) {
				this.getMapperFactory().parseSQL4Insert(method);
			}else if(methodMapper.type() == SqlCommandType.DELETE) {
				this.getMapperFactory().parseSQL4Delete(method);
			}else {
				log.warn("Invalid MethodMapper type:"+methodMapper.type());
			}
		}
		return null;
	}
	
	private String getResource(Method method) {
		Class<?> mapper = method.getDeclaringClass();
		return mapper.getName().replaceAll(".", "/") + ".java";
	}
	
	private AutoMapperFactory getMapperFactory() {
		return new MysqlMapperFactory();
	}

}