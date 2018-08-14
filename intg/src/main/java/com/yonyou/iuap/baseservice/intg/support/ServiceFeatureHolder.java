package com.yonyou.iuap.baseservice.intg.support;

import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceFeatureHolder  {


    private static AtomicBoolean isInit = new AtomicBoolean(false);

    private static Map<String, QueryFeatureExtension> extensionMap = new HashMap<>();

//    private ServiceFeatureHolder() {}


    private synchronized static void init() {
        if (isInit.get()) {
            return;
        }
        String[] names = ServiceContext.getApplicationContext().getBeanNamesForType(QueryFeatureExtension.class);
        for (String name : names) {
            System.out.println("========>" + name);
            QueryFeatureExtension instance = ServiceContext.getApplicationContext().getBean(name, QueryFeatureExtension.class);
            ServiceFeature feature = ServiceFeature.getFeature(instance);
            if (feature.equals(ServiceFeature.OTHER)) {
                extensionMap.put(name,instance);
            }else{
                extensionMap.put(feature.name(), instance);
            }
        }
        isInit.set(true);
    }

    public static QueryFeatureExtension getQueryExtension(String feature) {
        if (isInit.get()) {
            return extensionMap.get(feature);
        } else {
            init();
        }
        return extensionMap.get(feature);
    }


}
