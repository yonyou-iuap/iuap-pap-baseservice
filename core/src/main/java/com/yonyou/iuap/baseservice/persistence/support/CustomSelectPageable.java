package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomSelectPageable<T extends Identifier>   {

    SearchParams getSearchParams();
    PageRequest getPageRequest();
    Page<T> doCustomSelectPage();
}
