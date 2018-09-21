package com.yonyou.iuap.baseservice.print.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.entity.annotation.Associative;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.print.entity.Printable;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 说明：基础Controller——应用平台打印服务回调取参数接口
 * @author leon
 * 2018年7月20日
 */
@SuppressWarnings("all")
public abstract  class GenericPrintController<T extends Printable> extends BaseController {
    private Logger log = LoggerFactory.getLogger(GenericPrintController.class);

    @RequestMapping(value = "/dataForPrint", method = RequestMethod.POST)
	@ResponseBody
	public Object getDataForPrint(HttpServletRequest request) {
		String params = request.getParameter("params");
		JSONObject jsonObj = JSON.parseObject(params);
		String id = (String) jsonObj.get("id");
		
		T vo = service.findById(id);
        if (vo.getMainBoCode()==null){
            return buildError("mainBoCode","主表业务对象编码为打印关键参数不可为空",RequestStatusEnum.FAIL_FIELD);
        }


        JSONObject jsonVo = JSONObject.parseObject(JSONObject.toJSON(vo).toString());
		
		JSONObject mainData = new JSONObject();
		JSONObject childData = new JSONObject();
		
		JSONArray mainDataJson = new JSONArray();// 主实体数据

		
		Set<String> setKey = jsonVo.keySet();
		for(String key : setKey ){
			String value = jsonVo.getString(key);
			mainData.put(key, value);
		}
		mainDataJson.add(mainData);// 主表只有一行


		//增加子表的逻辑
		
		JSONObject boAttr = new JSONObject();
		//key：主表业务对象code
		boAttr.put(vo.getMainBoCode(), mainDataJson);

        for (String subBoCode:subServices.keySet()){
            Associative associative= vo.getClass().getAnnotation(Associative.class);
            if (associative==null|| StringUtils.isEmpty(associative.fkName())){
                return buildError("","主子表打印需要在entity上增加@Associative并指定fkName",RequestStatusEnum.FAIL_FIELD);
            }
            List subList= subServices.get(subBoCode).queryList(associative.fkName(),id);
            JSONArray childrenDataJson = new JSONArray();
            childrenDataJson.addAll(subList);
            boAttr.put(subBoCode, childrenDataJson);//子表填充
        }


        log.debug("打印回调数据:"+boAttr.toString());
		return boAttr.toString();
	}


    /************************************************************/
    private Map<String ,GenericIntegrateService> subServices = new HashMap<>();
    private GenericIntegrateService<T> service;

    protected void setService(GenericIntegrateService<T> genericService) {
        this.service = genericService;
    }
    protected void setSubService(String subBoCode, GenericIntegrateService subService) {
        subServices.put(subBoCode,subService);

    }

}
