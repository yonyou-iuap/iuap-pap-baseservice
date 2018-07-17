package com.yonyou.iuap.baseservice.support.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yonyou.iuap.baseservice.support.excel.convertor.ConvertorHolder;
import com.yonyou.iuap.baseservice.support.excel.convertor.ValueConvertor;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

@SuppressWarnings("all")
public class SimpleExcelImporter {
	
	public static final String splitSign = ":";
	
	public static SimpleExcelImporter inst() {
		return Inner.inst;
	}
	
	/**
	 * 读取Excel文件返回List<Map>
	 * @param file
	 * @return
	 */
	public static List<Map<String,Object>> readDataByMap(String file){
		try {
			InputStream ins = new FileInputStream(file);
			return readDataByMap(ins,0, 1);
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * 指定headerRow、startRow读取Excel输入流
	 * @param ins
	 * @param headerRow
	 * @param startRow
	 * @return
	 */
	public static List<Map<String,Object>> readDataByMap(InputStream ins,int headerRow, int startRow){
		ExcelReader excelReader = ExcelUtil.getReader(ins);
		return excelReader.read(headerRow, startRow, excelReader.getSheet().getLastRowNum());
	}
	
	
	public static <T> List<T> readData(String file, String[] listHeader, Class<T> clazz){
		try {
			InputStream ins = new FileInputStream(file);
			return readData(ins, listHeader, 1, clazz);
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}
	}
	
	public static <T> List<T> readData(InputStream ins, String[] listHeader, Class<T> clazz){
		return readData(ins, listHeader, 1, clazz);
	}

	public static <T> List<T> readData(String file, String[] listHeader, int startRow, Class<T> clazz){
		try {
			InputStream ins = new FileInputStream(file);
			return readData(ins, listHeader, startRow, clazz);
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}
	}

	public static <T> List<T> readData(InputStream ins, String[] listHeader, int startRow, Class<T> clazz){
		String[][] header = getHeaderKeyName(listHeader);
		ExcelReader excelReader = ExcelUtil.getReader(ins);
		List<List<Object>> listData = excelReader.read(startRow);
		List<T> listResult = new ArrayList<T>();
		for(int i=0; i<listData.size(); i++) {
			T data = ReflectUtil.newInstanceIfPossible(clazz);
			if(data != null) {
				buildRowData(data, listData.get(i), header[0]);
			}
			listResult.add(data);
		}
		return listResult;
	}
	
	private static <T> void buildRowData(T data, List<Object> rowData, String[] headKey) {
		if(rowData.size() != headKey.length) {
			throw new RuntimeException("Excel数据列数与Header定义不一致!");
		}
		for(int i=0; i<rowData.size(); i++) {
			Field field = ReflectUtil.getField(data.getClass(), headKey[i]);
			ValueConvertor convertor = ConvertorHolder.inst().getConvertor(field.getType());
			if(convertor!=null) {
				ReflectUtil.setFieldValue(data, field, convertor.convert(rowData.get(i)));
			}else {
				throw new RuntimeException("无效的数据类型:"+field.getType());
			}
		}
	}
	
	private static String[][] getHeaderKeyName(String[] listHeader){
		String[] headerKey = new String[listHeader.length];
		String[] headerName = new String[listHeader.length];
		for(int i=0; i<listHeader.length; i++) {
			String[] curHeader = listHeader[i].split(splitSign);
			headerKey[i] = curHeader[0];
			headerName[i] = curHeader[1];
		}
		return new String[][]{headerKey, headerName};
	}
	
	/********************************************************/
	private static class Inner{
		private static SimpleExcelImporter inst = new SimpleExcelImporter();
	}

}