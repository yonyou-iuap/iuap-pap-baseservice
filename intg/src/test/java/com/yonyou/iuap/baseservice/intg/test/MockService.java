package com.yonyou.iuap.baseservice.intg.test;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;

import java.io.Serializable;
import java.util.ArrayList;

public class MockService extends GenericIntegrateService<MockEntity> {
    @Override
    protected ServiceFeature[] getFeats() {
        return new ServiceFeature[0];
    }

    public void doSomeService(){

        super.customDeleteWithFeatures((x)->{
                return 1;

        },new MockEntity());

    }
}

class MockEntity implements Model{

    @Override
    public Serializable getId() {
        return null;
    }

    @Override
    public void setId(Serializable id) {

    }

    @Override
    public String getCreateTime() {
        return null;
    }

    @Override
    public void setCreateTime(String createTime) {

    }

    @Override
    public String getCreateUser() {
        return null;
    }

    @Override
    public void setCreateUser(String createUser) {

    }

    @Override
    public String getLastModified() {
        return null;
    }

    @Override
    public void setLastModified(String lastModified) {

    }

    @Override
    public String getLastModifyUser() {
        return null;
    }

    @Override
    public void setLastModifyUser(String lastModifyUser) {

    }

    @Override
    public String getTs() {
        return null;
    }

    @Override
    public void setTs(String ts) {

    }
}