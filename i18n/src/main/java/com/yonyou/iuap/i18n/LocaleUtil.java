package com.yonyou.iuap.i18n;

import java.util.Locale;

/**
 * 将cookie中的locale转化为java识别的Locale
 * 
 * @author wenfa
 *
 */
public class LocaleUtil
{
    private final static Locale DEFAULT_LOCALE = Locale.ENGLISH;
    
    public final static String IETF_SEPARATOR = "-";
    
    public final static String SEPARATOR = "_";
    
    public final static String EMPTY_STRING = ""; 
     
    
    public static Locale toLocale(String language) {
    	
        if(language!=null && !"".equals(language)) {
        	
        	Locale locale = langToLocale(language, SEPARATOR);
        	
        	if("".equals(locale.toString())){
        		locale = langToLocale(language, IETF_SEPARATOR);
        	}
        	
        	if("".equals(locale.toString())){
        		return DEFAULT_LOCALE;
        	}
        	return locale;
        
        }
        return DEFAULT_LOCALE;
    }
      
    public static Locale langToLocale(String lang , String separator) {
        
        String language = EMPTY_STRING;
        String country =  EMPTY_STRING;
        String variant =  EMPTY_STRING;

        int i1 = lang.indexOf(separator);
        if ( i1 < 0 ) {
            language = lang;
        } else {
            language = lang.substring(0, i1);
            ++i1;
            int i2 = lang.indexOf(separator, i1);
            if (i2 < 0) {
                country = lang.substring(i1);
            } else {
                country = lang.substring(i1, i2);
                variant = lang.substring(i2+1);
            }
        }
        
        if(language.length() == 2) {
            language = language.toLowerCase();
        }else {
            language = EMPTY_STRING;
        }
        
        if(country.length() == 2) {
            country = country.toUpperCase();
        } else {
            country = EMPTY_STRING;
        }
        
        if( (variant.length() > 0) && ((language.length() == 2) ||(country.length() == 2)) ) {
            variant = variant.toUpperCase();
        } else {
            variant = EMPTY_STRING;
        }
             
        return new Locale(language, country, variant);
    }
    
    public static void main(String[] args){
    	
    	System.out.println(toLocale("en-Us-test"));
    	
    }
}
