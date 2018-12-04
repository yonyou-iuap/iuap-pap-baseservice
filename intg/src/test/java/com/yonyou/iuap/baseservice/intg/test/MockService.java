package com.yonyou.iuap.baseservice.intg.test;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.intg.service.DrCommonService;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import com.yonyou.iuap.baseservice.persistence.support.AbsCustomSelectPage;
import com.yonyou.iuap.baseservice.persistence.support.CustomSelectPageable;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.mybatis.type.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Callable;

@SuppressWarnings("ALL")
public class MockService extends GenericIntegrateService<MockEntity> {
    @Override
    protected ServiceFeature[] getFeats() {
        return new ServiceFeature[0];
    }

    @Autowired
    DrCommonService drCommonService;
    @Autowired
    GenericMapper mapper;

    public Page doSomeService(){

       return  super.customSelectPageWithFeatures(new AbsCustomSelectPage<MockEntity>(new SearchParams(), new PageRequest(1, 100)) {
            @Override
            public Page doCunstomSelectPage() {
                PageResult resul = mapper.selectAllByPage(getPageRequest(), getSearchParams());
                return resul.getPage();
            }
        });
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