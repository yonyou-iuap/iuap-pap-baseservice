package com.yonyou.iuap.baseservice.ref.entity;

/**
 * 从uitemplate_common移入,解决发版时的依赖问题,后续可能切换为其他ref-sdk
 * @author leon
 * */
public enum RefUITypeEnum {
    CommonRef("CommonRef"),
    RefGrid("RefGrid"),
    RefTree("RefTree"),
    RefGridTree("RefGridTree"),
    Custom("Custom");

    private String name;

    private RefUITypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.getName();
    }
}