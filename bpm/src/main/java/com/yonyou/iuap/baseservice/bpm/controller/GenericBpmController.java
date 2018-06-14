package com.yonyou.iuap.baseservice.bpm.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.base.utils.RestUtils;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.service.GenericBpmService;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.controller.GenericExController;
import com.yonyou.iuap.bpm.web.IBPMBusinessProcessController;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;

import iuap.uitemplate.base.util.PropertyUtil;

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
		try {
			String processDefineCode = this.checkSubmit(request);
			this.service.submit(entity, processDefineCode);
			return this.buildSuccess("流程已提交！");
		}catch(Exception exp) {
			return this.buildGlobalError(exp.getMessage());
		}
	}

	@RequestMapping(value = "/doRevoke")
	@ResponseBody
	public Object doRevoke(@RequestBody T entity, HttpServletRequest request) throws Exception {
		this.service.revoke(entity);
		return this.buildSuccess("流程已撤回！");
	}

	public Object doApproveAction(@RequestBody Map<String, Object> params, HttpServletRequest request) 
			throws Exception {
		Object approvetype = params.get("approvetype");
		if(approvetype!=null && approvetype.toString().equals("agree")) {
			Object bpmNode = params.get("historicProcessInstanceNode");
			if(bpmNode != null && bpmNode instanceof Map) {
				Object busiId = ((Map)bpmNode).get("businessKey");
				Object endTime = ((Map)bpmNode).get("endTime");
				if(endTime != null) {
					this.service.doApprove(busiId.toString(), 3);			//已办结
				}else {
					this.service.doApprove(busiId.toString(), 2);			//审批中
				}
			}
		}else {
			Object bpmNode = params.get("historicProcessInstanceNode");
			if(bpmNode != null && bpmNode instanceof Map) {
				Object busiId = ((Map)bpmNode).get("businessKey");
				this.service.doReject(busiId.toString());
			}
		}
		JsonResponse response = new JsonResponse();
		return response;
	}

	public JsonResponse doRejectMarkerBillAction(@RequestBody Map<String, Object> params) 
			throws Exception {
		String busiId = params.get("billId").toString();
		this.service.doReject(busiId);
		return new JsonResponse();
	}

	public JsonResponse doTerminationAction(@RequestBody Map<String, Object> params) 
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String checkSubmit(HttpServletRequest request) {
		String checkUrl = PropertyUtil.getProperty("bpmrest.checkUrl");
		JSONObject result = RestUtils.getInstance().doGetWithSign(checkUrl, request, JSONObject.class);
		if(BpmExUtil.inst().isSuccess4CheckSubmit(result)) {
			Object detailMsg = result.get("detailMsg");
			if(detailMsg!=null) {
				Object jsonData = ((JSONObject)detailMsg).get("data");
				if(jsonData!=null) {
					return ((JSONObject)jsonData).getString("res_code");
				}
			}
		}
		throw new BusinessException("流程提交出错【资源分配中未分配流程】");
	}

	/************************************************************/
	private GenericBpmService<T> service;

	public void setService(GenericBpmService<T> bpmService) {
		this.service = bpmService;
		super.setService(bpmService);
	}

}