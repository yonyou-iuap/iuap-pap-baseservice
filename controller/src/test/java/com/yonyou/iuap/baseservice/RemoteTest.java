package com.yonyou.iuap.baseservice;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class RemoteTest {

	public static void main(String[] args) {
		//insert();
		//update();
		//delete();
	}
	
	//查询
	public static void select() {
		String result= HttpUtil.get("http://127.0.0.1:8088/gsp-orders/example_sany_order/list");
		System.out.println("分页查询："+result);
	}
	
	//新增
	public static void insert() {
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("orderCode", "SANY_10001");
		dataMap.put("orderName", "SANY_名称_新增");
		dataMap.put("supplier", "供应商编号1_新增");
		dataMap.put("supplierName", "供应商名称1_新增");
		dataMap.put("type", 1);
		dataMap.put("purchasing", "purchasing");
		dataMap.put("purchasingGroup", "purchasingGroup");
		dataMap.put("voucherDate", new Date());
		dataMap.put("approvalState", 1);
		dataMap.put("confirmState", 1);
		dataMap.put("closeState", 1);
		dataMap.put("remark", "备注1");
		String result= HttpUtil.post("http://127.0.0.1:8088/gsp-orders/example_sany_order/save",
				JSON.toJSONString(dataMap));
		System.out.println("新增保存："+result);
	}
	
	//更新
	public static void update() {
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("id", "10001");
		dataMap.put("orderCode", "SANY_10001");
		dataMap.put("orderName", "SANY_更新");
		dataMap.put("supplier", "供应商编号1_更新");
		dataMap.put("supplierName", "供应商名称1_更新");
		dataMap.put("type", 1);
		dataMap.put("purchasing", "purchasing");
		dataMap.put("purchasingGroup", "purchasingGroup");
		dataMap.put("voucherDate", new Date());
		dataMap.put("approvalState", 1);
		dataMap.put("confirmState", 1);
		dataMap.put("closeState", 1);
		dataMap.put("remark", "备注1");
		dataMap.put("ts", "2018-06-18 12:00:00 999");
		String result= HttpUtil.post("http://127.0.0.1:8088/gsp-orders/example_sany_order/save",
				JSON.toJSONString(dataMap));
		System.out.println("更新保存："+result);
	}
	
	//删除
	public static void delete() {
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("id", "1001");
		String result= HttpUtil.post("http://127.0.0.1:8088/gsp-orders/example_sany_order/delete",
				JSON.toJSONString(dataMap));
		System.out.println("删除操作："+result);
	}

}