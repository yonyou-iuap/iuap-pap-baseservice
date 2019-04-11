package com.yonyou.iuap.baseservice.intg.ext;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.annotation.I18nEnumCode;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.service.util.EnumValueUtils;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import com.yonyou.iuap.ucf.common.rest.SearchParams;
import com.yonyou.iuap.ucf.core.utils.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行枚举反写
 *
 * @author leon
 * @date 2019/4/11
 * @since UCF1.0
 */
@Service
public class ExtI18nEnumService<T extends Identifier>  implements QueryFeatureExtension<T> {
    private static Logger logger = LoggerFactory.getLogger(ExtI18nEnumService.class);
    private Class modelClass ;


    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams, Class modelClass) {
        this.modelClass=modelClass;
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        Map<Field,I18nEnumCode> annoFields = new HashMap<>();
        for (Field field:EntityUtil.getEntityFields(modelClass) ){
            if (field.getAnnotation(I18nEnumCode.class)!=null){
                annoFields.put(field,field.getAnnotation(I18nEnumCode.class));
            }
        }
        if (annoFields.size()==0){
            return  list;
        }
        for (T entity:list){
            for (Field  enumField:annoFields.keySet()){
                String enumCode = String.valueOf(ReflectUtil.getFieldValue(entity,enumField)) ;
                Object enumValue = EnumValueUtils.loadEnumInfo( annoFields.get(enumField).clazz()).get(enumCode);
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
