package com.yonyou.iuap.i18n.utils;

import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * 抽取已经国际化的所有中文信息
 *
 * 主要用于进行翻译
 *
 * @author wenfa
 *
 */
public class ResourceFileUtil {
	
	private static ResourceFileUtil _this = new ResourceFileUtil();
	
	private String legalityReamName = "iuap_zh_CN.properties";
//	private String legalityReamName = ".json";
	
	private ArrayList<File> files = new ArrayList<File>(0);
	
	private StringBuilder fileContent = new StringBuilder();
	
//	private Map<String, String> fileDescs = new HashMap<String, String>(0);

	private OrderedProperties prop = new OrderedProperties();

	private Properties corpus = new Properties();
	
	private String path;
	
	private ResourceFileUtil(){
		
	}
	
	/**
	 * 属性文件初始化
	 *
	 * @param path
	 * @return  void
	 */
    public static void init(String path){
    	if (null == _this.path) {
    		_this.path = path;
    		_this.loadFiles();
    		_this.initFileContent();
    		_this.initFileDescs();
    		_this.files.clear();
			_this.initCorpus();
			_this.macherCorpus();
    		_this.writeResourceFile();
    		_this.fileContent = null;
//    		_this.fileDescs = null;
    	}
	}
	
	/**
	 * 加载属性文件
	 * @return void
	 */
	private void loadFiles(){
		_this.getAllFileByFile(new File(_this.path));
	}

	/**
	 * 
	 * 递归获取所有File对象,包含子文件夹文件
	 * @param file 当前File
	 * @return void
	 * @throws Exception
	 */
	private void getAllFileByFile(File file){
		if(null != file){
			if(file.isFile()){
				if (_this.validateFileName(file)) {
					_this.files.add(file);
				}
			}
			if(file.isDirectory()){
				File[] fils = file.listFiles();
				if(null != fils){
					for(File tempFile:fils){
						_this.getAllFileByFile(tempFile);
					}
				}
			}
		}
	}
	
