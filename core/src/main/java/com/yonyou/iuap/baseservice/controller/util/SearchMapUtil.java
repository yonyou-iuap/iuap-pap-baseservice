package com.yonyou.iuap.baseservice.controller.util;

import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import com.yonyou.iuap.ucf.dao.utils.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO description
 *
 * @author leon
 * @date 2019/4/8
 * @since UCF1.0
 */
public class SearchMapUtil {
    private static Logger logger = LoggerFactory.getLogger(SearchMapUtil.class);
    private static String PARAM_SORT_MAP = "sortMap";

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
