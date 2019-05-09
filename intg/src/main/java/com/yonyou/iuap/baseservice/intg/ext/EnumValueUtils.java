package com.yonyou.iuap.baseservice.intg.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
     * @param enumClass
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Map loadEnumInfo(Class<? extends I18nEnum> enumClass)   {


        Map map = new HashMap();
        try {
            Method method = enumClass.getMethod("values");
            I18nEnum[] enumInst =  (I18nEnum[]) method.invoke(null, null);
            map.putAll(enumInst[0].getMappings());
        } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException e) {
            logger.error("loading fail on enum :"+enumClass);
        }
        return map;
    }


    /*
     * list<Map>处理
     */
    public static List i18nEnumMapKeyToValue(List<Map> dataMapList, Class classObject) {
        Map<Class, Set<Field>> enumCache = new HashMap();
        if(!enumCache.containsKey(classObject)){
            enumCache.put(classObject, new HashSet<Field>());
        }
        Set<Field> fields = enumCache.get(classObject).size() == 0
                ? new HashSet<>(Arrays.asList(classObject.getDeclaredFields())) : enumCache.get(classObject);
        try {
            for (Field field : fields) {
                I18nEnumCode annotation = field.getDeclaredAnnotation(I18nEnumCode.class);
                if (annotation != null) {
                    enumCache.get(classObject).add(field);
                    Class<? extends I18nEnum> enumClass = annotation.clazz();
                    if (annotation.target() == null || annotation.target() == "") {
                        continue;
                    }
                    for (Map<String, Object> item : dataMapList) {
                        if (item.get(field.getName()) != null) {
                            item.put(annotation.target(), (String)loadEnumInfo(enumClass)
                                    .get(String.valueOf(item.get(field.getName()))));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("loading fail on entity :"+classObject,e);
        }

        return dataMapList;
    }
}