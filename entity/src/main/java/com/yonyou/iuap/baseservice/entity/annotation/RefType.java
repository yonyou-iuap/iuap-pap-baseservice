package com.yonyou.iuap.baseservice.entity.annotation;

public enum RefType {
    Single(0, "单选"), Multi(1, "多选");
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
