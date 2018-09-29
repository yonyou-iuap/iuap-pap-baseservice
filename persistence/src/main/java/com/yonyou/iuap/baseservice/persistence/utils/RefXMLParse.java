package com.yonyou.iuap.baseservice.persistence.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yonyou.iuap.baseservice.entity.RefParamConfig;
import org.apache.commons.lang3.StringUtils;
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
	private static RefXMLParse refXMLParse=null;
	private static volatile ConcurrentHashMap<String,RefParamVO> refParamVOs=new ConcurrentHashMap<>();
	private static final String REF_DECUMENT_NAME="ref";
	private RefXMLParse() {

	}
	
	public static  RefXMLParse getInstance() {
		if (refXMLParse == null) {
			synchronized (RefXMLParse.class) {
				try {
					initRefXml();
				} catch (NoSuchFieldException | IllegalAccessException  |ClassNotFoundException e) {
					logger.error(REF_DECUMENT_NAME+".xml，解析失败");
					logger.error(e.getStackTrace().toString());
					e.printStackTrace();
				}
				return new RefXMLParse();
			}
		} else {
			return refXMLParse;
		}
	}

	/**
	 * 根据refcode获取配置信息
	 * @param refCode 参照code
	 * @return 参照配置信息
	 */
	public RefParamVO getReParamConfig(String refCode) {
		return refParamVOs.get(refCode);
	}

	/**
	 * 加载参照配置文件
	 * @return xml对象
	 */
	private static Document getDocument() {
		SAXReader reader = new SAXReader();
		Document doc = null;
		/* 先从Java -D的变量中取值*/
		String filePath_absolute = System.getProperty(REF_DECUMENT_NAME);
		/* 如果为空，再从java env的变量中取值 */
		if (filePath_absolute == null) {
			filePath_absolute = System.getenv().get(REF_DECUMENT_NAME);
		}
        /* 从默认路径中读取 */
		if (filePath_absolute == null) {
			try {
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(REF_DECUMENT_NAME+".xml");
				doc = reader.read(in);
			} catch (DocumentException e) {
				logger.error("指定文件路径：" + REF_DECUMENT_NAME + "不存在！", e);
			}
			
		} else{
			try {
				InputStream in = new FileInputStream(filePath_absolute);
				doc = reader.read(in);
			} catch (DocumentException e) {
				logger.error("解析文件：" + REF_DECUMENT_NAME + "时出错！", e);
			} catch (FileNotFoundException e) {
				logger.error("指定文件路径：" + REF_DECUMENT_NAME + "不存在！", e);
			}
		}
		return doc;
	}


	/**
	 * 解析ref xml配置文件
	 * @throws NoSuchFieldException 属性不存在
	 * @throws IllegalAccessException 属性不合法
	 */
	private static void initRefXml() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		Document document=getDocument();
		Element   root            = document.getRootElement();
		List<Element> refViewModelVOs = root.elements(RefXmlConstract.REF_VIEW_MODEL_VO);
		for(Element refViewModelVOElement:refViewModelVOs) {
			RefParamVO refParamVO=new RefParamVO();
			String refType=refViewModelVOElement.attributeValue(RefXmlConstract.REF_TYPE);
			String refname=refViewModelVOElement.attributeValue(RefXmlConstract.REF_NAME);
			String voCode=refViewModelVOElement.attributeValue(RefXmlConstract.REF_VIEW_MODEL_VO_CODE).trim();
			String i18nKey=refViewModelVOElement.attributeValue(RefXmlConstract.REF_VIEW_MODEL_VO_I18N);
			if(StringUtils.isEmpty(refType)){
				logger.error("参照类型不可为空，请设reftype值，例如：<RefViewModelVO code=\"***\" refType = \"1\">");
			}
			refParamVO.setReftype(refType);
			refParamVO.setRefname(refname);
			refParamVO.setRefi18n(i18nKey);
			initParamConfig(refParamVO,refViewModelVOElement.element(RefXmlConstract.REF_TABLE_NODE),RefXmlConstract.REF_TABLE_NODE);
			initParamConfig(refParamVO,refViewModelVOElement.element(RefXmlConstract.REF_TREE_NODE),RefXmlConstract.REF_TREE_NODE);
			initThead(refParamVO,refViewModelVOElement.element(RefXmlConstract.REF_THEAD_NODE));
			refParamVOs.put(voCode,refParamVO);
		}
	}

	/**
	 * 初始化表格配置
	 * @param refParamVO 参照配置对象
	 * @param tableElement xml解析对象
	 * @throws NoSuchFieldException 属性不存在
	 * @throws IllegalAccessException 属性不合法
	 */
	private static void initParamConfig(RefParamVO refParamVO,Element tableElement,String refNode) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		if(tableElement!=null){
			List<Element>  fields             = tableElement.elements(RefXmlConstract.REF_FIELD_NODE);
			RefParamConfig refParamConfig     =new RefParamConfig();
			refParamConfig.setTableName(tableElement.attributeValue(RefXmlConstract.REF_TABLE_NAME));
			for(Element field:fields){
				setFieldValue(field, refParamConfig);
			}
			Element  filterContainer          = tableElement.element(RefXmlConstract.REF_FIELD_CODE_FILTERS);
			List<Element>  filters=filterContainer==null?null:filterContainer.elements(RefXmlConstract.REF_FIELD_CODE_FILTER);
			setFilters(filters,refParamConfig);

			if(RefXmlConstract.REF_TABLE_NODE.equals(refNode)){
				refParamVO.setRefParamConfigTable(refParamConfig);
			}else{
				refParamVO.setRefParamConfigTableTree(refParamConfig);
			}


		}
	}

	private static void setFilters(List<Element> elements, RefParamConfig refParamConfig) {
		if(elements==null||elements.size()<1){
			return ;
		}
		Map<String,String> map =refParamConfig.getFilters();
		for(Element element:elements){
			String code=element.attributeValue(RefXmlConstract.REF_FIELD_CODE_ATTRIBUTE);
			String value=StringUtils.isEmpty(element.getStringValue())?element.attributeValue(RefXmlConstract.REF_FIELD_VALUE_ATTRIBUTE):element.getStringValue();
			if(StringUtils.isEmpty(code)||StringUtils.isEmpty(value)){
				continue;
			}else {
				map.put(code,value);
			}
		}
	}

	/**
	 * 参数设值
	 * @param field 属性
	 * @param refParamConfig 对象
	 * @throws NoSuchFieldException 属性不存在
	 * @throws IllegalAccessException 属性不合法
	 */
	private static  void setFieldValue(Element field,RefParamConfig refParamConfig) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		Class clzz =  Class.forName(RefParamConfig.class.getName());
		String code=field.attributeValue(RefXmlConstract.REF_FIELD_CODE_ATTRIBUTE).trim();
		String value=field.getStringValue();
		if(StringUtils.isEmpty(code)||StringUtils.isEmpty(value)){
			return ;
		}
		Field               codeField=clzz.getDeclaredField(code);
		codeField.setAccessible(true);
		if(RefXmlConstract.REF_FIELD_CODE_codition.equals(code)){
			refParamConfig.getCondition().add(value);
		}else if(RefXmlConstract.REF_FIELD_CODE_EXTENSION.equals(code)){
			refParamConfig.getExtension().add(value);
		}else if(RefXmlConstract.REF_FIELD_CODE_SORT.equals(code)){
			String order=field.attributeValue(RefXmlConstract.REF_FIELD_CODE_ORDER);
			refParamConfig.setOrder(order);
			refParamConfig.setSort(value);
		}else{
			codeField.set(refParamConfig,value);
		}

	}



	/**
	 * 初始化表头信息
	 * @param refParamVO 参照配置对象
	 * @param theadElement xml解析对象
	 */
	private static void initThead(RefParamVO refParamVO,Element theadElement) {
		if(theadElement!=null){
			List<Element> fields= theadElement.elements(RefXmlConstract.REF_FIELD_NODE);
			LinkedHashMap<String, String> thead=new LinkedHashMap<>();
			LinkedHashMap<String, String> theadI18n=new LinkedHashMap<>();
			for(Element field:fields){
				String code=field.attributeValue(RefXmlConstract.REF_FIELD_CODE_ATTRIBUTE);
				String i18n=field.attributeValue(RefXmlConstract.REF_VIEW_MODEL_VO_I18N);
				String value=field.getStringValue();
				thead.put(code,value);
				theadI18n.put(code,i18n);
			}
			refParamVO.setThead(thead);
			refParamVO.setTheadI18n(theadI18n);
		}
	}


}

