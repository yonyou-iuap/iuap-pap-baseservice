package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;

import java.util.Map;

/**
 * 删除接口增加特性
 * @param <T>
 */
public interface DeleteFeatureExtension<T extends Model> {

    T prepareDeleteParams(T entity,Map params);

    void afterDeteleEntity(T entity);
}
