package com.yonyou.iuap.baseservice.bpm.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.baseservice.bpm.dao.mapper.GenericBpmMapper;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.service.GenericExService;
import com.yonyou.iuap.bpm.pojo.BPMFormJSON;
import com.yonyou.iuap.bpm.service.BPMSubmitBasicService;
import com.yonyou.iuap.bpm.util.BpmRestVarType;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ReflectUtil;
import yonyou.bpm.rest.request.RestVariable;

/**
 * 说明：工作流基础Service
 * @author houlf
 * 2018年6月12日
 */
public abstract class GenericBpmService<T extends BpmModel> extends GenericExService<T>{

	private Logger log = LoggerFactory.getLogger(GenericBpmService.class);
	
	/**
	 * 提交工作流
	 */
	public JSONObject submit(T entity, String processDefineCode) {
		BPMFormJSON submitJson = this.buildBPMFormJSON(processDefineCode, entity);
		JSONObject result = bpmSubmitBasicService.submit(submitJson);
		if (BpmExUtil.inst().isSuccess4Submit(result)) {
			entity.setBpmState(BpmExUtil.BPM_STATE_START);				//流程状态调整为“已启动”;
			this.save(entity);
			return result;
		} else {
			Object msg = result.get("message")!=null ? result.get("message"):result.get("msg");
			throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + msg.toString());
		}
	}

	/**
	 * 撤回工作流
	 */
	public void revoke(T entity) {
		JSONObject result = bpmSubmitBasicService.unsubmit(entity.getId());
		if (BpmExUtil.inst().isSuccess4Revoke(result)) {
			entity.setBpmState(BpmExUtil.BPM_STATE_NOTSTART);			//工作流撤回，流程状态为“未开始”;
			this.save(entity);
		} else {
			Object msg = result.get("message")!=null ? result.get("message"):result.get("msg");
			throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + msg.toString());
		}
	}
	
	/**
	 * 审批通过——更新流程状态
	 * @param workorder
	 */
	public void doApprove(String id, Integer status) {
		T entity = this.findById(id);
		entity.setBpmState(status);
		this.save(entity);
	}
	
	/**
	 * 驳回：更新流程状态——未开始
	 * @param id
	 */
	public void doReject(String id) {
		T entity = this.findById(id);
		entity.setBpmState(0);
		this.save(entity);
	}
	
	/**
	 * 构建BPMFormJSON
	 * @param processDefineCode
	 * @param Workorder
	 * @return
	 * @throws  
	 */
	protected BPMFormJSON buildBPMFormJSON(String processDefineCode, T entity){
		try{
			BPMFormJSON bpmForm = new BPMFormJSON();
			bpmForm.setProcessDefinitionKey(processDefineCode);						// 流程定义编码
			bpmForm.setProcessInstanceName(this.getProcessInstance(entity));		// 流程实例名称
			bpmForm.setTitle(this.getTitle(entity));								// 流程标题
			bpmForm.setFormId(entity.getId());										// 单据id
			bpmForm.setBillNo(entity.getId());										// 单据号
			bpmForm.setBillMarker(InvocationInfoProxy.getUserid());					// 制单人
			String orgId = "";														// usercxt.getSysUser().getOrgId() ;				
			bpmForm.setOrgId(orgId);												// 组织
			bpmForm.setFormUrl(this.getBpmFormUrl(entity));							// 单据url
			String callBackUrl = this.getBpmCallBackUrl(entity);					// 流程审批后，执行的业务处理类(controller对应URI前缀)
			bpmForm.setServiceClass(callBackUrl);
			bpmForm.setOtherVariables(buildOtherVariables(entity));					// 其他变量
			return bpmForm;
		}catch(Exception exp){
			throw new BusinessException("构建BPM参数出错!", exp);
		}
	}
	
	
	protected List<RestVariable> buildOtherVariables(T entity) {
		Field[] fields = ReflectUtil.getFields(entity.getClass());
		List<RestVariable> variables = new ArrayList<RestVariable>();
		for (Field curField : fields) {
			Object fieldValue = ReflectUtil.getFieldValue(entity, curField);
			String variableType = BpmRestVarType.ClassToRestVariavleTypeMap.get(curField.getType());
			if (variableType==null || fieldValue==null) {
				continue;
			}

			RestVariable var = new RestVariable();
			var.setName(curField.getName());
			var.setType(variableType);
			
			if (variableType.equals("date") && fieldValue instanceof Date){
				var.setValue(DatePattern.NORM_DATE_FORMAT.format((Date)fieldValue));
			}else{
				var.setValue(fieldValue);
			}
			variables.add(var);
		}
		return variables;
	}
	
	public String getTitle(T entity) throws UnsupportedEncodingException {
		String userName = InvocationInfoProxy.getUsername();
		userName = URLDecoder.decode(userName,"utf-8");
		return "流程["+entity.getClass().getSimpleName()+"]，提交人:"+userName;
	}
	
	public String getProcessInstance(T entity) throws UnsupportedEncodingException {
		return this.getTitle(entity);
	}
	
	public abstract String getNodeKey(T entity);
	
	public abstract String getBpmFormUrl(T entity);
	
	public abstract String getBpmCallBackUrl(T entity);
	
	
	/***************************************************/
	protected GenericBpmMapper<T> genericBpmMapper;
	@Autowired
	private BPMSubmitBasicService bpmSubmitBasicService;

	public void setGenericBpmMapper(GenericBpmMapper<T> bpmMapper) {
		this.genericBpmMapper = bpmMapper;
	}

}