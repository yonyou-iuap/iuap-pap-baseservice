package com.yonyou.iuap.baseservice.multitenant.service;

import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.multitenant.dao.mapper.GenericMultiTenantMapper;
import com.yonyou.iuap.baseservice.multitenant.entity.MultiTenant;
import com.yonyou.iuap.baseservice.service.GenericService;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 说明：多租户基础Service
 *
 * @author jhb
 * 2018年8月8日
 *
 */
public class GenericMultiTenantService<T extends MultiTenant> extends GenericService<T> implements QueryFeatureExtension<T> {

	protected Logger log = LoggerFactory.getLogger(GenericMultiTenantService.class);

	protected GenericMultiTenantMapper multiTenantMapper;

	/**
	 * 设置dao层并调用父类dao层注入方法
	 * @param multiTenantMapper
	 */
	public void setmultiTenantMapper(GenericMultiTenantMapper<T> multiTenantMapper) {
		this.multiTenantMapper = multiTenantMapper;
		super.setGenericMapper(multiTenantMapper);
	}


	/**
	 * 分页查询
	 * @param pageRequest 分页条件
	 * @param searchParams 查询条件
	 * @return 分页数据
	 */
	@Override
	public Page<T> selectAllByPage(PageRequest pageRequest, SearchParams searchParams) {
		return this.selectAllByPageWithSign(pageRequest, searchParams,InvocationInfoProxy.getTenantid());
	}



	/**
	 * 查询所有数据
	 * @return 数据列表
	 */
	@Override
	public List<T> findAll(){
		return this.findAllWithSign(InvocationInfoProxy.getTenantid());
	}




	/**
	 * 根据参数查询List
	 * @param queryParams 查询条件
	 * @return 数据列表
	 */
	@Override
	public List<T> queryList(Map<String,Object> queryParams){
		return this.queryListWithSign(queryParams,InvocationInfoProxy.getTenantid());
	}


	/**
	 * 根据字段名查询List
	 * @param name  数据库字段 key
	 * @param value 字段值 value
	 * @return 数据列表
	 */
	@Override
	public List<T> queryList(String name, Object value){
		return this.queryListWithSign(name,value,InvocationInfoProxy.getTenantid());
	}



	/**
	 * 根据参数查询List【返回值为List<Map>】
	 * @param params 参数
	 * @return 数据列表
	 */
	@Override
	public List<Map<String,Object>> queryListByMap(Map<String,Object> params){
		return this.queryListByMapWithSign(params,InvocationInfoProxy.getTenantid());
	}



	/**
	 * 根据ID查询数据
	 * @param id 数据id
	 * @return 数据对象
	 */
	@Override
	public T findById(Serializable id) {
		return this.findByIdWithSign(id,InvocationInfoProxy.getTenantid());
	}


	/**
	 * 查询唯一数据
	 * @param name  数据库字段 key
	 * @param value 字段值 value
	 * @return 数据对象
	 */
	@Override
	public T findUnique(String name, Object value) {
		return this.findUniqueWithSign(name,value,InvocationInfoProxy.getTenantid());
	}


