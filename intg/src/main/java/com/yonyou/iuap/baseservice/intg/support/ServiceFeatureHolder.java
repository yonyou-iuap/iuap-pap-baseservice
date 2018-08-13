package com.yonyou.iuap.baseservice.intg.support;

import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceFeatureHolder {

    private static AtomicBoolean  isInit = new AtomicBoolean(false);

    private Map<ServiceFeature, QueryFeatureExtension> extensionMap = new HashMap<>();

    private ServiceFeatureHolder() {}

    public static ServiceFeatureHolder inst() {
        if(!ServiceFeatureHolder.Inner.inst.isInit.get()) {
            synchronized(ServiceFeatureHolder.Inner.inst) {
                if(!ServiceFeatureHolder.Inner.inst.isInit.get()) {
                    ServiceFeatureHolder.Inner.inst.init();
                }
                isInit.set(true);
            }
        }
        return ServiceFeatureHolder.Inner.inst;
    }

    private void init() {
        ServiceLoader<QueryFeatureExtension> spiLoader = ServiceLoader.load(QueryFeatureExtension.class);
        for (QueryFeatureExtension extension:spiLoader){
            ServiceFeature feature= ServiceFeature.getFeature(extension.getClass());
            if (!feature.equals(ServiceFeature.NONE)){
                extensionMap.put(feature, extension);
            }

        }

    }

    public QueryFeatureExtension getQueryExtension(ServiceFeature feature) {
        return extensionMap.get(feature);
    }



    private static class Inner {
        private static ServiceFeatureHolder inst = new ServiceFeatureHolder();
    }
}
