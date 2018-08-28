package com.yonyou.iuap.i18n;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * 使用框架上下文，主要获取资源文件
 *
 * @author wenfa
 *
 */
public class XmlWebApplicationContextEx extends XmlWebApplicationContext {
    protected DefaultListableBeanFactory createBeanFactory() {
        System.out.println("Init BeanFactory, allowRawInjectionDespiteWrapping is true!");
        DefaultListableBeanFactory beanFactory = super.createBeanFactory();
        beanFactory.setAllowRawInjectionDespiteWrapping(true);
        return beanFactory;
    }
}