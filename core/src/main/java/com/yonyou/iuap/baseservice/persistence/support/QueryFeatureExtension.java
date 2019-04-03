package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.ucf.common.entity.Identifier;
import com.yonyou.iuap.ucf.common.rest.SearchParams;

import java.util.List;

/**
 * 查询接口增加特性扩展
 * @param <T>
 */
public interface QueryFeatureExtension<T extends Identifier> {

    SearchParams prepareQueryParam(SearchParams searchParams, Class modelClass);

    List<T> afterListQuery(List<T> list);
}
