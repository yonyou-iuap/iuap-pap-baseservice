package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;

/**
 * 新增修改接口增加特性
 * @param <T>
 */
public interface SaveFeatureExtension<T extends Model> {

    T prepareEntityBeforeSave(T entity);

    T afterEntitySave(T entity);
}
