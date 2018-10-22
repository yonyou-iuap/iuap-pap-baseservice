package com.yonyou.iuap.baseservice.controller;

import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.annotation.Associative;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.service.GenericAssoService;
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

    /**
     * 主子表合并处理--主表单条查询
     * @param pageRequest
     * @param searchParams
     * @return GenericAssoVo ,entity中保存的是单条主表数据,sublist中保存的是字表数据,一次性全部加载
     */
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

    /**
     * 主子表合并处理--主表单条保存
     * @param vo GenericAssoVo ,entity中保存的是单条主表数据,sublist中保存的是字表数据
     * @return 主表的业务实体
     */
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

    /**
     * 主子表合并处理--主表单条删除
     * @param vo GenericAssoVo,待删除Vo
     * @return 删除成功的实体
     */
    @RequestMapping(value = "/deleAssoVo")
    @ResponseBody
    public Object  deleAssoVo(@RequestBody T entity){
        Associative annotation = (Associative)entity.getClass().getAnnotation(Associative.class);
        if (annotation != null && !StringUtils.isEmpty(annotation.fkName())) {
            if (StringUtils.isEmpty(entity.getId())) {
                return this.buildError("", "ID field must not be empty", RequestStatusEnum.FAIL_FIELD);
            } else if (StringUtils.isEmpty(entity.getTs())) {
                return this.buildError("", "TS field must not be empty", RequestStatusEnum.FAIL_FIELD);
            } else {
                int result = this.service.deleAssoVo(entity, annotation);
                return this.buildSuccess(result + " rows has been deleted!");
            }
        } else {
            return this.buildError("", "Nothing got @Associative or without fkName", RequestStatusEnum.FAIL_FIELD);
        }
    }


    /************************************************************/
//    private Map<Class ,GenericService> subServices = new HashMap<>();
    private GenericAssoService<T> service;

    protected void setService(GenericAssoService<T> genericService) {
        this.service = genericService;
    }

    protected void setSubService(Class entityClass, GenericIntegrateService subService) {
        service.setSubService( entityClass,subService  );
    }


}
