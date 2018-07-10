package com.yonyou.iuap.baseservice.ref.service;


import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public  class RefCommonService {

    @Autowired
    RefCommonMapper mapper;

    public List<Map<String, Object>> getFilterRef(String tablename, String idfield,
                                                  List<String> extColumns, List<String> ids) {

        List<Map<String, Object>> result = mapper.findRefListByIds(tablename,idfield,extColumns,ids);

        return result;
    }

    public Page<Map<String,Object>> getGridRefData(PageRequest pageRequest, String tablename, String idfield, String codefield, String namefield,
                                                   Map<String, String> condition, List<String> extColumns, String likefilter) {

        Page<Map<String,Object>> result = mapper.gridrefselectAllByPage(pageRequest,tablename,idfield,codefield,namefield, extColumns,condition,likefilter).getPage();

        return result;
    }

    public Page<Map<String, Object>> getTreeRefData(PageRequest pageRequest,
                                                    String tablename, String idfield, Map<String, String> condition,List<String> extColumns) {

        Page<Map<String,Object>> result = mapper.treerefselectAllByPage(pageRequest,tablename,idfield, extColumns,condition).getPage();
        return result;
    }

    public Page<Map<String, Object>> getTableRefData(PageRequest pageRequest,
                                                     String tablename, String idfield, String codefield,
                                                     String namefield, Map<String, String> condition,
                                                     List<String> extColumns, String likefilter) {

        Page<Map<String,Object>> result = mapper.tablerefselectAllByPage(pageRequest,tablename,idfield,codefield,namefield, extColumns,condition,likefilter).getPage();
        return result;
    }

    public Page<Map<String, Object>> selectRefTree(PageRequest pageRequest,
                                                   String tablename, String idfield, String pidfield,
                                                   String codefield, String namefield, Map<String, String> condition,List<String> extColumns) {

        Page<Map<String,Object>> result = mapper.selectRefTree(pageRequest,tablename,idfield,pidfield,codefield,namefield, extColumns,condition).getPage();
        return result;
    }

    public List fillListWithRef(List list){
        if (!list.isEmpty()) {
            /**
             * @Step 1 解析参照配置,一次加载参照数据全集
             * TODO 应优化为按需加载
             */
            Map<String, List<Map<String, Object>>> refContentMap = new HashMap<>();
            Map<String,Reference> refCache = new HashMap<>();
            Field[] fields = ReflectUtil.getFields(list.get(0).getClass());
            for (Field field : fields) {
                Reference ref = field.getAnnotation(Reference.class);
                if (null != ref) {
                    refCache.put(field.getName(),ref); //将所有参照和field的关系缓存起来后续使用
                    RefParamVO params = RefXMLParse.getInstance().getMSConfig(ref.code());
                    Map<String, String> conditions = new HashMap<String,String>();
//                    conditions.put("dr", "0");
                    String idfield = StringUtils.isBlank(params.getIdfield()) ? "id"
                            : params.getIdfield();
                    List<Map<String, Object>> refContents =
                            mapper.treerefselectAllByPage(
                                    null, params.getTablename(),
                                    idfield, params.getExtcol()
                                    , conditions).getContent();
                    refContentMap.put(field.getName(), refContents);//将所有参照数据集和field的关系缓存起来后续使用
                }
            }
            /**
             * @Step 2 逐条遍历业务结果集,将属性替换为参照值
             */
            if (!refContentMap.isEmpty()) {
                for (Object item : list) { //遍历结果集
                    for(String srcField: refCache.keySet() ){//遍历缓存的entity的全部参照字段
                        Reference refInCache = refCache.get(srcField);
                        if (  ReflectUtil.getFieldValue(item,srcField) == null ){
                            continue; // 参照字段为空,则跳过本字段数据解析
                        }
                        String refFieldValue = ReflectUtil.getFieldValue(item,srcField).toString();//取参照字段值
                        String[] mutiRefIds = refFieldValue.split(",");//参照字段值转数组
                        String[] mutiRefValues = new String[mutiRefIds.length];//定义结果载体
                        int loopSize =Math.min( refInCache.srcProperties().length ,refInCache.desProperties().length  );//参照配置多字段参照时需结构匹配
                        for (int i = 0; i < loopSize; i++) {//遍历参照中配置的多个srcPro和desPro 进行值替换
                            String srcCol = refInCache.srcProperties()[i]; //参照表value字段
                            String desField= refInCache.desProperties()[i];//entity对应参照value的字段
                            List<Map<String, Object>> refDatas =refContentMap.get(srcField);//取出参照缓存数据集
                            for (Map<String,Object> refData: refDatas){//遍历数据集
                                for (int j = 0; j <mutiRefIds.length ; j++) {//多值参照时,循环匹配拿到结果进行反写
                                    System.out.println("refData.get(\"ID\"): " + refData.get("ID"));
                                    System.out.println("mutiRefIds[j]： " + mutiRefIds[j]);
                                    System.out.println("-----------------------------------");
                                    if (refData.get("ID")!=null && refData.get("ID").toString().equals(mutiRefIds[j])){
                                        mutiRefValues[j] = String.valueOf( refData.get(srcCol.toUpperCase() )  ); //匹配到就存到结果数组里
                                    }
                                }
                            }
                            ReflectUtil.setFieldValue(item, desField, Arrays.toString(mutiRefValues)); //执行反写

                        }

                    }
                }
            }
        }

        return list;
    }

}
