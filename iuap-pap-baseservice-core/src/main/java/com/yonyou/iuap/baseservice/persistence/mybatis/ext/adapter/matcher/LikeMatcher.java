package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher;

import java.lang.reflect.Field;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.ParamUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.support.condition.Match;

public class LikeMatcher implements Matcher{

	@Override
	public Match getMatch() {
		return Match.LIKE;
	}

	@Override
	public String buildCondition(Field field, String prefix) {
		StringBuilder strb = new StringBuilder("\r\n\t<if test=\"");
		String fieldName = ParamUtil.contactParam(prefix, field.getName());
		//strb.append(fieldName).append("!=null \">\r\n");
		strb.append(ParamUtil.adjust4Condition(field, fieldName)).append("\">\r\n");
		strb.append("\t\t and ").append(FieldUtil.getColumnName(field)).append(" like CONCAT(CONCAT('%', #{")
			.append(fieldName).append("}), '%')\r\n");
		strb.append("\t</if>\r\n");
		return strb.toString();
	}

}
