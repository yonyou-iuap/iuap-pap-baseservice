package com.yonyou.iuap.baseservice.persistence.support;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CustomSelectListable<T extends Model> {

    SearchParams getSearchParams();

    PageRequest getPageRequest();

    List<T> doCustomSelectList();
}
