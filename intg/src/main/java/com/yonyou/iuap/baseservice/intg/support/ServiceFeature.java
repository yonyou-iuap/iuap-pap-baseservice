package com.yonyou.iuap.baseservice.intg.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 特性实现全局预定义,运行时可以根据需求动态加载
 */
public enum ServiceFeature {
    MULTI_TENANT("com.yonyou.iuap.baseservice.multitenant.service.MultenCommonService"),//多租户隔离特性
    AUDIT_TRAIL("com.yonyou.iuap.baseservice.intg.ext.AuditTrailCommonService"),//审计追踪信息
    UNION_REFERENCE("com.yonyou.iuap.baseservice.ref.service.RefUnionService"),//本地及远程参照统一解析特性
    BPM("com.yonyou.iuap.baseservice.bpm.service.BpmCommonService"),//流程特性
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
