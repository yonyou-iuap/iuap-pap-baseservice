package com.yonyou.iuap.baseservice.sdk.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class ResponseUtils {
	private static Logger logger= LoggerFactory.getLogger(ResponseUtils.class);

	public static Map<String,Object> map(HttpResponse response){
		try {
			String str= IOUtils.toString(response.getEntity().getContent());
			return JSON.parseObject(str,Map.class);
		} catch (UnsupportedOperationException e) {
			logger.debug(e.getMessage(),e);
		} catch (IOException e) {
			logger.debug(e.getMessage(),e);
		}
		return null;
	}

	public static JSONObject json(HttpResponse response) {
		try {
			String str= IOUtils.toString(response.getEntity().getContent());
			return JSONObject.parseObject(str);
		} catch (UnsupportedOperationException e) {
			logger.debug(e.getMessage(),e);
		} catch (IOException e) {
			logger.debug(e.getMessage(),e);
		}
		return null;
	}
	public static String text(HttpResponse response) {
		try {
			return IOUtils.toString(response.getEntity().getContent());
		} catch (UnsupportedOperationException e) {
			logger.debug(e.getMessage(),e);
		} catch (IOException e) {
			logger.debug(e.getMessage(),e);
		}
		return null;
	}
	public static JsonResponse jsonResponse(HttpResponse response) {
		try {
			String str= IOUtils.toString(response.getEntity().getContent());
			return JSONObject.parseObject(str,JsonResponse.class);
		} catch (UnsupportedOperationException e) {
			logger.debug(e.getMessage(),e);
		} catch (IOException e) {
			logger.debug(e.getMessage(),e);
		}
		return null;
	}
	public static <T> T obj(HttpResponse response, Class<T> t) {
		try {
			String str= IOUtils.toString(response.getEntity().getContent());
			return JSON.parseObject(str,t);
		} catch (UnsupportedOperationException e) {
			logger.debug(e.getMessage(),e);

		} catch (IOException e) {
			logger.debug(e.getMessage(),e);
		}
		return null;
	}
}
