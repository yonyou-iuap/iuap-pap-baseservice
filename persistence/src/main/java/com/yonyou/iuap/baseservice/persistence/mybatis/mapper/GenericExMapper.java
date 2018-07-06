package com.yonyou.iuap.baseservice.persistence.mybatis.mapper;

import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mybatis.type.PageResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * 说明：逻辑删除基础Mapper
 * @author houlf
 * 2018年6月12日
 */
public interface GenericExMapper<T extends Model & LogicDel> extends GenericMapper<T>{
    /**
     * 表（树表）的数据查询
     * @param pageRequest
     * @param tablename
     * @param idfield
     * @param extcols
     * @param condmap
     * @return
     */
    PageResult<Map<String,Object>> selectRefTable(
            @Param("page") PageRequest pageRequest,
            @Param("tablename") String tablename,
            @Param("idfield") String idfield,
            @Param("extcols") List<String> extcols,
            @Param("condmap") Map<String, String> condmap);

    /**
     * 树表型参照--树的数据查询
     * @param pageRequest
     * @param tablename
     * @param idfield
     * @param codefield
     * @param namefield
     * @return
     */
    PageResult<Map<String,Object>> selectRefTree(
            @Param("page") PageRequest pageRequest,
            @Param("tablename") String tablename,
            @Param("idfield") String idfield,
            @Param("pidfield") String pidfield,
            @Param("codefield") String codefield,
            @Param("namefield") String namefield,
            @Param("extcols") List<String> extcols,
            @Param("condmap") Map<String, String> condmap);
}