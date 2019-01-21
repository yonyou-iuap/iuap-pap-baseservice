package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;

/**
 *自定义查询的参数
 */
public class PageRequestAndSearchParams implements Serializable {
    private PageRequest pageRequest;
    private SearchParams searchParams;

    public PageRequestAndSearchParams(PageRequest pageRequest, SearchParams searchParams) {
        this.pageRequest = pageRequest;
        this.searchParams = searchParams;
    }
    public PageRequestAndSearchParams() {
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }

    public SearchParams getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(SearchParams searchParams) {
        this.searchParams = searchParams;
    }
}
