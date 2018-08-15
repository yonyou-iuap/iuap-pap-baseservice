package com.yonyou.iuap.baseservice.intg.support;

import com.yonyou.iuap.baseservice.attachment.service.AtCommonService;
import com.yonyou.iuap.baseservice.bpm.service.BpmCommonService;
import com.yonyou.iuap.baseservice.intg.service.DrCommonService;
import com.yonyou.iuap.baseservice.multitenant.service.MultenCommonService;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;

/**
 * 特性实现全局预定义,运行时可以根据需求动态加载
 */
public enum ServiceFeature {
    ATTACHMENT(AtCommonService.class),
    MULTI_TENANT(MultenCommonService.class),
    LOGICAL_DEL(DrCommonService.class),
    REFERENCE(RefCommonService.class),
    BPM(BpmCommonService.class),
    OTHER(Class.class),

//    PRINT(GenericAtService.class )
    ;

    private Class clazz;

    ServiceFeature(Class clazz) {
        this.clazz = clazz;
    }

    public static ServiceFeature getFeature(Object instance) {
        for (ServiceFeature feat : ServiceFeature.values()) {
            if (feat.clazz.isInstance(instance))
                return feat;
        }
        return OTHER;
    }

}
