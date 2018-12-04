package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public  abstract class AbsCustomSelectPage<T extends Model> implements CustomSelectPageable   {

    protected SearchParams searchParams;
    protected PageRequest pageRequest;

    public SearchParams getSearchParams() {
        return searchParams;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public AbsCustomSelectPage(SearchParams searchParams, PageRequest pageRequest) {
        this.searchParams = searchParams;
        this.pageRequest = pageRequest;
    }


}
