package com.yonyou.iuap.baseservice.controller;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.annotation.Associative;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;
import com.yonyou.iuap.baseservice.service.GenericService;
import com.yonyou.iuap.baseservice.vo.GenericAssoVo;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 说明：基础Controller——仅提供主子表关联特性,单表增删改查请参照GenericExController,GenericController
 * 使用时需要在Entity上增加@Associative注解
 * TODO 级联删除下个版本支持
 * @author leon
 * 2018年7月11日
 */
@SuppressWarnings("all")
public abstract  class GenericAssoController<T extends Model> extends BaseController {
    private Logger log = LoggerFactory.getLogger(GenericAssoController.class);

    @Autowired
    RefCommonService refService;
  

    @RequestMapping(value = "/getAssoVo")
    @ResponseBody
    public Object  getAssoVo(PageRequest pageRequest,
                             SearchParams searchParams){
        Serializable id = MapUtils.getString(searchParams.getSearchMap(), "id");
        if (null==id){ return buildSuccess();}
        T entity = service.findById(id);
        List<T> single= refService.fillListWithRef( Arrays.asList(entity) )  ;
        entity=single.get(0);
        Associative associative= entity.getClass().getAnnotation(Associative.class);
        if (associative==null|| StringUtils.isEmpty(associative.fkName())){
            return buildError("","Nothing got @Associative or without fkName",RequestStatusEnum.FAIL_FIELD);
        }
        GenericAssoVo vo = new GenericAssoVo(entity) ;
        for (Class assoKey:subServices.keySet() ){
            List subList= subServices.get(assoKey).queryList(associative.fkName(),id);
            if ( hasReferrence(assoKey)){
                subList=refService.fillListWithRef(subList);
            }
            String sublistKey = StringUtils.uncapitalize(assoKey.getSimpleName())+"List";
            vo.addList( sublistKey,subList);
        }
        JsonResponse result = this.buildSuccess("entity",vo.getEntity());//保证入参出参结构一致
        result.getDetailMsg().putAll(vo.getSublist());
        return  result;
    }

    @RequestMapping(value = "/SaveAssoVo")
    @ResponseBody
    public Object  saveAssoVo(@RequestBody GenericAssoVo<T> vo){
        Associative annotation= vo.getEntity().getClass().getAnnotation(Associative.class);
        if (annotation==null|| StringUtils.isEmpty(annotation.fkName())){
            return buildError("","Nothing got @Associative or without fkName",RequestStatusEnum.FAIL_FIELD);
        }
        T newEntity = service.save( vo.getEntity());
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
        return this.buildSuccess(newEntity) ;
    }

    protected boolean hasReferrence(Class entityClass){
        Field[] fields = ReflectUtil.getFields(entityClass);
        for (Field field : fields) {
            Reference ref = field.getAnnotation(Reference.class);
            if (null != ref) {
                return true;
            }
        }
        return false;
    }


    /************************************************************/
    private Map<Class ,GenericService> subServices = new HashMap<>();
    private GenericService<T> service;

    protected void setService(GenericService<T> genericService) {
        this.service = genericService;
    }
    protected void setSubService(Class entityClass, GenericService subService) {
        subServices.put(entityClass,subService);

    }

}
