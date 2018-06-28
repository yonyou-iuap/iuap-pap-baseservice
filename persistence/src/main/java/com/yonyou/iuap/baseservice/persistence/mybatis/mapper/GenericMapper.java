package com.yonyou.iuap.baseservice.persistence.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.data.domain.PageRequest;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.annotation.MethodMapper;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.mybatis.type.PageResult;


/**
 * 说明：基础Mapper
 * @author houlf
 * 2018年6月12日
 */
public interface GenericMapper<T extends Model> {

	public PageResult<T> selectAllByPage(@Param("page") PageRequest pageRequest, @Param("condition") SearchParams searchParams);
	
	@MethodMapper(type=SqlCommandType.SELECT)
	public List<T> queryList(@Param("condition")Map<String,Object> params);

	@MethodMapper(type=SqlCommandType.SELECT)
	public List<Map<String,Object>> queryListByMap(@Param("condition")Map<String,Object> params);

	@MethodMapper(type=SqlCommandType.INSERT)
    public int insert(T entity);
	
	@MethodMapper(type=SqlCommandType.UPDATE)
	public int update(T entity);

	@MethodMapper(type=SqlCommandType.DELETE)
	public int delete(@Param("condition")Map<String,Object> params);

}