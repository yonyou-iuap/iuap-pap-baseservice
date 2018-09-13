package com.yonyou.iuap.baseservice.bpm.utils;

import com.alibaba.fastjson.JSONObject;

public class BpmExUtil {
	
	public static final int BPM_STATE_NOTSTART = 0;				//未启动
	public static final int BPM_STATE_START = 1;				//已启动
	public static final int BPM_STATE_RUNNING = 2;				//流程中
	public static final int BPM_STATE_FINISH = 3;				//正常结束
	public static final int BPM_STATE_ABEND = 4;				//异常终止
	
	
	private BpmExUtil() {}
	
	public static BpmExUtil inst() {
		return Inner.INST;
	}
	
	public boolean isSuccess4CheckSubmit(JSONObject result) {
		return result!=null && "success".equals(result.get("success"));
	}
	
	public boolean isSuccess4Submit(JSONObject result) {
		return result!=null && "success".equals(result.get("flag"));
	}
	
	public boolean isSuccess4Revoke(JSONObject result) {
		return result!=null && "success".equals(result.getString("success"));
	}
	
	/*************************************************/
	private static class Inner{
		private static BpmExUtil INST = new BpmExUtil();
	} 

}
