package com.yonyou.iuap.baseservice.support.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

@SuppressWarnings("all")
public class SimpleExcelImporter {
	
	public static final String splitSign = ":";
	
	public static SimpleExcelImporter inst() {
		return Inner.inst;
	}
	
	public static List<Map<String,Object>> readDataByMap(String file){
		try {
			InputStream ins = new FileInputStream(file);
			return readDataByMap(ins,0, 1);
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}
	}
	
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
			throw new RuntimeException();
		}
		for(int i=0; i<rowData.size(); i++) {
			Field field = ReflectUtil.getField(data.getClass(), headKey[i]);
			String fieldType = field.getType().getName();
			if(field.getType() == String.class) {
				ReflectUtil.setFieldValue(data, field, rowData.get(i));
			}else if(fieldType.equals("int") || field.getType() == Integer.class) {
				ReflectUtil.setFieldValue(data, field, convert2Int(rowData.get(i)));
			}else if(fieldType.equals("long") || field.getType() == Long.class) {
				
			}else if(fieldType.equals("float") || field.getType() == Float.class) {
				
			}else if(fieldType.equals("double") || field.getType() == Double.class) {
				
			}else {
				
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
	
	private static String convert2Str(Object value) {
		if(value == null) {
			return null;
		}else {
			if(value instanceof String) {
				return (String)value;
			}else {
				return String.valueOf(value);
			}
		}
	}
	
	private static int convert2Int(Object value) {
		if(value == null) {
			return 0;
		}else {
			if(value instanceof Integer) {
				return ((Integer) value).intValue();
			}else {
				return Integer.parseInt(String.valueOf(value));
			}
		}
	}
	
	/********************************************************/
	private static class Inner{
		private static SimpleExcelImporter inst = new SimpleExcelImporter();
	}


}