package com.yonyou.iuap.baseservice.datapermission.service;

import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.datapermission.annotation.DataAccess;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.uap.ieop.security.entity.DataPermission;
import com.yonyou.uap.ieop.security.sdk.AuthRbacClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DpCommonService<T extends Model>  implements QueryFeatureExtension<T> {
    private static Logger log = LoggerFactory.getLogger(DpCommonService.class);

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams,Class modelClass) {
        if (null==modelClass|| modelClass== Model.class ){
            log.info("入参Class有误，无法进行数据权限解析");
            return searchParams;
        }
        String tenantId = InvocationInfoProxy.getTenantid();
        String sysId = InvocationInfoProxy.getSysid();
        String userId = InvocationInfoProxy.getUserid();

        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field f :fields){
            DataAccess annotaton= f.getAnnotation(DataAccess.class);
            if (annotaton!=null){

                try {
                    List<DataPermission> dataPerms
                            = AuthRbacClient.getInstance().queryDataPerms(tenantId, sysId, userId, annotaton.code());
                    if (null!=dataPerms && dataPerms.size()>0){
                        String[] ids=new String[dataPerms.size()];
                        Set<String> idSet= new HashSet<>();
                        for (int i = 0; i <dataPerms.size() ; i++) {
                             ids[i]=dataPerms.get(i).getResourceId();
                            idSet.add( dataPerms.get(i).getResourceId());
                        }
                        searchParams.addCondition(f.getName()+"_Dp", ids);
                    }
                } catch (Exception e) {
                    log.error("query permission fail！",e);
                    return searchParams;
                }
            }
        }
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        return list;
    }
}
