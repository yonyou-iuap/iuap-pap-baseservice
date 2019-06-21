package com.yonyou.iuap.baseservice.entity;


/**
 * 说明：多租户隔离属性声明,已废弃
 * 推荐使用新版的TenantId
 * @author jhb
 * 2018年8月8日
 */
@Deprecated
public interface MultiTenant extends Model {
    String getTenantid();
    void setTenantid(String tenantid);

}
