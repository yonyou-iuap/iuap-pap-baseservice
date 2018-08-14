package com.yonyou.iuap.baseservice.attachment.service;

import com.yonyou.iuap.baseservice.attachment.dao.mapper.AttachmentMapper;
import com.yonyou.iuap.baseservice.attachment.entity.AttachmentEntity;
import com.yonyou.iuap.baseservice.attachment.entity.Attachmentable;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AtCommonService<T extends Attachmentable> implements QueryFeatureExtension<T> {

    @Autowired
    private AttachmentMapper atMapper;
    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams) {
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
}
