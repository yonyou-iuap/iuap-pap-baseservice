package com.yonyou.iuap.baseservice.intg.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 特性实现全局预定义,运行时可以根据需求动态加载
 */
public enum ServiceFeature {
    @Deprecated
    ATTACHMENT("com.yonyou.iuap.baseservice.attachment.service.AtCommonService"), //附件特性
    MULTI_TENANT("com.yonyou.iuap.baseservice.multitenant.service.MultenCommonService"),//多租户隔离特性
    LOGICAL_DEL("com.yonyou.iuap.baseservice.intg.service.DrCommonService"),//逻辑删除特性
    @Deprecated
    REFERENCE("com.yonyou.iuap.baseservice.ref.service.RefCommonService"),//本地参照特性
    @Deprecated
    REMOTE_REFERENCE("com.yonyou.iuap.baseservice.ref.service.RefRemoteService"),//远程参照解析特性
    UNI_REFERENCE("com.yonyou.iuap.baseservice.ref.service.RefUnionService"),//本地及远程参照统一解析特性
    BPM("com.yonyou.iuap.baseservice.bpm.service.BpmCommonService"),//流程特性
    I18N("com.yonyou.iuap.baseservice.intg.service.I18nCommonService"),//国际化特性
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
