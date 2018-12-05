package com.yonyou.iuap.baseservice.statistics.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.RefParamConfig;
import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.multitenant.entity.MultiTenant;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import com.yonyou.iuap.baseservice.statistics.support.ParamProcessResult;
import com.yonyou.iuap.baseservice.statistics.support.StatFunctions;
import com.yonyou.iuap.baseservice.statistics.support.StatModel;
import com.yonyou.iuap.baseservice.statistics.support.StatModelResolver;
import com.yonyou.iuap.baseservice.support.condition.Match;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
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
     * @param pageRequest
     * @param searchParams
     * @param modelCode
     * @return
     */
    public static ParamProcessResult processServiceParams(PageRequest pageRequest, SearchParams searchParams, String modelCode) {
        ParamProcessResult result = new ParamProcessResult();

        StatModel m = StatModelResolver.getStatModel(modelCode);
        if (m == null) {
            throw new RuntimeException("calling model [" + modelCode + "] can not be resolved by Statistic Service!!");
        }
        result.setStateModel(m);
        String tableName = m.getTableName();
        Set<String> statStatements = new HashSet<>();
        Map<String, StatFunctions[]> statColFuncs = m.getStatColumnsFunctions();


        for (String col : statColFuncs.keySet()) {
            for (StatFunctions func : statColFuncs.get(col)) {
                statStatements.add(func + "(" + col + ") as " + m.getStatColumnsFields().get(col) + StringUtils.capitalize(func.name()));
            }
        }
        //解析排序条件sortMap
        if (searchParams.getSearchMap().get(sortMap.name()) != null) {
            List<Map<String, String>> sorts = (List<Map<String, String>>) searchParams.getSearchMap().get(sortMap.name());
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
        }
        //解析groupParam
        if (searchParams.getSearchMap().get(groupParams.name()) != null) {
            List<String> groups = (List<String>) searchParams.getSearchMap().get(groupParams.name());
            Set<String> groupStatements = new HashSet<>();
            Set<String> groupCols = new HashSet<>();
            for (String group : groups) {
                Field keyField = ReflectUtil.getField(m.getmClass(), group);
                if (keyField==null){
                    throw new RuntimeException("cannot find field "+group+" in  model [" + modelCode + "] ");
                }
                groupStatements.add(FieldUtil.getColumnName(keyField) + " as " + group);
                groupCols.add(FieldUtil.getColumnName(keyField));
            }
            searchParams.getSearchMap().put(groupParams.name(),groupCols);
            result.setGroupStatements(groupStatements);
            result.setGroupFields(groups);
        }

        // 解析模型特性,组装where 条件
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
                    whereStatement.put(condition.name(), "OTHER");
                    whereStatement.put(value.name(), valueObj.toString().replace(keyStr.toString(), FieldUtil.getColumnName(keyField)));
                    logger.warn("reading conditon type of " + statment.get(condition));
                }
                whereList.add(whereStatement);
            }
        }

        result.setWhereStatements(whereList);
        result.setTableName(tableName);
        result.setStatStatements(statStatements);
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
         * 解析参照配置,获取参照字段id集合,用于后续参照查询
         */
        Map<Field, Set<String>> idCache = new HashMap<>(); //缓存list中的所有entity属性参照内的id
        Map<Field, Reference> refCache = new HashMap<>();//缓存entity中的所有@Reference定义
        Map<Field, List<Map<String, Object>>> refDataCache = new HashMap<>();//缓存参照数据,用于最后的反写
        for (Map statResult : selectList) {
            for (String fieldName : pr.getGroupFields()) {
                Field field = ReflectUtil.getField(pr.getStateModel().getmClass(), fieldName);
                Reference ref = field.getAnnotation(Reference.class);
                if (ref != null) {
                    refCache.put(field, ref); //将所有参照和field的关系缓存起来后续使用
                    if (null != statResult.get(fieldName)) {
                        String[] fieldIds = statResult.get(fieldName).toString().split(",");//兼容参照多选
                        idCache.get(field).addAll(Arrays.asList(fieldIds));
                    }
                }
            }
        }
        /**
         * @Step 2解析参照配置, 一次按需(idCache)加载参照数据
         */

        for (Field field : refCache.keySet()) {
            RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(refCache.get(field).code());
            RefParamConfig refParamConfig = refParamVO.getRefParamConfigTable() == null ? refParamVO.getRefParamConfigTableTree() : refParamVO.getRefParamConfigTable();
            if (refParamVO == null || refParamConfig == null) {
                logger.warn("参照XML配置错误:" + refCache.get(field).code());
                continue;
            }
            List<String> setList = new ArrayList<>(idCache.get(field));
            if (setList == null || setList.size() == 0) {
                continue;
            }
            List<Map<String, Object>> refContents =
                    mapper.findRefListByIds(refParamConfig.getTableName(),
                            refParamConfig.getId(), refParamConfig.getExtension(), setList);
            if (null != refContents && refContents.size() > 0)
                refDataCache.put(field, refContents);//将所有参照数据集和field的关系缓存起来后续使用
        }

        /**
         * @Step 3 逐条遍历业务结果集,向entity参照指定属性写入参照值
         */
        if (refDataCache.isEmpty()) {
            return;
        }
        for (Map statResult : selectList) { //遍历结果集
            for (Field refField : refCache.keySet()) {//遍历缓存的entity的全部参照字段
                if (refDataCache.get(refField) == null) {
                    continue;//没有参照数据缓存,就不用后面的反写了,直接下一个参照字段
                }
                String refFieldValue = statResult.get(refField.getName()).toString();//取参照字段值
                if (refFieldValue == null) {
                    continue; // 参照field id值为空,则跳过本field数据解析
                }
                Reference refAnnotation = refCache.get(refField);
                String[] mutiRefIds = refFieldValue.split(",");     //参照字段值转数组
                String[] mutiRefValues = new String[mutiRefIds.length];  //定义结果载体
                int loopSize = Math.min(refAnnotation.srcProperties().length, refAnnotation.desProperties().length);//参照配置多字段参照时需结构匹配
                for (int i = 0; i < loopSize; i++) {                //遍历参照中配置的多个srcPro和desPro 进行值替换
                    String srcCol = refAnnotation.srcProperties()[i];  //参照表name字段
                    String desField = refAnnotation.desProperties()[i]; //entity对应参照value的字段
                    List<Map<String, Object>> refDatas = refDataCache.get(refField);//取出参照缓存数据集
                    for (Map<String, Object> refData : refDatas) {
                        for (int j = 0; j < mutiRefIds.length; j++) {//多值参照时,循环匹配拿到结果进行反写
                            if (refData.get("ID") != null && refData.get("ID").toString().equals(mutiRefIds[j])) { //数据库适配时 mysql也要将此字段as ID
                                for (String columnKey : refData.keySet()) {//解决大小写适配问题
                                    if (columnKey.equalsIgnoreCase(srcCol))
                                        mutiRefValues[j] = String.valueOf(refData.get(columnKey));
                                }
                            }
                        }
                    }
                    String fieldValue = ArrayUtil.join(mutiRefValues, ",");
                    statResult.put(desField, fieldValue);//执行反写
                }
            }
        }


    }
}
