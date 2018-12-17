package com.yonyou.iuap.baseservice.persistence.mybatis.ext.handler;


import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 解决mybatis ${}变量符替换为#{}的问题,在变量内进行显示声明
 * <P> 例如: #{var,jdbcType=OTHER,typeHandler=com.yonyou.iuap.baseservice.persistence.mybatis.ext.handler.OtherStatementTypeHandler }</>
 */
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(String.class)
public class OtherStatementTypeHandler implements TypeHandler {
    private  static Logger logger = LoggerFactory.getLogger(OtherStatementTypeHandler.class);


    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        logger.info("reading paramerer:"+parameter);

        ps.setBytes(i,String.valueOf(parameter).getBytes());
    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        logger.info("reading paramerer:"+str);
        return str;

    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        String str= rs.getString(columnIndex);
        logger.info("reading paramerer:"+str);
        return str;
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {

        String str = cs.getString(columnIndex);
        logger.info("reading paramerer:"+str);
        return str;
    }
}
