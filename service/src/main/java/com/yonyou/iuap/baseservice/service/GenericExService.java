package com.yonyou.iuap.baseservice.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 说明：基础Service扩展——支持逻辑删除
 * @author houlf
 * 2018年6月12日
 */
public abstract class GenericExService<T extends Model & LogicDel> extends GenericService<T>{
	
	private Logger log = LoggerFactory.getLogger(GenericExService.class);

	protected GenericExMapper<T> genericExMapper;

	public void setGenericMapper(GenericExMapper<T> mapper) {
		this.genericExMapper = mapper;
	}

	/**
	 * 新增保存数据
	 * @param entity
	 * @return
	 */
	public T insert(T entity) {
		if(entity != null) {
			entity.setDr(LogicDel.NORMAL);
			return super.insert(entity);
		}else {
			throw new BusinessException("新增保存数据出错，对象为空!");
		}
	}

	/**
	 * 更新保存数据
	 * @param entity
	 * @return
	 */
	@Override
	public T update(T entity) {
		if(entity != null) {
			entity.setDr(LogicDel.NORMAL);
			return super.update(entity);
		}else {
			throw new BusinessException("更新保存数据出错，对象为空!");
		}
	}
	
	/**
	 * 逻辑删除
	 * @param entity
	 * @return
	 */
	public int update4LogicDel(T entity) {
		if(entity != null) {
			entity.setDr(LogicDel.DELETED);
			int count = this.genericMapperEx.update(entity);
			if(count == 1) {
				return count;
			}else {
				log.error("删除数据出错,记录数="+count+"\r\n"+JSON.toJSONString(entity));
				throw new BusinessException();
			}
		}else {
			throw new BusinessException();
		}
	}

	/**
	 * 覆盖父方法，调整为逻辑删除
	 * @param entity
	 * @return
	 */
	@Override
	public int delete(T entity) {
		return this.update4LogicDel(entity);
	}

	/**
	 * 覆盖父方法，调整为逻辑删除
	 * @param id
	 */
	@Override
	public int delete(Serializable id) {
		T entity = this.findById(id);
		return this.delete(entity);
	}

	public Page<Map<String, Object>> selectRefTable(PageRequest pageRequest,
													String tablename, String idfield, Map<String, String> condition, List<String> extColumns) {
		Page<Map<String,Object>> result = genericExMapper.selectRefTable(pageRequest,tablename,idfield, extColumns,condition).getPage();
		return result;
	}

	public Page<Map<String, Object>> selectRefTree(PageRequest pageRequest,
												   String tablename, String idfield, String pidfield,
												   String codefield, String namefield, Map<String, String> condition,List<String> extColumns) {

		Page<Map<String,Object>> result = genericExMapper.selectRefTree(pageRequest,tablename,idfield,pidfield,codefield,namefield, extColumns,condition).getPage();
		return result;
	}
	
	/***************************************************/
	protected GenericExMapper<T> genericMapperEx;

	public void setIbatisMapperEx(GenericExMapper<T> mapper) {
		this.genericMapperEx = mapper;
		super.setGenericMapper(mapper);
	}

}