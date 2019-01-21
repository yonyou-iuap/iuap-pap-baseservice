package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mssql;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.abs.AbsBatchInsertTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * description：add new mssql batch insert sql provider for performance optimizing
 * @author leon
 * 2018.12.14
 */
public class MssqlBatchInsertTemplate  extends AbsBatchInsertTemplate {

    @Override
    public Dialect getDialect() {
        return Dialect.mssql;
    }


}
