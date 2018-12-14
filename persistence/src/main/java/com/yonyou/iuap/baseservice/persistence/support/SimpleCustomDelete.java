package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.data.domain.PageRequest;

public abstract class SimpleCustomDelete<T extends Model> implements CustomDeletable<T> {
    T entity;

    public SimpleCustomDelete(T entity){
        this.entity= entity;

    }

    @Override
    public T getEntity() {
        return entity;
    }


}
