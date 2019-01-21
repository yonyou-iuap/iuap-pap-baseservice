package com.yonyou.iuap.baseservice.support.excel.convertor;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ConvertorHolder {
	
	private Boolean isInit = new Boolean(false);
	
	private Map<Class<?>, ValueConvertor> convertorMap = new HashMap<Class<?>, ValueConvertor>();
	
	private ConvertorHolder() {}
	
	public static ConvertorHolder inst() {
		if(!Inner.inst.isInit) {
			synchronized(Inner.inst) {
				if(!Inner.inst.isInit.booleanValue()) {
					Inner.inst.init();
				}
			}
		}
		return Inner.inst;
	}
	
	private void init() {
		ServiceLoader<ValueConvertor> spiLoader = ServiceLoader.load(ValueConvertor.class);
		spiLoader.forEach(convertor->{
			convertorMap.put(convertor.getType(), convertor);
		});
	}
	
	public ValueConvertor getConvertor(Class<?> clazz) {
		return convertorMap.get(clazz);
	}

	public Object convertValue(Class<?> clazz, Object value) {
		return convertorMap.get(clazz).convert(value);
	}
	
	private static class Inner {
		private static ConvertorHolder inst = new ConvertorHolder();
	}
}
