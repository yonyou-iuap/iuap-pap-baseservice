package com.yonyou.iuap.baseservice.multitenant.dao.mapper;


import com.yonyou.iuap.baseservice.multitenant.entity.MultiTenant;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;

/**
 * 说明：工作流基础Mapper
 *
 * @author jhb
 * 2018年8月8日
 */
public interface GenericMultiTenantMapper<T extends MultiTenant> extends GenericMapper<T> {

}