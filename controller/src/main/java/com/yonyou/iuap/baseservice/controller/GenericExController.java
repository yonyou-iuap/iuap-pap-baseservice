package com.yonyou.iuap.baseservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.service.GenericService;

/**
 * 说明：基础Controller——提供数据增、删、改、查
 * @author houlf
 * 2018年6月13日
 */
public abstract class GenericExController<T extends Model & LogicDel> extends GenericController<T>{
	
	private Logger log = LoggerFactory.getLogger(GenericExController.class);

	/************************************************************/
	private GenericService<T> service;

	public void setService(GenericService<T> genericService) {
		this.service = genericService;
		super.setService(genericService);
	}

}