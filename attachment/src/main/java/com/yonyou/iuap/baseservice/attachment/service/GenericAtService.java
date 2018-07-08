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
    private RefCommonMapper rfMapper;

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

        List<T> contentList = page.getContent();

        if (!contentList.isEmpty()) {
            /**
             * @Step 1 解析参照配置,一次加载参照数据全集
             */
            Map<String, List<Map<String, Object>>> refContentMap = new HashMap<>();
            Map<String,Reference> refCache = new HashMap<>();
            Field[] fields = ReflectUtil.getFields(contentList.get(0).getClass());
            for (Field field : fields) {
                Reference ref = field.getAnnotation(Reference.class);
                if (null != ref) {
                    refCache.put(field.getName(),ref); //将所有参照和field的关系缓存起来后续使用
                    RefParamVO params = RefXMLParse.getInstance().getMSConfig(ref.code());
                    Map<String, String> conditions = new HashMap<String,String>();
//                    conditions.put("dr", "0");
                    String idfield = StringUtils.isBlank(params.getIdfield()) ? "id"
                            : params.getIdfield();
                    List<Map<String, Object>> refContents =
                            rfMapper.treerefselectAllByPage(
                                    null, params.getTablename(),
                                    idfield, params.getExtcol()
                                    , conditions).getContent();
                    refContentMap.put(field.getName(), refContents);//将所有参照数据集和field的关系缓存起来后续使用
                }
            }
            /**
             * @Step 2 逐条遍历业务结果集,将属性替换为参照值
             */
            if (!refContentMap.isEmpty()) {
                for (Object item : contentList) { //遍历结果集
                    for(String srcField: refCache.keySet() ){//遍历缓存的entity的全部参照字段
                        Reference refInCache = refCache.get(srcField);
                        String refFieldValue = ReflectUtil.getFieldValue(item,srcField).toString();
                        int loopSize =Math.min( refInCache.srcProperties().length ,refInCache.desProperties().length  );
                        for (int i = 0; i < loopSize; i++) {//遍历参照中的srcPro和desPro 进行值替换
                           String srcCol = refInCache.srcProperties()[i];
                           String desField= refInCache.desProperties()[i];
                           List<Map<String, Object>> refDatas =refContentMap.get(srcField);
                           for (Map<String,Object> refData: refDatas){
                               if (refData.get("ID")!=null && refData.get("ID").toString().equals(refFieldValue)){
                                   Object refValue = refData.get(srcCol.toUpperCase());
                                   ReflectUtil.setFieldValue(item,desField,refValue); //执行反写
                               }
                           }

                        }

                    }
                }
            }
        }

        List<T> list = page.getContent();
        for(T entity : list){
            Map params = new HashMap<>();
            params.put("refId",entity.getId()   );
            List<AttachmentEntity> attachments = atMapper.queryList(params);
            entity.setAttachment(attachments);
        }
        return page;
    }

}