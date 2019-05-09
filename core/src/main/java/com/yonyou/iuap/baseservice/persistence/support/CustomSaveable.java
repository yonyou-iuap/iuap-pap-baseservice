package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.ucf.common.entity.Identifier;

public interface CustomSaveable<T extends Identifier> {

    T getEntity();

    T  doCustomSave();
}
