package com.yonyou.iuap.i18n;
/*     */

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * 加载资源文件
 *
 * @author wenfa
 */
public class ResourceLoader {

    protected String dir;

    private String fileEncoding;
    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private boolean ignoreResourceNotFound = false;
    public static final String XML_FILE_EXTENSION = ".xml";
    public static final String Prop_FILE_EXTENSION = ".properties";
    public static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
    protected ResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();


    protected Set resolveResources() {
        if ((this.dir == null) || (this.dir.trim().equals("")))
            return null;
        String[] dirs = StringUtils.tokenizeToStringArray(this.dir, ",; \t\n");
        Set resources = new HashSet();
        for (int i = 0; i < dirs.length; i++) {
            try {
                CollectionUtils.addAll(resources, this.pathMatchingResourcePatternResolver.getResources(dirs[i]));
            } catch (IOException ex) {
                throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + dirs[i] + "]", ex);
            }
        }
        return resources;
    }

    /**
     * 加载资源
     *
     * @param props
     * @throws IOException
     */
    protected void loadProperties(Properties props) throws IOException {

        Set resources = resolveResources();

        if ((resources != null) && (resources.size() != 0)) {

            Iterator iterator = resources.iterator();

            while (iterator.hasNext()) {

                Resource location = (Resource) iterator.next();

                InputStream is = null;
                try {

                    is = location.getInputStream();

                    if (location.getFilename().endsWith(".xml")) {

                        this.propertiesPersister.loadFromXml(props, is);
                    } else if (this.fileEncoding != null) {

                        this.propertiesPersister.load(props, new InputStreamReader(is, this.fileEncoding));
                    } else {

                        this.propertiesPersister.load(props, is);
                    }
                } catch (IOException ex) {

                } finally {

                    if (is != null)
                        is.close();
                }
            }
        }
    }

    public String getDir() {

        return this.dir;
    }

    public void setDir(String dir) {

        this.dir = dir;
    }
}
