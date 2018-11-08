package com.yonyou.iuap.baseservice.sdk.adapter.request;
/*
                       _ooOoo_
                      o8888888o
                      88" . "88
                      (| -_- |)
                      O\  =  /O
                   ____/`---'\____
                 .'  \\|     |//  `.
                /  \\|||  :  |||//  \
               /  _||||| -:- |||||-  \
               |   | \\\  -  /// |   |
               | \_|  ''\---/''  |   |
               \  .-\__  `-`  ___/-. /
             ___`. .'  /--.--\  `. . __
          ."" '<  `.___\_<|>_/___.'  >'"".
         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
         \  \ `-.   \_ __\ /__ _/   .-` /  /
    ======`-.____`-.___\_____/___.-`____.-'======
                       `=---='
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
             佛祖保佑       永无BUG
*/

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.baseservice.sdk.adapter.IRpcAdapter;
import com.yonyou.iuap.baseservice.sdk.properties.SdkProperties;
import com.yonyou.iuap.baseservice.sdk.response.JsonResponse;
import com.yonyou.iuap.baseservice.sdk.response.ResponseUtils;
import com.yonyou.iuap.generic.sign.SignEntity;
import com.yonyou.iuap.generic.sign.SignMake;
import com.yonyou.iuap.generic.utils.IConstant;
import com.yonyou.iuap.generic.utils.PropertiesUtils;
import com.yonyou.iuap.generic.utils.RestAPIUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 加签调用接口工具类
 * @author jin
 */
public class SdkSignHttpToolkit implements IRpcAdapter {
    private static        Logger              logger     = LoggerFactory.getLogger(SdkSignHttpToolkit.class);
    private static CloseableHttpClient httpClient = null;

    private static final RequestConfig REQUEST_CONFIG =RequestConfig.custom().setConnectTimeout(360000).setSocketTimeout(360000).build();

    static {
        try {
            /*初始化httpclient 配置属性，最大连接数*/
            String maxTotal = PropertiesUtils.getLocalProperty("httpclient.MaxTotal");
            if ((org.apache.commons.lang.StringUtils.isBlank(maxTotal)) || (!org.apache.commons.lang.StringUtils.isNumeric(maxTotal))) {
                maxTotal = "";
            }
            /*初始化httpclient 配置属性，单个uri最大连接数*/
            String maxPerRoute = PropertiesUtils.getLocalProperty("httpclient.MaxPerRoute");
            if ((org.apache.commons.lang.StringUtils.isBlank(maxPerRoute)) || (!org.apache.commons.lang.StringUtils.isNumeric(maxPerRoute))) {
                maxPerRoute = "";
            }
            /*连接池管理器*/
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(Integer.valueOf(maxTotal).intValue());
            cm.setDefaultMaxPerRoute(Integer.valueOf(maxPerRoute).intValue());
            /*创建默认client*/
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(REQUEST_CONFIG).setConnectionManager(cm).build();

        } catch (IOException e) {
            logger.error("Workbench SDK HttpTookit Init failed : ", e);
        }
    }


    @Override
    public String getSupport() {
        return "with-sign";
    }


    @Override
    public  <T> JsonResponse<T> doGet(String url, Map<String, String> params) {
        return doGet(url, params, null, CharEncoding.UTF_8);
    }

