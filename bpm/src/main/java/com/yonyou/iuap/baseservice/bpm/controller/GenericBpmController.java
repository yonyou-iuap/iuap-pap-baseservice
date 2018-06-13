package com.yonyou.iuap.baseservice.bpm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.baseservice.bpm.model.BpmModel;
import com.yonyou.iuap.baseservice.service.GenericService;
import com.yonyou.iuap.bpm.web.IBPMBusinessProcessController;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.mvc.type.SearchParams;

import cn.hutool.core.util.StrUtil;

/**
 * 说明：工作流基础Controller：提供单据增删改查，以及工作流提交、撤回、以及工作流流转回调方法
 * @author Aton
 * 2018年6月13日
 */
public abstract class GenericBpmController<T extends BpmModel> extends BaseController
		implements IBPMBusinessProcessController {
	
	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list(PageRequest pageRequest, SearchParams searchParams) {
		Page<T> page = this.service.selectAllByPage(pageRequest, searchParams);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", page);
		return this.buildMapSuccess(map);
	}
	
	@RequestMapping(value = "/get")
	@ResponseBody
	public Object get(PageRequest pageRequest, SearchParams searchParams) {
		String id = MapUtils.getString(searchParams.getSearchMap(), "id");
		if(StrUtil.isBlank(id)) {
			return this.buildError("msg", "主键id参数为空!", RequestStatusEnum.FAIL_FIELD);
		}else {
			T entity = this.service.findById(id);
			return this.buildSuccess(entity);
		}
	}
	
	
	@RequestMapping(value = "/save")
	@ResponseBody
	public Object add(@RequestBody T entity) {
		JsonResponse jsonResp;
		try {
			this.service.save(entity);
			jsonResp = this.buildSuccess(entity);
		}catch(Exception exp) {
			jsonResp = this.buildError("msg", exp.getMessage(), RequestStatusEnum.FAIL_FIELD);
		}
		return jsonResp;
	}
	
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(@RequestBody T entity, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.service.delete(entity);
		return super.buildSuccess();
	}
	
	
	@RequestMapping(value = "/deleteBatch")
	@ResponseBody
	public Object deleteBatch(@RequestBody List<T> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.service.deleteBatch(list);
		return super.buildSuccess();
	}
	
	@RequestMapping(value = "/doSubmit")
	@ResponseBody
	public Object doSubmit(@RequestBody T entity, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/doRevoke")
	@ResponseBody
	public Object doRevoke(@RequestBody T entity, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object doApproveAction(Map<String, Object> arg0, HttpServletRequest arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public JsonResponse doRejectMarkerBillAction(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public JsonResponse doTerminationAction(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/************************************************************/
	private GenericService<T> service;

	public void setGenericService(GenericService<T> genericService) {
		this.service = genericService;
	}
	
}