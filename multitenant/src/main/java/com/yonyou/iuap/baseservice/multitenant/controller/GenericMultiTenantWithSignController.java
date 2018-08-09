package com.yonyou.iuap.baseservice.multitenant.controller;


import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.multitenant.entity.MultiTenant;
import com.yonyou.iuap.baseservice.multitenant.service.GenericMultiTenantService;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 说明：多租户基础Controller：提供单表增删改查。加签调用
 * @author jhb
 * 2018年8月8日
 */
public abstract   class GenericMultiTenantWithSignController<T extends MultiTenant> extends BaseController {

    private GenericMultiTenantService multiTenantService;

    public void setMultiTenantService(GenericMultiTenantService multiTenantService) {
        this.multiTenantService = multiTenantService;
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(PageRequest pageRequest, SearchParams searchParams,String tenantid) {
        Page<T>             page = this.multiTenantService.selectAllByPageWithSign(pageRequest, searchParams,tenantid);
        Map<String, Object> map  = new HashMap<>(1);
        map.put("data", page);
        return this.buildMapSuccess(map);
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public Object get(PageRequest pageRequest, SearchParams searchParams,String tenantid) {
        String id = MapUtils.getString(searchParams.getSearchMap(), "id");
        if (id==null){
            /*前端约定传空id则拿到空对象*/
            return this.buildSuccess();
        }
        if(StrUtil.isBlank(id)) {
            return this.buildError("msg", "主键id参数为空!", RequestStatusEnum.FAIL_FIELD);
        }else {
            T entity = (T) this.multiTenantService.findByIdWithSign(id,tenantid);
            return this.buildSuccess(entity);
        }
    }


    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save(@RequestBody T entity) {
        JsonResponse jsonResp;
        try {
            this.multiTenantService.saveWithSign(entity);
            jsonResp = this.buildSuccess(entity);
        }catch(Exception exp) {
            jsonResp = this.buildError("msg", exp.getMessage(), RequestStatusEnum.FAIL_FIELD);
        }
        return jsonResp;
    }

    @RequestMapping(value = "/saveBatch")
    @ResponseBody
    public Object saveBatch(@RequestBody List<T> listData) {
        this.multiTenantService.saveBatchWithSign(listData);
        return this.buildSuccess();
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestBody T entity, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.multiTenantService.deleteWithSign(entity);
        return super.buildSuccess();
    }

    @RequestMapping(value = "/deleteBatch")
    @ResponseBody
    public Object deleteBatch(@RequestBody List<T> listData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.multiTenantService.deleteBatchWithSign(listData);
        return super.buildSuccess();
    }





}