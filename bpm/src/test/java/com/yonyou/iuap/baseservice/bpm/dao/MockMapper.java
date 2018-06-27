package com.yonyou.iuap.baseservice.bpm.dao;

import com.yonyou.iuap.baseservice.bpm.model.MockModel;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.mybatis.anotation.MyBatisRepository;

@MyBatisRepository
public interface  MockMapper extends GenericExMapper<MockModel> {
}
