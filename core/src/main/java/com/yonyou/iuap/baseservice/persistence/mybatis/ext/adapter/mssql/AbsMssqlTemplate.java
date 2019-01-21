package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.mssql;

import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;

public abstract  class AbsMssqlTemplate  implements SqlTemplate {
    @Override
    public Dialect getDialect() {
        return Dialect.mssql;
    }

}
