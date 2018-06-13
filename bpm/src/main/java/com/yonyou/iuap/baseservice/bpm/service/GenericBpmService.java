package com.yonyou.iuap.baseservice.bpm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.bpm.dao.mapper.GenericBpmMapper;
import com.yonyou.iuap.baseservice.bpm.model.BpmModel;
import com.yonyou.iuap.baseservice.service.GenericExService;

/**
 * 说明：工作流基础Service
 * @author houlf
 * 2018年6月12日
 */
public class GenericBpmService<T extends BpmModel> extends GenericExService<T>{

	private Logger log = LoggerFactory.getLogger(GenericBpmService.class);
	
	/**
	 * 提交工作流
	 */
	public void doSubmit(T entity) {
		
	}

	/**
	 * 撤回工作流
	 */
	public void doRevoke(T entity) {
		
	}

	/***************************************************/
	protected GenericBpmMapper<T> genericBpmMapper;

	public void setGenericBpmMapper(GenericBpmMapper<T> bpmMapper) {
		this.genericBpmMapper = bpmMapper;
	}

}