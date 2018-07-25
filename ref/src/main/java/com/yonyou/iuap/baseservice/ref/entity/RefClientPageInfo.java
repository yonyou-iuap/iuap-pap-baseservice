package com.yonyou.iuap.baseservice.ref.entity;

import java.io.Serializable;

/**
 * 从uitemplate_common移入,解决发版时的依赖问题,后续可能切换为其他ref-sdk
 * @author leon
 * */
public class RefClientPageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int pageSize = 100;
    private int currPageIndex = 0;
    private int pageCount;

    public RefClientPageInfo() {
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrPageIndex() {
        return this.currPageIndex;
    }

    public void setCurrPageIndex(int currPageIndex) {
        this.currPageIndex = currPageIndex;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
