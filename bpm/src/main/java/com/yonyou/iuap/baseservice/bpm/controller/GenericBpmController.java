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
 *
 * @Modified by Leon
 */
public abstract class GenericBpmController<T extends BpmModel> extends GenericExController<T>
		implements IBPMBusinessProcessController {


	@RequestMapping(value = "/doStart")
	@ResponseBody
	public Object doStart(@RequestBody T entity, HttpServletRequest request) throws Exception {
		try {
			this.checkSubmit(request);
			this.service.doStartProcess(entity);
			return this.buildSuccess("流程已启动！");
		}catch(Exception exp) {
			return this.buildGlobalError(exp.getMessage());
		}
	}
	@RequestMapping(value = "/doSubmit")
	@ResponseBody
	public Object doSubmit(@RequestBody T entity, HttpServletRequest request) throws Exception {
		try {
			this.checkSubmit(request);
			String comment=request.getParameter("comment");
			this.service.doSubmit(entity,comment);
			return this.buildSuccess("流程已提交！");
		}catch(Exception exp) {
			return this.buildGlobalError(exp.getMessage());
		}
	}

	@RequestMapping(value = "/doRevoke")
	@ResponseBody
	public Object doRevoke(@RequestBody T entity, HttpServletRequest request) throws Exception {
		this.service.doRevoke(entity);
		return this.buildSuccess("流程已撤回！");
	}

    @RequestMapping(value = "/doApprove")
    @ResponseBody
	public Object doApproveAction(@RequestBody Map<String, Object> params, HttpServletRequest request) 
			throws Exception {
		Object approvetype = params.get("approvetype");
        Object comment = params.get("comment");     if (comment==null){ comment="";}
        Object bpmNode = params.get("historicProcessInstanceNode");
        if (bpmNode==null){
            throw new  BusinessException("入参historicProcessInstanceNode为空");
        }
        String busiId =
            ((Map)bpmNode).get("businessKey")==null ?  null:((Map)bpmNode).get("businessKey").toString();
        JsonResponse response ;
        boolean isSuccess ;
		if(approvetype!=null && approvetype.toString().equals("agree")) {
            isSuccess=this.service.doApprove(busiId  ,true,comment.toString() );	//审批通过
		}else {
            isSuccess=this.service.doApprove(busiId,false,comment.toString() );	//审批拒绝
		}
		if (isSuccess){
            response=this.buildSuccess();
        }else{
            response=this.buildGlobalError("流程审批失败");
        }
		return response;
	}
    @RequestMapping(value = "/doRejectBill")
    @ResponseBody
	public JsonResponse doRejectMarkerBillAction(@RequestBody T entity,HttpServletRequest request)
			  {
        String comment=request.getParameter("comment");
        Object result = this.service.doReject(entity, comment);
		return buildSuccess(result);
	}
    @RequestMapping(value = "/doSuspend")
    @ResponseBody
	public JsonResponse doSuspendAction(@RequestBody T entity)
			  {
			      Object result =         this.service.doSuspendProcess(entity.getId())    ;

		return  buildSuccess(result);
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