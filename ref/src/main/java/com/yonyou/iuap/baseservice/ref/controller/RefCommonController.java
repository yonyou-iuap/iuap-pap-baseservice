package com.yonyou.iuap.baseservice.ref.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;
import com.yonyou.iuap.pap.base.i18n.MessageSourceUtil;
import com.yonyou.iuap.pap.base.ref.entity.RefParamConfig;
import com.yonyou.iuap.pap.base.ref.entity.RefParamVO;
import com.yonyou.iuap.pap.base.ref.entity.RefVertion;
import com.yonyou.iuap.pap.base.ref.entity.RefViewModelVO;
import com.yonyou.iuap.pap.base.ref.utils.RefUitls;
import com.yonyou.iuap.pap.base.ref.utils.RefXMLParse;
import com.yonyou.iuap.pap.base.ref.utils.RefXmlConstract;
import com.yonyou.iuap.pap.base.utils.resp.JsonResponse ;
import com.yonyou.uap.ieop.security.datapermission.DataPermissionCenter;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 说明：参照基础controller,所有参照都通过平台回调到这个地址取数据
 * @WARN 需要平台的REF_REFINFO表中有相应的配置数据,例如 23    common_ref	通用树表参照	common_ref		/iuap_pap_quickstart/common/				AAAzpkAAGAAAev+AAA
 * @author leon
 * 2018年7月11日
 * @update 2018-7-25 移除了对平台uitemplate_common的依赖
 * @deprecated PAP3.5.5以后推荐使用pap_base_ref里的com.yonyou.iuap.pap.base.ref.controller.RefBaseCommonController
 */
@Controller
@RequestMapping(value = "/common")
public final class RefCommonController  {

    private Logger logger = LoggerFactory.getLogger(RefCommonController.class);

    @Autowired
    private RefCommonService service;



    @RequestMapping(value = {"/getRefModelInfo"}, method = {RequestMethod.POST})
    @ResponseBody
    public RefViewModelVO getRefModelInfo(@RequestBody RefViewModelVO refViewModel) {
        RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refViewModel.getRefCode());
        if (refParamVO.getRefi18n()==null){
            refViewModel.setRefName(refParamVO.getRefname());
            refViewModel.setRootName(refParamVO.getRefname());
        }else{
            refViewModel.setRefName(MessageSourceUtil.getMessage(refParamVO.getRefi18n(),refParamVO.getRefname()) );
            refViewModel.setRootName(MessageSourceUtil.getMessage(refParamVO.getRefi18n(),refParamVO.getRefname()) );
        }

        refViewModel.setRefVertion(RefVertion.NewRef);
        refViewModel.setRefUIType(RefUitls.getRefUIType(refParamVO.getReftype()));

        Map<String,String> showColMap = refParamVO.getThead();
        String[] showCode = null;
        String[] showName = null;
        String[] showNameI18n = null;
        if (showColMap != null) {
            showCode = new String[showColMap.size()];
            showName = new String[showColMap.size()];
            showNameI18n = new String[showColMap.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : showColMap.entrySet()) {
                showCode[i] = entry.getKey();
                showName[i] = entry.getValue();
                if (refParamVO.getTheadI18n()!=null  ){
                    showNameI18n[i]=
                            refParamVO.getTheadI18n().get(entry.getKey())==null
                                    ?showName[i]
                                    :MessageSourceUtil.getMessage(refParamVO.getTheadI18n().get(entry.getKey()),showName[i]);
                }
                i++;
            }
        }
        refViewModel.setStrFieldCode(showCode);
        //国际化
        if (refParamVO.getTheadI18n()==null){
            /*显示列编码和名称*/
            refViewModel.setStrFieldName(showName);
        }else{
            refViewModel.setStrFieldName(showNameI18n);
        }

