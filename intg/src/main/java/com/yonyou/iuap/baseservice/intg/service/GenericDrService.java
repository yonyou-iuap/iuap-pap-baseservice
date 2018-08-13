package com.yonyou.iuap.baseservice.intg.service;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.mvc.type.SearchParams;

import java.util.List;

public class GenericDrService<T extends Model> implements QueryFeatureExtension<T> {
    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams) {
        searchParams.addCondition("dr",LogicDel.NORMAL);
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        return list;
    }
}
