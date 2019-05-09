package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.ucf.common.entity.Identifier;

public interface CustomDeletable<T extends Identifier> {
    T getEntity();

    int  doCustomDelete();
}
