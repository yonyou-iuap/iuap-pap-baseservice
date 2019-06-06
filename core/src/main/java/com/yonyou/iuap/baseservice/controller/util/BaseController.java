package com.yonyou.iuap.baseservice.controller.util;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonErrorResponse;
import com.yonyou.iuap.mvc.type.JsonResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 工具化基类
 * @author leon
 * @date 2019/4/23
 * @since 3.5.6
 */
public class BaseController {
    public BaseController() {
    }

    public JsonResponse buildError(String field, String msg, RequestStatusEnum status) {
        JsonErrorResponse errorResponse = new JsonErrorResponse();
        if (RequestStatusEnum.SUCCESS.equals(status)) {
            throw new IllegalArgumentException("状态码设置错误!");
        } else {
            errorResponse.setSuccess(status.getStatus());
            if (RequestStatusEnum.FAIL_GLOBAL.equals(status)) {
                errorResponse.setMessage(StringEscapeUtils.escapeHtml4(msg));
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
            throw new IllegalArgumentException("状态码设置错误!");
        } else {
            errorResponse.setSuccess(status.getStatus());
            Iterator var4 = msgMap.entrySet().iterator();

            while(var4.hasNext()) {
                Entry<String, String> entry = (Entry)var4.next();
                errorResponse.getDetailMsg().put(StringEscapeUtils.escapeHtml4((String)entry.getKey()), StringEscapeUtils.escapeHtml4((String)entry.getValue()));
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
        response.setMessage("成功");
        response.getDetailMsg().put("data", value);
        return response;
    }

    public <T> JsonResponse buildSuccess() {
        JsonResponse response = new JsonResponse();
        return response;
    }

    public JsonResponse buildMapSuccess(Map msgMap) {
        JsonResponse response = new JsonResponse();
        response.setDetailMsg(msgMap);
        return response;
    }

    public JSONObject buildJsonSuccess(String msg) {
        JSONObject json = new JSONObject();
        json.put("flag", "success");
        json.put("msg", StringUtils.isEmpty(msg) ? "操作成功!" : msg);
        return json;
    }

    public JSONObject buildJsonFail(String msg) {
        JSONObject json = new JSONObject();
        json.put("flag", "fail");
        json.put("msg", StringUtils.isEmpty(msg) ? "操作失败!" : msg);
        return json;
    }

    public JSONObject buildJsonFail() {
        return this.buildJsonFail("");
    }
}
