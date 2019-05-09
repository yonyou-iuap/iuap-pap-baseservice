package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.ucf.common.entity.Identifier;

public abstract class SimpleCustomSave<T extends Identifier> implements  CustomSaveable<T> {
    T entity;

    public SimpleCustomSave(T entity){
        this.entity= entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }


}
