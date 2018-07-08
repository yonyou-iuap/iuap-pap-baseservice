package com.yonyou.iuap.baseservice.entity.annotation;

public enum RefType {
    Tree(1, "树形"), Single(2, "单表"), TreeCard(3, "树卡型"), Multi(4, "多选"), Shuttle(5, "穿梭框"), MFilter(6, "多过滤项");
    private int type;
    private String typeName;

    private RefType(int type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public int getType() {
        return type;
    }
}
