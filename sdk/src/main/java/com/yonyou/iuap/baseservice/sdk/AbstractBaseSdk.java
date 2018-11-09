package com.yonyou.iuap.baseservice.sdk;
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


import com.yonyou.iuap.baseservice.sdk.adapter.IRpcAdapter;
import com.yonyou.iuap.baseservice.sdk.properties.SdkProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author jin
 */
public abstract class  AbstractBaseSdk{
    private static Logger logger = LoggerFactory.getLogger(AbstractBaseSdk.class);
    private static final String CHANNEL=".channel";
    private static final String URL=".url";

    private static final String DEFAULT_CHANNEL="with-sign";
    /**
     * key值命名规范: workbench.sdk.[模块名称].[资源名称]
     * 例如返回值为：workbench.sdk.wbalone.user
     * application.properties配置
     * workbench.sdk.user.url=http://127.0.0.1:8080/wbalone
     * workbench.sdk.user.channel=xxx ，默认值为with-sign
     * @return application.properties 中配置url的key值前缀
     */
    protected abstract String getSdkKey();
    /**
     * @return application.properties 中配置的url
     */
    public String getUrlPre(){
        String urlPreKey=getSdkKey();
        if(StringUtils.isEmpty(urlPreKey)){
            logger.error("urlPreKey不能为空:{},请重写abstract String getUrlPreKey() 方法",urlPreKey);
            return null;
        }
        String  urlPre=SdkProperties.getProperty(getUrlPreKey());
        if(StringUtils.isEmpty(urlPre)){
            logger.error("urlPre不能为空,请在application.properties中配置:{}的对应值",urlPreKey);
            return null;
        }else {
            return urlPre;
        }
    }
    public String getUrl(String urlFix){
        return getUrlPre()+urlFix;
    }


    private  String getUrlPreKey(){
        return getSdkKey()+URL;
    }


    /**
     * 远程接口调用通道
     * @return 调用通道
     */
    public  IRpcAdapter getAdapter(){
        return RpcManager.getAdapter(getChannel());
    }

    /**
     * 获取sdk通道，优先级：服务开关>sdk总开关>默认
     * @return sdk通道
     */
    private  String getChannel(){
        String channelKey=getSdkKey()+CHANNEL;
        String channel=SdkProperties.getProperty(channelKey);
        channel=StringUtils.isEmpty(channel)?SdkProperties.getSdkChannel():channel;
        return StringUtils.isEmpty(channel)?DEFAULT_CHANNEL:channel;

    }
}
