package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle.byentity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlGenerator;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public class OracleSqlGenerator {

	private Logger log = LoggerFactory.getLogger(OracleSqlGenerator.class);
	
	private Integer isInit = new Integer(0);
	private ReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	private Map<SqlCommandType, SqlGenerator> sqlGeneratorMap = new HashMap<SqlCommandType, SqlGenerator>();
	
	public OracleSqlGenerator() {}
	
	public void initSqlTemplate() {
		SqlGenerator deleteGenerator = new DeleteSqlByEntity();
		SqlGenerator updateGenerator = new UpdateSqlByEntity();
		SqlGenerator insertGenerator = new InsertSqlByEntity();
		SqlGenerator selectGenerator = new SelectSqlByEntity();
		sqlGeneratorMap.put(deleteGenerator.getSQLType(), deleteGenerator);
		sqlGeneratorMap.put(updateGenerator.getSQLType(), updateGenerator);
		sqlGeneratorMap.put(insertGenerator.getSQLType(), insertGenerator);
		sqlGeneratorMap.put(selectGenerator.getSQLType(), selectGenerator);
		log.info("初始化SqlTemple成功,已加载insertTemplate,updateTemplate,deleteTemplate,selectTemplate!");
	}
	
	public Dialect getDialect() {
		return Dialect.oracle;
	}

	public SqlGenerator getSqlGenerator(SqlCommandType sqlType) {
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
		return sqlGeneratorMap.get(sqlType);
	}

}