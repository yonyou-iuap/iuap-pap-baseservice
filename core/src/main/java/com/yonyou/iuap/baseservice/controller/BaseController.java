package com.yonyou.iuap.baseservice.controller;


import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonErrorResponse;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.ucf.common.i18n.MessageUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BaseController {
    private final static String SUC = "success";
    public BaseController() {
    }

    public JsonResponse buildError(String field, String msg, RequestStatusEnum status) {
        JsonErrorResponse errorResponse = new JsonErrorResponse();
        if (RequestStatusEnum.SUCCESS.equals(status)) {
            throw new IllegalArgumentException("状态码设置错误!");
        } else {
            errorResponse.setSuccess(status.getStatus());
            if (RequestStatusEnum.FAIL_GLOBAL.equals(status)) {
                errorResponse.setMessage(StringEscapeUtils.escapeHtml4 (msg));
            } else {
                errorResponse.getDetailMsg().put(StringEscapeUtils.escapeHtml4(field), StringEscapeUtils.escapeHtml4(msg));
            }

            return errorResponse;
        }
    }

    public JsonResponse buildGlobalError(String msg) {
        JsonErrorResponse errorResponse = new JsonErrorResponse();
        errorResponse.setMessage(StringEscapeUtils.escapeHtml4(msg));
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
            throw new IllegalArgumentException(MessageUtils.getMessage("状态码设置错误!"));
        } else {
            errorResponse.setSuccess(status.getStatus());
            Iterator i$ = msgMap.entrySet().iterator();

            while(i$.hasNext()) {
                Entry<String, String> entry = (Entry)i$.next();
                errorResponse.getDetailMsg().put(StringEscapeUtils.escapeHtml4(entry.getKey()), StringEscapeUtils.escapeHtml4(entry.getValue()));
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
        response.setMessage(SUC);
        response.getDetailMsg().put("data", value);
        return response;
    }

    public <T> JsonResponse buildSuccess() {
        JsonResponse response = new JsonResponse();
        response.setMessage(SUC);
        return response;
    }

    public JsonResponse buildMapSuccess(Map<String, Object> msgMap) {
        JsonResponse response = new JsonResponse();
        response.setDetailMsg(msgMap);
        return response;
    }

    public JSONObject buildJsonSuccess(String msg) {
        JSONObject json = new JSONObject();
        json.put("flag", SUC);
        json.put("msg",  msg);
        return json;
    }

    public JSONObject buildJsonFail(String msg) {
        JSONObject json = new JSONObject();
        json.put("flag", "fail");
        json.put("msg",  msg);
        return json;
    }

    public JSONObject buildJsonFail() {
        return this.buildJsonFail("");
    }
}
