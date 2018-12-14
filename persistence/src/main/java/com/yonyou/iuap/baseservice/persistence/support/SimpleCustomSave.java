package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;

public abstract class SimpleCustomSave<T extends Model> implements  CustomSaveable<T> {
    T entity;

    public SimpleCustomSave(T entity){
        this.entity= entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }


}
