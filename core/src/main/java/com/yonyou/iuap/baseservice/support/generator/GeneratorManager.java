package com.yonyou.iuap.baseservice.support.generator;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.Model;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

public class GeneratorManager {
	
	private Logger log = LoggerFactory.getLogger(GeneratorManager.class);
	
	private Map<Strategy, Generator> generatorMap = new HashMap<Strategy, Generator>();
	private Map<String, Generator> generatorBeans = new HashMap<String, Generator>();
	private volatile Boolean isInited = new Boolean(false);			//一定要使用new
	
	/**
	 * 初始化
	 */
	public void init() {
		log.info("开始加载注册ID Generator!");
		ServiceLoader<Generator> services = ServiceLoader.load(Generator.class);
		for(Generator generator : services) {
			this.regist(generator);
		}
	}
	
	/**
	 * 注册Generator
	 * @param generator
	 */
	public void regist(Generator generator) {
		if(generatorBeans.containsKey(generator.name())) {
			log.warn("ID Generator["+generator.name()+"] Bean已存在，不再加载："+generator.getClass());
		}else {
			generatorBeans.put(generator.name(), generator);
			log.info("ID Generator["+generator.name()+"] Bean加载并注册成功："+generator.getClass());
		}
		
		if(generator.strategy()!=Strategy.CUSTOM && !generatorMap.containsKey(generator.strategy())) {
			generatorMap.put(generator.strategy(), generator);
			log.info("ID Generator["+generator.strategy()+"]策略加载并注册成功："+generator.getClass());
		}else {
			log.warn("ID Generator["+generator.strategy()+"]已存在，不再加载："+generator.getClass());
		}
	}
	
	/**
	 * 清理Generator
	 * @param generator
	 */
	public void clean(Generator generator) {
		if(StrUtil.isBlankIfStr(generator.name())) {
			generatorBeans.remove(generator.name());
		}else {
			generatorBeans.remove(generator.getClass().getName());
		}
		generatorMap.remove(generator.strategy());
		log.info("ID Generator["+generator.strategy()+"]已注销："+generator.getClass());
	}
	
	/**
	 * 生成
	 * @param strategy
	 * @param module
	 * @param clazz
	 * @return
	 */
	public Serializable generate(Class<?> entityClazz, Strategy strategy, String module, String clazz) {
		if(!StrUtil.isBlankIfStr(clazz)) {
			return generatorBeans.get(clazz).generate(module, entityClazz);
		}else if(strategy != null){
			return generatorMap.get(strategy).generate(module, entityClazz);
		}else {
			throw new RuntimeException("无效的ID生成策略Annotation!");
		}
	}
	
	public static GeneratorManager getInstance() {
		if(!Inner.generatorManager.isInited) {
			synchronized (Inner.generatorManager.isInited) {
				if(!Inner.generatorManager.isInited) {
					Inner.generatorManager.init();
					Inner.generatorManager.isInited = true;
				}
			}
		}
		return Inner.generatorManager;
	}
	
	/**
	 * 生成ID，并赋值
	 * @param entity
	 * @return
	 */
	public static Serializable generateID(Model entity) {
		Field field = ReflectUtil.getField(entity.getClass(), "id");
		if(field != null) {
			GeneratedValue annotation = field.getAnnotation(GeneratedValue.class);
			if(annotation!=null) {
				return getInstance().generate(entity.getClass(), annotation.strategy(), 
										annotation.module(), annotation.clazz());
			}else {
				throw new RuntimeException("Not found Annotation:@GeneratedValue in Class:" + entity.getClass());
			}
		}else {
			throw new RuntimeException("未找到生成策略field【id】，无效的Class:"+entity);
		}
	}
	
	/**
	 * 生成UUID
	 * @return
	 */
	public static String generateUUID() {
		return (String)getInstance().generate(null, Strategy.UUID, "", null);
	}
	
	/*********************************************************/
	private static class Inner {
		private static GeneratorManager generatorManager = new GeneratorManager();
	}

}