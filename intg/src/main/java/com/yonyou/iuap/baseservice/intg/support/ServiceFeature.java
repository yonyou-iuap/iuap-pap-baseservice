package com.yonyou.iuap.baseservice.intg.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 特性实现全局预定义,运行时可以根据需求动态加载
 */
public enum ServiceFeature {
    ATTACHMENT("com.yonyou.iuap.baseservice.attachment.service.AtCommonService"),
    MULTI_TENANT("com.yonyou.iuap.baseservice.multitenant.service.MultenCommonService"),
    LOGICAL_DEL("com.yonyou.iuap.baseservice.intg.service.DrCommonService"),
    REFERENCE("com.yonyou.iuap.baseservice.ref.service.RefCommonService"),
    BPM("com.yonyou.iuap.baseservice.bpm.service.BpmCommonService"),
    DATA_PERMISSION("com.yonyou.iuap.baseservice.datapermission.service.DpCommonService"),
    I18N("com.yonyou.iuap.baseservice.intg.service.I18nCommonService"),
    OTHER("java.lang.Class"),

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
