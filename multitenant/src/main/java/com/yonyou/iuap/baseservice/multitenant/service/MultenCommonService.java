package com.yonyou.iuap.baseservice.multitenant.service;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.MultiTenant;
import com.yonyou.iuap.baseservice.entity.TenantId;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 增加兼容能力,新版TenantId和老版MultiTenant都要hold住
 * @author  leon
 * @param <T>
 */
@Service
public class MultenCommonService<T extends Model> implements QueryFeatureExtension<T>,SaveFeatureExtension<T> ,DeleteFeatureExtension<T> {

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams,Class modelClass) {
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
        if (entity instanceof MultiTenant){
            ((MultiTenant)entity).setTenantid(InvocationInfoProxy.getTenantid());
        }
        if (entity instanceof TenantId){
            ((TenantId)entity).setTenantId(InvocationInfoProxy.getTenantid());
        }

        return entity;
    }

    @Override
    public T afterEntitySave(T entity) {
        return entity;
    }

    @Override
    public T prepareDeleteParams(T entity,Map params) {
        if (entity instanceof MultiTenant){
            ((MultiTenant)entity).setTenantid(InvocationInfoProxy.getTenantid());
        }
        if (entity instanceof TenantId){
            ((TenantId)entity).setTenantId(InvocationInfoProxy.getTenantid());
        }

        params.put("tenantid", InvocationInfoProxy.getTenantid());
        return entity;
    }

    @Override
    public void afterDeteleEntity(T entity) {

    }
}
