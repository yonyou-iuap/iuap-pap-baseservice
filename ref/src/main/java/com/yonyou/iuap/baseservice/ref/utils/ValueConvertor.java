package com.yonyou.iuap.baseservice.ref.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 值转换器
 *
 * @since 2017年10月31日 下午12:26:06
 * @version 1.0.0
 * @author xiadlc
 */
public class ValueConvertor {

  /**
   * 将json值按照指定类型转换为java值
   *
   * @param value json值
   * @param type 指定类型
   * @return java值
   */
  public Object convertToJavaType(Object value, Class<?> type) {
    if (value == null||"".equals(value)) {
      return value;
    }
    Object ret = value;
    if (type.equals(BigDecimal.class)) {
      ret = new BigDecimal(value.toString());
    }
    else if (type.equals(Double.class)){
    	ret = new Double(value.toString());
    }
    else if (type.equals(Integer.class) || type.equals(int.class)) {
      ret = Integer.valueOf(value.toString());
    }
    else if (type.equals(Date.class)) {
      String date = (String) value;
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      try {
        ret = format.parse(date);
      }
      catch (ParseException ex) {
        throw new RuntimeException(ex);
      }
    }
    else if (type.equals(Timestamp.class)) {
      String datetime = (String) value;
        try {
          ret = Timestamp.valueOf(datetime);
      } 
      catch (Exception ex) {
//        ExceptionUtils.wrapException(ex);
      }
    }
    else if(type.equals(String.class)) {
      String  str =(String) value;
      if(str != null) {
        str = str.trim();
        if(str.length() ==0 ) {
          str = null;
        }
      }
      ret = str;
    }
    return ret;
  }

  /**
   * 将java值转换为json值
   *
   * @param value java值
   * @return json值
   */
  public Object convertToJsonType(Object value) {
    if (value == null) {
      return value;
    }
    Class<?> type = value.getClass();
    Object ret = value;
    if (type.equals(BigDecimal.class)) {
      ret = value.toString();
    }
    else if (type.equals(Double.class)){
    	ret = value.toString();
    }
    else if (type.equals(Date.class)) {
      Date date = (Date) value;
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      ret = format.format(date);
    }
    else if (type.equals(Timestamp.class)) {
      Timestamp datetime = (Timestamp) value;
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      ret = format.format(datetime);
    }
//    else if (type.equals(TIMESTAMP.class)){
//    	TIMESTAMP datetime = (TIMESTAMP) value;
//    	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    	 try {
//    		 ret = format.format(datetime.timestampValue());
//         }
//         catch (Exception ex) {
////           ExceptionUtils.wrapException(ex);
//         }
//
//    }
    return ret;
  }

}
