package com.yonyou.iuap.baseservice.service.util;

import com.yonyou.iuap.baseservice.service.I18nEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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



}
