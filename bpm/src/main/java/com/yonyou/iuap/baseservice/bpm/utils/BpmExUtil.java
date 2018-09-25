package com.yonyou.iuap.baseservice.bpm.utils;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.bpm.util.BPMUtil;

public class BpmExUtil {
	
	public static final int BPM_STATE_NOTSTART = BPMUtil.BPM_BILLSTATUS_NOTSTART;		//未启动0
	public static final int BPM_STATE_START = BPMUtil.BPM_BILLSTATUS_RUN;				//已启动1
	public static final int BPM_STATE_RUNNING = BPMUtil.BPM_BILLSTATUS_RUN;				//流程中1
	public static final int BPM_STATE_FINISH = BPMUtil.BPM_BILLSTATUS_END;				//正常结束2
	public static final int BPM_STATE_ABEND = BPMUtil.BPM_BILLSTATUS_TERMINATION;		//异常终止3
	
	
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

	public boolean isSuccess(JSONObject resultJsonObject) {
		return resultJsonObject != null && resultJsonObject.get("flag").equals("success");
	}

	public boolean isFail(JSONObject resultJsonObject) {
		return resultJsonObject != null && resultJsonObject.get("flag").equals("fail");
	}

	private static class Inner{
		private static BpmExUtil INST = new BpmExUtil();
	} 

}
