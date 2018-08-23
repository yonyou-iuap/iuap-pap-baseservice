package com.yonyou.iuap.baseservice.bpm.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.baseservice.bpm.entity.BpmSimpleModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.service.GenericExService;
import com.yonyou.iuap.bpm.pojo.BPMFormJSON;
import com.yonyou.iuap.bpm.service.BPMSubmitBasicService;
import com.yonyou.iuap.bpm.util.BpmRestVarType;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import yonyou.bpm.rest.request.AssignInfo;
import yonyou.bpm.rest.request.Participant;
import yonyou.bpm.rest.request.RestVariable;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 说明：工作流基础Service
 * @author houlf
 * 2018年6月12日
 * 之前版本主要依赖了eiap-plus-common中的BPMSubmitBasicService,可用的方法比较少
 * 后改为参照在ubpm-modules内的example_iuap_bpm、bpm_quickstart等采用的
 * @See ProcessService，该Service提供了全面的方法调用，本版本主要参考之
 * @modified by Leon  2018-6-19

 */
public abstract class GenericBpmService<T extends BpmSimpleModel> extends GenericExService<T>{

	private Logger log = LoggerFactory.getLogger(GenericBpmService.class);
    @Value("${bpmrest.server}")
    private String serverUrl;
    @Value("${bpmrest.tenant}")
    private String tenant;
    @Value("${bpmrest.token}")
    private String token;

	@Autowired
	private BPMSubmitBasicService bpmSubmitBasicService;

    /**
     * 业务服务设定一些流程变量,例如
     *  bpmform.setTitle(title);
     *  bpmform.setFormUrl("/iuap_pap_quickstart/pages/workorder/workorder.js");	// 单据url
     *  bpmform.setProcessInstanceName(title);										// 流程实例名称
     *  bpmform.setServiceClass("/iuap_pap_quickstart/sany_order");// 流程审批后，执行的业务处理类(controller对应URI前缀)
     * @param entity
     * @return
     */
    public  BPMFormJSON buildVariables(T entity){
        return  new BPMFormJSON();
    }

    private String[] names = {"processDefinitionKey","formId","billMarker","orgId","billNo","formUrl","title","serviceClass"};

    /**
     * 构建其他变量，用于提交至流程系统
     * @param entity
     * @return
     */
    public List<RestVariable> buildOtherVariables(T entity) {
        List<RestVariable> variables = new ArrayList<RestVariable>();
        BPMFormJSON bpmjson = buildVariables(entity);
        if (bpmjson==null){
            bpmjson = new BPMFormJSON();
            bpmjson.setTitle("流程单号:"+entity.getBpmBillCode());
        }

        bpmjson.setProcessDefinitionKey(entity.getProcessDefineCode());
        bpmjson.setFormId(entity.getId().toString());							// 单据id
        bpmjson.setBillNo(entity.getBpmBillCode());								// 单据号
        bpmjson.setBillMarker(InvocationInfoProxy.getUserid());					// 制单人
        bpmjson.setOrgId(InvocationInfoProxy.getTenantid());					// 组织
        bpmjson.setOtherVariables(buildEntityVars(entity));
        for (String name : names) {
            RestVariable restVariable = new RestVariable();
            restVariable.setName(name);
            restVariable.setValue(bpmjson.getProperty(name));
            variables.add(restVariable);
        }

        return variables;
    }

    private BPMFormJSON buildBPMFormJSON(T entity) {
        BPMFormJSON bpmjson = buildVariables(entity);
        if (bpmjson==null){
            bpmjson = new BPMFormJSON();
            bpmjson.setTitle("流程单号:"+entity.getBpmBillCode());
        }

        bpmjson.setProcessDefinitionKey(entity.getProcessDefineCode());
        bpmjson.setFormId(entity.getId().toString());							// 单据id
        bpmjson.setBillNo(entity.getBpmBillCode());								// 单据号
        bpmjson.setBillMarker(InvocationInfoProxy.getUserid());					// 制单人
        bpmjson.setOrgId(InvocationInfoProxy.getTenantid());					// 组织
        bpmjson.setOtherVariables(buildEntityVars(entity));
        return bpmjson;
    }

    protected List<RestVariable> buildEntityVars(T entity){
        List<RestVariable> variables = new ArrayList<RestVariable>();
        Field[] fields = ReflectUtil.getFields(entity.getClass());

        for (Field curField : fields) {
            Object fieldValue = ReflectUtil.getFieldValue(entity, curField);
            String variableType = BpmRestVarType.ClassToRestVariavleTypeMap.get(curField.getType());
            if (variableType==null || fieldValue==null) {
                continue;
            }
            RestVariable var = new RestVariable();
            var.setName(curField.getName());
            if (variableType.equals("date") && fieldValue instanceof Date){
                var.setValue(DatePattern.NORM_DATE_FORMAT.format((Date)fieldValue));
                var.setType("string"); //date 类型的时候,如果日期不符合标准格式会导致流程引擎解析错误,故转为string
            }else{
                var.setValue(fieldValue);
            }
            variables.add(var);
        }
        return variables;

    }

    /**
     * 设置BPMFormJSON
     *
     * @param processDefineCode
     * @return
     * @throws
     */
     public abstract  BPMFormJSON buildBPMFormJSON(String processDefineCode, T entity);






    private boolean isSuccess(JSONObject resultJsonObject) {
        return resultJsonObject.get("flag").equals("success");
    }

