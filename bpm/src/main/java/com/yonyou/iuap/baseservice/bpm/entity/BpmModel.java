package com.yonyou.iuap.baseservice.bpm.entity;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;

/**
 * 说明：工作流Model接口
 *
 * @author houlf
 * 2018年6月12日
 */
public interface BpmModel extends BpmSimpleModel,Model, LogicDel {



    String getTaskKey();

    void setTaskKey(String taskKey);

    String getTaskId();

    void setTaskId(String taskId);



    String getComment() ;
    void setComment(String comment) ;
}