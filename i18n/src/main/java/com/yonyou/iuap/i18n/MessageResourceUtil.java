package com.yonyou.iuap.i18n;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

import java.util.Locale;

/**
 * 资源文件的统一获取以及使用
 *
 * 首先采用spring框架加载ReloadableResourceBundleMessageSource初始化资源
 *
 * 其次采用getMessage进行key-value的获取
 *
 *
 * @author wenfan
 *
 */
public class MessageResourceUtil implements InitializingBean {

    public static final String BEAN_NAME = "messageResourceUtil";

    @Autowired(required = false)
    @Qualifier("messageSource")
    private MessageSource messageSource;

    public String getMessage(String messagesKey) {
        return this.messageSource.getMessage(messagesKey, null, "undefined", LocaleContextHolder.getLocale());
    }


    public String getMessage(String messagesKey, Object[] args) {
        return this.messageSource.getMessage(messagesKey, args, "undefined", LocaleContextHolder.getLocale());
    }


    public String getMessage(String messagesKey, String defaultMessage) {
        return this.messageSource.getMessage(messagesKey, null, defaultMessage, LocaleContextHolder.getLocale());
    }


    public String getMessage(String messagesKey, Object[] args, String defaultMessage) {
        return this.messageSource.getMessage(messagesKey, args, defaultMessage, LocaleContextHolder.getLocale());
    }


    public String getMessage(String messagesKey, Object[] args, String defaultMessage, Locale locale) {
        return this.messageSource.getMessage(messagesKey, args, defaultMessage, locale);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.messageSource, "the messageSource must be not null.");
    }
}