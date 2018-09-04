package com.yonyou.iuap.baseservice.bpm.service;

import com.yonyou.iuap.baseservice.bpm.entity.BpmSimpleModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BpmCommonService<T extends BpmSimpleModel> implements SaveFeatureExtension<T>, DeleteFeatureExtension<T> {

    @Override
    public T prepareEntityBeforeSave(T entity) {
        if ( entity.getId()==null || entity.getBpmState() == null){
            entity.setBpmState(BpmExUtil.BPM_STATE_NOTSTART);
        }
        return entity;
    }

    @Override
    public T afterEntitySave(T entity) {
        return entity;
    }

    @Override
    public T prepareDeleteParams(T entity, Map params) {
        return null;
    }

    @Override
    public void afterDeteleEntity(T entity) {

    }
}
