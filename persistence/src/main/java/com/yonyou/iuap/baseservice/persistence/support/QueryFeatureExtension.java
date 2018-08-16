package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mvc.type.SearchParams;

import java.util.List;

/**
 * 查询接口增加特性扩展
 * @param <T>
 */
public interface QueryFeatureExtension<T extends Model > {

    SearchParams prepareQueryParam(  SearchParams searchParams);

    List<T> afterListQuery(List<T> list);
}
