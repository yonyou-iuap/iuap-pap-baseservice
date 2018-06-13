package com.yonyou.iuap.baseservice.persistence.mybatis.mapper;

import com.yonyou.iuap.baseservice.model.LogicDel;
import com.yonyou.iuap.baseservice.model.Model;

/**
 * 说明：逻辑删除基础Mapper
 * @author houlf
 * 2018年6月12日
 */
public interface GenericExMapper<T extends Model & LogicDel> extends GenericMapper<T>{

}