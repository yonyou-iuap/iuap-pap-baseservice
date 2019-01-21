package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.ParamUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.support.condition.Match;

import java.lang.reflect.Field;

public class INMatcher implements Matcher{
    @Override
    public Match getMatch() {
        return Match.IN;
    }

    @Override
    public String buildCondition(Field field, String prefix) {

        StringBuilder strb = new StringBuilder("\r\n\t<if test=\"");
        String fieldName = ParamUtil.contactParam(prefix, field.getName());
        //strb.append(fieldName).append("!=null \">\r\n");
        strb.append(ParamUtil.adjust4Condition(field, fieldName)).append("\">\r\n");
        strb.append("\t\t and ").append(FieldUtil.getColumnName(field))
                .append(" in " + "\r\n\t<foreach collection=\"")
                .append(fieldName)
                .append("\" item=\"item\"  open=\"(\" separator=\",\" close=\")\">")
                .append("${item} </foreach>")
                .append("\r\n");
        strb.append("\t</if>\r\n");
        return strb.toString();
    }
}
