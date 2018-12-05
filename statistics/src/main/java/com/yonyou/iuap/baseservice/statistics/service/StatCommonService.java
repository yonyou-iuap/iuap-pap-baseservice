package com.yonyou.iuap.baseservice.statistics.service;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.RefParamConfig;
import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import com.yonyou.iuap.baseservice.statistics.dao.StatCommonMapper;
import com.yonyou.iuap.baseservice.statistics.support.ParamProcessResult;
import com.yonyou.iuap.baseservice.statistics.util.SearchParamUtil;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("ALL")
@Service
public class StatCommonService {
    private static Logger logger = LoggerFactory.getLogger(StatCommonService.class);

    @Autowired
    private StatCommonMapper statCommonMapper;

    @Autowired
    RefCommonMapper mapper;

    /**
     * 分页查询,聚合统计结果
     *
     * @param pageRequest
     * @param searchParams
     * @return 某一页数据
     */
    public Page<Map> selectAllByPage(PageRequest pageRequest, SearchParams searchParams, String modelCode) {

        ParamProcessResult ppr = SearchParamUtil.processServiceParams( searchParams, modelCode);
        if (ppr.getSort() != null) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), ppr.getSort());
        }
        Page  page = statCommonMapper.selectAllByPage(pageRequest, searchParams, ppr.getTableName(), ppr.getStatStatements(),ppr.getGroupStatements(), ppr.getWhereStatements()).getPage();
        SearchParamUtil.processSelectList(page.getContent(),ppr,mapper);

        return page;


    }

    /**
     * 统计结果全集查询
     *
     * @param searchParams
     * @param modelCode
     * @return 集合函数统计结果
     */
    public List<Map> findAll(  SearchParams searchParams, String modelCode) {
        ParamProcessResult ppr = SearchParamUtil.processServiceParams( searchParams, modelCode);
        PageRequest pageRequest =null;
        if (ppr.getSort() != null) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), ppr.getSort());
        }
        List<Map> list = statCommonMapper.findAll(pageRequest, searchParams, ppr.getTableName(), ppr.getStatStatements(),ppr.getGroupStatements(), ppr.getWhereStatements());
        SearchParamUtil.processSelectList(list,ppr,mapper);
        return list;

    }

    public List<Map> findDistinct(SearchParams searchParams, String modelCode){
        ParamProcessResult ppr = SearchParamUtil.processServiceParams( searchParams, modelCode);
        List<Map> list = statCommonMapper.findDistinct( ppr.getStatStatements(), ppr.getTableName(), ppr.getWhereStatements());
        SearchParamUtil.processSelectList(list,ppr,mapper);
        return  list;
    }


}
