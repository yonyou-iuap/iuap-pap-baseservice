package com.yonyou.iuap.baseservice.bpm.model;

/**
 * 说明：工作流Model接口
 * @author houlf
 * 2018年6月12日
 */
public interface BpmModel{

	public String getFlowState();
	
	public void setFlowState(String flowState);
	
}