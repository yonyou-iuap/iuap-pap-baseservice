package com.yonyou.iuap.baseservice.ref.service;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * 通用参照服务,基于ref.XML解析,生成动态sql检索,此配置文件应部署在业务项目中,例如iuap-pap-quickStart/src/main/resources/ref.xml
 * @author leon
 * @Date 2018-07-11
 */
@Service
public  class RefCommonService<T extends Model>  implements QueryFeatureExtension<T>{
    private static Logger log= LoggerFactory.getLogger(RefCommonService.class);


    @Autowired
    RefCommonMapper mapper;

    public List<Map<String, Object>> getFilterRef(String tablename, String idfield,
                                                  List<String> extColumns, List<String> ids) {

        List<Map<String, Object>> result = mapper.findRefListByIds(tablename,idfield,extColumns,ids);

        return result;
    }

    public Page<Map<String, Object>> getTreeRefData(PageRequest pageRequest,
                                                    String tablename, String idfield, Map<String, String> condition,List<String> extColumns) {

        Page<Map<String,Object>> result = mapper.treerefselectAllByPage(pageRequest,tablename,idfield, extColumns,condition).getPage();
        return result;
    }
    
    public Page<Map<String, Object>> getCheckboxData(PageRequest pageRequest,
            String tablename, String idfield,
            String codefield, String namefield, Map<String, String> condition,List<String> extColumns) {
    	
    	Page<Map<String,Object>> result = mapper.selectRefCheck(pageRequest,tablename,idfield,codefield,namefield, extColumns,condition).getPage();
    	return result;
    }

    public Page<Map<String, Object>> selectRefTree(PageRequest pageRequest,
                                                   String tablename, String idfield, String pidfield,
                                                   String codefield, String namefield, Map<String, String> condition,List<String> extColumns) {

        Page<Map<String,Object>> result = mapper.selectRefTree(pageRequest,tablename,idfield,pidfield,codefield,namefield, extColumns,condition).getPage();
        return result;
    }

    /**
     * 参照数据加载,根据 @See  com.yonyou.iuap.baseservice.entity.annotation.Reference
     * 中定义的参照参数,将参照数据中检索出来的值写到对应的entity属性中,以便前端展示
     *
     * TODO 将此服务应用到更多的基类selecByAllPage方法中,可考虑通过切面来完成
     * @param list 未装填参照的原始list
     * @return 重新装填后的结果
     */
    public List fillListWithRef(List list){
        if (list!=null&&!list.isEmpty()) {
            Map<Field,Set<String>>idCache = new HashMap<>(); //缓存list中的所有entity属性参照内的id
            Map<Field, Reference> refCache = new HashMap<>();//缓存entity中的所有@Reference定义
            Map<Field, List<Map<String, Object>>> refDataCache = new HashMap<>();//缓存参照数据,用于最后的反写
            /**
             * @Step 1
             * 解析参照配置,获取参照字段id集合,用于后续参照查询
             */
            boolean isFirst = true;
            for (Object entity : list) {
                Field[] fields = ReflectUtil.getFields(entity.getClass());
                for (Field field : fields) {
                    Reference ref = field.getAnnotation(Reference.class);
                    if (null != ref) {
                        if (isFirst) { //  提高缓存装载效率,仅加载一次
                            refCache.put(field, ref); //将所有参照和field的关系缓存起来后续使用
                            idCache.put(field,new HashSet<String>());
                        }
                        Object refIds = ReflectUtil.getFieldValue(entity, field);
                        if (null!=refIds){
                            String [] fieldIds = refIds.toString().split(",");//兼容参照多选
                            idCache.get(field).addAll(Arrays.asList(fieldIds));
                         }
                    }
                }
                isFirst = false;
            }
            /**
             * @Step 2解析参照配置,一次按需(idCache)加载参照数据
             */

            for (Field field : refCache.keySet()) {
                RefParamVO params = RefXMLParse.getInstance().getMSConfig(refCache.get(field).code());
                if (params==null){
                    log.warn("参照配置错误:"+refCache.get(field).code()+"不存在");
                    continue;
                }
                String idfield = StringUtils.isBlank(params.getIdfield()) ? "id"
                        : params.getIdfield();
                List<String> setList = new ArrayList<>(idCache.get(field));
                if (setList==null || setList.size()==0){
                    continue;
                }
                List<Map<String, Object>> refContents =
                        mapper.findRefListByIds(params.getTablename(),
                                idfield, params.getExtcol(), setList);
//                CaseInsensitiveMap map =new CaseInsensitiveMap( list.get(0));
                if ( null!= refContents && refContents.size()>0)
                    refDataCache.put(field, refContents);//将所有参照数据集和field的关系缓存起来后续使用
            }
            /**
             * @Step 3 逐条遍历业务结果集,向entity参照指定属性写入参照值
             */
            if (!refDataCache.isEmpty()) {
                for (Object entity : list) { //遍历结果集
                    for(Field refField: refCache.keySet() ){//遍历缓存的entity的全部参照字段
                        if (refDataCache.get(refField)== null){
                            continue;//没有参照数据缓存,就不用后面的反写了,直接下一个参照字段
                        }
                        Reference refAnnotation = refCache.get(refField);
                        if (  ReflectUtil.getFieldValue(entity,refField) == null ){
                            continue; // 参照field id值为空,则跳过本field数据解析
                        }
                        String refFieldValue = ReflectUtil.getFieldValue(entity,refField).toString();//取参照字段值
                        String[] mutiRefIds = refFieldValue.split(",");     //参照字段值转数组
                        String[] mutiRefValues = new String[mutiRefIds.length];  //定义结果载体
                        int loopSize =Math.min( refAnnotation.srcProperties().length ,refAnnotation.desProperties().length  );//参照配置多字段参照时需结构匹配
                        for (int i = 0; i < loopSize; i++) {                //遍历参照中配置的多个srcPro和desPro 进行值替换
                            String srcCol = refAnnotation.srcProperties()[i];  //参照表value字段
                            String desField= refAnnotation.desProperties()[i]; //entity对应参照value的字段
                            List<Map<String, Object>> refDatas =refDataCache.get(refField);//取出参照缓存数据集
                            for (Map<String,Object> refData: refDatas){
                                for (int j = 0; j <mutiRefIds.length ; j++) {//多值参照时,循环匹配拿到结果进行反写
                                    if (refData.get("ID")!=null && refData.get("ID").toString().equals(mutiRefIds[j])){ //数据库适配时 mysql也要将此字段as ID
                                        for(String columnKey:refData.keySet()){//解决大小写适配问题
                                            if (columnKey.equalsIgnoreCase(srcCol))
                                                mutiRefValues[j] = String.valueOf( refData.get(columnKey) );
                                        }
                                    }
                                }
                            }
                            String fieldValue =ArrayUtil.join(mutiRefValues,",");
                            ReflectUtil.setFieldValue(entity, desField,fieldValue); //执行反写
                        }

                    }
                }
            }
        }

        return list;
    }

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams) {
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        return this.fillListWithRef(list);
    }
}
