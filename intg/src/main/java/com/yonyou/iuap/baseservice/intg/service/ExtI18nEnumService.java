package com.yonyou.iuap.baseservice.intg.service;


import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.intg.ext.EnumValueUtils;
import com.yonyou.iuap.baseservice.intg.ext.I18nEnumCode;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行枚举反写
 *
 * @author leon
 * @date 2019/4/11
 * @since 3.5.6
 */
@Service
public class ExtI18nEnumService<T extends Model>  implements QueryFeatureExtension<T> {
    private static Logger logger = LoggerFactory.getLogger(ExtI18nEnumService.class);
    private ThreadLocal<Class>  modelClass =new ThreadLocal<>() ;


    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams, Class modelClass) {
        this.modelClass.set(modelClass);
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        Map<Field,I18nEnumCode> annoFields = new HashMap<>();
        for (Field field: EntityUtil.getEntityFields(modelClass.get()) ){
            if (field.getAnnotation(I18nEnumCode.class)!=null){
                annoFields.put(field,field.getAnnotation(I18nEnumCode.class));
            }
        }
        if (annoFields.size()==0){
            return  list;
        }
        for (T entity:list){
            for (Field  enumField:annoFields.keySet()){
                Map enumContent = EnumValueUtils.loadEnumInfo( annoFields.get(enumField).clazz());
                String enumCode = String.valueOf(ReflectUtil.getFieldValue(entity,enumField)) ;
                Object enumValue ;
                //增加多选enumCode的支持
                String[] enumCodes = enumCode.split(",");
                if (enumCodes.length>1){
                    List enumValues = new ArrayList<>();
                    for (String code :enumCodes){
                        enumValues.add(enumContent.get(enumField.getName().toUpperCase()+"_"+code) );
                    }
                    enumValue = ArrayUtil.join(enumValues.toArray(), ",");
                }else{
                    enumValue = enumContent.get(enumField.getName().toUpperCase()+"_"+enumCode);
                }

                if (enumValue!= null){
                    try {
                        ReflectUtil.setFieldValue(entity,annoFields.get(enumField).target(),enumValue);
                    } catch (UtilException e) {
                        logger.warn("Fail on writing enum target field : "+annoFields.get(enumField).target());
                    }
                }

            }


        }


        return list;
    }
}