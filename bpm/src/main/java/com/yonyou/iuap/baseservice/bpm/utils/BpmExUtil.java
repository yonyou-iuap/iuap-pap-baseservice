package com.yonyou.iuap.baseservice.bpm.utils;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.bpm.pojo.BPMFormJSON;
import com.yonyou.iuap.bpm.util.BpmRestVarType;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;

import cn.hutool.core.util.ReflectUtil;
import yonyou.bpm.rest.request.RestVariable;

public class BpmExUtil {

	private BpmExUtil() {}
	
	public static BpmExUtil inst() {
		return Inner.INST;
	}
	
	public boolean isSuccess4Submit(JSONObject result) {
		return result!=null && "success".equals(result.getString("success"));
	}
	
	public boolean isSuccess4Revoke(JSONObject result) {
		return result!=null && "success".equals(result.getString("success"));
	}
	
	/**
	 * 构建BPMFormJSON
	 * @param processDefineCode
	 * @param Workorder
	 * @return
	 * @throws  
	 */
	public <T extends BpmModel> BPMFormJSON buildBPMFormJSON(String processDefineCode, T entity){
		try{
			BPMFormJSON bpmform = new BPMFormJSON();
			bpmform.setProcessDefinitionKey(processDefineCode);
			String userName = InvocationInfoProxy.getUsername();
			userName = URLDecoder.decode(userName,"utf-8");
			//String title = userName + "提交的【工单】,单号 是" + workorder.getCode() + ", 请审批";
			
			//bpmform.setTitle(title);			
			bpmform.setFormId(entity.getId());										// 单据id
			bpmform.setBillNo(entity.getId());										// 单据号
			bpmform.setBillMarker(InvocationInfoProxy.getUserid());					// 制单人
			String orgId = "";														// usercxt.getSysUser().getOrgId() ;				
			bpmform.setOrgId(orgId);												// 组织
			bpmform.setOtherVariables(buildOtherVariables(entity));					// 其他变量			
			bpmform.setFormUrl("/iuap-example/pages/workorder/workorder.js");		// 单据url
			//bpmform.setProcessInstanceName(title);										// 流程实例名称
			String url = "/iuap-example/example_workorder";								// 流程审批后，执行的业务处理类(controller对应URI前缀)
			bpmform.setServiceClass(url);
			return bpmform;
		}catch(Exception exp){
			throw new BusinessException("构建BPM参数出错!", exp);
		}
	}
	
	
	public <T extends BpmModel> List<RestVariable> buildOtherVariables(T entity) {
		Field[] fields = ReflectUtil.getFields(entity.getClass());
		List<RestVariable> otherVariables = new ArrayList<RestVariable>();
		for (Field curField : fields) {
			Object fieldValue = ReflectUtil.getFieldValue(entity, curField);
			
			String restVariableType = BpmRestVarType.ClassToRestVariavleTypeMap.get(curField.getType());
			if (restVariableType==null || fieldValue==null) {
				continue;
			}

			RestVariable var = new RestVariable();
			var.setName(curField.getName());
			var.setType(restVariableType);
			
			if (restVariableType.equals("date")){
				//var.setValue("2017-10-10 10:30:30");
			}else{
				var.setValue(fieldValue);
			}
			otherVariables.add(var);
		}
		return otherVariables;
	}
	
	/*************************************************/
	private static class Inner{
		private static BpmExUtil INST = new BpmExUtil();
	}

}