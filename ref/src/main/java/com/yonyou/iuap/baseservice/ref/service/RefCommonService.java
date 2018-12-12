package com.yonyou.iuap.baseservice.ref.service;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.RefParamConfig;
import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.pap.base.ref.service.RefBaseCommonService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;


/**
 * 通用本地参照服务，基于ref.XML解析,生成动态sql检索,此配置文件应部署在业务项目中,例如iuap-pap-quickStart/src/main/resources/ref.xml
 * <br>
 * 作为可插拔ServiceFeature#REFERENCE特性的默认实现，仅支持数据库集中存储式的参照id解析，远程参照id解析请参考RefRemoteService
 * @author leon
 * @date 2018-07-11
 * @deprecated  推荐PAP3.5.5以后使用pap_base_ref里的com.yonyou.iuap.pap.base.ref.service.RefBaseCommonService
 *
 */
@SuppressWarnings("ALL")
@Service
public  class RefCommonService<T extends Model>  implements QueryFeatureExtension<T>{
    private static Logger logger= LoggerFactory.getLogger(RefCommonService.class);


    @Autowired
    RefCommonMapper mapper;
    @Autowired
    RefBaseCommonService refBaseCommonService;


    public List<Map<String, Object>> getFilterRef(String tablename, String idfield,
                                                  List<String> extColumns, List<String> ids) {

        List<Map<String, Object>> result = mapper.findRefListByIds(tablename,idfield,extColumns,ids);

        return result;
    }

    public Page<Map<String, Object>> getTreeRefData(PageRequest pageRequest,String refType, RefParamConfig refParamConfigTable, String content,String fid, Set<String> ids) {
        StringBuilder keyword=new StringBuilder();

        Map<String, String> conditions =new HashMap<>();

        Map<String, String> conditionQuoter = new HashMap<>();

        setCondition(refParamConfigTable,keyword,conditions,conditionQuoter,content);

        List<String> idList=null;
        if(ids!=null&&ids.size()>0){
            idList=new ArrayList<>(ids);
        }
        Page<Map<String,Object>> result = mapper.treerefselectAllByPage(pageRequest,refParamConfigTable.getTableName(),refParamConfigTable.getId(),
                refParamConfigTable.getRefcode(),refParamConfigTable.getRefname(), refParamConfigTable.getExtension(),keyword.toString(),conditions,conditionQuoter,refParamConfigTable.getCondition(),refParamConfigTable.getFid(),fid,
                idList).getPage();

        return result;

    }

    public Page<Map<String, Object>> getCheckboxData(PageRequest pageRequest,String refType, RefParamConfig refParamConfigTable, String content, Set<String> ids) {
        StringBuilder keyword=new StringBuilder();

        Map<String, String> conditions =new HashMap<>();

        Map<String, String> conditionQuoter = new HashMap<>();

        setCondition(refParamConfigTable,keyword,conditions,conditionQuoter,content);

        List<String> idList=null;
        if(ids!=null&&ids.size()>0){
            idList=new ArrayList<>(ids);
        }

    	Page<Map<String,Object>> result = mapper.selectRefCheck(pageRequest,refParamConfigTable.getTableName(),refParamConfigTable.getId(),
                refParamConfigTable.getRefcode(),refParamConfigTable.getRefname(), refParamConfigTable.getExtension(),keyword.toString(),conditions,conditionQuoter,refParamConfigTable.getCondition(),
                idList).getPage();

    	return result;
    }

    public Page<Map<String, Object>> selectRefTree(PageRequest pageRequest,String refType, RefParamConfig refParamConfigTableTree, String content, Set<String> ids) {
        StringBuilder keyword=new StringBuilder();

        Map<String, String> conditions =new HashMap<>();

        Map<String, String> conditionQuoter = new HashMap<>();


        setCondition(refParamConfigTableTree,keyword,conditions,conditionQuoter,content);

        List<String> idList=null;
        if(ids!=null&&ids.size()>0){
            idList=new ArrayList<>(ids);
        }
        Page<Map<String,Object>> result = mapper.selectRefTree(pageRequest,refParamConfigTableTree.getTableName(),refParamConfigTableTree.getId(),
                refParamConfigTableTree.getRefcode(),refParamConfigTableTree.getRefname(),refParamConfigTableTree.getPid(), refParamConfigTableTree.getExtension(),keyword.toString(),conditions,conditionQuoter,refParamConfigTableTree.getCondition(),
                idList).getPage();
        return result;
    }
    private void setCondition( RefParamConfig refParamConfigTable,StringBuilder keyword,Map<String, String> conditions,Map<String, String> conditionQuoter,String content){

        try {
            Map<String,String> contentJson = JSON.parseObject(content,Map.class)==null? new HashMap<String, String>(): JSON.parseObject(content,Map.class);
            for (String key :contentJson.keySet()){
                if ( StringUtils.isNotEmpty( contentJson.get(key)  )){
                    conditions.put(key, contentJson.get(key) );
                }
            }

            Map<String,String> filters=refParamConfigTable.getFilters();
            for(String key : conditions.keySet()){
                String filter=filters.get(key);
                if(StringUtils.isEmpty(filter)){
                	//如果filter是空，默认like查询
                	conditions.put(key,"%"+conditions.get(key)+"%");
                	conditionQuoter.put(key,"like");
                }else{
                	if("like".equalsIgnoreCase(filter.trim())){
                        conditions.put(key,"%"+conditions.get(key)+"%");
                    }
                    conditionQuoter.put(key,filter);
                }
                
            }
        }catch (Exception e){
        	if(StringUtils.isNotEmpty(content)){
        		keyword.append("%").append(content).append("%");
                String filterStr=refParamConfigTable.getFilters().get("likeSearchField");
                if(StringUtils.isEmpty(filterStr)){
                    conditionQuoter.put(refParamConfigTable.getRefcode(),"like");
                    conditionQuoter.put(refParamConfigTable.getRefname(),"like");
                }else {
                    String[] filterAry=filterStr.split(",");
                    for(String filter:filterAry){
                        conditionQuoter.put(filter,"like");
                    }
                }
            }
        }
    }

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams,Class modelClass) {
        return searchParams;
    }

    /**
     * 通过特性集成，实现了在查询时与其他模块的任意组合
     * @param list 未装填参照的原始list
     * @return 重新装填后的结果
     */
    @Override
    public List<T> afterListQuery(List<T> list) {
        return refBaseCommonService.afterListQuery(list);
    }

    public List<Map<String, Object>> getByIds(String tablename, String idfield,String codefield, String namefield,
                                              List<String> extColumns, List<String> ids) {

        List<Map<String, Object>> result = mapper.getByIds(tablename,idfield, codefield,  namefield,extColumns,ids);

        return result;
    }
    public List<Map<String, Object>> likeSearch(String tablename, String idfield,String codefield, String namefield,
                                                List<String> extColumns, List<String> ids,String keyword) {
        return mapper.likeSearch(tablename,idfield, codefield,  namefield,extColumns,ids,keyword);
    }
}
