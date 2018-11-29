package com.yonyou.iuap.baseservice.statistics.support;

import java.util.*;

/**
 * 统计模型封装类,解析的时候存入StatModelResolver的缓存中
 */
public class StatModel {

    private String  code ; //编号
    private Class mClass ;  //类名
    private String tableName; //模型表名
    private Map<String,String> statColumnsFields = new HashMap<>(); //key:打注解@StatisticField的数据库字段名，value：打注解@StatisticField的属性
    private Map<String, StatFunctions[]> statColumnsFunctions = new HashMap<>(); //统计字段的统计函数

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Class getmClass() {
        return mClass;
    }

    public void setmClass(Class mClass) {
        this.mClass = mClass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public Map<String, StatFunctions[]> getStatColumnsFunctions() {
        return statColumnsFunctions;
    }

    public void setStatColumnsFunctions(Map<String, StatFunctions[]> statColumnsFunctions) {
        this.statColumnsFunctions = statColumnsFunctions;
    }

    public Map<String, String> getStatColumnsFields() {
        return statColumnsFields;
    }

    public void setStatColumnsFields(Map<String, String> statColumnsFields) {
        this.statColumnsFields = statColumnsFields;
    }

    @Override
    public String toString() {
        return "StatModel{" +
                "code='" + code + '\'' +
                ", mClass=" + mClass +
                ", tableName='" + tableName + '\'' +
                ", statColumnsFunctions=" + statColumnsFunctions +
                '}';
    }
}
