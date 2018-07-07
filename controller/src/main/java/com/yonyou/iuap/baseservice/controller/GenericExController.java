package com.yonyou.iuap.baseservice.controller;

import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.service.GenericExService;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.persistence.utils.ValueConvertor;
import iuap.ref.sdk.refmodel.vo.RefUITypeEnum;
import iuap.ref.sdk.refmodel.vo.RefViewModelVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明：基础Controller——提供数据增、删、改、查
 * @author houlf
 * 2018年6月13日
 */
@RequestMapping(value = "/common/ref")
public abstract class GenericExController<T extends Model & LogicDel> extends GenericController<T>{
	
	private Logger log = LoggerFactory.getLogger(GenericExController.class);

	/*
	 * 获取表头信息
	 * @see com.yonyou.iuap.ref.sdk.refmodel.model.AbstractTreeGridRefModel#getRefModelInfo(com.yonyou.iuap.ref.model.RefViewModelVO)
	 */
	@RequestMapping(
			value = {"/getRefModelInfo"},
			method = {RequestMethod.POST}
	)
	@ResponseBody
	public RefViewModelVO getRefTableTitle(@RequestBody RefViewModelVO refViewModel) {

		refViewModel.setRefUIType(RefUITypeEnum.RefGridTree);
		RefParamVO refParamVO = RefXMLParse.getInstance().getMSConfig(refViewModel.getRefCode());

		Map<String,String> showcolMap = refParamVO.getShowcol();
		String[] showcode = null;
		String[] showname = null;
		if (showcolMap != null) {
			showcode = new String[showcolMap.size()];
			showname = new String[showcolMap.size()];
			int i = 0;
			for (Map.Entry<String, String> entry : showcolMap.entrySet()) {
				showcode[i] = entry.getKey();
				showname[i] = entry.getValue();
				i++;
			}
		}
		//显示列编码和名称
		refViewModel.setStrFieldCode(showcode);
		refViewModel.setStrFieldName(showname);
		refViewModel.setDefaultFieldCount(showcolMap.size());
		return refViewModel;
	}

	/*
	 * 树
	 * @see com.yonyou.iuap.ref.sdk.refmodel.model.AbstractTreeGridRefModel#blobRefTree(com.yonyou.iuap.ref.model.RefViewModelVO)
	 */
	@RequestMapping(
			value = {"/blobRefTree"},
			method = {RequestMethod.POST}
	)
	@ResponseBody
	public Map<String, Object> getRefTreeData(@RequestBody RefViewModelVO refModel) {

		//构建表体，其中list中为要查询的字段，必须和表头设置的相同，并且必须为表中的字段值
		RefParamVO params = RefXMLParse.getInstance().getMSConfigTree(refModel.getRefCode());

		Map<String, Object> mapList = new HashMap<String, Object>();
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		try {
			int pageNum = refModel.getRefClientPageInfo().getCurrPageIndex();
			int pageSize = 10000;

			PageRequest request = buildPageRequest(pageNum, pageSize, null);

			Map<String, String> conditions = new HashMap<String, String>();
			conditions.put("dr", "0");

			String idfield = StringUtils.isBlank(params.getIdfield()) ? "id"
					: params.getIdfield();
			String pidfield = StringUtils.isBlank(params.getPidfield()) ? "pid"
					: params.getPidfield();
			String codefield = StringUtils.isBlank(params.getCodefield()) ? "code"
					: params.getCodefield();
			String namefield = StringUtils.isBlank(params.getNamefield()) ? "name"
					: params.getNamefield();

			Page<Map<String, Object>> headpage = this.service.selectRefTree(
					request, params.getTablename(), idfield, pidfield,codefield,
					namefield, conditions, params.getExtcol());
			List<Map<String, Object>> headVOs = headpage.getContent();

			if (CollectionUtils.isNotEmpty(headVOs)) {
				results = buildRtnValsOfRefTree(headVOs);
			}

			mapList.put("dataList", results);
			mapList.put("refViewModel", refModel);
		} catch (Exception e) {
			System.out.println(e);
		}
		return mapList;
	}

