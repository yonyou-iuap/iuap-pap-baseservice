package com.yonyou.iuap.baseservice.persistence.mybatis.ext;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

@Component
public class AutoMapperLoader{
	
    private SqlSessionFactory sqlSessionFactory;
    private String basePackage;

	@PostConstruct
    public void init() {
    	
    	if(StrUtil.isBlankIfStr(basePackage)) {
    		return;
    	}
    	String[] basePackages = basePackage.split(",");
    	
    	//获取Mybatis配置类
    	Configuration configuration = sqlSessionFactory.getConfiguration();
        
    	//创建AutoMapper扫描器
    	AutoMapperScanner scanner = AutoMapperScanner.inst();
    	
    	//扫描符合所有的Mapper，生成Mybatis SQL，并创建、注册至Mybatis统一管理
    	scanner.scan(basePackages, configuration);
    	//scanner.scanByAnnotation(ctx, configuration);
    }
	
	/***************************************************************************/
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	@Autowired
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
}