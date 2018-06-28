package com.yonyou.iuap.baseservice.support.excel;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class ExcelExporter {
	
	public static final String splitSign = ":";
	
	public void export(List<String> listHeader, List<Object> listData, String output) {
		ExcelWriter writer = new ExcelWriter(output);
		//创建sheet
		writer.setOrCreateSheet("sheet1");
		//写入Header
		List<String> listKey = this.writeHeader(writer, listHeader);
		//写入Body数据
		this.writeBody(writer, listData, listKey);
		//写入磁盘文件
		writer.flush();
	}
	
	public void export(List<String> listHeader, List<Object> listData, OutputStream os) {
		ExcelWriter writer = new ExcelWriter(true);
		//创建sheet
		writer.setOrCreateSheet("sheet1");
		//写入Header
		List<String> listKey = this.writeHeader(writer, listHeader);
		//写入Body数据
		this.writeBody(writer, listData, listKey);
		//写入磁盘文件
		writer.flush(os);
	}
	
	/**
	 * 返回Header Code List
	 * @param listHeader
	 * @return
	 */
	private List<String> writeHeader(ExcelWriter writer, List<String> listHeader) {
		List<String> listCode = new ArrayList<String>();
		List<String> listName = new ArrayList<String>();
		for(int i=0; i<listHeader.size(); i++) {
			String[] header = listHeader.get(i).split(":");
			if(header.length==2) {
				listCode.add(header[0]);
				listCode.add(header[1]);
			}else {
				throw new RuntimeException("Excel Header信息格式不正确，请检查:"+listHeader.toString());
			}
		}
		writer.writeHeadRow(listName);
		return listCode;
	}
	
	private void writeBody(ExcelWriter writer, List<Object> listData, List<String> listHeader) {
		for(int row=0; row<listData.size(); row++) {
			if(listData.get(row) instanceof Map) {
				this.writeBodyByMap(writer, row, (Map)listData.get(row), listHeader);
			}else {
				this.writeBodyByVo(writer, row, listData.get(row), listHeader);
			}
		}
	}
	
	private void writeBodyByMap(ExcelWriter writer, int row,  Map dataMap, List<String> listHeader) {
		for(int col=0; col<listHeader.size(); col++) {
			writer.writeCellValue(row, col, dataMap.get(listHeader.get(col)));
		}
	}

	private void writeBodyByVo(ExcelWriter writer, int row, Object data, List<String> listHeader) {
		for(int col=0; col<listHeader.size(); col++) {
			Object value = ReflectUtil.getFieldValue(data, listHeader.get(col));
			writer.writeCellValue(row, col, value);
		}
	}

}