	/**
	 * 保存方法
	 * @param entity 数据实体
	 * @return 保存后的数据实体
	 */
	@Override
	public T save(T entity) {
		boolean isNew = false;
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
	 * 新增保存数据
	 * @param entity 数据实体
	 * @return 插入后的数据实体
	 */
	@Override
	public T insert(T entity) {
		if(entity != null) {
			entity.setTenantid(InvocationInfoProxy.getTenantid());
			return insertWithSign(entity);
		}else {
			throw new RuntimeException("新增保存数据出错，对象为空!");
		}
	}

	/**
	 * 更新保存数据
	 * @param entity 数据实体
	 * @return 更新后的数据实体
	 */
	@Override
	public T update(T entity) {
		entity.setTenantid(InvocationInfoProxy.getTenantid());
		return updateWithSign(entity);
	}

	/**
	 * 批量删除数据
	 * @param list 数据列表
	 * @return 成功删除的条数
	 */

	@Override
	public int deleteBatch(List<T> list) {
		int count = 0;
		for(T entity: list) {
			count += this.delete(entity);
		}
		return count;
	}

	/**
	 * 删除数据
	 * @param entity 数据实体
	 * @return 是否成功删除
	 */
	@Override
	public int delete(T entity) {
		return this.deleteWithSign(entity);
	}

	/**
	 * 根据id删除数据
	 * @param id 数据id
	 * @return 是否删除
	 */
	@Override
	public int delete(Serializable id) {
		return this.deleteWithSign(id,InvocationInfoProxy.getTenantid());
	}


	/**
	 * 分页查询
	 * @param pageRequest 分页条件
	 * @param searchParams 查询条件
	 * @param tenantid 租户id
	 * @return 数据分页列表
 	 */
	public Page<T> selectAllByPageWithSign(PageRequest pageRequest, SearchParams searchParams,String tenantid) {
		Map<String,Object> searchMap=searchParams.getSearchMap();
		checkQueryMapTenantidWithSign(searchMap,tenantid);
		return super.selectAllByPage(pageRequest,searchParams);
	}

	/**
	 * 查询所有数据
	 * @param tenantid 租户id
	 * @return  数据列表
	 */
	public List<T> findAllWithSign(String tenantid){
		Map<String,Object> queryParams = new HashMap<>();
		checkQueryMapTenantidWithSign(queryParams,tenantid);
		return this.multiTenantMapper.queryList(queryParams);
	}

	/**
	 * 数据查询
	 * @param queryParams 查询条件
	 * @param tenantid 租户id
	 * @return 数据列表
	 */
	public List<T> queryListWithSign(Map<String,Object> queryParams,String tenantid){
		checkQueryMapTenantidWithSign(queryParams,tenantid);
		return super.queryList(queryParams);
	}

	/**
	 * 查询数据
	 * @param name  数据库字段 key
	 * @param value 字段值 value
	 * @param tenantid 租户id
	 * @return 数据列表
	 */
	public List<T> queryListWithSign(String name, Object value,String tenantid){
		Map<String,Object> queryParams = new HashMap<>(2);
		queryParams.put(name, value);
		checkQueryMapTenantidWithSign(queryParams,tenantid);
		return super.queryList(queryParams);
	}

	/**
	 * 查询数据
	 * @param params   查询条件
	 * @param tenantid 租户id
	 * @return 数据列表
	 */
	public List<Map<String,Object>> queryListByMapWithSign(Map<String,Object> params,String tenantid){
		checkQueryMapTenantidWithSign(params,tenantid);
		return super.queryListByMap(params);
	}

	/**
	 * 查询数据
	 * @param id 数据id
	 * @param tenantid 租户id
	 * @return 数据对象
	 */
	public T findByIdWithSign(Serializable id,String tenantid) {
		return findUniqueWithSign("id",id,tenantid);
	}

	/**
	 * 查询数据
	 * @param name  数据库字段 key
	 * @param value 字段值 value
	 * @param tenantid 租户id
	 * @return 数据对象
	 */
	public T findUniqueWithSign(String name, Object value,String tenantid) {
		Map<String,Object> queryParams = new HashMap<>(2);
		queryParams.put(name, value);
		checkQueryMapTenantidWithSign(queryParams,tenantid);
		List<T> listData = super.queryList(queryParams);
		if(listData!=null && listData.size()==1) {
			return listData.get(0);
		}else {
			throw new RuntimeException("检索数据不唯一, "+name + ":" + value);
		}
	}

	/**
	 * 保存对象实体
	 * @param entity 对象实体
	 * @return 保存后的对象实体
	 */
	public T saveWithSign(T entity) {
		boolean isNew ;
		if(entity.getId()==null) {
			isNew = true;
		}else {
			isNew = StrUtil.isEmptyIfStr(entity.getId());
		}
		if(isNew) {
			return insertWithSign(entity);
		}else {
			return updateWithSign(entity);
		}
	}

	/**
	 * 批量保存
	 * @param listEntity 对象列表
	 */
	public void saveBatchWithSign(List<T> listEntity){
		for(int i=0; i<listEntity.size(); i++) {
			this.saveWithSign(listEntity.get(i));
		}
	}

	/**
	 * 插入对象
	 * @param entity 对象实体
	 * @return 保存后的对象实体
	 */
	public T insertWithSign(T entity) {
		if(entity != null && entity.getTenantid()!=null) {
			return super.insert(entity);
		}else {
			throw new RuntimeException("新增保存数据出错，租户id为空!");
		}
	}

	/**
	 * 更新对象实体
	 * @param entity 对象实体
	 * @return 放回更新后的对象实体
	 */
	public T updateWithSign(T entity) {
		if(entity!=null) {
			T t=findByIdWithSign(entity.getId(),entity.getTenantid());
			if(t==null){
				throw new RuntimeException("更新保存数据出错，对象为空!");
			}else if(!t.getTenantid().equals(entity.getTenantid())){
				throw new RuntimeException("更新保存数据出错，租户不一致为空!");
			}
			return super.update(entity);
		}else {
			log.error("更新保存数据出错，输入参数对象为空!");
			throw new RuntimeException("更新保存数据出错，输入参数对象为空!");
		}
	}
	/**
	 * 删除数据
	 * @param entity 对象实体
	 * @return 是否删除成功
	 */
	public int deleteWithSign(T entity) {
		return deleteWithSign(entity.getId(),entity.getTenantid());
	}

	/**
	 * 根据id删除数据
	 * @param id 对象id
	 * @param tenantid 租户id
	 * @return 是否删除成功
	 */
	public int deleteWithSign(Serializable id,String tenantid ) {
		if(id==null||StringUtils.isEmpty(tenantid)){
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("id", id);
			data.put("tenantid", tenantid);
			return multiTenantMapper.delete(data);
		}else{
			throw new RuntimeException("更新保存数据出错，对象为空!");
		}
	}

	/**
	 * 批量删除数据
	 * @param list 数据列表
	 * @return 成功删除条数
	 */
	public int deleteBatchWithSign(List<T> list) {
		int count = 0;
		for(T entity: list) {
			count += this.deleteWithSign(entity);
		}
		return count;
	}

	/**
	 * 检测查询条件中是否有租户id
	 * @param searchMap
	 * @param tenantid
	 */
	private void checkQueryMapTenantidWithSign(Map<String, Object> searchMap,String tenantid){
		String tenantidInMap=String.valueOf(searchMap.get("tenantid"));
		if(StringUtils.isEmpty(tenantidInMap)|| "null".equals(tenantidInMap)){
			searchMap.put("tenantid",StringUtils.isEmpty(tenantid)?tenantid:InvocationInfoProxy.getTenantid());
		}
	}

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams) {
        Map<String,Object> searchMap=searchParams.getSearchMap();
        checkQueryMapTenantidWithSign(searchMap,InvocationInfoProxy.getTenantid());
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        return list;
    }
}
