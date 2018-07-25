package com.yonyou.iuap.baseservice.bpm.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.service.GenericExService;
import com.yonyou.iuap.bpm.pojo.BPMFormJSON;
import com.yonyou.iuap.bpm.service.TenantLimit;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import yonyou.bpm.rest.*;
import yonyou.bpm.rest.exception.RestException;
import yonyou.bpm.rest.param.BaseParam;
import yonyou.bpm.rest.request.RestVariable;
import yonyou.bpm.rest.request.historic.BpmHistoricProcessInstanceParam;
import yonyou.bpm.rest.request.historic.HistoricProcessInstancesQueryParam;
import yonyou.bpm.rest.request.historic.HistoricTaskQueryParam;
import yonyou.bpm.rest.request.runtime.ProcessInstanceStartParam;
import yonyou.bpm.rest.response.CommentResponse;
import yonyou.bpm.rest.response.historic.HistoricProcessInstanceResponse;
import yonyou.bpm.rest.response.historic.HistoricTaskInstanceResponse;
import yonyou.bpm.rest.response.runtime.task.TaskActionResponse;
import yonyou.bpm.rest.utils.BaseUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  * 说明：工作流基础Service
 *  * @author houlf
 *  * 2018年6月12日
 *  * 之前版本主要依赖了eiap-plus-common中的BPMSubmitBasicService,可用的方法比较少
 *  * 后改为参照在ubpm-modules内的example_iuap_bpm、bpm_quickstart等采用的
 *  * @See ProcessService，该Service提供了全面的方法调用，本版本主要参考之
 *  @modified 2018-7-25  将sdk调用方式的服务移动到此service中assignStartProcessByKey
 */
public abstract class GenericBpmSdkService<T extends BpmModel>  extends GenericBpmService<T> {
    private Logger log = LoggerFactory.getLogger(GenericBpmSdkService.class);
    @Value("${bpmrest.server}")
    private String serverUrl;
    @Value("${bpmrest.tenant}")
    private String tenant;
    @Value("${bpmrest.token}")
    private String token;


    /**
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
     * 提交表单
     *
     * @param userId
     * @param processId
     * @param variables
     * @return
     * @throws Exception
     */
    protected Object startProcessById(String userId, String processId, String procInstName, List<RestVariable> variables)
            throws RestException {
        if (log.isDebugEnabled()) log.debug("启动流程。流程变量数据=" + JSONObject.toJSONString(variables));
        RuntimeService rt = bpmRestServices(userId).getRuntimeService();
        ProcessInstanceStartParam parm = new ProcessInstanceStartParam();
        parm.setProcessDefinitionId(processId);
        parm.setVariables(variables);
        parm.setProcessInstanceName(procInstName);
        return rt.startProcess(parm);
    }

    /**
     * 获得流程后续task
     *
     * @param instanceId
     * @return
     * @throws RestException
     */
    protected ArrayNode queryInstanceNotFinishTaskAssigneeList(String userId, String instanceId)
            throws RestException {

        HistoryService ht = bpmRestServices(userId).getHistoryService();// 历史服务
        JsonNode obj = (JsonNode) ht.getHistoricProcessInstance(instanceId);
        String endTime = obj.get("endTime").textValue();
        if (endTime != null) {
            // 说明该流程实例已结束
            return null;
        }
        HistoricTaskQueryParam htp = new HistoricTaskQueryParam();
        htp.setProcessInstanceId(instanceId);
        htp.setFinished(false);// 只查询下一个未完成的task
        JsonNode jsonNode = (JsonNode) ht.getHistoricTaskInstances(htp);
        ArrayNode arrNode = BaseUtils.getData(jsonNode);
        return arrNode;
    }



