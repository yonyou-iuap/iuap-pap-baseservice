package com.yonyou.iuap.baseservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yonyou.iuap.baseservice.support.excel.SimpleExcelExporter;
import com.yonyou.iuap.baseservice.support.excel.SimpleExcelImporter;

@SuppressWarnings("all")
public class TestExcel {
	
	public static void main(String[] args) {
		//exporter();
		importer();
	}
	
	public static void importer() {
		SimpleExcelImporter importer = new SimpleExcelImporter();
		String[] listHeader = new String[] {"id:标识","code:编码","name:名称","createDate:日期", "intNum:整数","fltNum:小数","bdmNum:长小数"};
		List<Data> listData = importer.readData("d:/output.xlsx", listHeader, Data.class);
		
		//List<Map<String,Object>> listMap = importer.readDataByMap("d:/output.xlsx");
		System.out.println();
	}
	
	public static void exporter() {
		SimpleExcelExporter exporter = new SimpleExcelExporter();
		List listData = getObjectData(10);
		exporter.export(new String[] {"id:标识","code:编码","name:名称"}, (List)listData, "D:/output.xlsx");
	}
	
	public static List getMapData(int count){
		List<Map> list = new ArrayList<Map>();
		for(int i=0; i<count; i++) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", i);
			map.put("code", "code_"+i);
			map.put("name", "名称_"+i);
			list.add(map);
		}
		return list;
	}
	
	public static List getObjectData(int count){
		List<Entity> list = new ArrayList<Entity>();
		for(int i=0; i<count; i++) {
			Entity data = new Entity();
			data.setId(i);
			if(i%2==0) {
				data.setCode("code01_"+i);
			}
			data.setName("名称01_"+i);
			list.add(data);
		}
		return list;
	}

}
