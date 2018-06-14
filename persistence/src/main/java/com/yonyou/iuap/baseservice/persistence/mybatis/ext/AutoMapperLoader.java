package com.yonyou.iuap.baseservice.persistence.mybatis.ext;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("autoMapperLoader")
public class AutoMapperLoader {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void init() {
    	//获取Mybatis配置类
    	Configuration configuration = sqlSessionFactory.getConfiguration();
        
    	//创建AutoMapper扫描器
    	
    	//扫描符合所有的Mapper，生成Mybatis SQL，并创建、注册至Mybatis统一管理
    	
    	//Maven插件——
    }
    

	
}
