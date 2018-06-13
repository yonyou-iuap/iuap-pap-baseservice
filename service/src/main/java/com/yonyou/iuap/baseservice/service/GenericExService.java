package com.yonyou.iuap.baseservice.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;

/**
 * 说明：基础Service扩展——支持逻辑删除
 * @author houlf
 * 2018年6月12日
 */
public class GenericExService<T extends Model & LogicDel> extends GenericService<T>{
	
	private Logger log = LoggerFactory.getLogger(GenericExService.class);

	/**
	 * 新增保存数据
	 * @param entity
	 * @return
	 */
	public T insert(T entity) {
		if(entity != null) {
			entity.setDr(0);
			entity.setTs(new Date());
			return super.insert(entity);
		}else {
			throw new RuntimeException("新增保存数据出错，对象为空!");
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
			entity.setDr(0);
			entity.setTs(new Date());
			return super.update(entity);
		}else {
			throw new RuntimeException("更新保存数据出错，对象为空!");
		}
	}
	
	/**
	 * 逻辑删除
	 * @param entity
	 * @return
	 */
	public int update4LogicDel(T entity) {
		if(entity != null) {
			entity.setDr(1);
			entity.setTs(new Date());
			int count = this.genericMapperEx.update(entity);
			if(count == 1) {
				return count;
			}else {
				log.error("删除数据出错,记录数="+count+"\r\n"+JSON.toJSONString(entity));
				throw new RuntimeException();
			}
		}else {
			throw new RuntimeException();
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
	public int delete(String id) {
		T entity = this.findById(id);
		return this.delete(entity);
	}
	
	/***************************************************/
	protected GenericExMapper<T> genericMapperEx;

	public void setIbatisMapperEx(GenericExMapper<T> mapper) {
		this.genericMapperEx = mapper;
		super.setGenericMapper(mapper);
	}

}