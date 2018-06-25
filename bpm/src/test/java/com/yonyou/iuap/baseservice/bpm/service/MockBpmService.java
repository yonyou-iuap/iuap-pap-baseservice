package com.yonyou.iuap.baseservice.bpm.service;

import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockBpmService<MockModel> extends GenericBpmService<BpmModel>{
    String mockTaskID;
    String mockProcessInstanceId;
    public void setMockProcessInstanceId(String mockProcessInstanceId) {
        this.mockProcessInstanceId = mockProcessInstanceId;
    }

    public void setMockTaskID(String taskID) {
        this.mockTaskID = taskID;
    }

    /**
     * Mock保存数据
     */
    public BpmModel save(BpmModel entity) {
        entity.setId(UUID.randomUUID().toString());
        return entity;
    }
    @Override
    public BpmModel findById(String entityId) {
        BpmModel entity =new com.yonyou.iuap.baseservice.bpm.model.MockModel();
        entity.setId(entityId);
        entity.setProcessInstanceId(mockProcessInstanceId);
        entity.setTaskId(mockTaskID);
        return entity;
    }

    @Override
    public String getNodeKey(BpmModel entity) {
        return null;
    }

    @Override
    public String getBpmFormUrl(BpmModel entity) {
        return null;
    }

    @Override
    public String getBpmCallBackUrl(BpmModel entity) {
        return null;
    }
}
