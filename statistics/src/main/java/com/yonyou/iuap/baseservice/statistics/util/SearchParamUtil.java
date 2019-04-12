package com.yonyou.iuap.baseservice.statistics.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.MultiTenant;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import com.yonyou.iuap.baseservice.statistics.support.ParamProcessResult;
import com.yonyou.iuap.baseservice.statistics.support.StatFunctions;
import com.yonyou.iuap.baseservice.statistics.support.StatModel;
import com.yonyou.iuap.baseservice.statistics.support.StatModelResolver;
import com.yonyou.iuap.baseservice.support.condition.Match;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.pap.base.ref.utils.RefIdToNameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

import static com.yonyou.iuap.baseservice.statistics.support.StatParam.*;

@SuppressWarnings("ALL")
public class SearchParamUtil {
    private static Logger logger = LoggerFactory.getLogger(SearchParamUtil.class);


    public static boolean hasRefrence(Class clz) {
        for (Field field : ReflectUtil.getFields(clz)) {
            if (field.getAnnotation(Reference.class) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据模型编号（uri中取得），进行统计模型解析，并得到查询语句所需的关键参数
     *
     * @param searchParams
     * @param modelCode
     * @return
     */
    public static ParamProcessResult processServiceParams(SearchParams searchParams, String modelCode) {
        ParamProcessResult result = new ParamProcessResult();

        StatModel m = StatModelResolver.getStatModel(modelCode);
        if (m == null) {
            throw new RuntimeException("calling model [" + modelCode + "] can not be resolved by Statistic Service!!");
        }
        result.setStateModel(m);
        result.setTableName(m.getTableName());
        Set<String> statStatements = new HashSet<>();
        if (searchParams.getSearchMap().get(distinctParams.name()) != null) {
            /**
             * @Step 1.1
             * distinct 查询与其他StatFunctions类型 具有互斥的关系，
             * 所以，遇到distinct的入参，需要组织独立的查询脚本
             */
            List<String> distincts = (List<String>) searchParams.getSearchMap().get(distinctParams.name());
            for (String dis : distincts) {
                Field keyField = ReflectUtil.getField(m.getmClass(), dis);
                if (keyField == null) {
                    throw new RuntimeException("cannot find field " + distincts + " in  model [" + modelCode + "] ");
                }
                ;
                statStatements.add(FieldUtil.getColumnName(keyField) + " as \"" + dis+"\"");
            }
            result.setGroupFields(distincts);
        } else {

            /**
             * @Step 1.2
             * 根据业务实体上的注解，解析StatFunctions，构造 select部分的查询脚本
             */
            Map<String, StatFunctions[]> statColFuncs = m.getStatColumnsFunctions();
            for (String col : statColFuncs.keySet()) {
                for (StatFunctions func : statColFuncs.get(col)) {
                    statStatements.add(func + "(" + col + ") as \"" + m.getStatColumnsFields().get(col) + StringUtils.capitalize(func.name()) +"\"");
                }
            }
            /**
             * @Step 1.3
             * 解析groupParam，构造 select部分和group by部分的查询脚本
             * <p>mssql下sort和groupby有冲突，需要保持sort和group by一致，才能实现分页
             */
            if (searchParams.getSearchMap().get(groupParams.name()) != null) {
                List<String> groups = (List<String>) searchParams.getSearchMap().get(groupParams.name());
                Set<String> groupStatements = new HashSet<>();
                Set<String> groupCols = new HashSet<>();
                List<Sort.Order> orders = new ArrayList<>(); //构造sort
                for (String group : groups) {
                    Field keyField = ReflectUtil.getField(m.getmClass(), group);
                    if (keyField == null) {
                        throw new RuntimeException("cannot find field " + group + " in  model [" + modelCode + "] ");
                    }
                    groupStatements.add(FieldUtil.getColumnName(keyField) + " as \"" + group +"\"");
                    groupCols.add(FieldUtil.getColumnName(keyField));
                    orders.add(new Sort.Order(FieldUtil.getColumnName(keyField)));
                }
                searchParams.getSearchMap().put(groupParams.name(), groupCols);
                searchParams.getSearchMap().put(sortMap.name(),null);
                result.setGroupStatements(groupStatements);
                result.setGroupFields(groups);
                if (orders.size()>0){
                    result.setSort(new Sort(orders));
                }
            }
        }
        result.setStatStatements(statStatements);

        /**
         * @Step 2
         * 解析排序条件sortMap
         */

        if (searchParams.getSearchMap().get(sortMap.name()) != null) {
            List<Map<String, String>> sorts = (List<Map<String, String>>) searchParams.getSearchMap().get(sortMap.name());
            if (sorts.size()>0){
                List<Sort.Order> orders = new ArrayList<>();
                for (Map sort : sorts) {
                    if (sort.keySet().size() > 0 && sort.keySet().toArray()[0] != null) {
                        Field keyField = ReflectUtil.getField(m.getmClass(), sort.keySet().toArray()[0].toString());
                        if (keyField==null){
                            throw new RuntimeException("cannot find field "+sort.keySet().toArray()[0].toString()+" in  model [" + modelCode + "] ");
                        }
                        Sort.Order order =
                                new Sort.Order(
                                        Sort.Direction.valueOf(sort.get( sort.keySet().toArray()[0]).toString().toUpperCase()),
                                        FieldUtil.getColumnName(keyField));
                        orders.add(order);
                    }
                }
                result.setSort(new Sort(orders));
            }else {
                logger.debug("receiving none sort param in sortMap of querying =>"+modelCode);
            }
        }

        /**
         * @Step 3
         * 解析模型特性,组装where 条件脚本
         */

        List<Map<String, Object>> whereList = new ArrayList<>();

        //加入特性集成-逻辑删除
        if (LogicDel.class.isAssignableFrom(m.getmClass())) {
            Map<String, Object> whereStatement = new HashMap<>();
            whereStatement.put(key.name(), "dr");
            whereStatement.put(value.name(), "0");
            whereStatement.put(condition.name(), Match.EQ.name());
            whereList.add(whereStatement);
        }
        //加入特性集成 -多租户
        if (MultiTenant.class.isAssignableFrom(m.getmClass())) {
            Map<String, Object> whereStatement = new HashMap<>();
            whereStatement.put(key.name(), "TENANT_ID");
            whereStatement.put(value.name(), InvocationInfoProxy.getTenantid());
            whereStatement.put(condition.name(), Match.EQ.name());
            whereList.add(whereStatement);
        }
        //where 条件的合法性校验,非法条件直接跳过
        if (searchParams.getSearchMap().get(whereParams.name()) != null) {
            List<Map<String, Object>> wheres = (List<Map<String, Object>>) searchParams.getSearchMap().get(whereParams.name());
            for (Map<String, Object> statment : wheres) {
                Object keyStr = statment.get(key.name());
                Object valueObj = statment.get(value.name());
                Object conditionStr = statment.get(condition.name());
                if (StringUtils.isEmpty(keyStr) || StringUtils.isEmpty(valueObj)) { //关键参数缺一不可
                    logger.warn("reading incomplete whereParams [" + keyStr + ":" + valueObj + "]");
                    throw new RuntimeException("recieving incomplete whereParams [" + keyStr + ":" + valueObj + "]");
                }
                Field keyField = ReflectUtil.getField(m.getmClass(), keyStr.toString());
                if (keyField == null) {
                    logger.warn("finding none field [" + keyStr + "] in model class[" + m.getmClass() + "]!!");
                    throw new RuntimeException("thre is no field [" + keyStr + "] in model class[" + m.getmClass() + "]!!");
                }
                if (StringUtils.isEmpty(conditionStr)) {
                    conditionStr = Match.EQ.name();
                }
                Map<String, Object> whereStatement = new HashMap<>();
                whereStatement.put(key.name(), FieldUtil.getColumnName(keyField));

                try {
                    Match match = Match.valueOf(conditionStr.toString());
                    if (match.equals(Match.RANGE)) {
                        if (valueObj instanceof List && ((List) valueObj).size() == 2) {
                            logger.debug("reading where conditon [RANGE] of [" + keyStr + ":" + valueObj + "]");
                        } else {
                            // 范围条件RANGE 需要valueObj必须是一个双值的list
                            logger.warn("reading where conditon [RANGE] of wrong param [" + keyStr + ":" + valueObj + "]");
                            throw new RuntimeException("recieving where conditon [RANGE] of wrong param [" + keyStr + ":" + valueObj + "]");
                        }
                    }
                    whereStatement.put(condition.name(), conditionStr);
                    whereStatement.put(value.name(), valueObj);

                } catch (IllegalArgumentException e) {
                    //非规范范围的查询按，sql脚本方式改写valueStr
                    whereStatement.put(condition.name(), conditionStr.toString());
                    whereStatement.put(value.name(), valueObj.toString().replace(keyStr.toString(), FieldUtil.getColumnName(keyField)));
                    logger.warn("reading conditon type of " + statment.get(condition));
                }
                whereList.add(whereStatement);
            }
        }

        result.setWhereStatements(whereList);
        return result;

    }


    /**
     * 处理查询后特性集成-参照
     *
     * @param resultList
     */
    public static void processSelectList(List<Map> selectList, ParamProcessResult pr, RefCommonMapper mapper) {
        if (selectList == null || selectList.size() == 0) {
            logger.trace("model " + pr.getStateModel().getCode() + " has no query result");
            return;
        }
        if (!hasRefrence(pr.getStateModel().getmClass())) {
            logger.trace("model " + pr.getStateModel().getCode() + " has no reference escaping processSelectList");
            return;
        }
        /**
         * @Step 1
         * 一次遍历,获取参照字段id集合,用于@Step 2
         */
        Map<String, Map<Field, Set<String>>> refFieldIds = new HashMap<>();//key为refcode,value为idCache
        Map<Field, Set<String>> idCache = new HashMap<>(); //缓存list中的所有entity属性参照内的id
        Map<Field, Reference> refCache = new HashMap<>();//缓存entity中的所有@Reference定义
        Map<String, List<String>> utilParam = new HashMap<>(); //调util时的入参
        for (Map row : selectList) {
            for (String fieldName : pr.getGroupFields()) {
                Field field = ReflectUtil.getField(pr.getStateModel().getmClass(), fieldName);
                Reference ref = field.getAnnotation(Reference.class);
                if (ref != null) {
                    if (idCache.get(field)==null) { //  提高缓存装载效率,仅加载一次
                        idCache.put(field, new HashSet<String>());
                    }
                    refCache.put(field, ref); //将所有参照和field的关系缓存起来后续使用
                    if (null != row.get(fieldName)) {
                        String[] fieldIds = row.get(fieldName).toString().split(",");//兼容参照多选
                        idCache.get(field).addAll(Arrays.asList(fieldIds));

                    }
                    if (refFieldIds.get(ref.code())==null){
                        refFieldIds.put(ref.code(),idCache);
                    }
                    List ids = new ArrayList();
                    ids.addAll(idCache.get(field));
                    utilParam.put(ref.code(),ids);
                }
            }
        }
        /**
         * @Step 2解析参照配置, 一次按需(idCache)加载参照数据(远程与本地统一处理)
         * 返回结果重新整理到refDataCache,为@Step 3 做准备
         */
        Map<Field, Map<String, Map<String,Object>>> refDataCache = new HashMap<>();//key为参照field;value.key为field内的id,value.value为id对应的参照数据
        Map<String, List<Map<String, Object>>> utilResult =null;
        try {
            utilResult = RefIdToNameUtil.convertIdToName(utilParam);
        } catch (Exception e) {
            logger.error("unified ref-id2name service calling error：" + utilParam, e);
        }
        //utilResult都是按refcode分组的数据,我们反写都是基于field的,需要重新格式化到refDataCache中
        if (null != utilResult && utilResult.size() > 0){
            for (String refCode:utilResult.keySet()){
                for (Map<String,Object> refContent :utilResult.get(refCode) ){
                    for (Field field : refCache.keySet()) {
                        Map<String,Map<String,Object>> refFieldData = new HashMap<>();
                        for (String id: refFieldIds.get(refCode).get(field)){
                            if (id.equals( refContent.get("ID") ) || id.equals( refContent.get("id") )|| id.equals( refContent.get("refpk"))   ){
                                refFieldData.put(id,refContent);
                            }
                        }
                        if (refDataCache.containsKey(field)){//防止缓存的正确值被覆盖掉
                            refDataCache.get(field).putAll(refFieldData);
                        }else{
                            refDataCache.put(field, refFieldData);
                        }
                    }
                }
            }
        }else{
            logger.error("unified ref-id2name service returning nothing! :" + utilParam);
            return;
        }

        /**
         * @Step 3 二次遍历,执行反写
         */
        for (Map row : selectList) { //遍历结果集
            for (Field refField : refCache.keySet()) {//遍历缓存的entity的全部参照字段
                if (refDataCache.get(refField) == null) {
                    continue;//没有参照数据缓存,就不用后面的反写了,直接下一个参照字段
                }
                if (row.get(refField.getName()) == null) {
                    continue; // 参照field id值为空,则跳过本field数据解析
                }
                String refFieldValue = row.get(refField.getName()).toString();//取参照字段值
                Reference reference = refCache.get(refField);
                String[] ids = refFieldValue.split(",");     //参照字段值转数组
                String[] writeValues = new String[ids.length];  //定义结果载体
                int loopSize = Math.min(reference.srcProperties().length, reference.desProperties().length);//参照配置多字段参照时需结构匹配
                for (int i = 0; i < loopSize; i++) {                //遍历参照中配置的多个srcPro和desPro 进行值替换
                    String srcCol = reference.srcProperties()[i].toLowerCase();  //参照表name字段统一按小写处理
                    String desField = reference.desProperties()[i]; //entity对应参照value的字段
                    for (int j = 0; j < ids.length; j++) {//多值参照时,循环匹配拿到结果进行反写
                        writeValues[j]= String.valueOf( refDataCache.get(refField).get(ids[j]).get(srcCol)) ;

                    }
                    row.put(desField, ArrayUtil.join(writeValues, ","));//执行反写
                }
            }
        }


    }
}
