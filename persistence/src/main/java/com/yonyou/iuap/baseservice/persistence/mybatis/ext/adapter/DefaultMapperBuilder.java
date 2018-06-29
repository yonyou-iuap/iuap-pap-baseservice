package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

import java.lang.reflect.Method;
import java.util.ServiceLoader;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation.MethodMapper;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.MappedStatementHelper;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.MapperUtil;
import com.yonyou.iuap.utils.PropertyUtil;

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
					mapperBuilder.setConfiguration(configuration);
					mapperBuilder.createMapperFactory();
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
				if(configuration.hasStatement(statement.getId())) {
					log.warn("忽略加载Mybatis Mapper方法，该方法已经加载:" + statement.getId());
				}else {
					configuration.addMappedStatement(statement);
				}
			}
		}
	}
	
	private MappedStatement parseStatement(Method method, Class<?> entityClazz, Class<?> mapperClazz) {
		LanguageDriver driver = configuration.getDefaultScriptingLanuageInstance();
		MethodMapper methodMapper = method.getAnnotation(MethodMapper.class);
		if(methodMapper != null) {
			String sql = this.buildSql(method, methodMapper, entityClazz);
			log.debug("Auto generate SQL for Mapper:\r\n" + sql);
			if(!StrUtil.isBlank(sql)) {
				SqlSource sqlSource = driver.createSqlSource(configuration, sql, Object.class);
				String statementId = mapperClazz.getName() + "." + method.getName();
				Builder builder = new Builder(configuration, statementId, sqlSource, methodMapper.type());
				//添加Result
				if(methodMapper.type() == SqlCommandType.SELECT) {
					MappedStatementHelper.addResultMap(builder, method, entityClazz, statementId, configuration);
				}
				//设置资源信息
				String resource = MappedStatementHelper.getResource(method);
				builder.resource(resource).lang(driver).statementType(StatementType.PREPARED);
				MappedStatement mappedStatement = builder.build();
				return mappedStatement;
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
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	private AutoMapperFactory createMapperFactory() {
		String jdbcType = PropertyUtil.getPropertyByKey("jdbc.type");
		if(StrUtil.isBlankIfStr(jdbcType)) {
			log.error("无效的jdbc.type:"+jdbcType);
			throw new RuntimeException("无效的jdbc.type:"+jdbcType);
		}
		ServiceLoader<AutoMapperFactory> serviceloader = ServiceLoader.load(AutoMapperFactory.class);
		for(AutoMapperFactory mapperFactory: serviceloader) {
			if(mapperFactory.getDialect().getType().equalsIgnoreCase(jdbcType)) {
				this.mapperFactory = mapperFactory;
				return this.mapperFactory;
			}
		}
		log.warn("发现可用的..MapperFactory!");
		return null;
	}
	
}