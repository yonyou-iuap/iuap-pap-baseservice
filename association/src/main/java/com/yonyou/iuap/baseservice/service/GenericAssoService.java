package com.yonyou.iuap.baseservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.annotation.Associative;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.vo.GenericAssoVo;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主子表服务基础类,直接继承GenericIntegrateService,依赖其他所有组件,
 * @param <T>
 */
public abstract class GenericAssoService<T extends Model> extends GenericIntegrateService<T> {
    private Logger log = LoggerFactory.getLogger(GenericAssoService.class);


    @Transactional
    public GenericAssoVo  getAssoVo(PageRequest pageRequest,
                             SearchParams searchParams){
        Serializable id = MapUtils.getString(searchParams.getSearchMap(), "id");
        T entity = super.findById(id);
        Associative associative= entity.getClass().getAnnotation(Associative.class);
        GenericAssoVo vo = new GenericAssoVo(entity) ;
        for (Class assoKey:subServices.keySet() ){
            List subList= subServices.get(assoKey).queryList(associative.fkName(),id);
            String sublistKey = StringUtils.uncapitalize(assoKey.getSimpleName())+"List";
            vo.addList( sublistKey,subList);
        }
        return  vo;
    }

    @Transactional
    public Object  saveAssoVo( GenericAssoVo<T> vo,Associative annotation){
        T newEntity = super.save( vo.getEntity());
        for (Class assoKey:subServices.keySet() ){
            String sublistKey = StringUtils.uncapitalize(assoKey.getSimpleName())+"List";
            List<Map> subEntities=vo.getList(sublistKey);
            if ( subEntities !=null && subEntities.size()>0 ){
                for (Map subEntity:subEntities){
                    subEntity.put(annotation.fkName(),newEntity.getId());//外键保存
                    String mj=  JSONObject.toJSONString(subEntity);
                    Model entity = (Model) JSON.parseObject(mj,assoKey,Feature.IgnoreNotMatch);
                    if (entity.getId()!=null&&  subEntity.get("dr")!=null && subEntity.get("dr").toString().equalsIgnoreCase("1")){
                        subServices.get(assoKey).delete(entity.getId());
                    }else
                        subServices.get(assoKey).save(entity);
                }

            }

        }
        return  newEntity ;
    }

    /************************************************************/
    private Map<Class ,GenericService> subServices = new HashMap<>();

    public void setSubService(Class entityClass, GenericService subService) {
        subServices.put(entityClass,subService);
    }

}
