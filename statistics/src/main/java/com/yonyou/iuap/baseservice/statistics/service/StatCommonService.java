package com.yonyou.iuap.baseservice.statistics.service;

import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.matcher.MatcherFactory;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import com.yonyou.iuap.baseservice.statistics.dao.StatCommonMapper;
import com.yonyou.iuap.baseservice.statistics.support.StatFunctions;
import com.yonyou.iuap.baseservice.statistics.support.StatModel;
import com.yonyou.iuap.baseservice.statistics.support.StatModelResolver;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.mybatis.dialect.impl.PgDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

import static com.yonyou.iuap.baseservice.statistics.support.StatParam.*;

@SuppressWarnings("ALL")
@Service
public class StatCommonService {
    private static Logger logger = LoggerFactory.getLogger(StatCommonService.class);

    @Autowired
    private StatCommonMapper statCommonMapper;

    /**
     * 根据模型编号（uri中取得），进行统计模型解析，并得到查询语句所需的关键参数
     *
     * @param pageRequest
     * @param searchParams
     * @param modelCode
     * @return
     */
    protected ProcessResult processServiceParams(PageRequest pageRequest, SearchParams searchParams, String modelCode) {
        ProcessResult result = new ProcessResult();

        StatModel m = StatModelResolver.getStatModel(modelCode);
        if (m == null) {
            throw new RuntimeException("calling model [" + modelCode + "] can not be resolved by Statistic Service!!");
        }
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
            List<String> groupCols = new ArrayList<>();
            for (String group : groups) {
                Field keyField = ReflectUtil.getField(m.getmClass(), group);
                groupCols.add(FieldUtil.getColumnName(keyField));
            }
            searchParams.getSearchMap().put(groupParams.name(), groupCols);
        }

        // 解析模型特性,组装where 条件
        List<Map<String, Object>> whereList = new ArrayList<>();
        if (LogicDel.class.isAssignableFrom(m.getmClass())) {
            Map<String, Object> whereStatement = new HashMap<>();
            whereStatement.put(key.name(), "dr");
            whereStatement.put(value.name(), "0");
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

    public static void main(String[] args) {
        Match.valueOf("nLike");

    }

    /**
     * 分页查询,聚合统计结果
     *
     * @param pageRequest
     * @param searchParams
     * @return 某一页数据
     */
    public Page<Map> selectAllByPage(PageRequest pageRequest, SearchParams searchParams, String modelCode) {

        ProcessResult result = processServiceParams(pageRequest, searchParams, modelCode);
        if (result.getSort()!=null){
            pageRequest= new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),result.getSort());
        }
        return statCommonMapper.selectAllByPage(pageRequest, searchParams, result.getTableName(), result.getStatStatements(), result.getWhereStatements()).getPage();


    }

    /**
     * 统计结果全集查询
     *
     * @param searchParams
     * @param modelCode
     * @return 集合函数统计结果
     */
    public List<Map> findAll(PageRequest pageRequest, SearchParams searchParams, String modelCode) {
        ProcessResult result = processServiceParams(pageRequest, searchParams, modelCode);
        if (result.getSort()!=null){
            pageRequest= new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),result.getSort());
        }
        return statCommonMapper.findAll(pageRequest, searchParams, result.getTableName(), result.getStatStatements(), result.getWhereStatements());

    }

    /**
     * 内部处理包装类
     */
    class ProcessResult {
        Sort sort;
        String tableName;
        Set<String> statStatements = new HashSet<>();
        List<Map<String, Object>> whereStatements = new ArrayList<>();

        public List<Map<String, Object>> getWhereStatements() {
            return whereStatements;
        }

        public void setWhereStatements(List<Map<String, Object>> whereStatements) {
            this.whereStatements = whereStatements;
        }

        public Sort getSort() {
            return sort;
        }

        public void setSort(Sort sort) {
            this.sort = sort;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public Set<String> getStatStatements() {
            return statStatements;
        }

        public void setStatStatements(Set<String> statStatements) {
            this.statStatements = statStatements;
        }
    }

}
