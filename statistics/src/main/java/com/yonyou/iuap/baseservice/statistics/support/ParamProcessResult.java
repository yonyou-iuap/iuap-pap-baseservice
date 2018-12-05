package com.yonyou.iuap.baseservice.statistics.support;

import org.springframework.data.domain.Sort;

import java.util.*;

public class ParamProcessResult {
    private StatModel stateModel;
    private Sort sort;
    private String tableName;
    private Set<String> groupStatements = new HashSet<>();
    private List<String> groupFields = new ArrayList<>();
    private Set<String> statStatements = new HashSet<>();
    private List<Map<String, Object>> whereStatements = new ArrayList<>();

    public Set<String> getGroupStatements() {
        return groupStatements;
    }

    public void setGroupStatements(Set<String> groupStatements) {
        this.groupStatements = groupStatements;
    }

    public List<Map<String, Object>> getWhereStatements() {
        return whereStatements;
    }

    public void setWhereStatements(List<Map<String, Object>> whereStatements) {
        this.whereStatements = whereStatements;
    }

    public List<String> getGroupFields() {
        return groupFields;
    }

    public void setGroupFields(List<String> groupFields) {
        this.groupFields = groupFields;
    }

    public StatModel getStateModel() {
        return stateModel;
    }

    public void setStateModel(StatModel stateModel) {
        this.stateModel = stateModel;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Set<String> getStatStatements() {
        return statStatements;
    }

    public void setStatStatements(Set<String> statStatements) {
        this.statStatements = statStatements;
    }
}