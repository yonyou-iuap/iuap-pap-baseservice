package com.yonyou.iuap.baseservice.attachment.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.attachment.entity.AttachmentEntity;
import com.yonyou.iuap.baseservice.attachment.entity.Attachmentable;
import com.yonyou.iuap.baseservice.attachment.dao.mapper.AttachmentMapper;
import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;
import com.yonyou.iuap.baseservice.service.GenericExService;
import com.yonyou.iuap.baseservice.service.GenericService;
import com.yonyou.iuap.baseservice.support.generator.GeneratorManager;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class GenericAtService<T extends Attachmentable> extends GenericExService<T> {

    private Logger log = LoggerFactory.getLogger(GenericAtService.class);

    @Autowired
    private AttachmentMapper atMapper;
    @Autowired
    private RefCommonService rfService;

	protected GenericExMapper mapper;

	public void setMapper(GenericExMapper mapper) {
		this.mapper = mapper;
		super.setIbatisMapperEx(mapper);
	}

	private List<AttachmentEntity> getRefId(String id) {
        Map params = new HashMap<>();
        params.put("refId",id   );
		List<AttachmentEntity> AttachmentEntitys = atMapper.queryList(params);
		return AttachmentEntitys;
	}

    public T saveWithAttachment(T entity) {
        T newEntity  = super.save(entity);
        String id = newEntity.getId().toString();
        String name = newEntity.getClass().getSimpleName();
        List<AttachmentEntity> attachments = entity.getAttachment();
        for(AttachmentEntity att:attachments){
            if(att.getDel() != null){
                att.setDr(1);
                atMapper.update(att);
            }else{
                if(att.getId()==null || StrUtil.isBlankIfStr(att.getId())){
                    Serializable attid = GeneratorManager.generateID(att);
                    att.setId(attid);
                    att.setRefId(newEntity.getId().toString());
                    att.setRefName(newEntity.getClass().getSimpleName());
                    String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS");
                    att.setCreateTime(now);
                    att.setCreateUser(InvocationInfoProxy.getUserid());
                    att.setLastModified(now);
                    att.setLastModifyUser(InvocationInfoProxy.getUserid());
                    att.setTs(now);
                    atMapper.insert(att);
                }
            }
        }
        return entity;
    }

    public Page getListWithAttach(PageRequest pageRequest, SearchParams searchParams) throws Exception {
//        Page page = mapper.selectAllByPage(pageRequest, searchParams).getPage();

        Page<T> page = super.selectAllByPage(pageRequest, searchParams);
        List<T> list = page.getContent();
        list = rfService.fillListWithRef(list);
        for(T entity : list){
            Map params = new HashMap<>();
            params.put("refId",entity.getId()   );
            List<AttachmentEntity> attachments = atMapper.queryList(params);
            entity.setAttachment(attachments);
        }
        return page;
    }




}