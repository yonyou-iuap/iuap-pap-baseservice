package com.yonyou.iuap.baseservice.statistics.service;

import cn.hutool.core.util.ReflectUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        List<Map<String, Object>> whereList = new ArrayList<>();
        // 解析模型特性,组装where 条件
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
                Object valueStr = statment.get(value.name());
                Object conditionStr = statment.get(condition.name());
                if (StringUtils.isEmpty(keyStr) || StringUtils.isEmpty(valueStr)) { //关键参数缺一不可
                    logger.info("reading incomplete whereParams [" + keyStr + ":" + valueStr + "]");
                    continue;
                }
                Field keyField = ReflectUtil.getField(m.getmClass(), keyStr.toString());
                if (keyField == null) {
                    logger.info("finding none field [" + keyStr + "] in model class[" + m.getmClass() + "]!!");
                    continue;
                }
                if (StringUtils.isEmpty(conditionStr)) {
                    conditionStr = Match.EQ.name();
                }
                Map<String, Object> whereStatement = new HashMap<>();
                whereStatement.put(key.name(), FieldUtil.getColumnName(keyField));

                try {
                    Match match = Match.valueOf(conditionStr.toString());
                    whereStatement.put(condition.name(), conditionStr);
                    whereStatement.put(value.name(), valueStr);
                } catch (IllegalArgumentException e) {
                    //非规范范围的查询按，sql脚本方式改写valueStr
                    whereStatement.put(condition.name(), "OTHER");
                    whereStatement.put(value.name(), valueStr.toString().replace(keyStr.toString(), FieldUtil.getColumnName(keyField)));
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
        return statCommonMapper.findAll(pageRequest, searchParams, result.getTableName(), result.getStatStatements(), result.getWhereStatements());

    }

    /**
     * 内部处理包装类
     */
    class ProcessResult {
        String tableName;
        Set<String> statStatements = new HashSet<>();
        List<Map<String, Object>> whereStatements = new ArrayList<>();

        public List<Map<String, Object>> getWhereStatements() {
            return whereStatements;
        }

        public void setWhereStatements(List<Map<String, Object>> whereStatements) {
            this.whereStatements = whereStatements;
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
