package com.yonyou.iuap.baseservice.bpm.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yonyou.iuap.base.utils.RestUtils;
import com.yonyou.iuap.baseservice.bpm.entity.BpmModel;
import com.yonyou.iuap.baseservice.bpm.service.GenericBpmSdkService;
import com.yonyou.iuap.baseservice.bpm.utils.BpmExUtil;
import com.yonyou.iuap.baseservice.controller.GenericExController;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import iuap.uitemplate.base.util.PropertyUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import yonyou.bpm.rest.request.RestVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

class GenericBpmSdkController<T extends BpmModel> extends GenericExController<T> {

    @RequestMapping(value = "/doStart")
    @ResponseBody
    public Object doStart(@RequestBody T entity, HttpServletRequest request) throws Exception {
        try {
            String processDefCode = this.getAllocatedProcess(request);
            entity.setProcessDefineCode(processDefCode);
            entity=service.save(entity);
            this.service.doStartProcess(entity);
            return this.buildSuccess("流程已启动！");
        } catch (Exception exp) {
            return this.buildGlobalError(exp.getMessage());
        }
    }

    @RequestMapping(value = "/doSubmit")
    @ResponseBody
    public Object doSubmit(@RequestBody T entity, HttpServletRequest request) throws Exception {
        try {
//			String processDefCode = this.getAllocatedProcess(request);
//			String comment=request.getParameter("comment");
            this.service.doSubmit(entity, entity.getComment());
            return this.buildSuccess("流程已提交！");
        } catch (Exception exp) {
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
        Object comment = params.get("comment");
        if (comment == null) {
            comment = "no coomment";
        }
        Object bpmNode = params.get("historicProcessInstanceNode");
        if (bpmNode == null) {
            throw new BusinessException("入参historicProcessInstanceNode为空");
        }
        String busiId =
                ((Map) bpmNode).get("businessKey") == null ? null : ((Map) bpmNode).get("businessKey").toString();
        JsonResponse response;
        boolean isSuccess;
        if (approvetype != null && approvetype.toString().equals("agree")) {
            isSuccess = this.service.doApprove(busiId, true, comment.toString());    //审批通过
        } else {
            isSuccess = this.service.doApprove(busiId, false, comment.toString());    //审批拒绝
        }
        if (isSuccess) {
            response = this.buildSuccess();
        } else {
            response = this.buildGlobalError("流程审批失败");
        }
        return response;
    }

    @RequestMapping(value = {"/doTermination"}, method = {RequestMethod.POST})
    @ResponseBody
    public JsonResponse doTerminationAction(Map<String, Object> params) throws Exception {
        String entityID = String.valueOf(params.get("id"));
        Object result = service.doSuspendProcess(entityID);
        if (result != null) {
            buildSuccess(result);
        }
        return buildGlobalError("流程终止失败");
    }

    @RequestMapping(value = {"/doRejectMarkerBill"}, method = {RequestMethod.POST})
    @ResponseBody
    public JsonResponse doRejectMarkerBillAction(Map<String, Object> params) throws Exception {
        String busiId = String.valueOf(params.get("billId"));
        String comment = String.valueOf(params.get("comment"));
        service.doRejectToInitial(busiId, comment);
        return null;
    }

    @RequestMapping(value = "/doRejectBill")
    @ResponseBody
    public JsonResponse doRejectMarkerBillAction(@RequestBody T entity, HttpServletRequest request) {
        String comment = request.getParameter("comment");
        Object result = this.service.doReject(entity, comment);
        return buildSuccess(result);
    }

    @RequestMapping(value = "/doSuspend")
    @ResponseBody
    public JsonResponse doSuspendAction(@RequestBody T entity) {
        Object result = this.service.doSuspendProcess(entity.getId().toString());
        return buildSuccess(result);
    }


    @RequestMapping(value = "/doListTasks")
    @ResponseBody
    public JsonResponse doListHistoryTasks(@RequestBody T entity) throws Exception {
        ArrayNode result =
                service.doQueryHistoryTasks(entity.getProcessInstanceId());
        return buildSuccess(result);
    }

    /**
     * 提交前校验流程是都在平台资源分配时挂在到指定流程上
     *
     * @param request
     * @return
     */
    private String getAllocatedProcess(HttpServletRequest request) {
        String checkUrl = PropertyUtil.getProperty("bpmrest.checkUrl");
        JSONObject result = RestUtils.getInstance().doGetWithSign(checkUrl, request, JSONObject.class);
        if (BpmExUtil.inst().isSuccess4CheckSubmit(result)) {
            Object detailMsg = result.get("detailMsg");
            if (detailMsg != null) {
                Object jsonData = ((JSONObject) detailMsg).get("data");
                if (jsonData != null) {
                    return ((JSONObject) jsonData).getString("res_code");
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
        String delegateUser = params.get("userId");
        String comment = params.get("comment");
        if (comment == null) {
            comment = "";
        }
        if (delegateUser == null) {
            throw new BusinessException("入参userId为空");
        }
        if (taskId == null) {
            throw new BusinessException("入参taskId为空");
        }

        boolean isSuccess = service.doDelegateTask(taskId, delegateUser, comment);
//			NotifyService.instance().taskNotify() //TODO 消息发送暂时先不做
        if (isSuccess) {
            return buildSuccess("流程改派成功");
        }
        return buildError(null, "流程改派失败", RequestStatusEnum.FAIL_GLOBAL);
    }


    /************************************************************/
    private GenericBpmSdkService<T> service;

    public void setService(GenericBpmSdkService<T> bpmService) {
        this.service = bpmService;
        super.setService(bpmService);
    }

}
