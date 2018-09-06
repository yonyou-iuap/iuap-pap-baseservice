package com.yonyou.iuap.baseservice.intg.support;

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
    OTHER("java.lang.Class"),

//    PRINT(GenericAtService.class )
    ;

    private String clazz;

    ServiceFeature(String clazz) {
        this.clazz = clazz;
    }

    public static ServiceFeature getFeature(Object instance) {
        for (ServiceFeature feat : ServiceFeature.values()) {
            try {
                Class featClass = Class.forName(feat.clazz);
                if(featClass.isInterface()){
                    if (featClass.isInstance(instance))
                        return feat;
                }else{
                    if(instance.getClass().isAssignableFrom(featClass))
                        return feat;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return OTHER;
            }



        }
        return OTHER;
    }

}
