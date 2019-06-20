package com.yonyou.iuap.baseservice.entity;

/**
 *  为保证 MultiTenant升级时的兼容性,不改变原有代码结构,而将新的接口标准改为使用本类
 * @author leon
 * @date 2019/6/20
 */
public interface TenantId extends Model{
    String getTenantId();
    void setTenantId(String tenantId);
}
