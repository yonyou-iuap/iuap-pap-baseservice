package com.yonyou.iuap.baseservice.persistence.mybatis.ext;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;


public class AutoMapperLoader {
	
    private SqlSessionFactory sqlSessionFactory;
	private String[] basePackages = new String[] {"com.yonyou.iuap.pap.core.dao"};

	public AutoMapperLoader(SqlSessionFactory sessionFactory) {
		this.sqlSessionFactory = sessionFactory;
	}
    
    public void init() {
    	//获取Mybatis配置类
    	Configuration configuration = sqlSessionFactory.getConfiguration();
        
    	//创建AutoMapper扫描器
    	AutoMapperScanner scanner = AutoMapperScanner.inst();
    	
    	//扫描符合所有的Mapper，生成Mybatis SQL，并创建、注册至Mybatis统一管理
    	scanner.scan(basePackages, configuration);
    }
    

	
}
