package com.yonyou.iuap.baseservice.intg.service;

import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.annotation.I18n;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.i18n.MessageSourceUtil;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 说明: 通用国际化检索服务,查询条件自动国际化适配,查询结果自动替换值
 * 使用方法:可在继承GenericIntegrateService时,通过getFeats(ServiceFeature.I18N)方法注入
 * @param <T> 实现Model的业务实体
 */
@Service
public class I18nCommonService<T extends Model> implements QueryFeatureExtension<T> {
    private static Logger logger = LoggerFactory.getLogger(I18nCommonService.class);

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams, Class modelClass) {

        Field[] fields = ReflectUtil.getFields(modelClass);

        for (Field field : fields) {
            I18n annotation = field.getAnnotation(I18n.class);
            if (annotation!=null
                    &&  searchParams.getSearchMap().get(field.getName())!=null
                    &&  StringUtils.isNotEmpty( getLocaleIndex() )  ){
                searchParams.addCondition(field.getName()+getLocaleIndex(),searchParams.getSearchMap().get(field.getName()));//加入多语转换后的查询条件
                searchParams.removeCondition(field.getName()); //清掉旧查询条件
            }
        }

        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {

        for (T entity:list){
            Field[] fields = ReflectUtil.getFields(entity.getClass());
            for (Field field : fields) {
                I18n annotation = field.getAnnotation(I18n.class);
                if (annotation!=null){
                    Object i18nValue = ReflectUtil.getFieldValue(entity, field.getName() + getLocaleIndex());
                    ReflectUtil.setFieldValue(entity,field.getName(),  i18nValue  );
                }
            }
        }

        return list;
    }

    /**
     * 根据后台登陆服务上下文,或request中的cookie获取当前语种
     * @return 语种对应数据库字段的顺序号
     */
    private  String getLocaleIndex(){

        // 首先从系统上下文中获取locale
        // 如果无法获取，则从request中自行获取

        String c_locale = "";
        try{
            c_locale = InvocationInfoProxy.getLocale();

            if(c_locale == null || "".equals(c_locale)){
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

                c_locale = CookieUtil.findCookieValue(request.getCookies(), "u_locale");

                if(c_locale == null || "".equals(c_locale)){

                    c_locale = CookieUtil.findCookieValue(request.getCookies(), MessageSourceUtil.COOKIE_PREF + "u_locale");
                }
            }

        }catch(Exception e){
            // do nothing
            logger.warn("getting error where reading cookie [u_locale]:"+e.getMessage());
        }
        c_locale = StringUtils.isEmpty(c_locale)?"en_US" : c_locale;//空值校验加强

        LocaleStandard result= LocaleStandard.valueOf(c_locale);
        if (result.ordinal()!=0){
            return String.valueOf( result.ordinal()+1 );
        }else{
            return "";
        }
    }

    /**
     * TODO: 后期标准实现完善后,应改为调用wbalone的接口http://${server}/wbalone/i18n/classification/list获取
     * 枚举中所列遵循了iuap国际化总体方案.
     */
    enum LocaleStandard {
        zh_CN,//简体中文
        en_US,//英文
        zh_TW,//繁体中文
        fr_GN,//法文
        reserved_1,//预留1
        reserved_2;//预留2

    }

}
