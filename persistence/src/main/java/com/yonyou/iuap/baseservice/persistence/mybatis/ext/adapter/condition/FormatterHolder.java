package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.condition;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 说明：Mybatis查询条件 格式化 工具
 * @author Aton
 * 2018年7月18日
 */
public class FormatterHolder {
	
	private Logger log = LoggerFactory.getLogger(FormatterHolder.class);

	private Map<String,Formatter> formaterMap;
	private boolean isInited = false;
	
	public static Formatter get(String formater) {
		if(!Inner.holder.isInited) {
			synchronized(Inner.holder) {
				if(!Inner.holder.isInited) {
					Inner.holder.init();
					Inner.holder.isInited = true;
				}
			}
		}
		return Inner.holder.formaterMap.get(formater);
	}
	
	private void init() {
		formaterMap = new HashMap<String,Formatter>();
		ServiceLoader<Formatter> formatterLoader = ServiceLoader.load(Formatter.class);
		for(Formatter curFormater: formatterLoader) {
			formaterMap.put(curFormater.getName(), curFormater);
		}
		log.info("The formatter of Ccondition was loaded successfully!");
	}
	
	/*********************************************************/
	private static class Inner {
		private static FormatterHolder holder = new FormatterHolder();
	}

}