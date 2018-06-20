package com.yonyou.iuap.baseservice.bpm.entity;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;

/**
 * 说明：工作流Model接口
 * @author houlf
 * 2018年6月12日
 */
public interface BpmModel extends Model, LogicDel{

	Integer getBpmState();
	
	void setBpmState(Integer bpmState);
	
	String getBpmBillCode();

	String getTaskKey();


	void setTaskKey();


	void setTaskId();

	String getTaskId();

	void setProcessInstanceId();

	String getProcessInstanceId();

	String getProcessDefineCode();
	void setProcessDefineCode(String processDefineCode) ;
	
}