    @Override
    public  <T>  JsonResponse<T> doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return doGet(url, params, headers, CharEncoding.UTF_8);
    }

    @Override
    public  <T>  JsonResponse<T> doGet(String url, Map<String, String> params, Map<String, String> headers, String charset) {
        JsonResponse<T> jsonResponse=new JsonResponse<>();
        if (org.apache.commons.lang.StringUtils.isBlank(url)) {
            jsonResponse.failed("http client url  cannot be null");
            return jsonResponse;
        }
        try {
            if(headers==null){
                headers= new HashMap();
            }
            if(params==null){
                params=new HashMap();
            }
            logger.debug("传入的请求头为:{}",headers);
            logger.debug("传入的请求参数为:{}",params);
            /*初始化请求参数*/
            if (params!=null&&params.size()>0) {
                url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(generateParams(params), charset));
            }
            /*添加加签头*/
            url= sign(url,params, headers,SignMake.SIGNGET);

            logger.debug("生成请求的url为:{}",url);

            /*初始化请求*/
            HttpGet httpGet = new HttpGet(url);

            /*添加请求头*/
            Header[] headersAry=generateHeaders(headers);
            logger.debug("加签后的请求头为:{}",Arrays.toString(headersAry));
            httpGet.setHeaders(headersAry);

            httpGet.setConfig(REQUEST_CONFIG);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            /*判断返回状态码是否为200*/
            isRequestSuccess(response,httpGet);
            /*处理返回值信息*/
            jsonResponse= ResponseUtils.jsonResponse(response);
            jsonResponse.setStatusLine(response.getStatusLine());
            /*关闭response*/
            response.close();
            /*释放链接*/
            httpGet.releaseConnection();
            return jsonResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }



    @Override
    public  <T>  JsonResponse<T> doPost(String url, Map<String, String> params) {
        return doPost(url, params, null, CharEncoding.UTF_8);
    }

    @Override
    public  <T>  JsonResponse<T> doPost(String url, Map<String, String> params, Map<String, String> headers) {
        return doPost(url, params, headers, CharEncoding.UTF_8);
    }
    @Override
    public  <T>  JsonResponse<T> doPost(String url, Map<String, String> params, Map<String, String> headers, String charset) {
        JsonResponse<T> jsonResponse=new JsonResponse<>();
        if (org.apache.commons.lang.StringUtils.isBlank(url)) {
            jsonResponse.failed("http client url  cannot be null");
            return jsonResponse;
        }
        try {
            if(headers==null){
                headers= new HashMap();
            }
            if(params==null){
                params=new HashMap();
            }

            /*添加加签头*/
            url= sign(url,params, headers,SignMake.SIGNPOST);
            HttpPost httpPost = new HttpPost(url);

            /*添加参数*/
            httpPost.setEntity(new UrlEncodedFormEntity(generateParams(params), charset));
            /*添加请求头*/
            httpPost.setHeaders(generateHeaders(headers));
            httpPost.setConfig(REQUEST_CONFIG);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            /*判断返回状态码是否为200*/
            isRequestSuccess(response,httpPost);
            /*处理返回值信息*/
            jsonResponse= ResponseUtils.jsonResponse(response);
            jsonResponse.setStatusLine(response.getStatusLine());

            response.close();
            httpPost.releaseConnection();
            return jsonResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    @Override
    public  <T>  JsonResponse<T> doPostWithJson(String url, String json, Map<String, String> headers) {
        return doPostWithJson(url, json, headers, CharEncoding.UTF_8);
    }
    @Override
    public  <T>  JsonResponse<T> doPostWithJson(String url, String json, Map<String, String> headers, String charset) {
        JsonResponse<T> jsonResponse=new JsonResponse<>();
        if (org.apache.commons.lang.StringUtils.isBlank(url)) {
            jsonResponse.failed("http client url  cannot be null");
            return jsonResponse;
        }
        try {
            if(headers==null){
                headers= new HashMap();
            }
            if(StringUtils.isEmpty(json)){
                json= JSONObject.toJSONString(Collections.EMPTY_MAP);
            }
            /*添加加签头*/
            url= sign(url,json, headers);

            HttpPost httpPost = new HttpPost(url);
            /*添加请求头*/
            httpPost.setHeaders(generateHeaders(headers));

            /*添加请求体*/
            StringEntity s = new StringEntity(json, charset);
            s.setContentEncoding(CharEncoding.UTF_8);
            s.setContentType("application/json");
            httpPost.setEntity(s);
            httpPost.setConfig(REQUEST_CONFIG);
            /*处理返回结果*/
            CloseableHttpResponse response = httpClient.execute(httpPost);
            /*判断返回状态码是否为200*/
            isRequestSuccess(response,httpPost);

            /*处理返回值信息*/
            jsonResponse= ResponseUtils.jsonResponse(response);
            jsonResponse.setStatusLine(response.getStatusLine());

            response.close();
            httpPost.releaseConnection();
            return jsonResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }



    @Override
    public  <T>  JsonResponse<T> doDelete(String url, Map<String, String> params) {
        return doDelete(url, params, null, CharEncoding.UTF_8);
    }
    @Override
    public  <T>  JsonResponse<T> doDelete(String url, Map<String, String> params, Map<String, String> headers) {
        return doDelete(url, params, headers, CharEncoding.UTF_8);
    }
    @Override
    public  <T>  JsonResponse<T> doDelete(String url, Map<String, String> params, Map<String, String> headers, String charset) {
        JsonResponse<T> jsonResponse=new JsonResponse<>();
        if (org.apache.commons.lang.StringUtils.isBlank(url)) {
            jsonResponse.failed("http client url  cannot be null");
            return jsonResponse;
        }
        try {
            if(headers==null){
                headers= new HashMap();
            }
            if(params==null){
                params=new HashMap();
            }
            /*初始化请求参数*/
            if (params!=null&&params.size()>0) {
                url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(generateParams(params), charset));
            }

            /*添加加签头*/
            url= sign(url,params, headers,SignMake.SIGNGET);

            HttpDelete httpDelete = new HttpDelete(url);
            /*添加请求头*/
            httpDelete.setHeaders(generateHeaders(headers));

            httpDelete.setConfig(REQUEST_CONFIG);
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            /*判断返回状态码是否为200*/
            isRequestSuccess(response,httpDelete);

            /*处理返回值信息*/
            jsonResponse= ResponseUtils.jsonResponse(response);
            jsonResponse.setStatusLine(response.getStatusLine());

            response.close();
            httpDelete.releaseConnection();
            return jsonResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public  <T>  JsonResponse<T> doPut(String url, Map<String, String> params) {
        return doPut(url, params, null, CharEncoding.UTF_8);
    }

    @Override
    public  <T>  JsonResponse<T> doPut(String url, Map<String, String> params, Map<String, String> headers) {
        return doPut(url, params, headers, CharEncoding.UTF_8);
    }

    @Override
    public  <T>  JsonResponse<T> doPut(String url, Map<String, String> params, Map<String, String> headers, String charset) {
        JsonResponse<T> jsonResponse=new JsonResponse<>();
        if (org.apache.commons.lang.StringUtils.isBlank(url)) {
            jsonResponse.failed("http client url  cannot be null");
            return jsonResponse;
        }
        try {
            if(headers==null){
                headers= new HashMap();
            }
            if(params==null){
                params=new HashMap();
            }

            /*添加加签头*/
            url= sign(url,params, headers,SignMake.SIGNPOST);
            HttpPut httpPut = new HttpPut(url);

            /*添加参数*/
            httpPut.setEntity(new UrlEncodedFormEntity(generateParams(params), charset));
            /*添加请求头*/
            httpPut.setHeaders(generateHeaders(headers));
            httpPut.setConfig(REQUEST_CONFIG);
            CloseableHttpResponse response = httpClient.execute(httpPut);
            /*判断返回状态码是否为200*/
            isRequestSuccess(response,httpPut);
            /*处理返回值信息*/
            jsonResponse= ResponseUtils.jsonResponse(response);
            jsonResponse.setStatusLine(response.getStatusLine());

            response.close();
            httpPut.releaseConnection();
            return jsonResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 生成参数
     * @param params 参数map
     * @return 参数list
     */
    private static List<NameValuePair> generateParams(Map<String, String> params){
        List<NameValuePair> pairs = new ArrayList<>(params.size());
        if (params==null&&params.size()<1) {
            return pairs;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value =  entry.getValue();
            if (value != null) {
                pairs.add(new BasicNameValuePair( entry.getKey(), value));
            }
        }
        return pairs;
    }

    /**
     * 生成请求头
     * @param headers 请求头
     * @return 请求头数组
     */
    private static Header[] generateHeaders(Map<String, String> headers){
        List<Header> headerList=new ArrayList<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headerList.add(new BasicHeader(entry.getKey(),entry.getValue()));
        }
        return headerList.toArray(new Header[headerList.size()]);
    }

    private static void isRequestSuccess(HttpResponse response, HttpRequestBase requestBase) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            requestBase.abort();
            throw new RuntimeException("HttpClient,error status code :" + statusCode);
        }
    }
    /**
     * 加签方法
     * @param url url链接
     * @param params 参数
     * @param httpMethod http方法
     * @return
     */
    private static String sign(String url, Map<String, String> params,Map<String, String> headers, String httpMethod) {

        SignEntity signEntity = SignMake.signEntity(url, params, httpMethod, SdkProperties.getContextName(), SdkProperties.getAuthfilePath());
        headers.put("sign", signEntity.getSign());
        return RestAPIUtils.encode(signEntity.getSignURL(), IConstant.DEFAULT_CHARSET);
    }

    private static String sign(String url, String json,Map<String, String> headers){
        SignEntity signEntity = SignMake.signEntity(url,json, SdkProperties.getContextName(),SdkProperties.getAuthfilePath());
        headers.put("sign", signEntity.getSign());
        return RestAPIUtils.encode(signEntity.getSignURL(), IConstant.DEFAULT_CHARSET);
    }

}
