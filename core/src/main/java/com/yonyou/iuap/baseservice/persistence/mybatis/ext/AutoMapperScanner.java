package com.yonyou.iuap.baseservice.persistence.mybatis.ext;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.Configuration;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.DefaultMapperBuilder;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import com.yonyou.iuap.mybatis.anotation.MyBatisRepository;

import cn.hutool.core.util.ClassUtil;

public class AutoMapperScanner {
	
	private Logger log = LoggerFactory.getLogger(AutoMapperScanner.class);

	public static AutoMapperScanner inst() {
		return Inner.scanner;
	}

	/**
	 * 扫描、解析并注册MyBatis
	 * @param basePackages 扫描包的名称,支持多个
	 */
	public void scan(String[] basePackages, Configuration configuration) {
		for (String basePackage : basePackages) {
			int pos = basePackage.indexOf("*");
			String curPackage = basePackage.substring(0, pos-1);
			Set<Class<?>> clazzSet = ClassUtil.scanPackageBySuper(curPackage, GenericMapper.class);
			Iterator<Class<?>> itor = clazzSet.iterator();
			while(itor.hasNext()) {
				this.parseMapper(itor.next(), configuration);
			}
			//Set<Class<?>> clazzSet = ClassUtil.scanPackageBySuper(basePackage, GenericMapper.class);
			//Iterator<Class<?>> itor = clazzSet.iterator();
			//while(itor.hasNext()) {
			//	this.parseMapper(itor.next(), configuration);
			//}
		}
		//parsePendingMethods();
	}
	
	/**
	 * 扫描、解析并注册MyBatis
	 */
	public void scanByAnnotation(ApplicationContext ctx, Configuration configuration) {
		Map<String, Object> beans = ctx.getBeansWithAnnotation(MyBatisRepository.class);
		Set<String> keyset =beans.keySet();
		Iterator<String> itor = keyset.iterator();
		while(itor.hasNext()) {
			Object mapperBean = beans.get(itor.next());
			if(mapperBean instanceof MapperProxy) {

			}else {
				this.parseMapper(mapperBean.getClass(), configuration);
			}
		}
		//parsePendingMethods();
	}
	
	/**
	 * 解析Mapper
	 * @param clazz mapper的class
	 * @param configuration mybatis全局配置封装
	 */
	private void parseMapper(Class<?> clazz, Configuration configuration) {
		if(clazz.getAnnotation(MyBatisRepository.class)!=null) {
			if (!configuration.isResourceLoaded(clazz.getName())){
				configuration.addLoadedResource(clazz.getName());
				log.debug("开始解析Mapper:"+clazz.getName());
			}else {
				log.warn("Mapper已存在，skipped...."+clazz.getName());
			}
			
			DefaultMapperBuilder statementBuilder = DefaultMapperBuilder.instance(configuration);
			statementBuilder.parseMapper(clazz);
		}
	}
	
	/******************************************/
	private static class Inner {
		private static AutoMapperScanner scanner = new AutoMapperScanner();
	}

}