package com.yonyou.iuap.baseservice.bpm.model;

import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;
import com.yonyou.iuap.baseservice.support.generator.GeneratedValue;
import com.yonyou.iuap.baseservice.support.generator.Strategy;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Table(name = "example_order_bpm")
public class MockModel implements BpmModel
//extends AbsBpmModel
{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=Strategy.UUID, module="order")
    @Condition(match=Match.EQ)
    private String id;


    public void setId(Serializable id){
        this.id=id.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name="dr")
    @Condition(match=Match.EQ)
    private Integer dr = 0;

    public Integer getDr() {
        return dr;
    }
//    @Override
    public void setDr(Integer dr) {
        this.dr = dr;
    }

    private String orderCode;
    private String orderName;


    private String supplier;
    private String supplierName;
    private Integer type;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

//    @Override
    public String getBpmBillCode() {
        return null;
    }


    @Column(name="create_time")
    protected String createTime;

    @Column(name="create_user")
    protected String createUser;

    @Column(name="last_modified")
    protected String lastModified;

    @Column(name="last_modify_user")
    protected String lastModifyUser;

    @Version
    @Column(name="ts")
    protected String ts;


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifyUser() {
        return lastModifyUser;
    }

    public void setLastModifyUser(String lastModifyUser) {
        this.lastModifyUser = lastModifyUser;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @Column(name="bpm_state")
    protected Integer bpmState;			//流程状态：参考BpmExUtil
    @Column(name="bpm_task_key")
    protected String taskKey;			//流程及节点定义：例如ApproveUserTask
    @Column(name="bpm_taskid")
    protected String taskId;			//流程当前环节任务id
    @Column(name="bpm_process_instance")
    protected String processInstanceId;	//流程实例id
//    @Column(name="bpm_process_define")
    protected String processDefineCode;	//流程定义id

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

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public void setComment(String comment) {

    }

}
