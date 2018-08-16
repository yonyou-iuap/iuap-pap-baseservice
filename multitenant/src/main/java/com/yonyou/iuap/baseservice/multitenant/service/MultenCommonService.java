package com.yonyou.iuap.baseservice.multitenant.service;

import com.yonyou.iuap.baseservice.multitenant.entity.MultiTenant;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Service
public class MultenCommonService<T extends MultiTenant> implements QueryFeatureExtension<T>,SaveFeatureExtension<T> ,DeleteFeatureExtension<T> {

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams) {
        Map<String,Object> searchMap=searchParams.getSearchMap();
        String tenantidInMap=String.valueOf(searchMap.get("tenantid"));
        if(StringUtils.isEmpty(tenantidInMap)|| "null".equals(tenantidInMap)){
            searchMap.put("tenantid",InvocationInfoProxy.getTenantid());
        }

        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        return list;
    }

    @Override
    public T prepareEntityBeforeSave(T entity) {
        entity.setTenantid(InvocationInfoProxy.getTenantid());
        return entity;
    }

    @Override
    public T afterEntitySave(T entity) {
        return entity;
    }

    @Override
    public T prepareDeleteParams(T entity,Map params) {
        entity.setTenantid(InvocationInfoProxy.getTenantid());
        params.put("tenantid", InvocationInfoProxy.getTenantid());
        return entity;
    }

    @Override
    public void afterDeteleEntity(T entity) {

    }
}
