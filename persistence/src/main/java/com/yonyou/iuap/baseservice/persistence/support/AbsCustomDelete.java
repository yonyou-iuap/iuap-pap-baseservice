package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;

public abstract class AbsCustomDelete<T extends Model> implements CustomDeletable {
    T entity;

    public  AbsCustomDelete(T entity){
        this.entity= entity;

    }

    @Override
    public Model getEntity() {
        return entity;
    }


}
