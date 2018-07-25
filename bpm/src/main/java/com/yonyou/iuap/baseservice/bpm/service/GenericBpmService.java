package com.yonyou.iuap.baseservice.bpm.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.iuap.baseservice.bpm.entity.BpmSimpleModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.service.GenericExService;
import com.yonyou.iuap.bpm.pojo.BPMFormJSON;
import com.yonyou.iuap.bpm.service.BPMSubmitBasicService;
import com.yonyou.iuap.bpm.service.TenantLimit;
import com.yonyou.iuap.bpm.util.BpmRestVarType;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import yonyou.bpm.rest.BpmRest;
import yonyou.bpm.rest.BpmRests;
import yonyou.bpm.rest.RuntimeService;
import yonyou.bpm.rest.exception.RestException;
import yonyou.bpm.rest.param.BaseParam;
import yonyou.bpm.rest.request.AssignInfo;
import yonyou.bpm.rest.request.RestVariable;
import yonyou.bpm.rest.request.runtime.ProcessInstanceStartParam;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
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
    public abstract  BPMFormJSON buildVariables(T entity);

    private String[] names = {"processDefinitionKey","formId","billMarker","orgId","billNo","formUrl","title","serviceClass"};
    /**
     * @deprecated  应转为依赖BPMSubmitBasicService
     * 几套基本服务的实例化工厂
     * @param userId
     * @return
     */
    protected BpmRest bpmRestServices(String userId) {
        if(userId==null){
            throw new IllegalArgumentException("获取BpmRest时传入的userId["+userId+"]是空");
        }
        BaseParam baseParam=new BaseParam();
        baseParam.setOperatorID(userId);
        //1.U审rest服务地址：http://ys.yyuap.com/ubpm-web-rest
        baseParam.setServer(serverUrl);
        //2.==========rest安全调用=========begin
        //租户code
        //管理端租户管理节点生成的token
        baseParam.setTenant(tenant);
        baseParam.setClientToken(token);
        String limitTenantId=TenantLimit.getTenantLimit();
        //==========rest安全调用=========end
        //3.租户隔离，可为空，默认取rest安全多对应的戹
        if(limitTenantId!=null&&!"".equals(limitTenantId.trim())){
            baseParam.setTenantLimitId(limitTenantId);
        }
        return BpmRests.getBpmRest(baseParam);

    }
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

    private List<RestVariable> buildEntityVars(T entity){
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


/** =================================================================================================================== */

    /**
     * 根据流程定义、租户ID和业务key启动流程实例
     *
     * @param userId
     * @param tenantId
     * @param processKey
     * @param businessKey
     * @return
     * @throws RestException
     */
    protected Object assignStartProcessByKey(AssignInfo assignInfo, BPMFormJSON bpmjson , String userId, String processKey, String businessKey, List<RestVariable> variables) throws RestException {
        RuntimeService rt = bpmRestServices(userId).getRuntimeService();
        ProcessInstanceStartParam parm = new ProcessInstanceStartParam();
        parm.setAssignInfo(assignInfo);
        parm.setProcessDefinitionKey(bpmjson.getProcessDefinitionKey());
        parm.setVariables(variables);
        parm.setBusinessKey(bpmjson.getFormId());
        parm.setProcessInstanceName(bpmjson.getProcessInstanceName());
        parm.setReturnTasks(true);
        return rt.startProcess(parm);
    }
	
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

    /**
     * 启动工作流
     */
    public Object doStartProcess(T entity) throws RestException{
        List<RestVariable> var = buildOtherVariables(entity);
        Object result = null;
        try {
            entity.setBpmState(BpmExUtil.BPM_STATE_START);//流程状态调整为“已启动”;
            if (entity.getId()==null)
                entity=this.save(entity);//获得业务实体的id
            if ( entity.getProcessDefineCode()!=null) {
                JSONObject rejson = new JSONObject();
                BPMFormJSON bpmform = buildBPMFormJSON(entity);
                rejson = bpmSubmitBasicService.assignCheck(bpmform);
                if(rejson.getBoolean("assignAble") != null && rejson.getBoolean("assignAble")){
                    JSONObject assignedActivities=bpmSubmitBasicService.assignedActivities(bpmform);
                    JSONObject assignJson = new JSONObject();
                    assignJson.put("success","success");
                    assignJson.put("assignAble","true");
                    assignJson.put("assignedActivities",assignedActivities.get("assignedActivities"));
                    return assignJson;
                }else {
                    result = this.startProcessByKey(InvocationInfoProxy.getUserid(),entity.getProcessDefineCode(),entity.getId().toString(),var);
                }

                if(result!=null){
                    ObjectNode on= (ObjectNode) result;
                    String processId= String.valueOf(on.get("id") ).replaceAll("\"","");
//                    entity.setProcessInstanceId(processId);
                    entity=this.save(entity);//保存业务实体的流程信息
                }else
                {
                    throw new BusinessException("启动流程实例发生错误，请联系管理员！错误原因：流程调用无返回结果");
                }

                return result;
            }else{
                throw new BusinessException("启动流程实例发生错误，请联系管理员！错误原因：未指定流程模板");
            }
        } catch (RestException e) {
            throw new BusinessException("启动流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
    }


    /**
     * @deprecated 应改为依赖BPMSubmitBasicService
     */
    protected Object startProcessByKey(String userId, String processKey, String businessKey, List<RestVariable> variables) throws RestException {
        if (log.isDebugEnabled()) log.debug("启动流程。流程变量数据=" + JSONObject.toJSONString(variables));
        RuntimeService rt = bpmRestServices(userId).getRuntimeService();
        return rt.startProcessInstanceByKeyAndTenantId(processKey,businessKey,variables,tenant);
    }

    /**
	 * 工单申请提交（批量）
	 * @param list
	 * @param processDefineCode
	 */
	public Object batchSubmit(List<T> list, String processDefineCode) {
		StringBuffer errorMsg = new StringBuffer("");
		Object result = null;
		for (int i = 0; i <list.size() ; i++) {
			T inParam = list.get(i);
			if (inParam.getId()==null || inParam.getId().toString().equalsIgnoreCase("null"))
			{
				errorMsg.append("工单"+i+"ID为空提交失败!\r\n");
				continue;
			}
			T entity = this.findById(inParam.getId());
			entity.setProcessDefineCode(processDefineCode);
			try {
				result = this.doStartProcess(entity);
			} catch (Exception e) {
				errorMsg.append("工单["+inParam.getId()+"]提交失败!\r\n");
			}finally {
				continue;
			}
		}
		if(org.springframework.util.StringUtils.isEmpty(errorMsg.toString())) {
			return result;
		}else {
			return errorMsg.toString();
		}
	}
	
	
	/**
     * 设置BPMFormJSON
     *
     * @param processDefineCode
     * @param ygdemo
     * @return
     * @throws
     */
    public BPMFormJSON buildBPMFormJSON(String processDefineCode, T entity) {
        try {
        	BPMFormJSON bpmform = new BPMFormJSON();
        	bpmform.setProcessDefinitionKey(processDefineCode);
            String userName = InvocationInfoProxy.getUsername();
            try {
                userName = URLDecoder.decode(userName,"utf-8");
            } catch (UnsupportedEncodingException e) {
                userName =InvocationInfoProxy.getUsername();
            }
            //title
            String title = userName + "提交的【工单】,单号 是" + entity.getBpmBillCode() + ", 请审批";
            bpmform.setTitle(title);
            
            // 单据id
            bpmform.setFormId((String) entity.getId());
            // 单据号
            bpmform.setBillNo(entity.getBpmBillCode());
            // 制单人
            bpmform.setBillMarker(InvocationInfoProxy.getUserid());
            // 其他变量
            bpmform.setOtherVariables(buildEntityVars(entity));
            // 单据url
            bpmform.setFormUrl("/iuap_pap_quickstart/pages/workorder/workorder.js");	// 单据url
            // 流程实例名称
            bpmform.setProcessInstanceName(title);										// 流程实例名称
            // 流程审批后，执行的业务处理类(controller对应URI前缀)
            bpmform.setServiceClass("/iuap_pap_quickstart/sany_order");// 流程审批后，执行的业务处理类(controller对应URI前缀)

            return bpmform;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    private boolean isSuccess(JSONObject resultJsonObject) {
        return resultJsonObject.get("flag").equals("success");
    }

    private boolean isFail(JSONObject resultJsonObject) {
        return resultJsonObject.get("flag").equals("fail");
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
	
	/**
     * 指派提交启动流程
     * @param obj 实体对象
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
	 * 工单申请撤回
	 * @param list
	 */
	public String batchRecall(List<T> list) {
		StringBuffer errorMsg = new StringBuffer("");
		for(T item : list) {
			T entity = this.findById(item.getId());
			if(entity.getBpmState() == BpmExUtil.BPM_STATE_START) {		//当前单据状态：已开启流程,但还未进入运行时,否则无法撤回
                try {
                    boolean result= bpmRestServices( InvocationInfoProxy.getUserid()).getRuntimeService().deleteProcessInstance(entity.getProcessInstanceId());
                    if (result) {
                        entity.setBpmState( BpmExUtil.BPM_STATE_NOTSTART  );									// 从已提交状态改为未提交状态;
                        this.save(entity);
                    } else {
                        throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：流程调用失败" );
                    }
                } catch (RestException e) {
                    throw new BusinessException("提交启动流程实例发生错误，请联系管理员！错误原因：" + e.getMessage() );
                }


			}else {
				errorMsg.append("工单["+entity.getId()+"]状态不合法，无法撤回!\r\n");
			}
		}
		return errorMsg.toString();
	}


    @Override
    public T insert(T entity) {
        if(entity.getBpmState()==null ) {
            entity.setBpmState(BpmExUtil.BPM_STATE_NOTSTART);//默认为0
        }
        return super.insert(entity);
    }
    @Override
    public T save(T entity) {
        if(entity.getBpmState()==null ) {
            entity.setBpmState(BpmExUtil.BPM_STATE_NOTSTART);//默认为0
        }
        return super.save(entity);
    }


}
