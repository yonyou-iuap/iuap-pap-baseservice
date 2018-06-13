package com.yonyou.iuap.baseservice.bpm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.baseservice.bpm.dao.mapper.GenericBpmMapper;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.service.GenericExService;
import com.yonyou.iuap.bpm.service.BPMSubmitBasicService;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;

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
		JSONObject result = bpmSubmitBasicService.unsubmit(entity.getId());
		if (BpmExUtil.inst().isSuccess4Revoke(result)) {
			entity.setFlowState("0");				// 从已提交状态改为未提交状态;
			this.save(entity);
		} else {
			Object msg = result.get("message")!=null ? result.get("message"):result.get("msg");
			throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + msg.toString());
		}
	}

	/***************************************************/
	protected GenericBpmMapper<T> genericBpmMapper;
	@Autowired
	private BPMSubmitBasicService bpmSubmitBasicService;

	public void setGenericBpmMapper(GenericBpmMapper<T> bpmMapper) {
		this.genericBpmMapper = bpmMapper;
	}

}