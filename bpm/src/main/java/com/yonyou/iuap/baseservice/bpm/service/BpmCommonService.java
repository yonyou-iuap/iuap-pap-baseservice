package com.yonyou.iuap.baseservice.bpm.service;

import com.yonyou.iuap.baseservice.bpm.entity.BpmSimpleModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

@Service
public class BpmCommonService<T extends BpmSimpleModel& Identifier<ID>,ID extends Serializable> implements SaveFeatureExtension<T>, DeleteFeatureExtension<T> {


    /**
     * 流程状态初始化，当不存在流程状态时，设置为流程未启动
     * @param entity
     * @return
     */
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


    /**
     * <p>
     * 使用baseservice集成流程特性时，删除单据，进行流程状态判断，流程未终止，则无法进行删除。
     * <p/>
     * @param entity
     * @param params
     * @return
     */
    @Override
    public T prepareDeleteParams(T entity, Map params) {
        if(entity.getBpmState() != null){
            int bpmstate = entity.getBpmState();
            if( bpmstate == BpmExUtil.BPM_STATE_RUNNING || bpmstate ==  BpmExUtil.BPM_STATE_START){
                throw new BusinessException("流程正在运行，无法删除");
            }else{
                return entity;
            }
        }else{
            return entity;
        }
    }

    @Override
    public void afterDeteleEntity(T entity) {

    }

}
