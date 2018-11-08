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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author jin
 */
public  class RpcManager {
    private static Logger                            logger      = LoggerFactory.getLogger(RpcManager.class);
    private static         Map<String , IRpcAdapter> rpcAdapters =new HashMap<>();
    private static volatile RpcManager               rpcManager  = null;

    private static RpcManager getInstance(){
        if(rpcManager == null){
            synchronized (RpcManager.class){
                if(rpcManager == null){
                    rpcManager = new RpcManager();
                }
            }
        }
        return rpcManager;
    }

    private RpcManager(){
        ServiceLoader<IRpcAdapter> loader =ServiceLoader.load(IRpcAdapter.class);
        Iterator<IRpcAdapter>      it     =loader.iterator();
        while (it.hasNext()){
            IRpcAdapter rpcAdapter =it.next();
            if(StringUtils.isEmpty(rpcAdapter.getSupport())){
                throw  new RuntimeException("rpcAdapter support 方法返回值不可为空");
            }else if(rpcAdapters.containsKey(rpcAdapter.getSupport())){
                throw  new RuntimeException("rpcAdapter support 方法返回值不可重复,support值:["+rpcAdapter.getSupport()+"]重复");
            }else{
                rpcAdapters.put(rpcAdapter.getSupport(),rpcAdapter);
            }
        }
        logger.debug(rpcAdapters.toString());

    }
    public static IRpcAdapter getAdapter(String support){
        RpcManager.getInstance();
        return rpcAdapters.get(support);
    }

}
