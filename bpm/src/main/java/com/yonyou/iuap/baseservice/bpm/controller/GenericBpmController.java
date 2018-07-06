package com.yonyou.iuap.baseservice.bpm.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import net.sf.json.JSONNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.base.utils.RestUtils;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.service.GenericBpmService;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.controller.GenericExController;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;

import iuap.uitemplate.base.util.PropertyUtil;

/**
 * 说明：工作流基础Controller：提供单据增删改查，以及工作流提交、撤回、以及工作流流转回调方法
 * @author Aton
 * 2018年6月13日
 *
 * @modified by Leon
 */
public  class GenericBpmController<T extends BpmModel> extends GenericExController<T>
		 {


	@RequestMapping(value = "/doStart")
	@ResponseBody
	public Object doStart(@RequestBody T entity, HttpServletRequest request) throws Exception {
		try {
			String processDefCode = this.getAllocatedProcess(request);
			entity.setProcessDefineCode(processDefCode);
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
//			String processDefCode = this.getAllocatedProcess(request);
//			String comment=request.getParameter("comment");
			this.service.doSubmit(entity,entity.getComment());
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

    @RequestMapping(value = "/doTaskApprove")
    @ResponseBody
	public Object doApproveAction(@RequestBody Map<String, Object> params, HttpServletRequest request) 
			throws Exception {
		Object approvetype = params.get("approvetype");
        Object comment = params.get("comment");     if (comment==null){ comment="no coomment";}
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

	@RequestMapping(value = {"/doTermination"},	method = {RequestMethod.POST}	)
	@ResponseBody
	public JsonResponse doTerminationAction(Map<String, Object> params) throws Exception {
		String entityID=String.valueOf(params.get("id"));
		Object result = service.doSuspendProcess(entityID);
		if (result!=null){
			buildSuccess(result);
		}
		return buildGlobalError("流程终止失败");
	}

	@RequestMapping(value = {"/doRejectMarkerBill"},method = {RequestMethod.POST})
	@ResponseBody
	public JsonResponse doRejectMarkerBillAction(Map<String, Object> params) throws Exception {
		String busiId = String.valueOf( params.get("billId"));
		String comment = String.valueOf( params.get("comment"));
		service.doRejectToInitial(busiId,comment);
		return null;
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
			      Object result =  this.service.doSuspendProcess(entity.getId().toString())    ;
		return  buildSuccess(result);
	}


	@RequestMapping(value = "/doListTasks")
	@ResponseBody
	public JsonResponse doListHistoryTasks(@RequestBody T entity) throws Exception
	{
		ArrayNode result =
				service.doQueryHistoryTasks(entity.getProcessInstanceId());
		return  buildSuccess(result);
	}
	/**
	 * 提交前校验流程是都在平台资源分配时挂在到指定流程上
	 * @param request
	 * @return
	 */
	private String getAllocatedProcess(HttpServletRequest request) {
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

	@RequestMapping(value = "/doDelegate", method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse delegate(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		//參數
		String taskId = params.get("taskId");
		String delegateUser= params.get("userId");
		String comment = params.get("comment");if (comment==null){ comment="";}
		if (delegateUser==null){ throw new  BusinessException("入参userId为空"); }
		if (taskId==null){	throw new  BusinessException("入参taskId为空");	}

		boolean isSuccess = service.doDelegateTask(taskId, delegateUser, comment);
//			NotifyService.instance().taskNotify() //TODO 消息发送暂时先不做
		if (isSuccess) {
			return buildSuccess("流程改派成功");
		}
		return buildError(null,"流程改派失败",RequestStatusEnum.FAIL_GLOBAL);
	}

	 /**
	  * 回调后-提交申请
	  */
	 @RequestMapping(value = "/submit", method = RequestMethod.POST)
	 @ResponseBody
	 public Object callbackSubmit(@RequestBody List<T> list, HttpServletRequest request, HttpServletResponse response) {
		 String processDefineCode = request.getParameter("processDefineCode");
		 if (processDefineCode==null){ throw new BusinessException("入参流程定义为空"); }
		 try{
			String result= service.batchSubmit(list,processDefineCode);
			return buildSuccess(result);
		 }catch(Exception exp) {
			 return this.buildGlobalError(exp.getMessage());
		 }

	 }

	 /**
	  * 回调:撤回申请
	  */
	 @RequestMapping(value = "/recall", method = RequestMethod.POST)
	 @ResponseBody
	 public Object callbakRecall(@RequestBody List<T> list, HttpServletRequest request, HttpServletResponse response) {
		 String resultMsg = service.batchRecall(list);
		 if(StringUtils.isEmpty(resultMsg)) {
			 return this.buildSuccess("工单撤回操作成功!");
		 }else {
			 return this.buildGlobalError(resultMsg);
		 }
	 }

	 /**
	  * 回调:审批通过
	  */
	 @RequestMapping(value={"/doApprove"}, method={RequestMethod.POST})
	 @ResponseBody
	 public Object callbackApprove(@RequestBody Map<String, Object> params, HttpServletRequest request) throws Exception {
		 Object node = params.get("historicProcessInstanceNode");
		 if (node==null) throw new BusinessException("流程审批回调参数为空");
		 Map hisProc = (Map)node;
		 Object endTime = hisProc.get("endTime");
		 String busiId = hisProc.get("businessKey").toString();
		 T entity=service.findById(busiId);
		 if (endTime != null && endTime != JSONNull.getInstance() && !"".equals(endTime)) {
			 entity.setBpmState(BpmExUtil.BPM_STATE_FINISH);		//已办结
		 }else {
			 entity.setBpmState(BpmExUtil.BPM_STATE_RUNNING);	//审批中
		 }
		 T result = service.save(entity);
		 return buildSuccess(result);
	 }
			 /************************************************************/
	private GenericBpmService<T> service;

	public void setService(GenericBpmService<T> bpmService) {
		this.service = bpmService;
		super.setService(bpmService);
	}





}