    /**
     * 提交某个任务的审批
     *
     * @param taskId
     * @param agreed       是否同意申请
     * @param auditComment 填写的审批意见
     * @return 是否提交成功
     * @throws RestException
     */
    protected TaskActionResponse completeTask(String userId, String taskId, boolean agreed, String auditComment)
            throws RestException {
        List<RestVariable> taskVariables = new ArrayList<RestVariable>();
        RestVariable agreeVariable = new RestVariable();
        agreeVariable.setName("agree");
        agreeVariable.setValue(agreed ? "Y" : "N");
        agreeVariable.setVariableScope(RestVariable.RestVariableScope.LOCAL);
        taskVariables.add(agreeVariable);
        TaskService ts = bpmRestServices(userId).getTaskService();
        JsonNode node = (JsonNode) ts.completeWithComment(taskId, taskVariables, null, "",
                auditComment);
        if (node != null) {
            TaskActionResponse resp = JSONObject.parseObject(node.toString(), TaskActionResponse.class);
            return resp;
        } else {
            return null;
        }
    }
    /**
     * 部署流程定义
     * @param userId
     * @param inputStream
     * @param name 文件名称
     * @throws RestException
     */
    protected void deployment(String userId, InputStream inputStream, String name) throws RestException{
        bpmRestServices(userId).getRepositoryService().postNewDeploymentBPMNFile(inputStream, name);
    }

    /**
     * 取消抢占
     *
     * @param taskId
     * @return
     */
    protected boolean withdrawTask(String userId, String taskId) throws RestException {
        return bpmRestServices(userId).getTaskService().withdrawTask(taskId);
    }


    /**
     * 驳回任务
     *
     * @return
     */
    protected Object rejectToTask(String userId, String processInstanceId,String taskKey ,String comment,String taskId) throws RestException {
        return   bpmRestServices(userId).getRuntimeService().rejectToActivity(processInstanceId,taskKey,comment,taskId);
    }

    /**
     * 流程终止
     * @return
     */
    protected Object suspendProcess(String userId, String processInstanceId) throws RestException {
        return   bpmRestServices(userId).getRuntimeService().suspendProcessInstance(processInstanceId);
    }

    /**
     * 对一个任务进行评论
     *
     * @param userId
     * @param taskId
     * @param comment
     * @param saveInstanceId
     * @return
     * @throws Exception
     */
    protected List<CommentResponse> commentTask(String userId, String taskId, String comment, boolean saveInstanceId)
            throws RestException {
        TaskService ts = bpmRestServices(userId).getTaskService();
        JsonNode obj = (JsonNode) ts.addComment(taskId, comment, saveInstanceId);
        if (log.isDebugEnabled()) log.debug("commentTask 返回:" + obj);
        ArrayNode arr = BaseUtils.getData(obj);
        List<CommentResponse> list = new ArrayList<CommentResponse>(arr.size());
        for (int i = 0; i < arr.size(); i++) {
            JsonNode node = arr.get(i);
            CommentResponse resp = JSONObject.parseObject(node.toString(), CommentResponse.class);
            list.add(resp);
        }
        return list;
    }

    /**
     * 对实例添加comment
     *
     * @param userId
     * @param instanceId
     * @param comment
     * @throws Exception
     */
    protected CommentResponse commentInstance(String userId, String instanceId, String comment)
            throws RestException {
        HistoryService hs = bpmRestServices(userId).getHistoryService();
        JsonNode obj = (JsonNode) hs.addComment(instanceId, comment);
        log.debug("HistoryService.addComment=" + obj);
        if (obj != null)
            return JSONObject.parseObject (obj.toString(), CommentResponse.class);
        return null;
    }
    /**
     * 查询流程实例全部信息
     *
     * @param userId
     * @param instId
     * @throws RestException
     */
    protected JsonNode getProcessInstanceAllInfo(String userId, String instId, BpmHistoricProcessInstanceParam parm) throws RestException {
        HistoryService ht = bpmRestServices(userId).getHistoryService();// 历史服务
        JsonNode node = (JsonNode) ht.getHistoricProcessInstance(instId, parm);
        System.out.println("getProcessInstanceAllInfo=\r\n" + node);
        return node;
    }

