package com.yonyou.iuap.baseservice.sdk.response;
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


import org.apache.http.StatusLine;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jin
 */
public class JsonResponse<T> extends LinkedHashMap<String,Object> implements Serializable{
    private static final long serialVersionUID = -3957696416833580484L;
    private  int status;
    private StatusLine statusLine;
    private T data;

    private String msg;

    public JsonResponse() {
    }

    public JsonResponse(int flag, String msg) {
        this.status=flag;
        this.msg=msg;
    }

    public JsonResponse(int flag, String msg,T data) {
        this.status=flag;
        this.msg=msg;
        this.data=data;
    }

    public void failed() {
        this.status=JsonContract.FAILED;
    }

    public void failed(String msg) {
        this.status=JsonContract.FAILED;
        this.msg=msg;
    }


    public void success() {
        this.status=JsonContract.SUCCESS;
    }

    public void success(String msg) {
        this.status=JsonContract.SUCCESS;
        this.msg=msg;
    }
    public void success(String msg,T data) {
        this.status=JsonContract.SUCCESS;
        this.msg=msg;
        this.data=data;
    }


    public void addProperties(String key, Object value) {
        this.put(key, value);
    }

    public void addProperties(Map<String,Object> properties) {
        this.putAll(properties);
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static void main(String[] args) {
//        JsonResponse<String> jsonResponse=new JsonResponse();
//        jsonResponse.setData("d");
//        System.out.println(JSON.toJSONString(jsonResponse));
//        JsonResponse<String> jsonResponse1=JSON.parseObject("{\"data\":\"d\"}",jsonResponse.getClass());
//        System.out.println(jsonResponse1);

//        JsonResponse<WBUser> jsonResponse =new JsonResponse();
//        WBUser wbUser=new WBUser();
//        wbUser.setEmail("dddd");
//        wbUser.setId("sdfsdf");
//        wbUser.setAvator("dfsf");
//
//        jsonResponse.setData(wbUser);
//        System.out.println(JSON.toJSONString(jsonResponse));
//        JsonResponse<WBUser> jsonResponse1=JSON.parseObject("{\"data\":{\"avator\":\"dfsf\",\"dr\":0,\"email\":\"dddd\",\"id\":\"sdfsdf\"}}",JsonResponse.class);


//        JsonResponse<List<WBUser>> jsonResponse =new JsonResponse();
//        List<WBUser> wbUsers=new ArrayList<>();
//        WBUser wbUser=new WBUser();
//        wbUser.setEmail("dddd");
//        wbUser.setId("sdfsdf");
//        wbUser.setAvator("dfsf");
//        wbUsers.add(wbUser);
//
//        jsonResponse.setData(wbUsers);
//        System.out.println(JSON.toJSONString(jsonResponse));
//        JsonResponse<List<WBUser>> jsonResponse1=JSON.parseObject("{\"data\":[{\"avator\":\"dfsf\",\"dr\":0,\"email\":\"dddd\",\"id\":\"sdfsdf\"}]}",JsonResponse.class);
//        System.out.println(jsonResponse1);

//        JsonResponse<Page<WBUser>> jsonResponse =new JsonResponse();
//        List<WBUser> wbUsers=new ArrayList<>();
//        WBUser                     wbUser       =new WBUser();
//        wbUser.setEmail("dddd");
//        wbUser.setId("sdfsdf");
//        wbUser.setAvator("dfsf");
//        wbUsers.add(wbUser);
//        Pageable     pageable=new PageRequest(4,5);
//        Page<WBUser> page=new PageImpl<WBUser>(wbUsers,pageable,10L);
//
//        jsonResponse.setData(page);
//        System.out.println(JSON.toJSONString(jsonResponse));
//        JsonResponse<Page<WBUser>> jsonResponse1=JSON.parseObject("{\"data\":{\"content\":[{\"avator\":\"dfsf\",\"dr\":0,\"email\":\"dddd\",\"id\":\"sdfsdf\"}],\"first\":false,\"last\":true,\"number\":4,\"numberOfElements\":1,\"size\":5,\"totalElements\":21,\"totalPages\":5}}",JsonResponse.class);
//        System.out.println(jsonResponse1);



    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }
}
