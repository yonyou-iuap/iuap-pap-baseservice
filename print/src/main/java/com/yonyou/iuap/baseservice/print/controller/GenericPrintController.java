package com.yonyou.iuap.baseservice.print.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;
import com.yonyou.iuap.baseservice.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 说明：基础Controller——应用平台打印服务回调取参数接口
 * @author leon
 * 2018年7月11日
 */
@SuppressWarnings("all")
public abstract  class GenericPrintController<T extends Model> extends BaseController {
    private Logger log = LoggerFactory.getLogger(GenericPrintController.class);

    @Autowired
    RefCommonService refService;
  

    @RequestMapping(value = "/dataForPrint", method = RequestMethod.POST)
	@ResponseBody
	public Object getDataForPrint(HttpServletRequest request) {
		String params = request.getParameter("params");
		JSONObject jsonObj = JSON.parseObject(params);
		String id = (String) jsonObj.get("id");
		
		T vo = service.findById(id);
		JSONObject jsonVo = JSONObject.parseObject(JSONObject.toJSON(vo).toString());
		
		JSONObject mainData = new JSONObject();
		JSONObject childData = new JSONObject();
		
		JSONArray mainDataJson = new JSONArray();// 主实体数据
		JSONArray childrenDataJson = new JSONArray();// 第一个子实体数据,多个子表需要多个数组
		
		Set<String> setKey = jsonVo.keySet();
		for(String key : setKey ){
			String value = jsonVo.getString(key);
			mainData.put(key, value);
		}
		mainDataJson.add(mainData);// 主表只有一行
		
		//增加子表的逻辑
		
		JSONObject boAttr = new JSONObject();
		//key：主表业务对象code
		boAttr.put("example_print", mainDataJson);//TODO 参数从何而来?
		//key：子表业务对象code
		boAttr.put("ygdemo_yw_sub", childrenDataJson);//TODO 同上??
		System.out.println(boAttr.toString());
		return boAttr.toString();
	}


    /************************************************************/
    private Map<Class ,GenericService> subServices = new HashMap<>();
    private GenericService<T> service;

    protected void setService(GenericService<T> genericService) {
        this.service = genericService;
    }
    protected void setSubService(Class entityClass, GenericService subService) {
        subServices.put(entityClass,subService);

    }

}
