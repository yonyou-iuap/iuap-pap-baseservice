package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.condition.Formatter;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.condition.FormatterHolder;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.ParamUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.TypeMaping;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：数据范围条件构建
 * @author Aton
 * 2018年7月12日
 */
public class RangeMatcher implements Matcher{
	
	private static String SPLIT_SIGN = "::";
	private Logger log = LoggerFactory.getLogger(RangeMatcher.class);

	@Override
	public Match getMatch() {
		return Match.RANGE;
	}

	@Override
	public String buildCondition(Field field, String prefix) {
		Condition condition = field.getAnnotation(Condition.class);
		boolean isAllBlank = true;
		
		StringBuilder strb = new StringBuilder();
		if(StrUtil.isNotBlank(condition.param1())){
			this.buildField1(field, prefix, condition, strb);
			isAllBlank = false;
		}
		if(StrUtil.isNotBlank(condition.param1())){
			this.buildField2(field, prefix, condition, strb);
			isAllBlank = false;
		}
		if(isAllBlank) {
			log.error("装配SQL条件语句出错，数据范围条件参数未定义[param1、param2不能全为空]:"
							+field.getDeclaringClass()+"."+field.getName());
			throw new RuntimeException("装配SQL条件语句出错，数据范围条件参数未定义[param1、param2不能全为空]!");
		}else {
			return strb.toString();
		}
		
	}
	
	//构建Field1——开始值
	private void buildField1(Field field, String prefix, Condition condition, StringBuilder strb) {
		strb.append("\r\n\t<if test=\"");
		String fieldName = ParamUtil.contactParam(prefix, condition.param1());
		strb.append(ParamUtil.adjust4Condition(field, fieldName)).append("\">\r\n");
		strb.append("\t\t and ").append(FieldUtil.getColumnName(field)).append(" <![CDATA[ >= ]]> ");
		
		if(StrUtil.isBlank(condition.format())) {							//condition格式化工具为空
			strb.append(" #{").append(fieldName);
			String jdbcType = TypeMaping.getJdbcType(field.getType());		//获取jdbcType
			if(!StrUtil.isBlank(jdbcType)) {
				strb.append(", jdbcType=").append(jdbcType);
			}
			strb.append("}");
		} else {
			String[] format = condition.format().split(SPLIT_SIGN);
			Formatter formatter = FormatterHolder.get(format[0]);
			if(formatter == null) {
				log.error("未找到条件formatter:"+condition.format()+", 已注册的条件formatter:");
				throw new MapperException("未找到条件formatter:"+condition.format());
			}else {
				if(format.length==2) {
					strb.append(formatter.format(fieldName, format[1]));
				}else {
					strb.append(formatter.format(fieldName, null));
				}
			}
		}
		strb.append("\r\n\t</if>\r\n");
	}

	//构建Field2——结束值
	private void buildField2(Field field, String prefix, Condition condition, StringBuilder strb) {
		strb.append("\r\n\t<if test=\"");
		String fieldName = ParamUtil.contactParam(prefix, condition.param2());
		strb.append(ParamUtil.adjust4Condition(field, fieldName)).append("\">\r\n");
		strb.append("\t\t and ").append(FieldUtil.getColumnName(field)).append(" <![CDATA[ <= ]]> ");

		if(StrUtil.isBlank(condition.format())) {							//condition格式化工具为空
			strb.append(" #{").append(fieldName);
			String jdbcType = TypeMaping.getJdbcType(field.getType());		//获取jdbcType
			if(!StrUtil.isBlank(jdbcType)) {
				strb.append(", jdbcType=").append(jdbcType);
			}
			strb.append("}");
		} else {
			String[] format = condition.format().split(SPLIT_SIGN);
			Formatter formatter = FormatterHolder.get(format[0]);
			if(formatter == null) {
				log.error("未找到条件formatter:"+condition.format()+", 已注册的条件formatter:");
				throw new MapperException("未找到条件formatter:"+condition.format());
			}else {
				if(format.length==2) {
					strb.append(formatter.format(fieldName, format[1]));
				}else {
					strb.append(formatter.format(fieldName, null));
				}
			}
		}
		strb.append("\r\n\t</if>\r\n");
	}

}