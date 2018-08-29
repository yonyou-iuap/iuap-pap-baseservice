package com.yonyou.iuap.baseservice.intg.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 加载spring上下文用,暂未优化
 */
@Component
@Lazy(value = false)
public class ServiceContext implements ApplicationContextAware {

    private static ApplicationContext context = null;
//    private static SpringConfigTool stools = null;
//    public synchronized static SpringConfigTool init(){
//        if(stools == null){
//            stools = new SpringConfigTool();
//        }
//        return stools;
//    }


    public static ApplicationContext getApplicationContext() {
        return context;
    }
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {

        context = applicationContext;
    }



    public synchronized static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
}
