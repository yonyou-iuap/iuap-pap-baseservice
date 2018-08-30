package com.yonyou.iuap.baseservice.persistence.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.RefParamVO;

/**
 * 解析参照xml配置
 * 
 * @author taomk 2015-7-14
 */
public class RefXMLParse {

	private static Logger logger = LoggerFactory.getLogger(RefXMLParse.class);
	private static RefXMLParse refXMLParse;
	private static Document refConfigDocument = null;
	private RefXMLParse() {

	}
	
	public static RefXMLParse getInstance() {
		if (refXMLParse == null) {
			synchronized (RefXMLParse.class) {
				// 获取发送者信息
				refConfigDocument = getDocument("ref");
			}
			return new RefXMLParse();
		} else {
			return refXMLParse;
		}
	}

	private static Document getDocument(String filePath) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		// 先从Java -D的变量中取值
		String filePath_absolute = System.getProperty(filePath);
		// 如果为空，再从java env的变量中取值
		if (filePath_absolute == null) {
			filePath_absolute = System.getenv().get(filePath);
		}
        // 从默认路径中读取
		if (filePath_absolute == null) {
			try {
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath+".xml");
				doc = reader.read(in);
			} catch (DocumentException e) {
				logger.error("指定文件路径：" + filePath + "不存在！", e);
			}
			
		} else{
			try {
				InputStream in = new FileInputStream(filePath_absolute);
				doc = reader.read(in);
			} catch (DocumentException e) {
				logger.error("解析文件：" + filePath + "时出错！", e);
			} catch (FileNotFoundException e) {
				logger.error("指定文件路径：" + filePath + "不存在！", e);
			}
		}
		return doc;
	}
	
	//根据refCode获取表名和字段 --表格
	public RefParamVO getMSConfig(String refCode) {
		// 得到根节点
		Element root = refConfigDocument.getRootElement();
		List<Element> RefViewModelVOs = root.elements("RefViewModelVO");
		for(Element refviewmodel:RefViewModelVOs){
			if(refCode.equals(refviewmodel.attributeValue("code"))){
				List<Element> ele = refviewmodel.elements("table");
				Element tableE = null;
				if(ele.size() == 1){
					tableE = ele.get(0);
				}else{
					//xml结构错误
				}
				
				RefParamVO refParamVO = new RefParamVO();
				//解析ref.xml表名
				String tableName = tableE.attributeValue("name");
				refParamVO.setTablename(tableName);
				//解析参照模型是否是标准模型
				String isBasic = tableE.attributeValue("isBasicTable");
				if(isBasic != null){
					refParamVO.setIsBasic(isBasic);
				}
				
				Map<String,String> map = new LinkedHashMap<String,String>();
				List<String> list = new ArrayList<String>();
				
				List<Element> showele = tableE.elements();
				for(Element showe : showele){
					String code = showe.attributeValue("code");
					String name = showe.getText();
					if("pidfield".equals(code)){
						if(!"".equals(name)){
							refParamVO.setPidfield(name);
							list.add(name);
						}
					}else if("idfield".equals(code)){
						if(!"".equals(name)){
							refParamVO.setIdfield(name);
						}	
					}else if("ts".equals(code)){
						if(!"".equals(name)){
							refParamVO.setTs(name);
						}	
					}else if("dr".equals(code)){
						if(!"".equals(name)){
							String[] sArray = name.split(",");
							refParamVO.setDr(sArray[0]);
							refParamVO.setDrValue(sArray[1]);
						}	
					}else{
						map.put(code,name);
						list.add(code);
					}
				}
				refParamVO.setShowcol(map);
				refParamVO.setExtcol(list);
				return refParamVO;
			}
		}
		return null;
	}
	
	//根据refCode获取表名和字段 --单选多选
	public RefParamVO getCheckboxMSConfig(String refCode) {
		// 得到根节点
		Element root = refConfigDocument.getRootElement();
		List<Element> RefViewModelVOs = root.elements("RefViewModelVO");
		for(Element refviewmodel:RefViewModelVOs){
			if(refCode.equals(refviewmodel.attributeValue("code"))){
				List<Element> ele = refviewmodel.elements("table");
				Element tableE = null;
				if(ele.size() == 1){
					tableE = ele.get(0);
				}else{
					//xml结构错误
				}
				
				RefParamVO refParamVO = new RefParamVO();
				//解析ref.xml表名
				String tableName = tableE.attributeValue("name");
				refParamVO.setTablename(tableName);
				//解析参照模型是否是标准模型
				String isBasic = tableE.attributeValue("isBasicTable");
				if(isBasic != null){
					refParamVO.setIsBasic(isBasic);
				}
				
				Map<String,String> map = new HashMap<String,String>();
				List<String> list = new ArrayList<String>();
				
				List<Element> showele = tableE.elements();
				for(Element showe : showele){
					String code = showe.attributeValue("code");
					String name = showe.getText();
					if("refcode".equals(code)){
						if(!"".equals(name)){
							refParamVO.setCodefield(name);
						}
					}else if("idfield".equals(code)){
						if(!"".equals(name)){
							refParamVO.setIdfield(name);
						}	
					}else if("refname".equals(code)){
						if(!"".equals(name)){
							refParamVO.setNamefield(name);
						}	
					}else if("ts".equals(code)){
						if(!"".equals(name)){
							refParamVO.setTs(name);
						}	
					}else if("dr".equals(code)){
						if(!"".equals(name)){
							String[] sArray = name.split(",");
							refParamVO.setDr(sArray[0]);
							refParamVO.setDrValue(sArray[1]);
						}	
					}else{
						map.put(code,name);
						list.add(code);
					}
				}
				refParamVO.setShowcol(map);
				refParamVO.setExtcol(list);
				return refParamVO;
			}
		}
		return null;
	}
	
	
	//根据refCode获取表名和字段 --树
	public RefParamVO getMSConfigTree(String refCode) {
		// 得到根节点
		Element root = refConfigDocument.getRootElement();
		List<Element> RefViewModelVOs = root.elements("RefViewModelVO");
		for(Element refviewmodel:RefViewModelVOs){
			if(refCode.equals(refviewmodel.attributeValue("code"))){
				List<Element> ele = refviewmodel.elements("tableTree");
				Element tableE = null;
				if(ele.size() == 1){
					tableE = ele.get(0);
				}else{
					//xml结构错误
				}
				
				RefParamVO refParamVO = new RefParamVO();
				String tableName = tableE.attributeValue("name");				
				refParamVO.setTablename(tableName);
				//解析参照模型是否是标准模型
				String isBasic = tableE.attributeValue("isBasicTable");
				if(isBasic != null && "false".equals(isBasic)){
					refParamVO.setIsBasic(isBasic);
				}
				
				Map<String,String> map = new HashMap<String,String>();
				List<String> list = new ArrayList<String>();
				List<Element> showele = tableE.elements();
				for(Element showe : showele){
					String code = showe.attributeValue("code");
					String name = showe.getText();
					if("pidfield".equals(code)){
						refParamVO.setPidfield(name);
					}else if("idfield".equals(code)){
						refParamVO.setIdfield(name);
					}else if("codefield".equals(code)){
						refParamVO.setCodefield(name);
					}else if("namefield".equals(code)){
						refParamVO.setNamefield(name);
					}else if("ts".equals(code)){
						if(!"".equals(name)){
							refParamVO.setTs(name);
						}	
					}else if("dr".equals(code)){
						if(!"".equals(name)){
							String[] sArray = name.split("|");
							refParamVO.setDr(sArray[0]);
							refParamVO.setDrValue(sArray[1]);
						}	
					}
				}
				return refParamVO;
			}
		}	
		return null;
	}
	
	//参照回显查询
		public RefParamVO getFilterConfig(String refCode) {
			// 得到根节点
			Element root = refConfigDocument.getRootElement();
			List<Element> RefViewModelVOs = root.elements("RefViewModelVO");
			String refType = null;
			for(Element refviewmodel:RefViewModelVOs){
				refType = refviewmodel.attributeValue("reftype");
				if(refType == null){
					if(refCode.equals(refviewmodel.attributeValue("code"))){
						List<Element> ele = refviewmodel.elements("table");
						Element tableE = null;
						if(ele.size() == 1){
							tableE = ele.get(0);
						}else{
							//xml结构错误
						}
						
						RefParamVO refParamVO = new RefParamVO();
						//解析ref.xml表名
						String tableName = tableE.attributeValue("name");
						refParamVO.setTablename(tableName);
						//解析参照模型是否是标准模型
						String isBasic = tableE.attributeValue("isBasicTable");
						if(isBasic != null){
							refParamVO.setIsBasic(isBasic);
						}
						
						Map<String,String> map = new HashMap<String,String>();
						List<String> list = new ArrayList<String>();
						
						List<Element> showele = tableE.elements();
						for(Element showe : showele){
							String code = showe.attributeValue("code");
							String name = showe.getText();
							if("pidfield".equals(code)){
								if(!"".equals(name)){
									refParamVO.setPidfield(name);
									list.add(name);
								}
							}else if("idfield".equals(code)){
								if(!"".equals(name)){
									refParamVO.setIdfield(name);
								}	
							}else if("ts".equals(code)){
								if(!"".equals(name)){
									refParamVO.setTs(name);
								}	
							}else if("dr".equals(code)){
								if(!"".equals(name)){
									String[] sArray = name.split(",");
									refParamVO.setDr(sArray[0]);
									refParamVO.setDrValue(sArray[1]);
								}	
							}else{
								map.put(code,name);
								list.add(code);
							}
						}
						refParamVO.setShowcol(map);
						refParamVO.setExtcol(list);
						return refParamVO;
					}
				}else if(refType != null && "1".equals(refType) ){
					if(refCode.equals(refviewmodel.attributeValue("code"))){
						List<Element> ele = refviewmodel.elements("tableTree");
						Element tableE = null;
						if(ele.size() == 1){
							tableE = ele.get(0);
						}else{
							//xml结构错误
						}
						
						RefParamVO refParamVO = new RefParamVO();
						String tableName = tableE.attributeValue("name");				
						refParamVO.setTablename(tableName);
						//解析参照模型是否是标准模型
						String isBasic = tableE.attributeValue("isBasicTable");
						if(isBasic != null && "false".equals(isBasic)){
							refParamVO.setIsBasic(isBasic);
						}
						
						Map<String,String> map = new HashMap<String,String>();
						List<String> list = new ArrayList<String>();
						List<Element> showele = tableE.elements();
						for(Element showe : showele){
							String code = showe.attributeValue("code");
							String name = showe.getText();
							if("pidfield".equals(code)){
								refParamVO.setPidfield(name);
								list.add(name);
							}else if("idfield".equals(code)){
								refParamVO.setIdfield(name);
								list.add(name);
							}else if("codefield".equals(code)){
								refParamVO.setCodefield(name);
								list.add(name);
							}else if("namefield".equals(code)){
								refParamVO.setNamefield(name);
								list.add(name);
							}else if("ts".equals(code)){
								if(!"".equals(name)){
									refParamVO.setTs(name);
								}	
							}else if("dr".equals(code)){
								if(!"".equals(name)){
									String[] sArray = name.split("|");
									refParamVO.setDr(sArray[0]);
									refParamVO.setDrValue(sArray[1]);
								}	
							}
						}
						refParamVO.setExtcol(list);
						return refParamVO;
					}
				}else if(refType != null && "4".equals(refType)){
					if(refCode.equals(refviewmodel.attributeValue("code"))){
						List<Element> ele = refviewmodel.elements("table");
						Element tableE = null;
						if(ele.size() == 1){
							tableE = ele.get(0);
						}else{
							//xml结构错误
						}
						
						RefParamVO refParamVO = new RefParamVO();
						//解析ref.xml表名
						String tableName = tableE.attributeValue("name");
						refParamVO.setTablename(tableName);
						//解析参照模型是否是标准模型
						String isBasic = tableE.attributeValue("isBasicTable");
						if(isBasic != null){
							refParamVO.setIsBasic(isBasic);
						}
						
						Map<String,String> map = new HashMap<String,String>();
						List<String> list = new ArrayList<String>();
						
						List<Element> showele = tableE.elements();
						for(Element showe : showele){
							String code = showe.attributeValue("code");
							String name = showe.getText();
							if("refcode".equals(code)){
								if(!"".equals(name)){
									refParamVO.setCodefield(name);
									list.add(name);
								}
							}else if("idfield".equals(code)){
								if(!"".equals(name)){
									refParamVO.setIdfield(name);
									list.add(name);
								}	
							}else if("refname".equals(code)){
								if(!"".equals(name)){
									refParamVO.setNamefield(name);
									list.add(name);
								}	
							}else if("ts".equals(code)){
								if(!"".equals(name)){
									refParamVO.setTs(name);
								}	
							}else if("dr".equals(code)){
								if(!"".equals(name)){
									String[] sArray = name.split(",");
									refParamVO.setDr(sArray[0]);
									refParamVO.setDrValue(sArray[1]);
								}	
							}else{
								map.put(code,name);
								list.add(code);
							}
						}
						refParamVO.setShowcol(map);
						refParamVO.setExtcol(list);
						return refParamVO;
					}
				}
				
			}
			return null;
		}
	
}
