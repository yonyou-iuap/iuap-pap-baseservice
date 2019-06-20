package com.yonyou.iuap.baseservice.entity;


/**
 * 说明：多租户基础Model,已废弃
 * 推荐使用新版的TenantId
 * @author jhb
 * 2018年8月8日
 */
@Deprecated
public interface MultiTenant extends Model {
    public String getTenantid();
    public void setTenantid(String tenantid);

}
