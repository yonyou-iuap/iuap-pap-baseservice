package com.yonyou.iuap.baseservice.controller.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.util.StrUtil;

@SuppressWarnings("all")
public class DownloadUtil {
	
	private static Logger log = LoggerFactory.getLogger(DownloadUtil.class);

	/**
	 * 下载本地文件
	 * @param response
	 * @param filepath
	 * @param displayName
	 */
	public static void downloadLocal(HttpServletResponse response, String filepath, String displayName) {
		downloadLocal(response, filepath, displayName, "application/octet-stream");
	}
	
	/**
	 * 下载本地文件
	 * @param response
	 * @param filepath
	 * @param displayName
	 * @param contentType
	 */
	public static void downloadLocal(HttpServletResponse response, String filepath, 
										String displayName, String contentType) {
        try {
            File file = new File(filepath);
            String fileName = displayName;
            if(StrUtil.isBlankIfStr(fileName)) {
            	fileName = file.getName();
            }
            //取得文件的后缀名。
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
 
            //以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(filepath));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            
            //清空response
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(fileName, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            response.setContentType(contentType);
            OutputStream ros = new BufferedOutputStream(response.getOutputStream());
            ros.write(buffer);
            ros.flush();
            ros.close();
        } catch (Exception exp) {
        	log.error("下载文件出错：", exp);
        	throw new RuntimeException(exp);
        }
	}
	
	/**
	 * 下载准备工作
	 * @param response
	 * @param displayName
	 */
	public static void prepare4Download(HttpServletResponse response, String fileName) {
		try {
			response.reset();
	        response.addHeader("Content-Disposition", "attachment;filename="
	        						+ URLEncoder.encode(fileName, "UTF-8"));
	        response.setContentType("application/octet-stream");
		} catch (Exception exp) {
        	log.error("下载文件出错：", exp);
        	throw new RuntimeException(exp);
        }
	}
	
	/**
	 * 下载准备工作
	 * @param response
	 * @param displayName
	 */
	public static void prepare4Download(HttpServletResponse response, String fileName, String contentType) {
		try {
			response.reset();
	        response.addHeader("Content-Disposition", "attachment;filename="
	        						+ URLEncoder.encode(fileName, "UTF-8"));
	        response.setContentType(contentType);
		} catch (Exception exp) {
        	log.error("设置response header出错：", exp);
        	throw new RuntimeException(exp);
        }
	}
	
}