package com.yonyou.iuap.baseservice.bpm.entity;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;

/**
 * 说明：工作流Model接口
 *
 * @author houlf
 * 2018年6月12日
 */
public interface BpmSimpleModel extends Model,LogicDel {

    Integer getBpmState();

    void setBpmState(Integer bpmState);

    String getProcessDefineCode();

    void setProcessDefineCode(String processDefineCode);

    String getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);

    String getBpmBillCode();
}
