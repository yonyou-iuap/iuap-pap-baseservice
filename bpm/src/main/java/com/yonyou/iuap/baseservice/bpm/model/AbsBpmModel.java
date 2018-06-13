package com.yonyou.iuap.baseservice.bpm.model;

import javax.persistence.Column;

import com.yonyou.iuap.baseservice.model.AbsDeleteModel;

/**
 * 说明：工作流基础Model
 * @author houlf
 * 2018年6月12日
 */
public class AbsBpmModel extends AbsDeleteModel implements BpmModel{

	@Column(name="flow_state")
	protected String flowState;			//流程状态：0-未启动，1：流程中，2：已完结

	public String getFlowState() {
		return flowState;
	}

	public void setFlowState(String flowState) {
		this.flowState = flowState;
	}

}