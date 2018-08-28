package com.yonyou.iuap.i18n;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 匹配所有相关的资源文件
 *
 * @author wenfan
 */
public class PathMatchingResourcePatternResolverEx extends PathMatchingResourcePatternResolver {

    static Log logger = LogFactory.getLog(PathMatchingResourcePatternResolverEx.class);

    private DefaultResourceLoader resourceLoader = new DefaultResourceLoader();


    public Resource[] getResourcesEx(String pex, String jarNamePex, String regex) throws IOException {
        return findPathMatchingResources(pex, null, regex);
    }

    /**
     *
     * @param pex
     * @param jarNamePex
     * @param regex
     * @return
     * @throws IOException
     */
    protected Resource[] findPathMatchingResources(String pex, String jarNamePex, String regex) throws IOException {
        if ((jarNamePex == null) || ("".equals(jarNamePex))) jarNamePex = "*";
        String classRootDirPath = determineRootDir("");
        logger.info("classRootDirPath" + classRootDirPath);
        String libRootDirPath = determineRootDir("");
        logger.info("libRootDirPath" + libRootDirPath);
        Set resultLib = new HashSet();
        Resource[] classRootDirResources = getResources(pex);

        resultLib.addAll(doFindMatchingFileSystemResources(new File(libRootDirPath), jarNamePex));
        Resource[] libRootDirResources = (Resource[]) resultLib.toArray(new Resource[resultLib.size()]);
        Set result = new HashSet();
        logger.info("classRootDirResources.length=" + classRootDirResources.length);
        for (int i = 0; i < classRootDirResources.length; i++) {
            File fileName = classRootDirResources[i].getFile();
            String path = null;
            if (fileName.isDirectory()) {
                path = fileName.getAbsolutePath();
            } else
                path = fileName.getParent();
            logger.info(doFindMatchingFileSystemResources(new File(path), regex));
            try {
                result.addAll(doFindMatchingFileSystemResources(new File(path), regex));
            } catch (Exception localException) {
            }
        }

        logger.info("libRootDirResources.length" + libRootDirResources.length);
        for (int i = 0; i < libRootDirResources.length; i++) {
            logger.info("加载jar文件中的资源文件" + libRootDirResources[i].getURI().toString());
            try {
                if (libRootDirResources[i].getURI().toString().lastIndexOf(".jar") > 0) {
                    result.addAll(doFindPathMatchingJarResources(libRootDirResources[i], regex));
                }
            } catch (Exception localException1) {
            }
        }


        return (Resource[]) result.toArray(new Resource[result.size()]);
    }

    /**
     * 匹配jar包中的资源文件
     *
     * @param rootDirResource
     * @param subPattern
     * @return
     * @throws IOException
     */
    protected Set doFindPathMatchingJarResources(Resource rootDirResource, String subPattern) throws IOException {
        URLConnection con = rootDirResource.getURL().openConnection();
        JarFile jarFile = null;
        String jarFileUrl = null;
        String rootEntryPath = null;
        boolean newJarFile = false;

        if ((con instanceof JarURLConnection)) {
            JarURLConnection jarCon = (JarURLConnection) con;
            jarCon.setUseCaches(false);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = jarEntry != null ? jarEntry.getName() : "";
        } else {
            String urlFile = rootDirResource.getURL().getFile();
            int separatorIndex = urlFile.indexOf("!/");
            if (separatorIndex != -1) {
                jarFileUrl = urlFile.substring(0, separatorIndex);
                rootEntryPath = urlFile.substring(separatorIndex + "!/".length());
                jarFile = getJarFile(jarFileUrl);
            } else {
                jarFile = new JarFile(urlFile);
                jarFileUrl = urlFile;
                rootEntryPath = "";
            }
            newJarFile = true;
        }
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Looking for matching resources in jar file [" + jarFileUrl + "]");
            }
            if ((!"".equals(rootEntryPath)) && (!rootEntryPath.endsWith("/"))) {

                rootEntryPath = rootEntryPath + "/";
            }
            Set result = new LinkedHashSet(8);
            for (Enumeration entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (getPathMatcher().match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            return result;

        } finally {
            if (newJarFile) {
                jarFile.close();
            }
        }
    }
}
