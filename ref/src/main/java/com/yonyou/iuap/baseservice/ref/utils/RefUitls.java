package com.yonyou.iuap.baseservice.ref.utils;
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

import com.yonyou.iuap.ref.model.RefUITypeEnum;

public class RefUitls {

    public static RefUITypeEnum getRefUIType(String reftype){
        int refTypeNum=Integer.valueOf(reftype);
        switch (refTypeNum){
            case 1:
                return RefUITypeEnum.RefTree;
            case 2:
                return RefUITypeEnum.RefGrid;
            case 3:
                return RefUITypeEnum.RefGridTree;
            case 4:
                return RefUITypeEnum.CommonRef;
            case 5:
                return RefUITypeEnum.RefGridTree;
            case 6:
                return RefUITypeEnum.RefGrid;
            default:
                return RefUITypeEnum.RefGrid;
        }
    }
}
