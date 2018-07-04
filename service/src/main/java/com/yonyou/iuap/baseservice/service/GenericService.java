package com.yonyou.iuap.baseservice.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.annotation.CodingEntity;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import com.yonyou.iuap.baseservice.service.util.CodingUtil;
import com.yonyou.iuap.baseservice.support.generator.GeneratorManager;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;

import cn.hutool.core.util.StrUtil;

/** 
 * 说明：基础Service基类
 * @author houlf
 * 2018年6月12日
 */
public abstract class GenericService<T extends Model>{
	
	private Logger log = LoggerFactory.getLogger(GenericService.class);

	/**
	 * 分页查询
	 * @param pageRequest
	 * @param searchParams
	 * @return
	 */
    public Page<T> selectAllByPage(PageRequest pageRequest, SearchParams searchParams) {
        return genericMapper.selectAllByPage(pageRequest, searchParams).getPage();
    }

    /**
     * 查询所有数据
     * @return
     */
    public List<T> findAll(){
    	Map<String,Object> queryParams = new HashMap<String,Object>();
    	return this.queryList(queryParams);
    }
    
    /**
     * 根据参数查询List
     * @param queryParams
     * @return
     */
    public List<T> queryList(Map<String,Object> queryParams){
    	return this.genericMapper.queryList(queryParams);
    }
    
    /**
     * 根据字段名查询List
     * @param name
     * @param value
     * @return
     */
    public List<T> queryList(String name, Object value){
    	Map<String,Object> queryParams = new HashMap<String,Object>();
    	queryParams.put(name, value);
    	return this.queryList(queryParams);
    }
    
    /**
     * 根据参数查询List【返回值为List<Map>】
     * @param params
     * @return
     */
    public List<Map<String,Object>> queryListByMap(Map<String,Object> params){
    	return this.genericMapper.queryListByMap(params);
    }

    /**
     * 根据ID查询数据
     * @param id
     * @return
     */
    public T findById(Serializable id) {
    	return this.findUnique("id", id);
    }

    /**
     * 查询唯一数据
     * @param name
     * @param value
     * @return
     */
    public T findUnique(String name, Object value) {
    	List<T> listData = this.queryList(name, value);
    	if(listData!=null && listData.size()==1) {
    		return listData.get(0);
    	}else {
    		throw new RuntimeException("检索数据不唯一, "+name + ":" + value);
    	}
    }
    
    /**
     * 保存数据
     * @param entity
     * @return
     */
	public T save(T entity) {
		boolean isNew = false;					//是否新增数据
		if(entity instanceof Model) {
			if(entity.getId()==null) {
				isNew = true;
			}else {
				isNew = StrUtil.isEmptyIfStr(entity.getId());
			}
		}
		if(isNew) {
			return insert(entity);
		}else {
			return update(entity);
		}
	}
	
	/**
	 * 批量保存
	 * @param listEntity
	 */
	public void saveBatch(List<T> listEntity){
		for(int i=0; i<listEntity.size(); i++) {
			this.save(listEntity.get(i));
		}
	}
	
	/**
	 * 新增保存数据
	 * @param entity
	 * @return
	 */
	public T insert(T entity) {
		if(entity != null) {
			//ID为空的情况下，生成生成主键
			if(entity.getId()==null || StrUtil.isBlankIfStr(entity.getId())) {
			    Serializable id = GeneratorManager.generateID(entity);
				entity.setId(id);
			}
			String now = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss SSS");
			entity.setCreateTime(now);
			entity.setCreateUser(InvocationInfoProxy.getUserid());
			entity.setLastModified(now);
			entity.setLastModifyUser(InvocationInfoProxy.getUserid());
			entity.setTs(now);
			
			if(entity.getClass().getAnnotation(CodingEntity.class)!=null) {
				CodingUtil.inst().buildCoding(entity);		//按编码规则设置编码
			}
			
			this.genericMapper.insert(entity);
			log.info("新增保存数据：\r\n"+JSON.toJSONString(entity));
			return entity;
		}else {
			throw new RuntimeException("新增保存数据出错，对象为空!");
		}
	}

	/**
	 * 更新保存数据
	 * @param entity
	 * @return
	 */
	public T update(T entity) {
		if(entity!=null) {
			String now = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss SSS");
			entity.setLastModified(now);
			entity.setLastModifyUser(InvocationInfoProxy.getUserid());

			int count = genericMapper.update(entity);
			if(count != 1) {
				log.error("更新保存数据出错，更新记录数="+count+"\r\n"+JSON.toJSONString(entity));
				throw new RuntimeException("更新保存数据出错，更新记录数="+count);
			}
			return entity;
		}else {
			log.error("更新保存数据出错，输入参数对象为空!");
			throw new RuntimeException("更新保存数据出错，输入参数对象为空!");
		}
	}

	/**
	 * 删除数据
	 * @param entity
	 * @return
	 */
	public int deleteBatch(List<T> list) {
		int count = 0;
		for(T entity: list) {
			count += this.delete(entity);
		}
		return count;
	}
	
	/**
	 * 删除数据
	 * @param entity
	 * @return
	 */
	public int delete(T entity) {
		return this.delete(entity.getId());
	}

	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	public int delete(Serializable id) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		int count = this.genericMapper.delete(data);
		if(count == 1) {
			return count;
		}else {
			log.error("删除数据出错,记录数="+count+"\r\n"+JSON.toJSONString(id));
			throw new RuntimeException("删除数据出错,记录数="+count+"\r\n"+JSON.toJSONString(id));
		}
	}

	/***************************************************/
	protected GenericMapper<T> genericMapper;

	public void setGenericMapper(GenericMapper<T> mapper) {
		this.genericMapper = mapper;
	}

}