	/*
	 * 获取表体信息
	 * @see com.yonyou.iuap.ref.sdk.refmodel.model.AbstractTreeGridRefModel#blobRefSearch(com.yonyou.iuap.ref.model.RefViewModelVO)
	 */
	@RequestMapping(
			value = {"/blobRefSearch"},
			method = {RequestMethod.POST}
	)
	@ResponseBody
	public Map<String, Object> getRefTableData(@RequestBody RefViewModelVO refModel) {

		//构建表体，其中list中为要查询的字段，必须和表头设置的相同，并且必须为表中的字段值
		RefParamVO refParamVO = RefXMLParse.getInstance().getMSConfig(refModel.getRefCode());

		Map<String, Object> mapList = new HashMap<String, Object>();
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		try {
			//获取当前页
			int pageNum = refModel.getRefClientPageInfo().getCurrPageIndex();
			//每页显示的数量
			int pageSize = 10;
			//拼装分页请求对象
			PageRequest request = buildPageRequest(pageNum, pageSize, null);

			refModel.getRefClientPageInfo().setPageSize(pageSize);

			//获取查询条件
			String content = refModel.getContent();

			//树节点的ID
			String condition = refModel.getCondition();

			Map<String, String> conditions = new HashMap<String,String>();
			if(content != null && !"".equals(content)){
				//按照自定义第一个字段做搜索查询
				conditions.put(refParamVO.getExtcol().get(0), content);
			}
			conditions.put("dr", "0");

			String idfield = StringUtils.isBlank(refParamVO.getIdfield()) ? "id"
					: refParamVO.getIdfield();

			//根据树节点 查找树下的列表
			if(condition != null && !"".equals(condition) && refParamVO.getPidfield()!=null && !"".equals(refParamVO.getPidfield())){
				conditions.put(refParamVO.getPidfield(),condition);
			}

			Page<Map<String, Object>> headpage = this.service.selectRefTable(
					request, refParamVO.getTablename(), idfield, conditions, refParamVO.getExtcol());

			//总页数
			refModel.getRefClientPageInfo().setPageCount(headpage.getTotalPages());

			List<Map<String, Object>> headVOs = headpage.getContent();

			if (CollectionUtils.isNotEmpty(headVOs)) {
				results = buildRtnValsOfRef(headVOs);
			}
			mapList.put("dataList", results);
			mapList.put("refViewModel", refModel);
		} catch (Exception e) {
			System.out.println(e);
		}
		return mapList;
	}

	private PageRequest buildPageRequest(int pageNum, int pageSize,
										 String sortColumn) {
		Sort sort = null;
		if (("auto".equalsIgnoreCase(sortColumn))
				|| (StringUtils.isEmpty(sortColumn))) {
			sort = new Sort(Sort.Direction.ASC, "ts");
		} else {
			sort = new Sort(Sort.Direction.DESC, sortColumn);
		}
		return new PageRequest(pageNum, pageSize, sort);
	}

	/**
	 * 过滤完的数据组装--树
	 *
	 * @return
	 */
	private List<Map<String, String>> buildRtnValsOfRefTree(
			List<Map<String, Object>> headVOs) {

		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		if ((headVOs != null) && (!headVOs.isEmpty())) {
			ValueConvertor convertor = new ValueConvertor();
			for (Map<String, Object> entity : headVOs) {
				Map<String, String> refDataMap = new HashMap<String, String>();
				String pid =null;
				for (String key : entity.keySet()) {
					if(key.equalsIgnoreCase("id")){
						refDataMap.put("refpk", entity.get(key).toString());
						refDataMap.put(key.toLowerCase(), entity.get(key).toString());
					}else if(key.equalsIgnoreCase("pid")){
						pid = key;
					}else{
						refDataMap.put(key.toLowerCase(), convertor.convertToJsonType(entity.get(key)).toString());
					}
				}
				if(pid != null){
					refDataMap.put(pid.toLowerCase(), convertor.convertToJsonType(entity.get(pid)).toString());
				}else{
					refDataMap.put("pid", "");
				}
				results.add(refDataMap);
			}
		}
		return results;
	}

	/**
	 * 过滤完的数据组装--表格
	 *
	 * @return
	 */
	private List<Map<String, String>> buildRtnValsOfRef(
			List<Map<String, Object>> headVOs) {
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		if ((headVOs != null) && (!headVOs.isEmpty())) {
			ValueConvertor convertor = new ValueConvertor();
			for (Map<String, Object> entity : headVOs) {
				Map<String, String> refDataMap = new HashMap<String, String>();
				for (String key : entity.keySet()) {
					if(key.equalsIgnoreCase("id")){
						refDataMap.put("refpk", entity.get(key).toString());
						refDataMap.put(key.toLowerCase(), entity.get(key).toString());
					}else{
						refDataMap.put(key.toLowerCase(),
								convertor.convertToJsonType(entity.get(key)).toString());
					}
				}
				results.add(refDataMap);
			}
		}
		return results;
	}

	/************************************************************/
	private GenericExService<T> service;

	public void setService(GenericExService<T> genericService) {
		this.service = genericService;
		super.setService(genericService);
	}

}