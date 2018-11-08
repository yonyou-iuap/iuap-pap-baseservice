package com.yonyou.iuap.baseservice.sdk.properties;
/*
                       _ooOoo_
                      o8888888o
                      88" . "88
                      (| -_- |)
                      O\  =  /O
                   ____/`---'\____
                 .'  \\|     |//  `.
                /  \\|||  :  |||//  \
               /  _||||| -:- |||||-  \
               |   | \\\  -  /// |   |
               | \_|  ''\---/''  |   |
               \  .-\__  `-`  ___/-. /
             ___`. .'  /--.--\  `. . __
          ."" '<  `.___\_<|>_/___.'  >'"".
         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
         \  \ `-.   \_ __\ /__ _/   .-` /  /
    ======`-.____`-.___\_____/___.-`____.-'======
                       `=---='
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
             佛祖保佑       永无BUG
*/


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author jhb
 */
public class SdkProperties {

    private static Logger logger= LoggerFactory.getLogger(SdkProperties.class);

    private static String authfilePath;

    private static String contextName;

    private static Properties properties;

    private static String sdkChannel;
    static{
        properties =new Properties();
        try {
            properties.load(SdkProperties.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            logger.error("加载属性配置文件application.properties文件失败");
            logger.error(e.getMessage(),e);
        }
        authfilePath=properties.getProperty("authfile.path")==null?"/iuap/authfile.txt":properties.getProperty("authfile.path");
        contextName=properties.getProperty("context.name")==null?"":properties.getProperty("context.name");
        sdkChannel=properties.getProperty("workbench.sdk.channel")==null?"":properties.getProperty("workbench.sdk.channel");
    }

    

    public static String getAuthfilePath() {
        return authfilePath;
    }

    public static void setAuthfilePath(String authfilePath) {
        authfilePath = authfilePath;
    }

    public static String getContextName() {
        return contextName;
    }

    public static void setContextName(String contextName) {
        contextName = contextName;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getSdkChannel() {
        return sdkChannel;
    }

    public static void setSdkChannel(String sdkChannel) {
        SdkProperties.sdkChannel = sdkChannel;
    }
}
