package com.yonyou.iuap.baseservice.sdk.adapter;
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

import com.yonyou.iuap.baseservice.sdk.response.JsonResponse;

import java.util.Map;

public interface IRpcAdapter {
    String getSupport();

    <T>  JsonResponse<T> doGet(String url, Map<String, String> params) ;
    <T>  JsonResponse<T> doGet(String url, Map<String, String> params, Map<String, String> headers);
    <T>  JsonResponse<T> doGet(String url, Map<String, String> params, Map<String, String> headers, String charset) ;



    <T>  JsonResponse<T> doPost(String url, Map<String, String> params) ;
    <T>  JsonResponse<T> doPost(String url, Map<String, String> params, Map<String, String> headers) ;
    <T>  JsonResponse<T> doPost(String url, Map<String, String> params, Map<String, String> headers, String charset);


    <T>  JsonResponse<T> doPostWithJson(String url, String json, Map<String, String> headers) ;
    <T>  JsonResponse<T> doPostWithJson(String url, String json, Map<String, String> headers, String charset);



    <T>  JsonResponse<T> doDelete(String url, Map<String, String> params) ;
    <T>  JsonResponse<T> doDelete(String url, Map<String, String> params, Map<String, String> headers);
    <T>  JsonResponse<T> doDelete(String url, Map<String, String> params, Map<String, String> headers, String charset) ;

    <T>  JsonResponse<T> doPut(String url, Map<String, String> params);
    <T>  JsonResponse<T> doPut(String url, Map<String, String> params, Map<String, String> headers);
    <T>  JsonResponse<T> doPut(String url, Map<String, String> params, Map<String, String> headers, String charset);

}
