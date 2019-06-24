package com.yonyou.iuap.baseservice.controller.util;

import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import com.yonyou.iuap.ucf.common.rest.SearchParams;
import com.yonyou.iuap.ucf.dao.support.UcfSearchParams;
import com.yonyou.iuap.ucf.dao.utils.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 兼容iuap3.5.6的查询条件构造器
 *
 * @author leon
 * @date 2019/4/8
 * @since UCF1.0
 */
public class SearchUtil {
    private static Logger logger = LoggerFactory.getLogger(SearchUtil.class);
    private static String PARAM_SORT_MAP = "sortMap";
    private static String PARAM_SEARCH_PREFIX= "search_";

    public static SearchParams convertSearchParam(com.yonyou.iuap.mvc.type.SearchParams params,Class<? extends Identifier> entityClass){
        UcfSearchParams result= UcfSearchParams.of(entityClass);

        if (params==null || params.getSearchMap()==null || params.getSearchMap().size() ==0){
            return result;

        }else{
            for (String key :params.getSearchMap().keySet()){
                Field keyField;
                if (key.toLowerCase().startsWith(PARAM_SEARCH_PREFIX)){
                    keyField= ReflectUtil.getField(entityClass,key.replace(PARAM_SEARCH_PREFIX,""));
                }else{
                    keyField= ReflectUtil.getField(entityClass,key);
                }
                if (keyField !=null){
                    Condition cond = keyField.getAnnotation(Condition.class);
                    String keyCol =FieldUtil.getColumnName(keyField);
                    if (cond==null || cond.match()== Match.EQ){
                        result.addEqualCondition(keyCol,String.valueOf(params.getSearchMap().get(key) ));
                    }else if(cond.match()== Match.IN){
                        try {
                            List<Object> ls = (List<Object>) params.getSearchMap().get(key);
                            result.addInCondition(keyCol,ls);
                        } catch (Exception e) {
                            logger.error("error happened while reading IN param from search params:"+keyField.getName(),e);
                        }
                    }else if(cond.match()== Match.BETWEEN){
                        try {
                            Object[] values = (Object[]) params.getSearchMap().get(key);
                            result.addBetweenCondition(keyCol,values[0],values[1] );
                        } catch (Exception e) {
                            logger.error("error happened while reading BETWEEN param from search params:"+keyField.getName(),e);
                        }
                    }
                    else{

                        result.addCondition(keyCol,UcfSearchParams.Match.valueOf(cond.match().name()),String.valueOf(params.getSearchMap().get(key) ));
                    }

                }

            }

        }

        return result;
    }

    public static Sort  getSortFromSortMap (LinkedHashMap<String,String> sortMap,Class entityClass){
        if (sortMap.size() > 0) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String key : sortMap.keySet()) {
                    Field keyField = ReflectUtil.getField(entityClass, key);
                    if (keyField == null) {
                        throw new RuntimeException("cannot find field " + key + " in  model [" + entityClass + "] ");
                    }
                    Sort.Order order =
                            new Sort.Order(
                                    Sort.Direction.valueOf(sortMap.get(key).toUpperCase()),
                                    FieldUtil.getColumnName(keyField));
                    orders.add(order);
            }
            return Sort.by(orders);
        } else {
            logger.debug("receiving none sort param in sortMap ");
        }
        return Sort.by("id");
    }


    public static Sort getSortFromSearchParams(Map<String,Object> searchMap, Class<? extends Identifier> entityClass) {

        if (searchMap!=null && searchMap.get(PARAM_SORT_MAP) != null) {
            @SuppressWarnings("all")
            List<Map<String, String>> sorts = (List<Map<String, String>>) searchMap.get(PARAM_SORT_MAP);
            if (sorts.size() > 0) {
                List<Sort.Order> orders = new ArrayList<>();
                for (Map<String, String> sort : sorts) {
                    if (sort.keySet().size() > 0 && sort.keySet().toArray()[0] != null) {
                        Field keyField = ReflectUtil.getField(entityClass, sort.keySet().toArray()[0].toString());
                        if (keyField == null) {
                            throw new RuntimeException("cannot find field " + sort.keySet().toArray()[0].toString() + " in  model [" + entityClass + "] ");
                        }
                        Sort.Order order =
                                new Sort.Order(
                                        Sort.Direction.valueOf(sort.get(sort.keySet().toArray()[0]).toString().toUpperCase()),
                                        FieldUtil.getColumnName(keyField));
                        orders.add(order);
                    }
                }
                return Sort.by(orders);
            } else {
                logger.debug("receiving none sort param in sortMap of querying =>" + entityClass);
            }
        }
        logger.debug("receiving none  sortMap of searchParams =>" + searchMap);
        return Sort.by("id");
    }
}
