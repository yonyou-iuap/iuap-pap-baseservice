package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mysql;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.abs.AbsBatchInsertTemplate;
/**
 * description：add new mysql batch insert sql provider for performance optimizing
 * @author leon
 * 2018.12.14
 */
public class MysqlBatchInsertTemplate  extends AbsBatchInsertTemplate {

    @Override
    public Dialect getDialect() {
        return Dialect.mysql;
    }

}