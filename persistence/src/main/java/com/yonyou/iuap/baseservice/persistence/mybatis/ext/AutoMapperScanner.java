package com.yonyou.iuap.baseservice.persistence.mybatis.ext;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.DefaultMapperBuilder;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import cn.hutool.core.util.ClassUtil;

public class AutoMapperScanner {
	
	private Logger log = LoggerFactory.getLogger(AutoMapperScanner.class);

	public static AutoMapperScanner inst() {
		return Inner.scanner;
	}

	/**
	 * 扫描、解析并注册MyBatis
	 * @param basePackages
	 */
	public void scan(String[] basePackages, Configuration configuration) {
		for (String basePackage : basePackages) {
			Set<Class<?>> clazzSet = ClassUtil.scanPackageBySuper(basePackage, GenericMapper.class);
			Iterator<Class<?>> itor = clazzSet.iterator();
			while(itor.hasNext()) {
				this.parseMapper(itor.next(), configuration);
			}
		}
		//parsePendingMethods();
	}
	
	/**
	 * 解析Mapper
	 * @param clazz
	 * @param configuration
	 */
	private void parseMapper(Class<?> clazz, Configuration configuration) {
		if (!configuration.isResourceLoaded(clazz.getName())){
			configuration.addLoadedResource(clazz.getName());
			log.debug("开始解析Mapper:"+clazz.getName());
		}else {
			log.warn("Mapper已存在，skipped...."+clazz.getName());
		}
		
		DefaultMapperBuilder statementBuilder = DefaultMapperBuilder.instance(configuration);
		statementBuilder.parseMapper(clazz);
	}
	
	/******************************************/
	private static class Inner {
		private static AutoMapperScanner scanner = new AutoMapperScanner();
	}

}