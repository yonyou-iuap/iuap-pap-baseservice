package com.yonyou.iuap.baseservice.persistence.mybatis.ext.template;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle.byentity.OracleSqlGenerator;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：MyBatis CRUD 标准SQL构建器
 * @author houlf
 * 2018年6月12日
 */
public class SqlProvider {

	private Logger log = LoggerFactory.getLogger(SqlProvider.class);

	private static Map<String,String> sqlMap = new HashMap<String,String>();
	
	private static OracleSqlGenerator sqlFactory = new OracleSqlGenerator();
	
	/**
	 * 自动装配insert SQL
	 * @param entity
	 * @return
	 */
	public String insert(Object entity) {
		String sqlId = this.getSqlId(entity, "insert");
		String insertSql = sqlMap.get(sqlId);
		if(StrUtil.isBlank(insertSql)) {
			insertSql = sqlFactory.getSqlGenerator(SqlCommandType.INSERT).parseSQL(entity.getClass());
			sqlMap.put(sqlId, insertSql);
			log.info("已加载auto mapper sql:"+insertSql);
		}
		return insertSql;
	}
	
	private String getSqlId(Object entity, String method) {
		return entity.getClass()+"."+method;
	}
	
}