    /**
     * 获得流程实例信息
     *
     * @param userId
     * @param instId
     * @return
     * @throws Exception
     */
    protected HistoricProcessInstanceResponse getProcessInstance(String userId, String instId, boolean includeProcessVariable)
            throws RestException {
        if (log.isDebugEnabled()) log.debug("根据实例id查询实例信息:" + instId);
        HistoryService ht = bpmRestServices(userId).getHistoryService();// 历史服务
        HistoricProcessInstancesQueryParam param = new HistoricProcessInstancesQueryParam();
        param.setProcessInstanceId(instId);
        if (includeProcessVariable)
            param.setIncludeProcessVariables(true);
        else
            param.setIncludeProcessVariables(false);

        JsonNode node = (JsonNode) ht.getHistoricProcessInstances(param);
        if (log.isDebugEnabled()) log.debug("getHistoricProcessInstance=" + node);
        ArrayNode arrNode = BaseUtils.getData(node);
        if (arrNode != null && arrNode.size() > 0) {
            HistoricProcessInstanceResponse resp = JSONObject.parseObject (arrNode.get(0).toString(), HistoricProcessInstanceResponse.class);
            return resp;
        }
        return null;
    }

    /**
     * 获得待办任务
     * @param userId
     * @param instanceId
     * @return
     * @throws RestException
     */
    protected HistoricTaskInstanceResponse getInstanceNotFinishFirstTask(String userId, String instanceId)
            throws RestException {
        HistoryService ht = bpmRestServices(userId).getHistoryService();// 历史服务
        HistoricTaskQueryParam htp = new HistoricTaskQueryParam();
        htp.setProcessInstanceId(instanceId);
        htp.setFinished(false);// 只查询下一个未完成的task
//		htp.setSize(1);
        JsonNode jsonNode = (JsonNode) ht.getHistoricTaskInstances(htp);
        ArrayNode data = BaseUtils.getData(jsonNode);
        int size = data.size();
        if (size > 0) {
            HistoricTaskInstanceResponse resp = JSONObject.parseObject (data.get(0).toString(), HistoricTaskInstanceResponse.class,Feature.UseBigDecimal);
            return resp;
        }
        return null;
    }

