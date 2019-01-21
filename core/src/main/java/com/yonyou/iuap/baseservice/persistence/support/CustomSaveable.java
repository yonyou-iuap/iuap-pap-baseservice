package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;

public interface CustomSaveable<T extends Model> {

    T getEntity();

    T  doCustomSave();
}
