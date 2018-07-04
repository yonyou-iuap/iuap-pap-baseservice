package com.yonyou.iuap.baseservice.bpm.service;

import com.yonyou.iuap.baseservice.bpm.dao.MockMapper;
import com.yonyou.iuap.baseservice.bpm.model.MockModel;
import com.yonyou.iuap.bpm.pojo.BPMFormJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MockBpmService extends GenericBpmService<MockModel>{

    private MockMapper mockMapper;

    @Autowired
    public void setSanyOrderMapper(MockMapper mockMapper) {
        this.mockMapper = mockMapper;
        super.setGenericMapper(mockMapper);
    }


    /**
     * Mock保存数据
     */
//    public BpmModel save(BpmModel entity) {
//        entity.setId(UUID.randomUUID().toString());
//        return entity;
//    }
//    @Override
//    public BpmModel findById(String entityId) {
////        BpmModel entity =new com.yonyou.iuap.baseservice.bpm.model.MockModel();
////        entity.setId(entityId);
////        entity.setProcessInstanceId(mockProcessInstanceId);
////        entity.setTaskId(mockTaskID);
////        return entity;
////    }


}
