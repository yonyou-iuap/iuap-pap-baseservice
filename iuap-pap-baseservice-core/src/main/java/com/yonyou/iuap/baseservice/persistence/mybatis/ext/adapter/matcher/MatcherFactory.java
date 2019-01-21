package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.yonyou.iuap.baseservice.support.condition.Match;

public class MatcherFactory {

	private static Map<Match,Matcher> matcherMap; 
	
	public static Matcher getMatcher(Match match) {		
		if(matcherMap == null) {
			matcherMap = new HashMap<Match,Matcher>();
			ServiceLoader<Matcher> matcherLoader = ServiceLoader.load(Matcher.class);
			for(Matcher curMatcher: matcherLoader) {
				matcherMap.put(curMatcher.getMatch(), curMatcher);
			}
		}
		return matcherMap.get(match);
	}

}
