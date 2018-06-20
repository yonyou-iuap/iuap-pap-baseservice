package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.AutoMapperFactory;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public class OracleMapperFactory implements AutoMapperFactory{
	
	private Logger log = LoggerFactory.getLogger(OracleMapperFactory.class);
	
	private Integer isInit = new Integer(0);
	private ReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	private Map<SqlCommandType, SqlTemplate> sqlTemplateMap = new HashMap<SqlCommandType, SqlTemplate>();
	
	public OracleMapperFactory() {}
	
	public void initSqlTemplate() {
		SqlTemplate deleteTemplate = new OracleDeleteTemplate();
		SqlTemplate updateTemplate = new OracleUpdateTemplate();
		SqlTemplate insertTemplate = new OracleInsertTemplate();
		SqlTemplate selectTemplate = new OracleSelectTemplate();
		sqlTemplateMap.put(deleteTemplate.getSQLType(), deleteTemplate);
		sqlTemplateMap.put(updateTemplate.getSQLType(), updateTemplate);
		sqlTemplateMap.put(insertTemplate.getSQLType(), insertTemplate);
		sqlTemplateMap.put(selectTemplate.getSQLType(), selectTemplate);
		log.info("初始化SqlTemple成功,已加载insertTemplate,updateTemplate,deleteTemplate,selectTemplate!");
	}
	
	@Override
	public Dialect getDialect() {
		return Dialect.oracle;
	}

	@Override
	public SqlTemplate getSqlTempalte(SqlCommandType sqlType) {
		if(isInit == 0) {
			LOCK.writeLock().lock();
			try {
				if(isInit == 0) {
					this.initSqlTemplate();
					isInit = 1;
				}
			}finally {
				LOCK.writeLock().unlock();
			}
		}
		return sqlTemplateMap.get(sqlType);
	}

	@Override
	public String parseSQL4Insert(Method method, Class<?> entityClazz) {
		return this.getSqlTempalte(SqlCommandType.INSERT).parseSQL(method, entityClazz);
	}

	@Override
	public String parseSQL4Update(Method method, Class<?> entityClazz) {
		return this.getSqlTempalte(SqlCommandType.UPDATE).parseSQL(method, entityClazz);
	}

	@Override
	public String parseSQL4Delete(Method method, Class<?> entityClazz) {
		return this.getSqlTempalte(SqlCommandType.DELETE).parseSQL(method, entityClazz);
	}

	@Override
	public String parseSQL4Select(Method method, Class<?> entityClazz) {
		return this.getSqlTempalte(SqlCommandType.SELECT).parseSQL(method, entityClazz);
	}

}