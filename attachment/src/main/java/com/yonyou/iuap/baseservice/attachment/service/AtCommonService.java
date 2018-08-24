package com.yonyou.iuap.baseservice.attachment.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.attachment.dao.mapper.AttachmentMapper;
import com.yonyou.iuap.baseservice.attachment.entity.AttachmentEntity;
import com.yonyou.iuap.baseservice.attachment.entity.Attachmentable;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.baseservice.support.generator.GeneratorManager;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AtCommonService<T extends Attachmentable> implements QueryFeatureExtension<T>,SaveFeatureExtension<T> {

    @Autowired
    private AttachmentMapper atMapper;
    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams,Class modelClass) {
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        for(T entity : list){
            Map params = new HashMap<>();
            params.put("refId",entity.getId()   );
            List<AttachmentEntity> attachments = atMapper.queryList(params);
            entity.setAttachment(attachments);
        }
        return list;
    }

    @Override
    public T prepareEntityBeforeSave(T entity) {
        return entity;
    }

    @Override
    public T afterEntitySave(T entity) {
        String id = entity.getId().toString();
        String name = entity.getClass().getSimpleName();
        List<AttachmentEntity> attachments = entity.getAttachment();
        if (attachments==null){//没有附件需要保存
            return entity;
        }
        for(AttachmentEntity att:attachments){
            if(att.getDel() != null){
                att.setDr(LogicDel.DELETED);
                atMapper.update(att);
            }else{
                if(att.getId()==null || StrUtil.isBlankIfStr(att.getId())){
                    Serializable attid = GeneratorManager.generateID(att);
                    att.setId(attid);
                    att.setRefId(id);
                    att.setRefName(name);
                    String now = DateUtil.now();
                    att.setCreateTime(now);
                    att.setCreateUser(InvocationInfoProxy.getUserid());
                    att.setLastModified(now);
                    att.setLastModifyUser(InvocationInfoProxy.getUserid());
                    att.setDr(LogicDel.NORMAL);
                    att.setTs(now);
                    atMapper.insert(att);
                }
            }
        }
        return entity;
    }
}
