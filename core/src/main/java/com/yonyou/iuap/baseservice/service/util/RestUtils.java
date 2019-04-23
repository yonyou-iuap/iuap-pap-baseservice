package com.yonyou.iuap.baseservice.service.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.generic.sign.SignEntity;
import com.yonyou.iuap.generic.sign.SignMake;
import com.yonyou.iuap.generic.utils.PropertiesUtils;
import com.yonyou.iuap.utils.PropertyUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ContextLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RestUtils {
    protected Log logger = LogFactory.getLog(this.getClass());
    private static  RestUtils restUtils = null;
    public static final String CALLID_1 = "?callid=";
    public static final String CALLID_2 = "&callid=";
    public static final String URL = ",URL:";
    public static final String CALLID = "callid:";
    public static final String URL_TIME_CONSUME = ",url请求总耗时:";
    public static final String REQUEST = "\n request:";
    public static final String RESPONSE = "\n response:";
    private long slowUriMillis = 500L;
    private RestTemplate template;

    public static  RestUtils getInstance() {
        if (restUtils == null) {
            restUtils = new  RestUtils();
        }

        return restUtils;
    }

    private RestUtils() {
        String s_slowUriMillis = PropertyUtil.getPropertyByKey("slowUriMillis");
        if (s_slowUriMillis != null && !"".equals(s_slowUriMillis)) {
            this.slowUriMillis = Long.parseLong(s_slowUriMillis);
        }

        this.init();
    }

    private void init() {
        ApplicationContext ac = this.getAc();
        this.template = (RestTemplate)ac.getBean("restTemplate");
    }

    private ApplicationContext getAc() {
        return ContextLoader.getCurrentWebApplicationContext();
    }

    public <T> T doGet(String url, Object request, Class<T> responseType) {
        return this.doExe(url, request, responseType, HttpMethod.GET);
    }

    public <T> T doPost(String url, Object request, Class<T> responseType) {
        return this.doExe(url, request, responseType, HttpMethod.POST);
    }

    public <T> T doPost(String url, Object request, Map<String, String> requestHeaders, Class<T> responseType) {
        return this.doExe(url, request, responseType, requestHeaders, HttpMethod.POST);
    }

    private <T> T doExe(String url, Object request, Class<T> responseType, Map<String, String> requestHeaders, HttpMethod post) {
        ResponseEntity<T> rss = this.doService(url, request, responseType, post, requestHeaders);
        return rss.getBody();
    }

    private <T> ResponseEntity<T> doService(String url, Object request, Class<T> responseType, HttpMethod method, Map<String, String> requestHeaderMap) {
        HttpHeaders requestHeaders = new HttpHeaders();
        String cvalue = this.invocationToStr();
        requestHeaders.add("Authority", cvalue);
        requestHeaders.add("X-Requested-With", "XMLHttpRequest");
        requestHeaders.add("custmer", "jvm-http-client");
        Iterator var8 = requestHeaderMap.entrySet().iterator();

        while(var8.hasNext()) {
            Entry<String, String> headEntity = (Entry)var8.next();
            requestHeaders.add((String)headEntity.getKey(), (String)headEntity.getValue());
        }

        HttpEntity<Object> requestEntity = new HttpEntity(request, requestHeaders);
        JSONObject jsonObj = new JSONObject();
        if (method.equals(HttpMethod.GET) && request != null) {
            String json = JSON.toJSONString(request);
            jsonObj = JSON.parseObject(json);
        }

        long beforeTs = System.currentTimeMillis();
        String callid = InvocationInfoProxy.getCallid();
        String lasturl;
        if (url.indexOf("?") == -1) {
            lasturl = url + "?callid=" + callid;
        } else {
            lasturl = url + "&callid=" + callid;
        }

        ResponseEntity<T> rss = this.template.exchange(lasturl, method, requestEntity, responseType, jsonObj);
        long afterTs = System.currentTimeMillis();
        long ts = afterTs - beforeTs;
        if (ts > this.slowUriMillis) {
            this.logger.warn("callid:" + callid + ",url请求总耗时:" + ts + ",URL:" + lasturl);
            this.logger.info("callid:" + callid + ",URL:" + lasturl + "\n request:" + (request == null ? "" : request.toString()));
            this.logger.info("callid:" + callid + ",URL:" + lasturl + "\n response:" + rss.toString());
        } else {
            this.logger.debug("callid:" + callid + ",url请求总耗时:" + ts + ",URL:" + lasturl);
            this.logger.debug("callid:" + callid + ",URL:" + lasturl + "\n request:" + (request == null ? "" : request.toString()));
            this.logger.debug("callid:" + callid + ",URL:" + lasturl + "\n response:" + rss.toString());
        }

        return rss;
    }

    public <T> T doDelete(String url, Object request, Class<T> responseType) {
        return this.doExe(url, request, responseType, HttpMethod.DELETE);
    }

    private <T> T doExe(String url, Object request, Class<T> responseType, HttpMethod method) {
        ResponseEntity<T> rss = this.doService(url, request, responseType, method);
        return rss.getBody();
    }

    public <T> ResponseEntity<T> doService(String url, Object request, Class<T> responseType, HttpMethod method) {
        HttpHeaders requestHeaders = new HttpHeaders();
        String cvalue = this.invocationToStr();
        requestHeaders.add("Authority", cvalue);
        requestHeaders.add("X-Requested-With", "XMLHttpRequest");
        requestHeaders.add("custmer", "jvm-http-client");
        HttpEntity<Object> requestEntity = new HttpEntity(request, requestHeaders);
        JSONObject jsonObj = new JSONObject();
        if (method.equals(HttpMethod.GET) && request != null) {
            String json = JSON.toJSONString(request);
            jsonObj = JSON.parseObject(json);
        }

        long beforeTs = System.currentTimeMillis();
        String callid = InvocationInfoProxy.getCallid();
        String lasturl;
        if (url.indexOf("?") == -1) {
            lasturl = url + "?callid=" + callid;
        } else {
            lasturl = url + "&callid=" + callid;
        }

        ResponseEntity<T> rss = this.template.exchange(lasturl, method, requestEntity, responseType, jsonObj);
        long afterTs = System.currentTimeMillis();
        long ts = afterTs - beforeTs;
        if (ts > this.slowUriMillis) {
            this.logger.warn("callid:" + callid + ",url请求总耗时:" + ts + ",URL:" + lasturl);
            this.logger.info("callid:" + callid + ",URL:" + lasturl + "\n request:" + (request == null ? "" : request.toString()));
            this.logger.info("callid:" + callid + ",URL:" + lasturl + "\n response:" + rss.toString());
        } else {
            this.logger.debug("callid:" + callid + ",url请求总耗时:" + ts + ",URL:" + lasturl);
            this.logger.debug("callid:" + callid + ",URL:" + lasturl + "\n request:" + (request == null ? "" : request.toString()));
            this.logger.debug("callid:" + callid + ",URL:" + lasturl + "\n response:" + rss.toString());
        }

        return rss;
    }

    public <T> T doGetWithSign(String url, Object request, Class<T> responseType) {
        return this.doExeWithSign(url, request, responseType, HttpMethod.GET);
    }

    public <T> T doPostWithSign(String url, Object request, Class<T> responseType) {
        return this.doExeWithSign(url, request, responseType, HttpMethod.POST);
    }

    public <T> T doDeleteWithSign(String url, Object request, Class<T> responseType) {
        return this.doExeWithSign(url, request, responseType, HttpMethod.DELETE);
    }

    private <T> T doExeWithSign(String url, Object request, Class<T> responseType, HttpMethod method) {
        ResponseEntity<T> rss = this.doServiceWithSign(url, request, responseType, method);
        return rss.getBody();
    }

    public <T> ResponseEntity<T> doServiceWithSign(String url, Object request, Class<T> responseType, HttpMethod method) {
        String prefix = null;
        String authFilePath = null;

        try {
            prefix = PropertiesUtils.getCustomerProperty("context.name");
            authFilePath =  RestUtils.class.getClassLoader().getResource("authfile.txt").getPath();
        } catch (IOException var21) {
            var21.printStackTrace();
        }

        String json = "{}";
        if (request != null) {
            json = JSON.toJSONString(request);
        }

        String callid = InvocationInfoProxy.getCallid();
        if (url.indexOf("?") == -1) {
            url = url + "?callid=" + callid;
        } else {
            url = url + "&callid=" + callid;
        }

        SignEntity signEntity;
        if (method == HttpMethod.POST) {
            signEntity = SignMake.signEntity(url, json, prefix, authFilePath);
        } else {
            signEntity = SignMake.signEntity(url, (Map)null, method.toString(), prefix, authFilePath);
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("sign", signEntity.getSign());
        HttpEntity<Object> requestEntity = new HttpEntity(request, requestHeaders);
        JSONObject jsonObj = new JSONObject();
        if (method.equals(HttpMethod.GET)) {
            jsonObj = JSON.parseObject(json);
        }

        long beforeTs = System.currentTimeMillis();
        String lasturl = signEntity.getSignURL();
        ResponseEntity<T> rss = this.template.exchange(lasturl, method, requestEntity, responseType, jsonObj);
        long afterTs = System.currentTimeMillis();
        long ts = afterTs - beforeTs;
        if (ts > this.slowUriMillis) {
            this.logger.warn("callid:" + callid + ",url请求总耗时:" + ts + ",URL:" + lasturl);
            this.logger.info("callid:" + callid + ",URL:" + lasturl + "\n request:" + (request == null ? "" : request.toString()));
            this.logger.info("callid:" + callid + ",URL:" + lasturl + "\n response:" + rss.toString());
        } else {
            this.logger.debug("callid:" + callid + ",url请求总耗时:" + ts + ",URL:" + lasturl);
            this.logger.debug("callid:" + callid + ",URL:" + lasturl + "\n request:" + (request == null ? "" : request.toString()));
            this.logger.debug("callid:" + callid + ",URL:" + lasturl + "\n response:" + rss.toString());
        }

        return rss;
    }

    private String invocationToStr() {
        String cvalue = "";
        Map<String, String> data = this.setInvocationInfo();
        if (MapUtils.isNotEmpty(data)) {
            Iterator iterator = data.entrySet().iterator();

            while(iterator.hasNext()) {
                Entry<String, String> entry = (Entry)iterator.next();
                if (entry.getValue() != null && StringUtils.isNotEmpty((CharSequence)entry.getValue())) {
                    cvalue = cvalue + (String)entry.getKey() + "=" + (String)entry.getValue() + ";";
                }
            }
        }

        return cvalue;
    }

    private Map<String, String> setInvocationInfo() {
        Map<String, String> map = new HashMap();
        if (InvocationInfoProxy.getCallid() != null) {
            map.put("u_callid", InvocationInfoProxy.getCallid());
        }

        if (InvocationInfoProxy.getLocale() != null) {
            map.put("u_locale", InvocationInfoProxy.getLocale());
        }

        if (InvocationInfoProxy.getSysid() != null) {
            map.put("u_sysid", InvocationInfoProxy.getSysid());
        }

        if (InvocationInfoProxy.getTenantid() != null) {
            map.put("tenantid", InvocationInfoProxy.getTenantid());
            map.put("current_tenant_id", InvocationInfoProxy.getTenantid());
        }

        if (InvocationInfoProxy.getTheme() != null) {
            map.put("u_theme", InvocationInfoProxy.getTheme());
        }

        if (InvocationInfoProxy.getTimeZone() != null) {
            map.put("u_timezone", InvocationInfoProxy.getTimeZone());
        }

        if (InvocationInfoProxy.getUserid() != null) {
            map.put("u_usercode", InvocationInfoProxy.getUserid());
            map.put("current_user_name", InvocationInfoProxy.getUserid());
        }

        if (InvocationInfoProxy.getUsername() != null) {
            map.put("u_username", InvocationInfoProxy.getUsername());
        }

        if (InvocationInfoProxy.getAppCode() != null) {
            map.put("u_appCode", InvocationInfoProxy.getAppCode());
        }

        if (InvocationInfoProxy.getProviderCode() != null) {
            map.put("u_providerCode", InvocationInfoProxy.getProviderCode());
        }

        if (InvocationInfoProxy.getToken() != null) {
            map.put("token", InvocationInfoProxy.getToken());
        }

        if (InvocationInfoProxy.getLogints() != null) {
            map.put("u_logints", InvocationInfoProxy.getLogints());
        }

        if (InvocationInfoProxy.getParamters() != null) {
            map.putAll(InvocationInfoProxy.getParamters());
        }

        map.put("call_thread_id", String.valueOf(MDC.get("call_thread_id")));
        return map;
    }

    public static String createParam(String url, Map<String, Object> map) {
        url = url + "?";
        Iterator var2 = map.keySet().iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            if (map.get(key) != null) {
                url = url + key + "=" + map.get(key) + "&";
            }
        }

        return url.substring(0, url.length() - 1);
    }
}
