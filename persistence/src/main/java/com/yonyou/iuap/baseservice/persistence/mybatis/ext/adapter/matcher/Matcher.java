package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher;

import java.lang.reflect.Field;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Match;

public interface Matcher {
	
	public Match getMatch();
	
	public String buildCondition(Field field, String prefix);

}