        refViewModel.setDefaultFieldCount(showColMap.size());
        return refViewModel;
    }

    /**
     * 通过pk查询所有数据,String pk数组入参
     * @param vo RefViewModelVO参照入参
     * @return id-to-name 的结果集
     */
    @RequestMapping(value = {"/matchPKRefJSON"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Map<String, Object>> matchPKRefJSON(RefViewModelVO vo) {
        if (vo.getRefCode()==null){
            logger.info("matchPKRefJSON 接口入参的refcode为空,返回空结果");
            return  new ArrayList<>();
        }
        if (vo.getId()==null){
            logger.info("matchPKRefJSON 接口入参的id为空,返回空结果");
            return  new ArrayList<>();
        }
        Map<String,String> params = new HashMap<>();
        params.put("refCode",vo.getRefCode());
        params.put("id",vo.getId());
        List<Map<String,String>> list= new ArrayList<>()  ;
        list.add(params);
        return  filterRef(list);
    }

    /**
     * 通过pk查询所有数据,String pk数组入参
     * @return
     */
    @RequestMapping(value = {"/getCommonRefData"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> commonRefsearch(@RequestBody RefViewModelVO refModel) {

        /*构建表体，其中list中为要查询的字段，必须和表头设置的相同，并且必须为表中的字段值*/
        RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refModel.getRefCode());
        RefParamConfig refParamConfigTable=refParamVO.getRefParamConfigTable();
        Map<String, Object> mapList = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            /*获取当前页*/
            int pageNum = refModel.getRefClientPageInfo().getCurrPageIndex();
            pageNum=pageNum<0?0:pageNum;

            /*每页显示的数量*/
            int pageSize = refModel.getRefClientPageInfo().getPageSize();
            pageSize=pageSize<1?10:pageSize;

            /*拼装分页请求对象*/
            PageRequest request = buildPageRequest(pageNum, pageSize,refParamVO.getRefParamConfigTable());

            String dataPermission=refParamConfigTable.getDataPermission();
            dataPermission=StringUtils.isEmpty(dataPermission)?refModel.getRefCode():dataPermission;
            Set<String> ids = DataPermissionCenter.getInstance().getDataPermissionIds(dataPermission, refModel.getClientParam());
            if ((DataPermissionCenter.getInstance().isUserDataPower(refModel.getClientParam())) && ((ids == null) || (ids.size() < 1))) {
                mapList.put("dataList", results);
                mapList.put("refViewModel", refModel);
                return mapList;
            }
            Page<Map<String, Object>> headpage = this.service.getCheckboxData(request,refParamVO.getReftype(), refParamConfigTable, refModel.getContent(), ids);


            /*总页数*/
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
    @RequestMapping(value = {"/filterRefJSON"}, method = {RequestMethod.POST})
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
    @RequestMapping(value = {"/matchBlurRefJSON"}, method = {RequestMethod.POST})
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
    @RequestMapping(value = {"/blobRefClassSearch"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<Map<String, String>> blobRefClassSearch(RefViewModelVO arg0) {
        //
        return null;
    }


    @RequestMapping(value = {"/blobRefTree"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> blobRefTree(@RequestBody RefViewModelVO refModel) {

        /*构建表体，其中list中为要查询的字段，必须和表头设置的相同，并且必须为表中的字段值*/
        RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refModel.getRefCode());
        RefParamConfig refParamConfigTableTree=refParamVO.getRefParamConfigTableTree();

        Map<String, Object> mapList = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            /*获取当前页*/
            int pageNum = refModel.getRefClientPageInfo().getCurrPageIndex();
            pageNum=pageNum<0?0:pageNum;

            /*每页显示的数量*/
            int pageSize = refModel.getRefClientPageInfo().getPageSize();
            pageSize=pageSize<1?10:pageSize;

            /*拼装分页请求对象*/
            PageRequest request = buildPageRequest(pageNum, pageSize,refParamVO.getRefParamConfigTable());
            /*获取查询条件 --如果content*/
            String dataPermission=refParamConfigTableTree.getDataPermission();
            dataPermission=StringUtils.isEmpty(dataPermission)?refModel.getRefCode():dataPermission;
            Set<String> ids = DataPermissionCenter.getInstance().getDataPermissionIds(dataPermission, refModel.getClientParam());
            if ((DataPermissionCenter.getInstance().isUserDataPower(refModel.getClientParam())) && ((ids == null) || (ids.size() < 1))) {
                List<Map<String, Object>> headVOs =new ArrayList<>();
                mapList.put("dataList", headVOs);
                mapList.put("refViewModel", refModel);
                return mapList;
            }

            Page<Map<String, Object>> headpage = this.service.selectRefTree(request,refParamVO.getReftype(), refParamConfigTableTree, refModel.getContent(), ids);


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
    @RequestMapping(value = {"/blobRefSearch"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> blobRefSearch(@RequestBody RefViewModelVO refModel) {
//        try {
////        	String content = URLDecoder.decode(refModel.getContent(), "UTF-8");
//        	String content = new String(refModel.getContent().getBytes("iso8859-1"),"utf-8");
//        	refModel.setContent(content);
//		} catch (Exception e) {
//		}
    	RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refModel.getRefCode());
        RefParamConfig refParamConfigTable=refParamVO.getRefParamConfigTable();

        Map<String, Object> mapList = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            /*获取当前页*/
            int pageNum = refModel.getRefClientPageInfo().getCurrPageIndex();
            pageNum=pageNum<0?0:pageNum;

            /*每页显示的数量*/
            int pageSize = refModel.getRefClientPageInfo().getPageSize();
            pageSize=pageSize<1?10:pageSize;

            /*拼装分页请求对象*/
            PageRequest request = buildPageRequest(pageNum, pageSize,refParamVO.getRefParamConfigTable());
            String dataPermission=refParamConfigTable.getDataPermission();
            dataPermission=StringUtils.isEmpty(dataPermission)?refModel.getRefCode():dataPermission;
            Set<String> ids = DataPermissionCenter.getInstance().getDataPermissionIds(dataPermission, refModel.getClientParam());
            if ((DataPermissionCenter.getInstance().isUserDataPower(refModel.getClientParam())) && ((ids == null) || (ids.size() < 1))) {
                mapList.put("dataList", results);
                mapList.put("refViewModel", refModel);
                return mapList;
            }
            /*树节点id*/
            String condition=refModel.getCondition();
            condition=StringUtils.isEmpty(condition)||condition.equals("null")?null:condition;
            Page<Map<String, Object>> headpage = this.service.getTreeRefData(request,refParamVO.getReftype(), refParamConfigTable, refModel.getContent(),condition, ids);

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

    private PageRequest buildPageRequest(int pageNum, int pageSize, RefParamConfig refParamConfig) {
        String sortColumn=refParamConfig.getSort();
        String order=refParamConfig.getOrder();
        Sort sort = null;
        if(StringUtils.isEmpty(sortColumn)){
            return new PageRequest(pageNum, pageSize, sort);
        }else{
            if(RefXmlConstract.REF_FIELD_CODE_ORDER_DESC.equals(order)){
                sort = new Sort(Sort.Direction.DESC, sortColumn.split(","));
            }else{
                sort = new Sort(Sort.Direction.ASC, sortColumn.split(","));
            }
        }
        return new PageRequest(pageNum, pageSize, sort);
    }

    /**
     * 过滤完的数据组装--表格
     *
     */
    private List<Map<String, Object>> buildRtnValsOfRef(List<Map<String, Object>> headVOs) {
        return toLowerKey(headVOs);
    }

    /**
     * 过滤完的数据组装--单选多选
     *
     */
    private List<Map<String, Object>> buildRtnValsOfCheckboxRef(
            List<Map<String, Object>> headVOs) {
        return toLowerKey(headVOs);
    }
    /**
     * 过滤完的数据组装--树
     *
     * @return
     */
    private List<Map<String, Object>> buildRtnValsOfRefTree(
            List<Map<String, Object>> headVOs) {

        return toLowerKey(headVOs);
    }

    /**
     * 根据id反查所有参照信息,参照组件加载是需调用
     * @param list
     * @return
     */
    @RequestMapping(value = "/filterRef",method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String,Object>> filterRef(@RequestBody List<Map<String,String>> list) {
        if(list.size() > 0){
            String refCode = "";
            List<String> idsList = new ArrayList<String>();
            List<Map<String, Object>> results = new ArrayList<>();

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
            RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refCode);
//            RefParamConfig refParamConfig=refParamVO.getRefParamConfigTable();
            RefParamConfig refParamConfig=refParamVO.getRefParamConfigTable()==null?refParamVO.getRefParamConfigTableTree():refParamVO.getRefParamConfigTable();
            List<Map<String, Object>> obj = service.getFilterRef(refParamConfig.getTableName(),refParamConfig.getId(),refParamConfig.getExtension(),idsList);
            if (CollectionUtils.isNotEmpty(obj)) {
                results = buildRtnValsOfRef(obj);
            }
            results = testSortList(idsList,results);
            return results;
        }
        return (new ArrayList<>());
    }

    private List<Map<String,Object>> testSortList(List<String> idsList ,List<Map<String,Object>> obj){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(String idvalue : idsList){
			for(Map<String,Object> map : obj){
				if(idvalue.equals(map.get("id").toString())){
					list.add(map);
				}
			}
		}
		return list;
	}
    
    @RequestMapping(
            value = {"/getByIds"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public JsonResponse getByIds(HttpServletRequest request) {
        JsonResponse results = new JsonResponse();
        String data = request.getParameter("data");
        String refCode = request.getParameter("resourcetypecode");
        if (StringUtils.isBlank(data)) {
            results.failed("请求参数Data不能为空！");
            return results;
        } else {
            JSONArray                 array    = JSON.parseArray(data);
            List<Map<String, Object>> dataList = new ArrayList();
            if (!CollectionUtils.isEmpty(array)) {
                String[] strArray = array.toArray(new String[array.size()]);
                RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refCode);
//                RefParamConfig refParamConfigTable=refParamVO.getRefParamConfigTable();
                RefParamConfig refParamConfig=refParamVO.getRefParamConfigTable()==null?refParamVO.getRefParamConfigTableTree():refParamVO.getRefParamConfigTable();
                dataList.addAll(this.service.getByIds(refParamConfig.getTableName(), refParamConfig.getId(), refParamConfig.getRefcode(),
                        refParamConfig.getRefname(), refParamConfig.getExtension(), Arrays.asList(strArray)));
            }

            List<Map<String, Object>> dataListResult = toLowerKey(dataList);


            results.success("操作成功！", "data", dataListResult);
            return results;
        }
    }

    @RequestMapping(
            value = {"/search"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public JsonResponse search(HttpServletRequest request) {
        JsonResponse results = new JsonResponse();
        String refCode = request.getParameter("resourcetypecode");
        String data = request.getParameter("data");
        if (StringUtils.isBlank(data)) {
            results.failed("请求参数Data不能为空！");
            return results;
        } else {
            JSONArray array = JSON.parseArray(data);
            List<String> idList = Arrays.asList(array.toArray(new String[array.size()]));
            RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refCode);
            RefParamConfig refParamConfigTable=refParamVO.getRefParamConfigTable();

            String keyword = request.getParameter("keyword");
            List<Map<String, Object>> dataList = this.service.likeSearch(refParamConfigTable.getTableName(), refParamConfigTable.getId(),
                    refParamConfigTable.getRefcode(), refParamConfigTable.getRefname(),
                    refParamConfigTable.getExtension(), idList, "%" + keyword + "%");
            List<Map<String, Object>> dataListResult = toLowerKey(dataList);


            results.success("操作成功！", "data", dataListResult);
            return results;
        }
    }
    private  List<Map<String, Object>> toLowerKey(List<Map<String, Object>> dataList){
        List<Map<String, Object>> dataListResult = new ArrayList();
        if(dataList!=null&&dataList.size()>0){
            for(Map<String, Object> map:dataList){
                Map<String, Object> mapResult = new HashMap();
                for(Map.Entry<String,Object> entry:map.entrySet()){

                    if("id".equalsIgnoreCase(entry.getKey())){
                        mapResult.put("refpk",entry.getValue());
                        mapResult.put(entry.getKey().toLowerCase(),entry.getValue());
                    }else if("pid".equalsIgnoreCase(entry.getKey())){
                        mapResult.put(entry.getKey().toLowerCase(),entry.getValue()==null?"":entry.getValue());
                    }else{
                        mapResult.put(entry.getKey().toLowerCase(),entry.getValue());
                    }

                }
                dataListResult.add(mapResult);

            }
        }
        return dataListResult;
    }


}