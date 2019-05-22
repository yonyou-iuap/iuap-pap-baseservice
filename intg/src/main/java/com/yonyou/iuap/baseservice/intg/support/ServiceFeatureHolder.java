package com.yonyou.iuap.baseservice.intg.support;

import cn.hutool.core.util.ArrayUtil;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import org.springframework.core.ResolvableType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 解析在spring上下文中哪些服务实现了QueryFeatureExtension,SaveFeatureExtension或DeleteFeatureExtension
 * 并把他们缓存住,按要求返还给GenericIntegrateService,用于CRUD的整体特性集成
 */
@SuppressWarnings("unchecked")
public class ServiceFeatureHolder  {


    private static AtomicBoolean isInit = new AtomicBoolean(false);

    private static Map<String, QueryFeatureExtension> qExtMap = new HashMap<>();
    private static Map<String, SaveFeatureExtension> sExtMap = new HashMap<>();
    private static Map<String, DeleteFeatureExtension> dExtMap = new HashMap<>();

//    private ServiceFeatureHolder() {}

    /**
     * 三套map缓存的单例初始化
     */
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

    public static Set getModelExtensions(Class modelClass, Class extIf){
        Set result =new HashSet<>();
        if (modelClass.equals(Identifier.class)){
            return result;
        }
        if (!isInit.get()){
            init();
        }

        if (extIf == QueryFeatureExtension.class) {
            for (String name : qExtMap.keySet()) {
                if (qExtMap.get(name) != null) {
                    if(isExtServiceAssignedToModel(qExtMap.get(name),extIf,modelClass)) {
                        result.add( qExtMap.get(name));
                    }
                }
            }
        }
        if (extIf ==  SaveFeatureExtension.class){
            for (String name : sExtMap.keySet()) {
                if (sExtMap.get(name) != null ) {
                    if(isExtServiceAssignedToModel(sExtMap.get(name),extIf,modelClass)) {
                        result.add( sExtMap.get(name));
                    }
                }
            }
        }

        if (extIf ==  DeleteFeatureExtension.class){
            for (String name : dExtMap.keySet()) {
                if (dExtMap.get(name) != null ) {
                    if(isExtServiceAssignedToModel(dExtMap.get(name),extIf,modelClass)) {
                        result.add( dExtMap.get(name));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 解析feature service实例中实现接口的泛型model是否跟当前service的泛型model一致
     * @param instance  feature service的实例
     * @param extIf  instance实现的接口
     * @param modelClass 当前service的model类名
     * @param <T> service类型保留
     * @return
     */
    private static<T> boolean isExtServiceAssignedToModel(T instance,Class extIf,Class modelClass){

        ResolvableType serviceType = ResolvableType.forClass(extIf,instance.getClass());
        return  ArrayUtil.isNotEmpty(serviceType.getGenerics())
                && serviceType.getGeneric(0).resolve() == modelClass;

    }


}
