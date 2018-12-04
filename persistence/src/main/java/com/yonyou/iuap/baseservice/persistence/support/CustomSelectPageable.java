package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomSelectPageable<T extends Model>   {

    SearchParams getSearchParams();
    PageRequest getPageRequest();
    Page<T> doCunstomSelectPage();
}
