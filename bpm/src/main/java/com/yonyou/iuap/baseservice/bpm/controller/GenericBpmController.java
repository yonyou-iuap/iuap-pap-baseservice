package com.yonyou.iuap.baseservice.bpm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yonyou.iuap.baseservice.bpm.model.BpmModel;
import com.yonyou.iuap.baseservice.bpm.service.GenericBpmService;
import com.yonyou.iuap.baseservice.controller.GenericExController;
import com.yonyou.iuap.bpm.web.IBPMBusinessProcessController;
import com.yonyou.iuap.mvc.type.JsonResponse;

/**
 * 说明：工作流基础Controller：提供单据增删改查，以及工作流提交、撤回、以及工作流流转回调方法
 * @author Aton
 * 2018年6月13日
 */
public abstract class GenericBpmController<T extends BpmModel> extends GenericExController<T>
		implements IBPMBusinessProcessController {
	
	@RequestMapping(value = "/doSubmit")
	@ResponseBody
	public Object doSubmit(@RequestBody T entity, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/doRevoke")
	@ResponseBody
	public Object doRevoke(@RequestBody T entity, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object doApproveAction(Map<String, Object> arg0, HttpServletRequest arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public JsonResponse doRejectMarkerBillAction(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public JsonResponse doTerminationAction(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/************************************************************/
	private GenericBpmService<T> service;

	public void setService(GenericBpmService<T> bpmService) {
		this.service = bpmService;
		super.setService(bpmService);
	}

}