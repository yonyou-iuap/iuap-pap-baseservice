package com.yonyou.iuap.baseservice.ref.dao.mapper;

import com.yonyou.iuap.mybatis.type.PageResult;
import com.yonyou.iuap.persistence.mybatis.anotation.MyBatisRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * 查询参照数据mybatis
 * @author xiadlc
 *
 * 
 */
@MyBatisRepository
public interface RefCommonMapper {

	/**
	 * 表型参照(包括树表和单表)--表的数据查询
	 * @param pageRequest
	 * @param tablename
	 * @param idfield
	 * @param extcols
	 * @param condmap
	 * @return
	 */
	PageResult<Map<String,Object>> treerefselectAllByPage(
			@Param("page") PageRequest pageRequest,
			@Param("tablename") String tablename,
			@Param("idfield") String idfield,
			@Param("codefield") String codefield,
			@Param("namefield") String namefield,
			@Param("extcols") List<String> extcols,
			@Param("keyword") String keyword,
			@Param("condmap") Map<String, String> condmap,
			@Param("condQu") Map<String, String> condQu,
			@Param("condList") List<String> condList,
			@Param("fidfield") String fidfield,
			@Param("fidvalue") String fidvalue,
			@Param("ids") List<String> ids);
	/**
	 * 根据ids批量查询
	 * @param tablename
	 * @param idfield
	 * @param extcols
	 * @return
	 */
	List<Map<String, Object>> findRefListByIds(
            @Param("tablename") String tablename,
            @Param("idfield") String idfield,
            @Param("extcols") List<String> extcols,
            @Param("ids") List<String> ids);

	/**
	 * 树表型参照--树的数据查询
	 * @param pageRequest
	 * @param tablename
	 * @param idfield
	 * @param codefield
	 * @param namefield
	 * @return
	 */
	PageResult<Map<String,Object>> selectRefTree(
			@Param("page") PageRequest pageRequest,
			@Param("tablename") String tablename,
			@Param("idfield") String idfield,
			@Param("codefield") String codefield,
			@Param("namefield") String namefield,
			@Param("pidfield") String pidfield,
			@Param("extcols") List<String> extcols,
			@Param("keyword") String keyword,
			@Param("condmap") Map<String, String> condmap,
			@Param("condQu") Map<String, String> conditionQuoter,
			@Param("condList") List<String> condList,
			@Param("ids") List<String> ids);
	
	
	/**
	 * 单选多选型参照
	 * @param pageRequest
	 * @param tablename
	 * @param idfield
	 * @param codefield
	 * @param namefield
	 * @return
	 */
	PageResult<Map<String,Object>> selectRefCheck(
			@Param("page") PageRequest pageRequest,
			@Param("tablename") String tablename,
			@Param("idfield") String idfield,
			@Param("codefield") String codefield,
			@Param("namefield") String namefield,
			@Param("extcols") List<String> extcols,
			@Param("keyword") String keyword,
			@Param("condmap") Map<String, String> condmap,
			@Param("condQu") Map<String, String> conditionQuoter,
			@Param("condList") List<String> condList,
			@Param("ids") List<String> ids);

	/**
	 * 根据ids批量查询
	 * @param tablename
	 * @param idfield
	 * @param extcols
	 * @return
	 */
	List<Map<String, Object>> getByIds(
			@Param("tablename") String tablename,
			@Param("idfield") String idfield,
			@Param("codefield") String codefield,
			@Param("namefield") String namefield,
			@Param("extcols") List<String> extcols,
			@Param("ids") List<String> ids);


	/**
	 * 根据ids和keyword进行搜索
	 * @param tablename
	 * @param idfield
	 * @param codefield
	 * @param namefield
	 * @param extColumns
	 * @param ids
	 * @param keyword
	 * @return
	 */
	List<Map<String,Object>> likeSearch(
			@Param("tablename") String tablename,
			@Param("idfield") String idfield,
			@Param("codefield") String codefield,
			@Param("namefield") String namefield,
			@Param("extcols") List<String> extColumns,
			@Param("ids") List<String> ids,
			@Param("keyword") String keyword);

}
