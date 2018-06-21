package com.yonyou.iuap.baseservice.bpm.vo;

import yonyou.bpm.rest.request.RestVariable;

import java.util.ArrayList;
import java.util.List;

public class BpmVo {

    private String processDefinitionKey = "";	// 流程定义编码
    private String processInstanceName = "";	// 流程实例名称
    private String title = "";	                // 流程标题
    private String formId = "";	                // 单据id
    private String orgId = "";	                // 组织
    private String billMarker = "";	            // 制单人
    private String billNo = "";	                // 单据号
    private String formUrl = "";                // 单据url
    private boolean isApproved= true;	        // 流程审批标识
    private String comment ="";                 // 流程评论,审批意见
    private List<RestVariable> otherVariables = new ArrayList();	// 其他流程变量


    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getBillMarker() {
        return billMarker;
    }

    public void setBillMarker(String billMarker) {
        this.billMarker = billMarker;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<RestVariable> getOtherVariables() {
        return otherVariables;
    }

    public void setOtherVariables(List<RestVariable> otherVariables) {
        this.otherVariables = otherVariables;
    }

    @Override
    public String toString() {
        return "BpmVo{" +
                "processDefinitionKey='" + processDefinitionKey + '\'' +
                ", processInstanceName='" + processInstanceName + '\'' +
                ", title='" + title + '\'' +
                ", formId='" + formId + '\'' +
                ", orgId='" + orgId + '\'' +
                ", billMarker='" + billMarker + '\'' +
                ", billNo='" + billNo + '\'' +
                ", formUrl='" + formUrl + '\'' +
                ", isApproved=" + isApproved +
                ", comment='" + comment + '\'' +
                '}';
    }
}
