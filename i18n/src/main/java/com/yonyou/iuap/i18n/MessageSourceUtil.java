package com.yonyou.iuap.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.yonyou.iuap.i18n.utils.LocaleUtil;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.utils.CookieUtil;

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
public class MessageSourceUtil {

	private static MessageSource messageSource;

	private static MessageSourceUtil instance = null;

	// 默认英文，通过U_locale转换而来
	private static Locale locale = Locale.ENGLISH;
	// 该值默认美国英语，取值于cookie
	private static String u_locale = "en_US";
	
	public static final String COOKIE_PREF = "_A_P_";

	// 单例模式
	private static MessageSourceUtil getInstance() {
		if (instance == null) {
			synchronized (MessageSourceUtil.class) {
				instance = new MessageSourceUtil();
				instance.init();
			}
		}

		return instance;
	}

	// 初始化资源文件
	private static void init() {
		if (messageSource == null) {
			synchronized (MessageSourceUtil.class) {
				messageSource = (MessageSource) ApplicationContextUtil.getApplicationContext().getBean("messageSource");
			}
		}
		
		locale = getLocale();

	}

	// 在具體使用時可以不使用getInstance，直接使用該對象即可
	public static String getMessage(String id) {
		init();
		return messageSource.getMessage(id, null, id, locale);

	}

	public static String getMessage(String id, Object[] param) {
		init();
		return messageSource.getMessage(id, param, id, locale);
	}

	public static String getMessage(String id, Object[] param, String defaultMessage) {
		init();
		return messageSource.getMessage(id, param, defaultMessage, locale);
	}

	/**
	 * 从上下文中获取定义的locale值，然后转换成java Locale对象
	 * @return
	 */
	private static Locale getLocale(){
		
		// 首先从系统上下文中获取locale
		// 如果无法获取，则从request中自行获取
		
		String c_locale = "";
		try{
			c_locale = InvocationInfoProxy.getLocale();
			
			if(c_locale == null || "".equals(c_locale)){
				HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
				
				c_locale = CookieUtil.findCookieValue(request.getCookies(), "u_locale");
				
				if(c_locale == null || "".equals(c_locale)){
					
					c_locale = CookieUtil.findCookieValue(request.getCookies(), COOKIE_PREF + "u_locale");
				}
			}

		}catch(Exception e){
			// do nothing
		}

		if(c_locale != null && !"".equals(c_locale)){
			u_locale = c_locale;
		}
		
		return LocaleUtil.toLocale(u_locale);
	}
}
