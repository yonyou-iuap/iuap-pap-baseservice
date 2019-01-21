package com.yonyou.iuap.baseservice.support.generator;

import java.io.Serializable;

public interface Generator {
	
	public Strategy strategy();
	
	public String name();
	
	public Serializable generate(String module, Class<?> entityClazz);

}