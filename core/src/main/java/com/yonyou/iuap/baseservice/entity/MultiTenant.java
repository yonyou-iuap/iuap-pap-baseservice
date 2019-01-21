package com.yonyou.iuap.baseservice.entity;


/**
 * 说明：多租户基础Model
 * @author jhb
 * 2018年8月8日
 */
public interface MultiTenant extends Model {
    public String getTenantid();
    public void setTenantid(String tenantid);

}