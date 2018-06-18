package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter;

public interface MapperBuilder {

	/**
	 * 解析Mapper接口，根据方法生成、注册SQL
	 * @param mapperClazz
	 */
	public void parseMapper(Class<?> mapperClazz);

}