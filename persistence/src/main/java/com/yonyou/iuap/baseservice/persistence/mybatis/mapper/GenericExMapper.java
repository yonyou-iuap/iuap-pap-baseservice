package com.yonyou.iuap.baseservice.persistence.mybatis.mapper;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mybatis.type.PageResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * 说明：逻辑删除基础Mapper
 * @author houlf
 * 2018年6月12日
 */
public interface GenericExMapper<T extends Model & LogicDel> extends GenericMapper<T>{

}