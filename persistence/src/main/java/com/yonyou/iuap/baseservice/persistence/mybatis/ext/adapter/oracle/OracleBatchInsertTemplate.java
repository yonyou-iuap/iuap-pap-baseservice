package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.abs.AbsBatchInsertTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
/**
 * description：add new oracle batch insert sql provider for performance optimizing
 * @author leon
 * 2018.12.14
 */
public class OracleBatchInsertTemplate  extends AbsBatchInsertTemplate {

    @Override
    public Dialect getDialect() {
        return Dialect.oracle;
    }


}