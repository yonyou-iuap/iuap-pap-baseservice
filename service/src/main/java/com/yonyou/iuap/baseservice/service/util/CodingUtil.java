package com.yonyou.iuap.baseservice.service.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.base.utils.RestUtils;
import com.yonyou.iuap.baseservice.entity.annotation.CodingEntity;
import com.yonyou.iuap.baseservice.entity.annotation.CodingField;
import com.yonyou.iuap.baseservice.persistence.utils.AnnotationUtil;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import com.yonyou.iuap.persistence.vo.pub.BusinessRuntimeException;
import com.yonyou.iuap.utils.PropertyUtil;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 说明：编码服务工具类
 * @author houlf
 * 2018年6月13日
 */
public class CodingUtil {
	
	private Logger log = LoggerFactory.getLogger(CodingUtil.class);
	
	private CodingUtil() {}
	
	public static CodingUtil inst() {
		return Inner.INST;
	}
	
	/**
	 * 为对象设置Code
	 * @param entity
	 */
	public void buildCoding(Object entity) {
		CodingEntity anno = entity.getClass().getDeclaredAnnotation(CodingEntity.class);
		Field codingField  = null;
		if(!StrUtil.isEmpty(anno.codingField())) {
			try {
				codingField = ReflectUtil.getField(entity.getClass(), anno.codingField());
			} catch (Exception exp) {
				log.error("获取Coding Field定义出错!", exp);
				throw new BusinessRuntimeException("获取Coding Field定义出错!");
			}
		}else {
			codingField = AnnotationUtil.getFirstFieldByAnnotation(entity.getClass(), CodingField.class);
		}
		if(codingField != null) {
			CodingField annatation = codingField.getAnnotation(CodingField.class);
			String code = CodingUtil.inst().genCode(annatation.code(), entity);

			if(ReflectUtil.getFieldValue(entity,codingField)==null|| "".equals(ReflectUtil.getFieldValue(entity,codingField) ) ){
				ReflectUtil.setFieldValue(entity, codingField, code);
			}

		}
	}
	
	/**
	 * 生成编码
	 * @param billObjCode
	 * @param entity
	 * @return
	 */
    public String genCode(String billObjCode, Object entity){
    	String billVo = JSONObject.toJSONString(entity);
        Map<String,String> data = new HashMap<String,String>();
        data.put("billObjCode",billObjCode);
        data.put("pkAssign","");
        data.put("billVo",billVo);

        //调用编号生成服务
        String getCodeUrl = PropertyUtil.getPropertyByKey("billcodeservice.base.url")+"/billcoderest/getBillCode";
        JSONObject billCodeInfo = RestUtils.getInstance().doPost(getCodeUrl,data,JSONObject.class);

        String getFlag = billCodeInfo.getString("status");
        String billCode = billCodeInfo.getString("billcode");

        if ("failed".equalsIgnoreCase(getFlag)){
            String errMsg = billCodeInfo.getString("msg");
            log.error("按编码规则生成编码出错："+JSON.toJSONString(data)+"，错误信息："+errMsg);
            throw new BusinessException("按编码规则生成编码出错",errMsg);
        }
        return billCode;
    }
    
    /**
     * 单据退号，以保证单据号连号的业务需要
     * @param billObjCode 编码对象code
     * @param entity
     * @param code 编码字段
     * @return
     */
    public void returnCode(String billObjCode, Object entity, String code){
        String billVo = JSONObject.toJSONString(entity);
        Map<String,String> data = new HashMap<String,String>();
        data.put("billObjCode",billObjCode);
        data.put("pkAssign","");
        data.put("billVo",billVo);
        data.put("billCode",code);

        //调用退号服务
        String returnUrl = PropertyUtil.getPropertyByKey("billcodeservice.base.url")+"/billcoderest/returnBillCode";
        JSONObject returnCodeInfo = RestUtils.getInstance().doPost(returnUrl,data,JSONObject.class);
        String returnFlag = returnCodeInfo.getString("status");
        if("failed".equalsIgnoreCase(returnFlag)){
            String errMsg = returnCodeInfo.getString("msg");
            log.error("退号失败，错误信息：" + JSON.toJSONString(returnCodeInfo) + 
            		  "\r\n退号申请信息：" + JSON.toJSONString(data));
            throw new BusinessException("单据退号失败!",errMsg);
        }else {
        	log.info("单据退号成功，退号申请信息："+JSON.toJSONString(data));
        }
    }

    /*****************************************************************/
    private static class Inner {
    	private static final CodingUtil INST = new CodingUtil();
	}

}