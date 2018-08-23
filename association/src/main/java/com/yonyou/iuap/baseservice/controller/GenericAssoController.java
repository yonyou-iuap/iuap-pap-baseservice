package com.yonyou.iuap.baseservice.controller;

import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.annotation.Associative;
import com.yonyou.iuap.baseservice.service.GenericAssoService;
import com.yonyou.iuap.baseservice.service.GenericService;
import com.yonyou.iuap.baseservice.vo.GenericAssoVo;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

/**
 * 说明：基础Controller——仅提供主子表关联特性,单表增删改查请参照GenericExController,GenericController
 * 使用时需要在Entity上增加@Associative注解
 * @author leon
 * 2018年7月11日
 */
@SuppressWarnings("all")
@Transactional
public abstract  class GenericAssoController<T extends Model> extends BaseController {
    private Logger log = LoggerFactory.getLogger(GenericAssoController.class);

//    @Autowired
//    RefCommonService refService;


    @RequestMapping(value = "/getAssoVo")
    @ResponseBody
    public Object  getAssoVo(PageRequest pageRequest,
                             SearchParams searchParams){

        Serializable id = MapUtils.getString(searchParams.getSearchMap(), "id");
        if (null==id){ return buildSuccess();}
        GenericAssoVo vo = service.getAssoVo(pageRequest, searchParams);
        JsonResponse result = this.buildSuccess("entity",vo.getEntity());//保证入参出参结构一致
        result.getDetailMsg().putAll(vo.getSublist());
        return  result;
    }

    @RequestMapping(value = "/saveAssoVo")
    @ResponseBody
    public Object  saveAssoVo(@RequestBody GenericAssoVo<T> vo){
        Associative annotation= vo.getEntity().getClass().getAnnotation(Associative.class);
        if (annotation==null|| StringUtils.isEmpty(annotation.fkName())){
            return buildError("","Nothing got @Associative or without fkName",RequestStatusEnum.FAIL_FIELD);
        }
        Object result =service.saveAssoVo(vo,annotation);
        return this.buildSuccess(result) ;
    }

//    protected boolean hasReferrence(Class entityClass){
//        Field[] fields = ReflectUtil.getFields(entityClass);
//        for (Field field : fields) {
//            Reference ref = field.getAnnotation(Reference.class);
//            if (null != ref) {
//                return true;
//            }
//        }
//        return false;
//    }


    /************************************************************/
//    private Map<Class ,GenericService> subServices = new HashMap<>();
    private GenericAssoService<T> service;

    protected void setService(GenericAssoService<T> genericService) {
        this.service = genericService;
    }

    protected void setSubService(Class entityClass, GenericService subService) {
        service.setSubService( entityClass,subService  );
    }


}
