package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.yonyou.iuap.utils.PropertyUtil;

public class ConvertorHolder {
	
	private Integer isInit = new Integer(0);
	private Map<String, NamingConvertor> convertHolder = new HashMap<String, NamingConvertor>();
	
	private ConvertorHolder() {}
	
	public static ConvertorHolder inst() {
		if(Inner.INST.isInit == 0) {
			synchronized(Inner.INST.convertHolder) {
				if(Inner.INST.isInit == 0) {
					Inner.INST.loadConvertor();
				}
				Inner.INST.isInit = 1;
			}
		}
		return Inner.INST;
	}
	
	public void loadConvertor() {
		ServiceLoader<NamingConvertor> convertors = ServiceLoader.load(NamingConvertor.class);
        for(NamingConvertor convertor : convertors) {
        	convertHolder.put(convertor.getType().getType(), convertor);
        }
	}
	
	public NamingConvertor getConvertor() {
		String convertor = PropertyUtil.getPropertyByKey("iuap.pap.mapper.strategy", "hump");
		return convertHolder.get(convertor);
	}
	
	/*************************************************/
	private static class Inner{
		private static ConvertorHolder INST = new ConvertorHolder();
	}

}
