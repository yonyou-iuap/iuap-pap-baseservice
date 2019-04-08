package com.yonyou.iuap.baseservice.entity;

/**
 * 刷行式审计追踪
 *
 * @author leon
 * @date 2019/4/8
 * @since UCF1.0
 */
public interface AuditTrail {
     String getCreateTime();

     void setCreateTime(String createTime);

     String getCreateUser();

     void setCreateUser(String createUser);

     String getLastModified();

     void setLastModified(String lastModified);

     String getLastModifyUser();

     void setLastModifyUser(String lastModifyUser);

}
