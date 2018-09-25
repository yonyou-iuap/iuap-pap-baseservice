package com.yonyou.iuap.baseservice.intg.service;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DrCommonService<T extends Model& LogicDel> implements QueryFeatureExtension<T>, SaveFeatureExtension<T>, DeleteFeatureExtension<T> {
    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams,Class modelClass) {
        searchParams.addCondition("dr", LogicDel.NORMAL);
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        return list;
    }

    @Override
    public T prepareEntityBeforeSave(T entity) {
        if (entity.getId() == null||entity.getDr()==null) {
            entity.setDr( LogicDel.NORMAL);
//            ReflectUtil.setFieldValue(entity, "dr", LogicDel.NORMAL);
        }
        return entity;
    }

    @Override
    public T afterEntitySave(T entity) {
        return entity;
    }

    @Override
    public T prepareDeleteParams(T entity,Map params) {
        if (entity.getId() != null) {
            entity.setDr( LogicDel.DELETED);
//            ReflectUtil.setFieldValue(entity, "dr", LogicDel.DELETED); //update 的set 值
        }
        return entity;
    }

    @Override
    public void afterDeteleEntity(T entity) {

    }
}
