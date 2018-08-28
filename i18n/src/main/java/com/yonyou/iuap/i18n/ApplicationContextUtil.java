package com.yonyou.iuap.i18n;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 使用框架上下文，主要获取资源文件
 * 
 * @author wenfa
 *
 */
public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext context ;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context  = applicationContext;
	}
}