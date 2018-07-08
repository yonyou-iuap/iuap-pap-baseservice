package com.yonyou.iuap.baseservice.ref.service;


import com.yonyou.iuap.baseservice.ref.dao.mapper.RefCommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public  class RefCommonService {

    @Autowired
    RefCommonMapper mapper;

    public List<Map<String, Object>> getFilterRef(String tablename, String idfield,
                                                  List<String> extColumns, List<String> ids) {

        List<Map<String, Object>> result = mapper.findUserListByIds(tablename,idfield,extColumns,ids);

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



}
