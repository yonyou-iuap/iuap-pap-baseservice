package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;

public interface CustomDeletable<T extends Model> {
    T getEntity();

    int  doCustomDelete();
}
