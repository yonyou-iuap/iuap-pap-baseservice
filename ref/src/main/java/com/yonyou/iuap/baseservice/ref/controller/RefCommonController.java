package com.yonyou.iuap.baseservice.ref.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;
import com.yonyou.iuap.baseservice.ref.utils.ValueConvertor;
import com.yonyou.iuap.ref.model.RefUITypeEnum;
import com.yonyou.iuap.ref.model.RefVertion;
import com.yonyou.iuap.ref.model.RefViewModelVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明：参照基础controller,所有参照都通过平台回调到这个地址取数据
 * @WARN 需要平台的REF_REFINFO表中有相应的配置数据,例如 23    common_ref	通用树表参照	common_ref		/iuap_pap_quickstart/common/				AAAzpkAAGAAAev+AAA
 * @author leon
 * 2018年7月11日
 * @update 2018-7-25 移除了对平台uitemplate_common的依赖
 */
@Controller
@RequestMapping(value = "/common")
public final class RefCommonController  {
	
	private Logger log = LoggerFactory.getLogger(RefCommonController.class);

    /*
     * 获取表头信息
     * @see com.yonyou.iuap.ref.sdk.refmodel.model.AbstractTreeGridRefModel#getRefModelInfo(com.yonyou.iuap.ref.model.RefViewModelVO)
     */
    @RequestMapping(
            value = {"/getRefModelInfo"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public RefViewModelVO getRefModelInfo(@RequestBody RefViewModelVO refViewModel) {
        refViewModel.setRefUIType(RefUITypeEnum.RefGridTree);
        refViewModel.setRefVertion(RefVertion.NewRef);
        RefViewModelVO refModel =  refViewModel;
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
        refModel.setStrFieldCode(showcode);
        refModel.setStrFieldName(showname);
        refModel.setDefaultFieldCount(showcolMap.size());
        return refModel;
    }

    /**
     * 通过pk查询所有数据,String pk数组入参
     * @param arg0
     * @return
     */
    @RequestMapping(
            value = {"/matchPKRefJSON"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public List<Map<String, String>> matchPKRefJSON(RefViewModelVO arg0) {
        return null;
    }
    
    /**
     * 通过pk查询所有数据,String pk数组入参
     * @return
     */
    @RequestMapping(
    		value = {"/getCommonRefData"},
    		method = {RequestMethod.POST}
    		)
    @ResponseBody
    public Map<String, Object> commonRefsearch(@RequestBody RefViewModelVO refModel) {
    	//前台传过来的refType来做请求参数过滤
        String transmitParam = refModel.getTransmitParam();
        String refType = transmitParam;

        //构建表体，其中list中为要查询的字段，必须和表头设置的相同，并且必须为表中的字段值
        RefParamVO refParamVO = RefXMLParse.getInstance().getCheckboxMSConfig(refModel.getRefCode());

        Map<String, Object> mapList = new HashMap<String, Object>();
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        try {
            //获取当前页
            int pageNum = refModel.getRefClientPageInfo().getCurrPageIndex();
            //每页显示的数量
            int pageSize = 10;
            //拼装分页请求对象
            PageRequest request = null;

            Map<String, String> conditions = new HashMap<String,String>();
            
            String basic = refParamVO.getIsBasic();
            if(basic != null && "false".equals(basic)){//需要业务自己传递orderby字段
            	String ts = refParamVO.getTs();
            	request = buildPageRequest(pageNum, pageSize, ts);
            	conditions.put(refParamVO.getDr(),refParamVO.getDrValue());
            }else if(basic != null && "true".equals(basic)){//orderby ts
            	request = buildPageRequest(pageNum, pageSize, "auto");
            	conditions.put("dr", "0");
            }else{//不加orderby过滤
            	request = buildPageRequest(pageNum, pageSize, null);
            }
            
            refModel.getRefClientPageInfo().setPageSize(pageSize);

            //获取查询条件 --如果content
            String content = refModel.getContent();
            
            if(content != null && !"".equals(content)){
                //对参照所有列进行模糊查询
                for(String extcol : refParamVO.getExtcol()){
                    conditions.put(extcol, content);
                }
            }

            String idfield = StringUtils.isBlank(refParamVO.getIdfield()) ? "id"
                    : refParamVO.getIdfield();
            String codefield = StringUtils.isBlank(refParamVO.getIdfield()) ? "refcode"
            		: refParamVO.getCodefield();
            String namefield = StringUtils.isBlank(refParamVO.getIdfield()) ? "refname"
            		: refParamVO.getNamefield();

            Page<Map<String, Object>> headpage = this.service.getCheckboxData(
                    request, refParamVO.getTablename(), idfield,codefield,namefield, conditions, refParamVO.getExtcol());

            //总页数
            refModel.getRefClientPageInfo().setPageCount(headpage.getTotalPages());

            List<Map<String, Object>> headVOs = headpage.getContent();

            if (CollectionUtils.isNotEmpty(headVOs)) {
                results = buildRtnValsOfCheckboxRef(headVOs);
            }
            
            mapList.put("dataList", results);
            mapList.put("refViewModel", refModel);
        } catch (Exception e) {
            System.out.println(e);
        }
        return mapList;
    }

    /**
     * 模糊查询,content 入参
     * @param arg0
     * @return
     */
    @RequestMapping(
            value = {"/filterRefJSON"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public List<Map<String, String>> filterRefJSON(RefViewModelVO arg0) {
        //
        return null;
    }

    /**
     * 表单下拉提示数据,智能输入检索用
     * @param arg0
     * @return
     */
    @RequestMapping(
            value = {"/matchBlurRefJSON"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public List<Map<String, String>> matchBlurRefJSON(RefViewModelVO arg0) {
        //
        return null;
    }

    /**
     * 左树查询
     * @param arg0
     * @return
     */
    @RequestMapping(
            value = {"/blobRefClassSearch"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public List<Map<String, String>> blobRefClassSearch(RefViewModelVO arg0) {
        //
        return null;
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
    public Map<String, Object> blobRefTree(@RequestBody RefViewModelVO refModel) {

        //构建表体，其中list中为要查询的字段，必须和表头设置的相同，并且必须为表中的字段值
        RefParamVO params = RefXMLParse.getInstance().getMSConfigTree(refModel.getRefCode());

        Map<String, Object> mapList = new HashMap<String, Object>();
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        try {
            int pageNum = refModel.getRefClientPageInfo().getCurrPageIndex();
            int pageSize = refModel.getRefClientPageInfo().getPageSize();
            PageRequest request = null;
            Map<String, String> conditions = new HashMap<String, String>();
            
            String basic = params.getIsBasic();
            if(basic != null && "false".equals(basic)){//需要业务自己传递orderby字段
            	String ts = params.getTs();
            	request = buildPageRequest(pageNum, pageSize, ts);
            	conditions.put(params.getDr(), params.getDrValue());
            }else if(basic != null && "true".equals(basic)){//orderby ts
            	request = buildPageRequest(pageNum, pageSize, "auto");
            	conditions.put("dr", "0");
            }else{//不加orderby过滤
            	request = buildPageRequest(pageNum, pageSize, null);
            }

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
    public Map<String, Object> blobRefSearch(@RequestBody RefViewModelVO refModel) {

        //前台传过来的refType来做请求参数过滤
        String transmitParam = refModel.getTransmitParam();
        String refType = transmitParam;


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
            PageRequest request = null;

            Map<String, String> conditions = new HashMap<String,String>();
            
            String basic = refParamVO.getIsBasic();
            if(basic != null && "false".equals(basic)){//需要业务自己传递orderby字段
            	String ts = refParamVO.getTs();
            	request = buildPageRequest(pageNum, pageSize, ts);
            	conditions.put(refParamVO.getDr(),refParamVO.getDrValue());
            }else if(basic != null && "true".equals(basic)){//orderby ts
            	request = buildPageRequest(pageNum, pageSize, "auto");
            	conditions.put("dr", "0");
            }else{//不加orderby过滤
            	request = buildPageRequest(pageNum, pageSize, null);
            }
            
            refModel.getRefClientPageInfo().setPageSize(pageSize);

            //树节点的ID
            String condition = refModel.getCondition();

            //获取查询条件 --如果content
            String content = refModel.getContent();
            if("6".equals(refType) && content != null){
                JSONObject jsonObject = JSON.parseObject(content);
                Map<String,Object> map = jsonObject;
                for(String key : map.keySet()){
                    conditions.put(key, (String)map.get(key));
                }
            }else{
                if(content != null && !"".equals(content)){
                    //对参照所有列进行模糊查询
                    for(String extcol : refParamVO.getExtcol()){
                        conditions.put(extcol, content);
                    }
                }
            }

            String idfield = StringUtils.isBlank(refParamVO.getIdfield()) ? "id"
                    : refParamVO.getIdfield();

            //根据树节点 查找树下的列表
            if(condition != null && !"".equals(condition) && refParamVO.getPidfield()!=null && !"".equals(refParamVO.getPidfield())){
                conditions.put(refParamVO.getPidfield(),condition);
            }

            Page<Map<String, Object>> headpage = this.service.getTreeRefData(
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
        if(StringUtils.isEmpty(sortColumn)){
        	
        }else{
        	if ("auto".equalsIgnoreCase(sortColumn)) {
                 sort = new Sort(Sort.Direction.ASC, "ts");
             } else {
                 sort = new Sort(Sort.Direction.DESC, sortColumn);
             }
        }
        return new PageRequest(pageNum, pageSize, sort);
    }

    /**
     * 过滤完的数据组装--表格
     *
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
    
    /**
     * 过滤完的数据组装--单选多选
     *
     */
    private List<Map<String, String>> buildRtnValsOfCheckboxRef(
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
                    	refDataMap.put(key.toLowerCase(), convertor.convertToJsonType(entity.get(key)).toString());
                    }
    			}
    			results.add(refDataMap);
    		}
    	}
    	return results;
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
     * 根据id反查所有参照信息,参照组件加载是需调用
     * @param list
     * @return
     */
    @RequestMapping(value = "/filterRef",method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String,String>> filterRef(@RequestBody List<Map<String,String>> list) {
        if(list.size() > 0){
            String refCode = "";
            List<String> idsList = new ArrayList<String>();
            List<Map<String, String>> results = new ArrayList<Map<String, String>>();

            for(Map<String,String> map:list){
                refCode = map.get("refCode");
                String ids = map.get("id");
                if(ids != null && !"".equals(ids)){
                    String[] idArray =ids.split(",");
                    for(String id:idArray){
                        idsList.add(id);
                    }
                }
            }

            RefParamVO refParamVO = RefXMLParse.getInstance().getFilterConfig(refCode);
            String idfield = StringUtils.isBlank(refParamVO.getIdfield()) ? "id"
                    : refParamVO.getIdfield();
            String tableName = refParamVO.getTablename();
            List<Map<String, Object>> obj = service.getFilterRef(tableName,idfield,refParamVO.getExtcol(),idsList);
            if (CollectionUtils.isNotEmpty(obj)) {
                results = buildRtnValsOfRef(obj);
            }
            return results;
        }
        return (new ArrayList<Map<String,String>>());
    }




	/************************************************************/
    @Autowired
	private RefCommonService service;

}