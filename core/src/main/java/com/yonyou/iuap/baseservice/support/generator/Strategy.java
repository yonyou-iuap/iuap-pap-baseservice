package com.yonyou.iuap.baseservice.support.generator;

public enum Strategy {
	
	UUID,		//UUID
	AUTOINC,		//表自增
	CUSTOM, 	//用户自定义
	CONFIG;		//Properties配置文件
		
	private Strategy() {}

}
