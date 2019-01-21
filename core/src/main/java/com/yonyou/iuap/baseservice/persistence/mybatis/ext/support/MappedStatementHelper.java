package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.mybatis.type.PageResult;

/**
 * 说明：MappedStatement构建帮助类
 * @author Aton
 * 2018年6月20日
 */
public class MappedStatementHelper {
	
	private static Logger log = LoggerFactory.getLogger(MappedStatementHelper.class);
	
	/**
	 * 添加补充-MappedStatement
	 * @param mapStatement
	 * @param returnType
	 */
	public static void addResultMap(Builder builder, Method method, Class<?> clazz, String statementId,Configuration configuration) {
		Type returnType = method.getGenericReturnType();
		if(returnType != null && returnType instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType)returnType).getRawType();
			if(((Class<?>)rawType).isAssignableFrom(java.util.List.class) || ((Class<?>)rawType).isAssignableFrom(PageResult.class)) {
				Type[] genericType = ((ParameterizedType)returnType).getActualTypeArguments();
				if(genericType.length != 1) {
					throw new MapperException("无法识别的泛型类型!");
				}else {
					if(genericType[0].getTypeName().length()==1) {
						ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration,
						          											statementId + "-Inline", clazz,
						          											new ArrayList<ResultMapping>(), null);
						List<ResultMap> resultMaps = new ArrayList<ResultMap>();
						resultMaps.add(inlineResultMapBuilder.build());
						builder.resultMaps(resultMaps);
						return;
					}else if(genericType[0].getTypeName().startsWith("java.util.Map")) {
						ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration,
																			statementId + "-Inline", java.util.LinkedHashMap.class,
																			new ArrayList<ResultMapping>(), null);
						List<ResultMap> resultMaps = new ArrayList<ResultMap>();
						resultMaps.add(inlineResultMapBuilder.build());
						builder.resultMaps(resultMaps);
						return;
					}else {
						log.error("返回类型非java.util.List，无法生成Mapper映射:" + rawType);
					}
				}
			}else {
				log.error("返回类型非java.util.List，无法生成Mapper映射:" + rawType);
			}
		}
		throw new MapperException("无法识别的返回类型!");
	}
	
	/**
	 * 获取ResultMaps
	 * @return
	 */
	public List<ResultMap> getResultMaps(){
		
		//ResultMap resultMap = 
		return null;
	}
	
	/**
	 * 获取资源文件信息
	 * @param method
	 * @return
	 */
	public static String getResource(Method method) {
		Class<?> mapper = method.getDeclaringClass();
		return mapper.getName().replaceAll(".", "/") + ".java";
	}

}