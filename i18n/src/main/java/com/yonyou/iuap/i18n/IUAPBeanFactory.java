package com.yonyou.iuap.i18n;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

/**
 * 使用框架上下文，主要获取资源文件
 *
 * @author wenfa
 *
 */
public class IUAPBeanFactory {

    private static ApplicationContext context;

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }


    public static Object getBean(Class<?> clazz) {
        return context.getBeansOfType(clazz);
    }


    public static Object getBean(Class<?> clazz, boolean includeNonSingletons, boolean allowEagerInit) {
        return BeanFactoryUtils.beanOfType(context, clazz, includeNonSingletons, allowEagerInit);
    }


    public static ApplicationContext getApplicationContext() {
        return context;
    }


    public static void setApplicationContext(ApplicationContext applicationContext) {
        if (context == null) {
            context = applicationContext;
        }
    }
}