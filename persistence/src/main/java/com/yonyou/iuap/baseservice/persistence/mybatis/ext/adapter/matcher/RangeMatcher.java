package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher;

import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.ParamUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：数据范围条件构建
 * @author Aton
 * 2018年7月12日
 */
public class RangeMatcher implements Matcher{
	
	private Logger log = LoggerFactory.getLogger(RangeMatcher.class);

	@Override
	public Match getMatch() {
		return Match.RANGE;
	}

	@Override
	public String buildCondition(Field field, String prefix) {
		Condition condition = field.getDeclaredAnnotation(Condition.class);
		boolean isAllBlank = true;
		
		StringBuilder strb = new StringBuilder();
		if(StrUtil.isNotBlank(condition.param1())){
			this.buildField1(field, prefix, condition.param1(), strb);
			isAllBlank = false;
		}
		if(StrUtil.isNotBlank(condition.param1())){
			this.buildField2(field, prefix, condition.param2(), strb);
			isAllBlank = false;
		}
		if(isAllBlank) {
			log.error("装配SQL条件语句出错，数据范围条件参数未定义[field1、field2不能全为空]:"
							+field.getDeclaringClass()+"."+field.getName());
			throw new RuntimeException("");
		}else {
			return strb.toString();
		}
		
	}
	
	//构建Field1——开始值
	private void buildField1(Field field, String prefix, String field1, StringBuilder strb) {
		strb.append("\r\n\t<if test=\"");
		String fieldName = ParamUtil.contactParam(prefix, field1);
		strb.append(ParamUtil.adjust4Condition(field, fieldName)).append("\">\r\n");
		strb.append("\t\t and ").append(FieldUtil.getColumnName(field)).append(" <![CDATA[ >= ]]> #{")
			.append(fieldName).append("}\r\n");
		strb.append("\t</if>\r\n");
	}

	//构建Field2——结束值
	private void buildField2(Field field, String prefix, String field2, StringBuilder strb) {
		strb.append("\r\n\t<if test=\"");
		String fieldName = ParamUtil.contactParam(prefix, field2);
		strb.append(ParamUtil.adjust4Condition(field, fieldName)).append("\">\r\n");
		strb.append("\t\t and ").append(FieldUtil.getColumnName(field)).append(" <![CDATA[ <= ]]> #{")
			.append(fieldName).append("}\r\n");
		strb.append("\t</if>\r\n");	
	}

}