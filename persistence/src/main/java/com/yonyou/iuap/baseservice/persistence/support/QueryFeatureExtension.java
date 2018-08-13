package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mvc.type.SearchParams;

import java.util.List;

public interface QueryFeatureExtension<T extends Model > {

    SearchParams prepareQueryParam(  SearchParams searchParams);

    List<T> afterListQuery(List<T> list);
}
