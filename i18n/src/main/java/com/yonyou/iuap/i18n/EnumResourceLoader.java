package com.yonyou.iuap.i18n;


import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.*;

/**
 * 加载枚举资源文件
 *
 * 主要用于特定场合的资源加载，比如数据字典等
 *
 * @author wenfa
 */
public class EnumResourceLoader extends ResourceLoader implements InitializingBean {

    public static final String BEAN_NAME = "enumServiceImpl";
    public static final String SPLIT = ":";
    private String dir;
    private String[] includes;
    private String[] excludes;
    private Map<String, Map<String, String>> enumsHash;
    private Map valuesHash;
    private boolean isInitialized;
    private Map<String, String> keyValue;

    public EnumResourceLoader() {
        this.enumsHash = new HashMap();
        this.valuesHash = new HashMap();
        this.keyValue = new HashMap();
        this.isInitialized = false;
    }

    /**
     * 加载枚举定义文件
     */
    private synchronized void init() {
        if (this.isInitialized)
            return;
        try {
            loadEnums();
            this.isInitialized = true;
        } catch (Throwable e) {
        }
    }

    public void reloadEnums() {
        this.enumsHash = new HashMap();
        this.valuesHash = new HashMap();
        this.keyValue = new HashMap();
        this.isInitialized = false;
        init();
    }

    private void loadEnums() throws IOException {
        Properties result = new Properties();
        this.dir = this.dir;
        loadProperties(result);
        processProps(result);
    }

    private void processProps(Properties props) {
        for (Enumeration keys = props.keys(); keys.hasMoreElements(); ) {
            String enumName = (String) keys.nextElement();
            String valstring = props.getProperty(enumName);
            if (!this.enumsHash.containsKey(enumName)) {
                Map enums = new LinkedHashMap();
                Map descs = new LinkedHashMap();
                for (StringTokenizer st = new StringTokenizer(valstring, ","); st.hasMoreTokens(); ) {
                    String string = st.nextToken();
                    String[] strings = string.split(":");
                    if ((strings == null) || (strings.length != 3)) {
                        continue;
                    }
                    enums.put(strings[0], strings[0] + ":" + strings[1] + ":" + strings[2]);
                    descs.put(strings[1], strings[2]);
                }

                this.enumsHash.put(enumName, enums);
                this.valuesHash.put(enumName, descs);
            } else {
            }

            if (!this.keyValue.containsKey(enumName))
                this.keyValue.put(enumName, valstring);
        }
    }

    public String getValueByConstant(String enumName, String constant) {
        if (this.enumsHash.containsKey(enumName)) {
            String[] str = ((String) ((Map) this.enumsHash.get(enumName)).get(constant)).split(":");
            String value = "";

            if (str.length == 3)
                value = str[1];
            else if (str.length == 2) {
                value = str[0];
            }
            return value;
        }

        throw new RuntimeException("Undefined enum[" + enumName + "]");
    }

    public List<String> getEnum(String enumName) {
        if (this.enumsHash.containsKey(enumName)) {
            return convertMapToList((Map) this.enumsHash.get(enumName));
        }

        throw new RuntimeException("Undefined enum[" + enumName + "]");
    }

    public List<String> getEnumValue(String enumName) {
        if (this.enumsHash.containsKey(enumName)) {
            return convertMapToListValue((Map) this.enumsHash.get(enumName));
        }

        throw new RuntimeException("Undefined enum[" + enumName + "]");
    }

    private List<String> convertMapToListValue(Map<String, String> map) {
        List list = new ArrayList();
        Object[] o = ((HashMap) map).keySet().toArray();
        for (int i = 0; i <= o.length - 1; i++) {
            if (map.containsKey(String.valueOf(o[i])))
                list.add(map.get(String.valueOf(o[i])));
            else
                list.add(map.get(""));
        }
        return list;
    }

    public String getDescByConstant(String enumName, String constant) {
        if (this.enumsHash.containsKey(enumName)) {
            return ((String) ((Map) this.enumsHash.get(enumName)).get(constant)).split(":")[2];
        }

        throw new RuntimeException("Undefined enum[" + enumName + "]");
    }

    public Map<String, String> getValueDesc(String enumName) {
        if (this.enumsHash.containsKey(enumName)) {
            Map resultEnumsHash = getEnumsHashValuesDesc(enumName);
            return resultEnumsHash;
        }

        throw new RuntimeException("Undefined enum[" + enumName + "]");
    }

    private Map<String, String> getEnumsHashValuesDesc(String enumName) {
        Map map = new HashMap();
        Map tempMap = (Map) this.enumsHash.get(enumName);
        Iterator keys = ((HashMap) tempMap).keySet().iterator();
        while (keys.hasNext()) {
            String key = ((String) keys.next()).toString();
            String value = tempMap.get(key).toString();
            value = value.split(":")[1] + ":" + value.split(":")[2];
            map.put(key, value);
        }
        return map;
    }

    public String getDescByValue(String enumName, String value) {
        if (this.valuesHash.containsKey(enumName)) {
            return (String) ((Map) this.valuesHash.get(enumName)).get(value);
        }
        throw new RuntimeException("Undefined enum[" + enumName + "]");
    }

    public String getDescByValue(String enumName, Character value) {
        if (this.valuesHash.containsKey(enumName)) {
            return (String) ((Map) this.valuesHash.get(enumName)).get(String.valueOf(value));
        }
        throw new RuntimeException("Undefined enum[" + enumName + "]");
    }

    public void afterPropertiesSet()
            throws Exception {
        init();
        if (this.enumsHash.size() == 0)
            throw new Exception("enumsHash is empty! ");
    }

    public String getDir() {
        return this.dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String[] getIncludes() {
        return this.includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return this.excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public String getValueByKey(String key) {
        return (String) this.keyValue.get(key);
    }

    private static List convertMapToList(Map map) {
        List list = new ArrayList();
        Object[] o = ((HashMap) map).keySet().toArray();
        for (int i = 0; i <= o.length - 1; i++)
            if (map.containsKey(String.valueOf(o[i]))) {
                String[] value = String.valueOf(map.get(String.valueOf(o[i]))).split(":");
                list.add(value[1] + ":" + value[2]);
            } else {
                list.add(map.get(""));
            }
        return list;
    }
}