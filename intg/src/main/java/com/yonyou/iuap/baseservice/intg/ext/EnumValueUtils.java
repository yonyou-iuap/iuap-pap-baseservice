package com.yonyou.iuap.baseservice.intg.ext;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 国际化枚举翻译工具
 *
 * @author leon
 * @date 2019/3/14
 * @since UCF1.0
 */
public class EnumValueUtils {
    private static Logger logger = LoggerFactory.getLogger(EnumValueUtils.class);
    /**
     * 输出就靠这波了
     * @param enumClass 枚举类,只服务实现I18nEnum的
     * @return
     */
    public static Map loadEnumInfo(Class<? extends I18nEnum> enumClass)   {


        Map map = new HashMap();
        try {
            Method method = enumClass.getMethod("values");
            I18nEnum[] enumInst =  (I18nEnum[]) method.invoke(null, null);
            if (enumInst==null || enumInst.length==0){
                return map;
            }
            map.putAll(enumInst[0].getMappings());
        } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException e) {
            logger.error("loading fail on enum :"+enumClass);
        }
        return map;
    }


    /**
     * list<Map>处理
     */
    public static List i18nEnumMapKeyToValue(List<Map> list, Class modelClass) {
        Map<Field,I18nEnumCode> annoFields = new HashMap<>();
        for (Field field: EntityUtil.getEntityFields(modelClass) ){
            if (field.getAnnotation(I18nEnumCode.class)!=null){
                annoFields.put(field,field.getAnnotation(I18nEnumCode.class));
            }
        }
        if (annoFields.size()==0){
            return  list;
        }
        for (Map entity:list){
            for (Field  enumField:annoFields.keySet()){
                String enumCode = String.valueOf(entity.get(enumField.getName()));
                Object enumValue = loadEnumInfo( annoFields.get(enumField).clazz()).get(enumField.getName().toUpperCase()+"_"+enumCode);
                if (enumValue!= null){
                    entity.put(annoFields.get(enumField).target(),enumValue);
                }
            }
        }

        return list;
    }
}