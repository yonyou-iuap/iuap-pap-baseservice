package com.yonyou.iuap.baseservice.bpm.dao.mapper;


import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;

/**
 * 说明：工作流基础Mapper
 * @author houlf
 * 2018年6月13日
 */
public interface GenericBpmMapper<T extends BpmModel> extends GenericExMapper<T>{

}