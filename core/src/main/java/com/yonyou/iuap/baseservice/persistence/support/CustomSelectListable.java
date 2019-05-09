package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CustomSelectListable<T extends Identifier> {

    SearchParams getSearchParams();

    PageRequest getPageRequest();

    List<T> doCustomSelectList();
}
