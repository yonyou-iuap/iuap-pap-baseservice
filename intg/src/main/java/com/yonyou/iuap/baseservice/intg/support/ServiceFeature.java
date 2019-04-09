package com.yonyou.iuap.baseservice.intg.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 特性实现全局预定义,运行时可以根据需求动态加载
 */
public enum ServiceFeature {
    AUDIT("com.yonyou.iuap.baseservice.intg.ext.ExtAuditTrailService"),//审计追踪信息
    REF("com.yonyou.iuap.baseservice.intg.ext.ExtReferenceService"),//本地及远程参照统一解析特性
    OTHER("java.lang.Class"),//其他，用于客户化扩展特性加载

//    PRINT(GenericAtService.class )
    ;

    private String clazz;
    private static List<String> nonClass = new ArrayList<>();//用于增强检索命中率
    ServiceFeature(String clazz) {
        this.clazz = clazz;
    }

    public static ServiceFeature getFeature(Object instance) {


        for (ServiceFeature feat : ServiceFeature.values()) {
            try {
                if (nonClass.contains(feat.clazz)){
                    continue;//证明此class根本不存在没必要做后续校验
                }
                Class featClass = Class.forName(feat.clazz);
                if (featClass.isInstance(instance))
                    return feat;
            } catch (ClassNotFoundException e) {
                nonClass.add(feat.clazz);
                e.printStackTrace();
            }
        }
        return OTHER;
    }

}
