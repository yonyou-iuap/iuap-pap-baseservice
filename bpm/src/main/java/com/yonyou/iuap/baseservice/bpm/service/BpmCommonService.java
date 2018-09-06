package com.yonyou.iuap.baseservice.bpm.service;

import com.yonyou.iuap.baseservice.bpm.entity.BpmSimpleModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
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
        int bpmstate = entity.getBpmState();
        if( bpmstate == BpmExUtil.BPM_STATE_RUNNING ){
            throw new BusinessException("流程正在运行，无法删除");
        }else{
            return entity;
        }
    }

    @Override
    public void afterDeteleEntity(T entity) {

    }
}
