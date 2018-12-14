package com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.oracle;

import cn.hutool.core.util.StrUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.SqlTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.adapter.abs.AbsBatchInsertTemplate;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception.MapperException;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.support.Dialect;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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