    /**
     * 提交工作流节点
     */
    public boolean doSubmit(T entity,String comment)  {
        try {
            TaskActionResponse resp = this.completeTask(InvocationInfoProxy.getUserid(), entity.getTaskId(), true, comment);
            if ( resp!=null) {
                entity.setTaskId(resp.getTaskId());
                Object procInstance = bpmRestServices(InvocationInfoProxy.getUserid()).getHistoryService().getHistoricProcessInstance(entity.getProcessInstanceId());
                if (procInstance!=null){
                    HistoricProcessInstanceResponse procResp = JSONObject.parseObject(procInstance.toString(), HistoricProcessInstanceResponse.class);
                    if (procResp.getEndTime()==null)
                        entity.setBpmState(BpmExUtil.BPM_STATE_RUNNING) ;//流程状态调整为“运行中”;
                    else
                        entity.setBpmState(BpmExUtil.BPM_STATE_FINISH);//流程状态调整为“已完成”;
                }
                this.save(entity);
                return true;
            }
        } catch (Exception e) {
            throw new BusinessException("提交流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
        return  false;
    }

    /**
     * 撤回工作流,或称弃审
     */
    public boolean doRevoke(T entity) {
        try {
            boolean isSuccess = this.withdrawTask(InvocationInfoProxy.getUserid(),entity.getTaskId());
            if ( isSuccess) {
                entity.setBpmState(BpmExUtil.BPM_STATE_RUNNING);				//如果撤回到流程起始,流程状态调整为“未开始”;
                this.save(entity);
                return isSuccess;
            }
        } catch (RestException e) {
            throw new BusinessException("撤回流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
        return  false;
    }

    /**
     * 审批流程——更新流程状态
     */
    public boolean doApprove(String entityId ,boolean agreed,String comment) {
        try {
            if (entityId==null){
                throw new RestException("流程实例未通过BizKey绑定业务实体!");
            }
            T entity = //this.findById(entityId);
                    this.findUnique("id",entityId);
            TaskActionResponse resp  = this.completeTask(InvocationInfoProxy.getUserid(), entity.getTaskId(), agreed, comment);
            if ( resp!=null) {
                entity.setTaskId(resp.getTaskId());
                Object procInstance = bpmRestServices(InvocationInfoProxy.getUserid()).getHistoryService().getHistoricProcessInstance(entity.getProcessInstanceId());
                if (procInstance!=null){
                    HistoricProcessInstanceResponse procResp = JSONObject.parseObject(procInstance.toString(), HistoricProcessInstanceResponse.class);
                    if (procResp.getEndTime()==null)
                        entity.setBpmState(BpmExUtil.BPM_STATE_RUNNING) ;//流程状态调整为“运行中”;
                    else
                        entity.setBpmState(BpmExUtil.BPM_STATE_FINISH);//流程状态调整为“已完成”;
                }
                this.save(entity);
                return true;
            }
        } catch (RestException e) {
            throw new BusinessException("审批流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
        return  false;
    }

    /**
     * 驳回：更新流程状态——运行中
     */
    public Object doReject(T entity,String comment) {
        try {
            Object result = this.rejectToTask(InvocationInfoProxy.getUserid(),entity.getProcessInstanceId(),entity.getTaskKey(),comment,entity.getTaskId());
            if ( result!=null) {
                entity.setBpmState(BpmExUtil.BPM_STATE_RUNNING);				//如果撤回到流程起始,流程状态调整为“未开始”;
                this.save(entity);
                return result;
            }
        } catch (RestException e) {
            throw new BusinessException("驳回流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
        return null;
    }

    /**
     * 驳回到制单人：更新流程状态——未开始
     */
    public Object doRejectToInitial(String entityId ,String comment) {
        try {
            if (entityId==null){
                throw new RestException("流程实例未通过BizKey绑定业务实体!");
            }
            T entity = this.findById(entityId);
            Object result = bpmRestServices(InvocationInfoProxy.getUserid()).getRuntimeService().rejectToInitialActivity(entity.getProcessInstanceId(), entity.getComment(), comment);
            if ( result!=null) {
                entity.setBpmState(BpmExUtil.BPM_STATE_RUNNING);				//如果撤回到流程起始,流程状态调整为“未开始”;
                this.save(entity);
                return result;
            }else{
                throw new RestException("驳回到制单人接口调用失败,返回结果为空");
            }
        } catch (RestException e) {
            throw new BusinessException("驳回流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
    }

    /**
     * 终止：更新流程状态——人工终止
     */
    public Object doSuspendProcess(String entityId) {
        T entity=   this.findById(entityId);
        try {
            Object result = this.suspendProcess(InvocationInfoProxy.getUserid(),entity.getProcessInstanceId());
            if ( result!=null) {
                entity.setBpmState(BpmExUtil.BPM_STATE_ABEND);				//流程状态调整为“已终止”;
                this.save(entity);
                return result;
            }
        } catch (RestException e) {
            throw new BusinessException("终止流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
        return null;
    }

    /**
     * 改派:任务改给另外用户,并提供审批意见
     * @param taskId
     * @param delegateUser
     * @param comment
     * @return
     */
    public boolean doDelegateTask(String taskId, String delegateUser, String comment)  {
        try {
            boolean isSuccess = this.bpmRestServices(InvocationInfoProxy.getUserid()).getTaskService()
                    .delegateTaskCompleelyWithCommants(taskId, delegateUser, comment);
            return isSuccess;
        } catch (RestException e) {
            throw new BusinessException("撤回流程实例发生错误，请联系管理员！错误原因：" + e.getMessage());
        }
    }


    /**
     * 查询流程所有task列表
     *
     * @param instanceId
     * @return
     * @throws Exception
     */
    public ArrayNode doQueryHistoryTasks( String instanceId)
            throws RestException {
        HistoryService ht = bpmRestServices(InvocationInfoProxy.getUserid()).getHistoryService();// 历史服务
        HistoricTaskQueryParam htp = new HistoricTaskQueryParam();
        htp.setProcessInstanceId(instanceId);
        htp.setIncludeProcessVariables(true);//包含变量
        JsonNode jsonNode = (JsonNode) ht.getHistoricTaskInstances(htp);
        if (log.isDebugEnabled()) log.debug("queryInstanceAllHistoryTaskList==>" + jsonNode);
        if (jsonNode == null) return null;
        ArrayNode arrayNode = BaseUtils.getData(jsonNode);
        return arrayNode;
    }

}
