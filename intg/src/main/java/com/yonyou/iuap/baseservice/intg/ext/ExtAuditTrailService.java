package com.yonyou.iuap.baseservice.intg.ext;

import cn.hutool.core.date.DateUtil;
import com.yonyou.iuap.baseservice.entity.AuditTrail;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 增补审计信息
 *
 * @author leon
 * @date 2019/4/8
 * @since UCF1.0
 */
@Service
public class ExtAuditTrailService<T extends Identifier& AuditTrail>  implements SaveFeatureExtension<T> {
    @Override
    public T prepareEntityBeforeSave(T entity) {
        if(entity.getId()== null){
            entity.setCreateTime(DateUtil.formatDate(new Date()));
            entity.setLastModified(DateUtil.formatDate(new Date()));
            entity.setCreateUser(InvocationInfoProxy.getUserid());
            entity.setLastModifyUser(InvocationInfoProxy.getUserid());
        }else{
            entity.setLastModified(DateUtil.formatDate(new Date()));
            entity.setLastModifyUser(InvocationInfoProxy.getUserid());
        }
        return entity;
    }

    @Override
    public T afterEntitySave(T entity) {
        return entity;
    }
}
