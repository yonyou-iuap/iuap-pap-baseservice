package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.ucf.common.entity.Identifier;

public abstract class SimpleCustomDelete<T extends Identifier> implements CustomDeletable<T> {
    T entity;

    public SimpleCustomDelete(T entity){
        this.entity= entity;

    }

    @Override
    public T getEntity() {
        return entity;
    }


}
