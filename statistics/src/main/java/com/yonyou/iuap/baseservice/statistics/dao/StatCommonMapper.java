package com.yonyou.iuap.baseservice.statistics.dao;

import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.mybatis.anotation.MyBatisRepository;
import com.yonyou.iuap.mybatis.type.PageResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 统计查询通用dao层，解析统计模型，根据传参进行聚合函数查询
 * 目前仅支持分组查询和分页
 */
@MyBatisRepository
public interface StatCommonMapper {
    /**
     * 分组查询之后再进行分页
     * @param pageRequest
     * @param searchParams
     * @param tableName
     * @param statStatement
     * @return
     */
    PageResult<Map> selectAllByPage(@Param("page") PageRequest pageRequest, @Param("condition") SearchParams searchParams,
                                    @Param("tableName") String tableName, @Param("statStatements") Set<String> statStatements,
                                    @Param("whereStatements") List<Map<String,String>> whereStatements);

    /**
     * 直接查询所有分组数据
     * @param pageRequest
     * @param searchParams
     * @param tableName
     * @param statStatement
     * @return
     */
    List<Map> findAll(@Param("page") PageRequest pageRequest, @Param("condition") SearchParams searchParams,
                      @Param("tableName") String tableName, @Param("statStatements") Set<String> statStatements,
                      @Param("whereStatements") List<Map<String,String>> whereStatements);

}
