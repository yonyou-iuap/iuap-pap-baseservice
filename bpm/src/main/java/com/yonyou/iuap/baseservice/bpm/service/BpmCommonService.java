package com.yonyou.iuap.baseservice.bpm.service;

import com.yonyou.iuap.baseservice.bpm.entity.BpmSimpleModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;

public class BpmCommonService<T extends BpmSimpleModel> implements SaveFeatureExtension<T> {
    @Override
    public T prepareEntityBeforeSave(T entity) {
        if ( entity.getId()==null){
            entity.setBpmState(BpmExUtil.BPM_STATE_NOTSTART);
        }
        return entity;
    }

    @Override
    public T afterEntitySave(T entity) {
        return entity;
    }
}
