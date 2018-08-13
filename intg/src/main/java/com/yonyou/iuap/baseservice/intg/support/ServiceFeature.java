package com.yonyou.iuap.baseservice.intg.support;

import com.yonyou.iuap.baseservice.attachment.service.GenericAtService;
import com.yonyou.iuap.baseservice.intg.service.GenericDrService;
import com.yonyou.iuap.baseservice.multitenant.service.GenericMultiTenantService;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;

/**
 * 特性实现全局预定义,运行时可以根据需求动态加载
 */
public enum ServiceFeature {
    ATTACHMENT(GenericAtService.class ),
    MULTI_TENANT(GenericMultiTenantService.class ),
//    ASSOCIATIVE(GenericAssoController.class ),
    LOGICAL_DEL(GenericDrService.class ),
    REFERENCE(RefCommonService.class ),
    NONE(Class.class ),
//    BPM(GenericAtService.class ),
//    PRINT(GenericAtService.class )
    ;

    private Class clazz;

    ServiceFeature(Class clazz){
        this.clazz=clazz;
    }

    public static ServiceFeature getFeature(Class clazz){
       for(ServiceFeature feat : ServiceFeature.values() ){
           if (feat.clazz==clazz)
               return feat;
       }
       return NONE;
    }

}
