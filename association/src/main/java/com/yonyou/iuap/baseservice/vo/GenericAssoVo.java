package com.yonyou.iuap.baseservice.vo;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.ucf.common.entity.Identifier;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 之子表查询Vo包装类,支持一主多子
 *
 * @param <T> 主表类对象限定为Model,否则无法装填数据
 */
public class GenericAssoVo<T extends Identifier> implements Serializable {


    protected T entity; //主表对象
    protected Map<String, List> sublist = new HashMap<>();//字表list,支持一主多子

    public GenericAssoVo() {
    }
    public GenericAssoVo(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public void addList(String assoKey, List list) {
        sublist.put(assoKey, list);
    }

    public List getList(String assoKey) {
        return sublist.get(assoKey);
    }
    public Map<String, List> getSublist() {
        return sublist;
    }

    public void setSublist(Map<String, List> sublist) {
        this.sublist = sublist;
    }

}
