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
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.MapperUtil;

import cn.hutool.core.util.StrUtil;

public class DefaultMapperBuilder implements MapperBuilder{

	private Logger log = LoggerFactory.getLogger(DefaultMapperBuilder.class);
	
	private static Integer isInit = new Integer(0);	
	private static DefaultMapperBuilder mapperBuilder;
	private AutoMapperFactory mapperFactory;	
	private Configuration configuration;
	
	private DefaultMapperBuilder() {};
	
	public static DefaultMapperBuilder instance(Configuration configuration) {
		if(isInit == 0) {
			synchronized(isInit) {
				if(isInit==0) {
					mapperBuilder = new DefaultMapperBuilder();
					mapperBuilder.configuration = configuration;
					mapperBuilder.mapperFactory = new MysqlMapperFactory();
					isInit = 1;
				}
			}
		}
		return mapperBuilder;
	}

	@Override
	public void parseMapper(Class<?> mapperClazz) {
		Class<?> genericClass = MapperUtil.getGenericClass(mapperClazz);
		if(genericClass == null) {
			log.warn("未找到泛型，无法进行AutoMapper,Class="+mapperClazz.getName());
			return;
		}
		
		Method[] methods = mapperClazz.getMethods();
		for(Method curMethod : methods) {
			MappedStatement statement = this.parseStatement(curMethod, genericClass, mapperClazz);
			if(statement != null) {
				configuration.addMappedStatement(statement);
			}
		}
	}
	
	private MappedStatement parseStatement(Method method, Class<?> entityClazz, Class<?> mapperClazz) {
		LanguageDriver driver = configuration.getDefaultScriptingLanuageInstance();
		MethodMapper methodMapper = method.getAnnotation(MethodMapper.class);
		if(methodMapper != null) {
			String sql = this.buildSql(method, methodMapper, entityClazz);
			System.out.println(sql);
			if(!StrUtil.isBlank(sql)) {
				SqlSource sqlSource = driver.createSqlSource(configuration, sql, Object.class);
				String statementId = mapperClazz.getName() + "." + method.getName();
				Builder builder = new Builder(configuration, statementId, sqlSource, methodMapper.type());
				String resource = this.getResource(method);
				builder.resource(resource).lang(driver).statementType(StatementType.PREPARED);
				return builder.build();
			}
		}
		return null;
	}
	
	private String buildSql(Method method, MethodMapper methodMapper, Class<?> entityClazz) {
		if(methodMapper != null) {
			if(methodMapper.type() == SqlCommandType.SELECT) {
				return this.mapperFactory.parseSQL4Select(method, entityClazz);
			}else if(methodMapper.type() == SqlCommandType.UPDATE) {
				return this.mapperFactory.parseSQL4Update(method, entityClazz);
			}else if(methodMapper.type() == SqlCommandType.INSERT) {
				return this.mapperFactory.parseSQL4Insert(method, entityClazz);
			}else if(methodMapper.type() == SqlCommandType.DELETE) {
				return this.mapperFactory.parseSQL4Delete(method, entityClazz);
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
	
}