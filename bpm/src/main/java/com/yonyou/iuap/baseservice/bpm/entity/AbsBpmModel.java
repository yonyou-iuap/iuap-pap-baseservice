package com.yonyou.iuap.baseservice.bpm.entity;

import javax.persistence.Column;

import com.yonyou.iuap.baseservice.entity.AbsDrModel;


/**
 * 说明：工作流基础Model
 * @author houlf
 * 2018年6月12日
 */
public abstract class AbsBpmModel extends AbsDrModel implements BpmModel{

	@Column(name="bpm_state")
	protected Integer bpmState;			//流程状态：参考BpmExUtil

	public Integer getBpmState() {
		return bpmState;
	}

	public void setBpmState(Integer bpmState) {
		this.bpmState = bpmState;
	}
	
}