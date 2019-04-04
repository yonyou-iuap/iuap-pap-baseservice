package com.yonyou.iuap.baseservice.controller;


import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonErrorResponse;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.pap.base.i18n.MessageSourceUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class BaseController {
    public BaseController() {
    }

    public JsonResponse buildError(String field, String msg, RequestStatusEnum status) {
        JsonErrorResponse errorResponse = new JsonErrorResponse();
        if (RequestStatusEnum.SUCCESS.equals(status)) {
            throw new IllegalArgumentException(MessageSourceUtil.getMessage("ja.bpm.bas.0001", "状态码设置错误!"));
        } else {
            errorResponse.setSuccess(status.getStatus());
            if (RequestStatusEnum.FAIL_GLOBAL.equals(status)) {
                errorResponse.setMessage(StringEscapeUtils.escapeHtml(msg));
            } else {
                errorResponse.getDetailMsg().put(StringEscapeUtils.escapeHtml(field), StringEscapeUtils.escapeHtml(msg));
            }

            return errorResponse;
        }
    }

    public JsonResponse buildGlobalError(String msg) {
        JsonErrorResponse errorResponse = new JsonErrorResponse();
        errorResponse.setMessage(StringEscapeUtils.escapeHtml(msg));
        return errorResponse;
    }

    public JsonResponse buildFaild(String msg) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(RequestStatusEnum.FAIL_FIELD.getStatus());
        response.setMessage(msg);
        return response;
    }

    public JsonResponse buildError(Map<String, String> msgMap, RequestStatusEnum status) {
        JsonErrorResponse errorResponse = new JsonErrorResponse();
        if (RequestStatusEnum.SUCCESS.equals(status)) {
            throw new IllegalArgumentException(MessageSourceUtil.getMessage("ja.bpm.bas.0001", "状态码设置错误!"));
        } else {
            errorResponse.setSuccess(status.getStatus());
            Iterator i$ = msgMap.entrySet().iterator();

            while(i$.hasNext()) {
                Entry<String, String> entry = (Entry)i$.next();
                errorResponse.getDetailMsg().put(StringEscapeUtils.escapeHtml((String)entry.getKey()), StringEscapeUtils.escapeHtml((String)entry.getValue()));
            }

            return errorResponse;
        }
    }

    public JsonResponse buildSuccess(String key, Object value) {
        JsonResponse response = new JsonResponse();
        response.getDetailMsg().put(key, value);
        return response;
    }

    public <T> JsonResponse buildSuccess(Object value) {
        JsonResponse response = new JsonResponse();
        response.setMessage(MessageSourceUtil.getMessage("ja.bpm.bas.0002", "操作成功"));
        response.getDetailMsg().put("data", value);
        return response;
    }

    public <T> JsonResponse buildSuccess() {
        JsonResponse response = new JsonResponse();
        response.setMessage(MessageSourceUtil.getMessage("ja.bpm.bas.0002", "操作成功"));
        return response;
    }

    public JsonResponse buildMapSuccess(Map<String, Object> msgMap) {
        JsonResponse response = new JsonResponse();
        response.setDetailMsg(msgMap);
        return response;
    }

    public JSONObject buildJsonSuccess(String msg) {
        JSONObject json = new JSONObject();
        json.put("flag", "success");
        json.put("msg", StringUtils.isEmpty(msg) ? MessageSourceUtil.getMessage("ja.bpm.bas.0003", "操作成功!") : msg);
        return json;
    }

    public JSONObject buildJsonFail(String msg) {
        JSONObject json = new JSONObject();
        json.put("flag", "fail");
        json.put("msg", StringUtils.isEmpty(msg) ? MessageSourceUtil.getMessage("ja.bpm.bas.0004", "操作失败!") : msg);
        return json;
    }

    public JSONObject buildJsonFail() {
        return this.buildJsonFail("");
    }
}
