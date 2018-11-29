package com.yonyou.iuap.baseservice.statistics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yonyou.iuap.baseservice.statistics.dao.StatCommonMapper;
import com.yonyou.iuap.baseservice.statistics.support.StatParam;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = { "com.yonyou.iuap.baseservice.statistics" })
@ContextConfiguration(locations = { "classpath:t-app-persistence.xml" })
public class MapperTest {
    @Autowired
    StatCommonMapper statCommonMapper;

    @Test
    public void testSelect(){

        Set<String> statStatement = new HashSet<>();
        statStatement.add("count(1) as idCount ");
        statStatement.add("max(ly_code) as codeMax");
        SearchParams searchParams = new SearchParams();
        List<String> groupBys = new ArrayList<>();
        groupBys.add("id");
        groupBys.add("ly_code");
        searchParams.addCondition(StatParam.groupParams.name(),groupBys);
        PageRequest pagerequest = new PageRequest(1,100,new Sort("ts")){ };
        System.out.println(JSON.toJSONString(pagerequest, SerializerFeature.QuoteFieldNames));
        System.out.println(JSON.toJSONString(searchParams, SerializerFeature.QuoteFieldNames));
        List<Map<String,Object>> whereStatement= new ArrayList<>();
        List<Map> result = statCommonMapper.findAll(pagerequest, searchParams, "duban", statStatement,whereStatement);
        System.out.println(result.toString());
    }


}
