package com.yonyou.iuap.baseservice.support.generator.impl;

import java.io.Serializable;
import java.util.UUID;

import com.yonyou.iuap.baseservice.support.generator.Generator;
import com.yonyou.iuap.baseservice.support.generator.Strategy;

public class UUIDGenerator implements Generator{

	@Override
	public Strategy strategy() {
		return Strategy.UUID;
	}
	
	public String name() {
		return "uuid";
	}

	@Override
	public Serializable generate(String module) {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
