package com.yonyou.iuap.baseservice.bpm.dao.mapper;


import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;

/**
 * 说明：工作流基础Mapper
 * @TODO  by leon
 * 工作流的表都是通过流程引擎操作的，这里的mapper是用于操作业务实体么，基本的增删该查都已经在GenericMapper里做了呀，这里还需要做什么？
 *
 * @author houlf
 * 2018年6月13日
 */
public interface GenericBpmMapper<T extends BpmModel> extends GenericMapper<T> {

}