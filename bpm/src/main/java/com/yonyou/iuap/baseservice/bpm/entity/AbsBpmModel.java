package com.yonyou.iuap.baseservice.bpm.entity;

import com.yonyou.iuap.baseservice.entity.AbsDrModel;

import javax.persistence.Column;
import javax.persistence.Transient;


/**
 * 说明：工作流基础Model,建议实现BpmModel或BpmSimpleModel接口
 * @author houlf
 * 2018年6月12日
 */
public abstract class AbsBpmModel extends AbsDrModel implements BpmModel {

	@Column(name="bpm_state")
	protected Integer bpmState;			//流程状态：参考BpmExUtil
//	@Column(name="bpm_task_key")
    @Transient
	protected String taskKey;			//流程及节点定义：例如ApproveUserTask
    @Transient
    //@Column(name="bpm_taskid")
	protected String taskId;			//流程当前环节任务id
    @Transient
    //@Column(name="bpm_process_instance")
	protected String processInstanceId;	//流程实例id
	//@Column(name="bpm_process_define")
    @Transient
	protected String processDefineCode;	//流程定义编码
	@Transient
	protected String comment;	//流程审批意见



	public String getTaskKey() {
		return taskKey;
	}
	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Integer getBpmState() {
		return bpmState;
	}
	public void setBpmState(Integer bpmState) {
		this.bpmState = bpmState;
	}

	public String getProcessDefineCode() {return processDefineCode;	}
	public void setProcessDefineCode(String processDefineCode) {
		this.processDefineCode = processDefineCode;
	}

	public String getComment() {return comment;	}
	public void setComment(String comment) {this.comment = comment;	}
}