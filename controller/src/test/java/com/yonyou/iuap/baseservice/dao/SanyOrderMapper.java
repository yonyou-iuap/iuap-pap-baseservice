package com.yonyou.iuap.baseservice.dao;

import com.yonyou.iuap.baseservice.entity.SanyOrder;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import com.yonyou.iuap.mybatis.anotation.MyBatisRepository;

@MyBatisRepository
public interface SanyOrderMapper extends GenericMapper<SanyOrder> {
	
}