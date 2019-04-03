package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.ucf.common.entity.Identifier;

/**
 * 新增修改接口增加特性
 * @param <T>
 */
public interface SaveFeatureExtension<T extends Identifier> {

    T prepareEntityBeforeSave(T entity);

    T afterEntitySave(T entity);
}