    private boolean isFail(JSONObject resultJsonObject) {
        return resultJsonObject.get("flag").equals("fail");
    }


/** =================================================================================================================== */

	/**
	 * 获取单据编号
	 * @param entity
	 * @return
	 */
	public String getBpmBillCode(T entity) {
		if(StrUtil.isBlank(entity.getBpmBillCode())) {
			return entity.getId().toString();
		}else {
			return entity.getBpmBillCode();
		}
	}
	
	/**
	 * 获取流程说明Title
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getTitle(T entity) throws UnsupportedEncodingException {
		String userName = InvocationInfoProxy.getUsername();
		userName = URLDecoder.decode(userName,"utf-8");
		return "流程[" + entity.getProcessDefineCode()+ "], 单据号：" + entity.getBpmBillCode()
					+"，提交人:"+userName;
	}
	
	/**
	 * 获取流程实例信息
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getProcessInstance(T entity) throws UnsupportedEncodingException {
		return this.getTitle(entity);
	}

    public Object submit(List<T> list, String processDefineCode) {
		for(int i = 0 ; i < list.size() ;i++){
			T entity = list.get(i);
			BPMFormJSON bpmform = buildBPMFormJSON(processDefineCode, entity);
			JSONObject resultJsonObject = bpmSubmitBasicService.submit(bpmform);
			//判断是否是提交指派
	        if (resultJsonObject.getBoolean("assignAble") != null && resultJsonObject.getBoolean("assignAble")) {
	            return resultJsonObject;
	        }

	        if (isSuccess(resultJsonObject)) {
	            entity.setBpmState(1);// 从未提交状态改为已提交状态;
//	            //修改DB表数据
	            save(entity);
	        } else if (isFail(resultJsonObject)) {
	            String msg = resultJsonObject.get("msg").toString();
	            throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + msg);
	        }
	        return resultJsonObject;
		}
		return null;
	}

    public Object submit(List<T> list, String processDefineCode,Participant[] copyUsers) {
        for(int i = 0 ; i < list.size() ;i++){
            T entity = list.get(i);
            BPMFormJSON bpmform = buildBPMFormJSON(processDefineCode, entity);
            if(copyUsers!=null && copyUsers.length>0)bpmform.setCopyUsers(Arrays.asList(copyUsers));
            JSONObject resultJsonObject = bpmSubmitBasicService.submit(bpmform);
            //判断是否是提交指派
            if (resultJsonObject.getBoolean("assignAble") != null && resultJsonObject.getBoolean("assignAble")) {
                return resultJsonObject;
            }

            if (isSuccess(resultJsonObject)) {
                entity.setBpmState(1);// 从未提交状态改为已提交状态;
//	            //修改DB表数据
                save(entity);
            } else if (isFail(resultJsonObject)) {
                String msg = resultJsonObject.get("msg").toString();
                throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + msg);
            }
            return resultJsonObject;
        }
        return null;
    }

	/**
     * 指派提交启动流程
     * @param entity 实体对象
     * @param processDefineCode 流程定义Key
     * @param assignInfo 指派信息
     * @return
     */
    public void assignSubmitEntity(T entity, String processDefineCode, AssignInfo assignInfo) {
        BPMFormJSON bpmform = buildBPMFormJSON(processDefineCode, entity);
        JSONObject resultJsonObject = bpmSubmitBasicService.assignSubmit(bpmform, assignInfo);
        if (isSuccess(resultJsonObject)) {
        	entity.setBpmState(1);// 从未提交状态改为已提交状态;
            //修改DB表数据
            save(entity);
        } else if (isFail(resultJsonObject)) {
            String msg = resultJsonObject.get("msg").toString();
            throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + msg);
        }
    }

    /**
     * 指派提交启动流程
     * @param entity 实体对象
     * @param processDefineCode 流程定义Key
     * @param assignInfo 指派信息
     * @param copyUsers 抄送人员信息
     * @return
     */
    public void assignSubmitEntity(T entity, String processDefineCode, AssignInfo assignInfo, List<Participant> copyUsers) {
        BPMFormJSON bpmform = buildBPMFormJSON(processDefineCode, entity);
        if(copyUsers!=null && copyUsers.size()>0)
            bpmform.setCopyUsers(copyUsers);
        JSONObject resultJsonObject = bpmSubmitBasicService.assignSubmit(bpmform, assignInfo);
        if (isSuccess(resultJsonObject)) {
            entity.setBpmState(1);// 修改业务对象流程状态字段，从未提交改为提交。
            save(entity);
        } else if (isFail(resultJsonObject)) {
            String msg = resultJsonObject.get("msg").toString();
            throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + msg);
        }
    }

	/**
	 * 工单申请撤回
	 * @param list
	 */
	public JSONObject  batchRecall(List<T> list) {
        T entity = list.get(0);
        JSONObject result = bpmSubmitBasicService.unsubmit(entity.getId().toString());
        if (result.get("success") != null ) {
            entity=findById(entity.getId().toString());
            entity.setBpmState(BpmExUtil.BPM_STATE_NOTSTART);// 从已提交状态改为未提交状态;
            save(entity);
        }
        return result;

	}

    /**
     * 驳回到制单人
     * @param billId  单据ID
     */
    public void doRejectMarkerBill(String billId) {
       T entity=findById(billId);
        entity.setBpmState(BpmExUtil.BPM_STATE_NOTSTART);// 从已提交状态改为未提交状态;
        //修改DB表数据
        save(entity);
    }
}
