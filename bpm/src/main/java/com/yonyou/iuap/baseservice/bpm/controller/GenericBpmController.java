package com.yonyou.iuap.baseservice.bpm.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yonyou.iuap.baseservice.controller.GenericExController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.service.GenericBpmService;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.bpm.service.JsonResultService;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;

import net.sf.json.JSONNull;
import yonyou.bpm.rest.request.AssignInfo;

/**
 * 说明：工作流基础Controller：提供单据增删改查，以及工作流提交、撤回、以及工作流流转回调方法
 * @author Aton
 * 2018年6月13日
 *
 * @update  将依赖sdk的rest接口转移到GenericBpmSdkController by Leon
 */
public  class GenericBpmController<T extends BpmModel> extends GenericExController<T>
		 {
	 /**
	  * 回调后-提交申请
	  */
	 @RequestMapping(value = "/submit", method = RequestMethod.POST)
	 @ResponseBody
	 public Object callbackSubmit(@RequestBody List<T> list, HttpServletRequest request, HttpServletResponse response) {
		 String processDefineCode = request.getParameter("processDefineCode");
		 if (processDefineCode==null){ throw new BusinessException("入参流程定义为空"); }
		 try{
			Object result= service.batchSubmit(list,processDefineCode);
			JSONObject json = (JSONObject)result;
			if("true".equals(json.getString("assignAble"))){
				return result;
			}
			return super.buildSuccess(result);
		 }catch(Exception exp) {
			 return this.buildGlobalError(exp.getMessage());
		 }

	 }
	 
	 /** 指派审批 */
		@RequestMapping(value = "/assignSubmit", method = RequestMethod.POST)
		@ResponseBody
		public Object assignSubmit(@RequestBody Map<String, Object> data,HttpServletRequest request) {
			try { 
//				String jsonString = jsonResultService.toJson(data);
//				JSONObject jsonObject = JSONObject.parseObject(jsonString);
				Type superclassType = this.getClass().getGenericSuperclass();
			    if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
			        return null;
			    }
			    Type[] t = ((ParameterizedType) superclassType).getActualTypeArguments();
				
				String processDefineCode = data.get("processDefineCode").toString();
				Object map = data.get("obj");
				String mj=  JSONObject.toJSONString(map);
				
				T entity = (T) JSON.parseObject(mj,t[0], Feature.IgnoreNotMatch);
				
				String aj=  JSONObject.toJSONString(data.get("assignInfo"));
				AssignInfo assignInfo = jsonResultService.toObject(aj, AssignInfo.class);
				
				service.assignSubmitEntity(entity, processDefineCode, assignInfo);
				return super.buildSuccess(entity);
			} catch (Exception e) {
				return super.buildGlobalError(e.getMessage());
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


	@Autowired
	private JsonResultService jsonResultService;



}