	/**
	 *  
	 * 校验文件名
	 * @param file 当前File
	 * @return boolean
	 * @throws Exception
	 */
	private boolean validateFileName(File file){
		if (file.getName().contains(_this.legalityReamName)) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * 初始化语料库
	 *
	 * @return void
	 */
	private void initCorpus() {
		try {
			_this.corpus.load(new InputStreamReader(new FileInputStream(new File(_this.path + File.separator + "corpus-en.properties")), "UTF-8"));
		}catch (Exception e){
			// do nothing
		}
	}
	
	/**
	 *  
	 * 初始化属性文件内容
	 * @return void 
	 */
	private void initFileContent() {
		for (File file:_this.files) {
			if (null != file && file.exists()) {
				FileReader fileReader = null;
				BufferedReader bufferedReader = null;
				try {
					fileReader = new FileReader(file);
					bufferedReader = new BufferedReader(fileReader);
		            String temp = bufferedReader.readLine();
		            while(null != temp) {
		            	_this.fileContent.append(temp).append("\n");
		            	temp = bufferedReader.readLine();
		            }
	            } catch (Exception e) {
	            	LogFactory.getLog(ResourceFileUtil.class).error(e);
	            } finally {
	            	if (null != bufferedReader) {
	  	        	   try {
			                bufferedReader.close();
		                } catch (IOException e) {
		                	LogFactory.getLog(ResourceFileUtil.class).error(e);
		                }
	  	           	}
	  	           	if (null != fileReader) {
	  	        	   try {
	  	        		   fileReader.close();
		                } catch (IOException e) {
		                	LogFactory.getLog(ResourceFileUtil.class).error(e);
		                }
	  	           	}
	            }
			}
		}
	}

	/**
	 * 匹配语料库
	 */
	private void macherCorpus() {
		for (String key : _this.prop.stringPropertyNames()) {
			if (_this.corpus.containsKey(_this.prop.getProperty(key))) {
				prop.setProperty(key, _this.corpus.getProperty(_this.prop.getProperty(key)));
			}else{
//				prop.setProperty(key, "");
			}
		}
	}
	
	
	/**
	 * 
	 * 获取所有的资源，剔除重复的资源id
	 *
	 * @return void
	 */
	private void initFileDescs() {

		for (String str:delSpecialChar(_this.fileContent.toString()).split("\n")) {
			if (null != str && !"".equals(str.trim())) {
				if (_this.isDescRow(str)) {
					continue;
				}
				else if (_this.isValueRow(str)) {
					String[] value = str.split("=");
					if (value.length == 2 && null != value[1] && !"".equals(value[1])) {
						if (prop.containsKey(value[0]) && !prop.get(value[0]).equals(value[1])){
							System.out.println(value[0]);
						}else{
							prop.setProperty(value[0], value[1]);
						}
					}
				}
			}
		}
	}

	/**
	 * 删除\n、~、等特殊字符
	 *
	 * @param str
	 * @return
	 */
	private String delSpecialChar(String str){
		return str.replaceAll(Matcher.quoteReplacement("\\n"),"")
				.replaceAll("~","")
				.replaceAll("&nbsp;","")
				.replaceAll(" ","")
				.replaceAll(Matcher.quoteReplacement("\\"),"")
				.replaceAll(Matcher.quoteReplacement(":"),"")
				.replaceAll(Matcher.quoteReplacement("!"),"")
				.replaceAll(Matcher.quoteReplacement("："),"")
				.replaceAll(Matcher.quoteReplacement("！"),"");
	}
	/**
	 * 
	 * 判断该行是否有描述信息
	 * @param str 当前该行字符串
	 * @return boolean
	 *
	 */
	private boolean isDescRow(String str) {
		return str.trim().substring(0, 1).equals("#");
	}
	
	
	/**
	 * 
	 * 判断该行是否有注释信息
	 * @param str 当前该行字符串
	 * @return boolean
	 */
	private boolean isValueRow(String str) {
		return !str.trim().substring(0, 1).equals("#") && str.trim().contains("=") && str.trim().indexOf("=") == str.trim().lastIndexOf("=");
	}


	/**
	 * 将抽取出来的资源写入资源文件中
	 * 做英文资源文件
	 *
	 */
	public void writeResourceFile(){

		File file = new File(_this.path + File.separator + "iuap_all.properties");

		// 为了保证资源的顺序，采用LinkedHashSet存储
//		OrderedProperties prop = new OrderedProperties();

		BufferedWriter output = null;

		try {
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			// 设置属性值
//			Iterator<Entry<String, String>> descs = _this.fileDescs.entrySet().iterator();
//			while(descs.hasNext()){
//				Entry<String, String> desc = descs.next();
//				prop.setProperty(desc.getKey(), desc.getValue());
//			}

			// 保存属性值
			_this.prop.store(output, "create the resource file");

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {

//	    init("D:\\workspace\\iuap_apportal\\iweb_apportal\\workbench\\wbalone\\target\\workbench\\locales");

	    init("/Users/yanyong/workspace/java/yonyou/iweb_apportal/workbench/wbalone/target/workbench/locales");

//	    System.out.println(_this.fileDescs);
	    
    }
	
	
	/**
	 * 
	 * 得到描述信息
	 * @param key
	 * @return
	 */
	public static String getDesc(String key) {
//		return _this.fileDescs.get(key);
		return null;
	}
	
	
	/**
	 * 
	 * 文件编码
	 * @param theString
	 * @return String
	 */
	private String decodeUnicode(String theString) {    
		       char aChar;    
		       int len = theString.length();    
		       StringBuffer outBuffer = new StringBuffer(len);    
		       for (int x = 0; x < len;) {    
		        aChar = theString.charAt(x++);    
		        if (aChar == '\\') {    
		         aChar = theString.charAt(x++);    
		         if (aChar == 'u') {    
		          int value = 0;    
		          for (int i = 0; i < 4; i++) {    
		           aChar = theString.charAt(x++);    
		           switch (aChar) {    
		           case '0':    
		           case '1':    
		           case '2':    
		           case '3':    
		           case '4':    
		           case '5':    
		          case '6':    
		           case '7':    
		           case '8':    
		           case '9':    
		            value = (value << 4) + aChar - '0';    
		            break;    
		           case 'a':    
		           case 'b':    
		           case 'c':    
		           case 'd':    
		           case 'e':    
		           case 'f':    
		            value = (value << 4) + 10 + aChar - 'a';    
		           break;    
		           case 'A':    
		           case 'B':    
		           case 'C':    
		           case 'D':    
		           case 'E':    
		           case 'F':    
		            value = (value << 4) + 10 + aChar - 'A';    
		            break;    
		           default:    
		            throw new IllegalArgumentException(    
		              "Malformed   \\uxxxx   encoding.");    
		           }    
		         }    
		          outBuffer.append((char) value);    
		         } else {    
		          if (aChar == 't')    
		           aChar = '\t';    
		          else if (aChar == 'r')    
		           aChar = '\r';    
		          else if (aChar == 'n')    
		           aChar = '\n';    
		          else if (aChar == 'f')    
		           aChar = '\f';    
		          outBuffer.append(aChar);    
		         }    
		        } else   
		        outBuffer.append(aChar);    
		       }    
		       return outBuffer.toString();    
		      }    

}