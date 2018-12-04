package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;

public abstract class AbsCustomSave<T extends Model> implements  CustomSaveable {
    T entity;

    public AbsCustomSave(T entity){
        this.entity= entity;
    }

    @Override
    public Model getEntity() {
        return entity;
    }


}
