package com.yonyou.iuap.baseservice.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.model.BpmModel;
import com.yonyou.iuap.baseservice.model.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericBpmMapper;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：工作流基础Service
 * @author houlf
 * 2018年6月12日
 */
public class GenericBpmService<T extends BpmModel>{

	private Logger log = LoggerFactory.getLogger(GenericBpmService.class);

	/**
	 * 分页查询
	 * @param pageRequest
	 * @param searchParams
	 * @return
	 */
    public Page<T> selectAllByPage(PageRequest pageRequest, SearchParams searchParams) {
        return genericBpmMapper.selectAllByPage(pageRequest, searchParams).getPage();
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
    	return this.genericBpmMapper.queryList(queryParams);
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
    	return this.genericBpmMapper.queryListByMap(params);
    }

    /**
     * 根据ID查询数据
     * @param id
     * @return
     */
    public T findById(String id) {
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
	public int save(T entity) {
		boolean isNew = false;					//是否新增数据
		if(entity instanceof Model) {
			isNew = StrUtil.isEmpty(((Model)entity).getId());
		}
		if(isNew) {
			return insert(entity);
		}else {
			return update(entity);
		}
	}
	
	/**
	 * 新增保存数据
	 * @param entity
	 * @return
	 */
	public int insert(T entity) {
		if(entity != null) {
			entity.setId(UUID.randomUUID().toString());
			entity.setCreateTime(new Date());
			entity.setCreateUser(InvocationInfoProxy.getUserid());
			entity.setLastModified(new Date());
			entity.setLastModifyUser(InvocationInfoProxy.getUserid());
			
			entity.setDr(0);
			entity.setTs(new Date());
			
			int count = this.genericBpmMapper.insert(entity);
			log.info("新增保存数据：\r\n"+JSON.toJSONString(entity));
			return count;
		}else {
			throw new RuntimeException("新增保存数据出错，对象为空!");
		}
	}

	/**
	 * 更新保存数据
	 * @param entity
	 * @return
	 */
	public int update(T entity) {
		if(entity!=null) {
			entity.setLastModified(new Date());
			entity.setLastModifyUser(InvocationInfoProxy.getUserid());

			int count = genericBpmMapper.update(entity);
			if(count != 1) {
				log.error("更新保存数据出错，更新记录数="+count+"\r\n"+JSON.toJSONString(entity));
				throw new RuntimeException();
			}
			return count;
		}else {
			throw new RuntimeException();
		}
	}

	/**
	 * 删除数据
	 * @param entity
	 * @return
	 */
	public int delete(T entity) {
		entity.setDr(1);
		entity.setTs(new Date());
		int count = this.update(entity);
		if(count == 1) {
			return count;
		}else {
			log.error("删除数据出错,记录数="+count+"\r\n"+JSON.toJSONString(entity));
			throw new RuntimeException();
		}
	}
	
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	public int delete(String id) {
		T entity = this.findById(id);
		return this.delete(entity);
	}
	
	/**
	 * 提交工作流
	 */
	public void doSubmit() {
		
	}

	/**
	 * 撤回工作流
	 */
	public void doRevoke() {
		
	}

	/***************************************************/
	protected GenericBpmMapper<T> genericBpmMapper;

	public void setGenericBpmMapper(GenericBpmMapper<T> bpmMapper) {
		this.genericBpmMapper = bpmMapper;
	}

}