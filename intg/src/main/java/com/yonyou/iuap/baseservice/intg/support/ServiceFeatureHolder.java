package com.yonyou.iuap.baseservice.intg.support;

import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceFeatureHolder  {


    private static AtomicBoolean isInit = new AtomicBoolean(false);

    private static Map<String, QueryFeatureExtension> qExtMap = new HashMap<>();
    private static Map<String, SaveFeatureExtension> sExtMap = new HashMap<>();
    private static Map<String, DeleteFeatureExtension> dExtMap = new HashMap<>();

//    private ServiceFeatureHolder() {}


    private synchronized static void init() {
        if (isInit.get()) {
            return;
        }
        String[] names = ServiceContext.getApplicationContext().getBeanNamesForType(QueryFeatureExtension.class);
        for (String name : names) {
            QueryFeatureExtension instance = ServiceContext.getApplicationContext().getBean(name, QueryFeatureExtension.class);
            ServiceFeature feature = ServiceFeature.getFeature(instance);
            if (feature.equals(ServiceFeature.OTHER)) {
                qExtMap.put(name,instance);
            }else{
                qExtMap.put(feature.name(), instance);
            }
        }

        names = ServiceContext.getApplicationContext().getBeanNamesForType(SaveFeatureExtension.class);
        for (String name : names) {
            SaveFeatureExtension instance = ServiceContext.getApplicationContext().getBean(name, SaveFeatureExtension.class);
            ServiceFeature feature = ServiceFeature.getFeature(instance);
            if (feature.equals(ServiceFeature.OTHER)) {
                sExtMap.put(name,instance);
            }else{
                sExtMap.put(feature.name(), instance);
            }
        }

        names = ServiceContext.getApplicationContext().getBeanNamesForType(DeleteFeatureExtension.class);
        for (String name : names) {
            DeleteFeatureExtension instance = ServiceContext.getApplicationContext().getBean(name, DeleteFeatureExtension.class);
            ServiceFeature feature = ServiceFeature.getFeature(instance);
            if (feature.equals(ServiceFeature.OTHER)) {
                dExtMap.put(name,instance);
            }else{
                dExtMap.put(feature.name(), instance);
            }
        }
        isInit.set(true);
    }

    public static QueryFeatureExtension getQueryExtension(String feature) {
        if (isInit.get()) {
            return qExtMap.get(feature);
        } else {
            init();
        }
        return qExtMap.get(feature);
    }


    public static SaveFeatureExtension getSaveExtension(String feature) {
        if (isInit.get()) {
            return sExtMap.get(feature);
        } else {
            init();
        }
        return sExtMap.get(feature);
    }

    public static DeleteFeatureExtension getDeleteExtension(String feature) {
        if (isInit.get()) {
            return dExtMap.get(feature);
        } else {
            init();
        }
        return dExtMap.get(feature);
    }

}
