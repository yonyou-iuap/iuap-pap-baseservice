package com.yonyou.iuap.baseservice.persistence.mybatis.ext.support;

/**
 * move from ucf ,needed for adapt iuap356-web
 *
 * @author leon
 * @date 2019/6/13
 * @since UCF1.0
 */
public enum Match {
    EQ("="),
    GT(">"),
    LT("<"),
    GTEQ(">="),
    LTEQ("<="),
    LIKE("like"),
    LLIKE("llike"),
    RLIKE("rlike"),
    RANGE("range"),
    BETWEEN("between"),
    IN("in"),
    OTHER("other");

    private String type;

    private Match(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
