package com.yonyou.iuap.baseservice.model;

import com.yonyou.iuap.baseservice.entity.annotation.Reference;

public class TestBean {
    private String id;
    private String name;
    @Reference(path="org.yonyou.demo", srcProperties={"idSrc", "nameSrc"}, desProperties={"id", "name"})
    private String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
