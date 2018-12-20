package com.yonyou.iuap.baseservice.statistics.support;

import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 通过全局性扫描Entity及注解@StatisticField，得到整体的统计模型，后续可自动拼凑sql
 */
@Component
@Lazy(value = false)
public class StatModelResolver {
    private static Logger logger = LoggerFactory.getLogger(StatModelResolver.class);
    private static Map<String, StatModel> modelCache;
    private static final String SCAN_ROOT ="com,cn,org,net,io,de";//默认的模型扫描范围

    /**
     * 优化性能，异步方式扫描
     */
    @PostConstruct
    public void init() {
        Thread thread = new Thread(){

          @Override
          public void run(){
              modelCache = new CaseInsensitiveMap<>();
              String[] basePackages = SCAN_ROOT.split(",");
              Arrays.stream(basePackages).parallel().forEach(
                      (basePackage)->{
                          Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Table.class);
                          for (Class clz : classes) {
                              logger.debug("Resolving model class:" + clz);
                              Field[] fields = ReflectUtil.getFields(clz);
                              StatModel statModel = null;

                              if (clz.getAnnotation(Entity.class)!=null){
                                  statModel =   new StatModel();
                              }
                              for (Field f : fields) {
                                  if (f.getAnnotation(StatisticsField.class) != null) {
                                      statModel = statModel == null ? new StatModel() : statModel;
                                      statModel.getStatColumnsFields().put(FieldUtil.getColumnName(f), f.getName());
                                      statModel.getStatColumnsFunctions().put(FieldUtil.getColumnName(f), f.getAnnotation(StatisticsField.class).functions());
                                  }
                              }
                              if (statModel != null) {
                                  statModel.setCode(clz.getSimpleName());
                                  statModel.setmClass(clz);
                                  statModel.setTableName(EntityUtil.getTableName(clz));
                                  logger.info("StatModelResolver caching model:" + statModel);
                                  synchronized (this){
                                      modelCache.put(clz.getSimpleName(), statModel);
                                  }
                              }
                          }


                      }
              );
              logger.info("StatModelResolver cached model:" + modelCache);
          }

        };
        thread.start();


    }

    /**
     * 从缓存中加载统计模型信息
     *
     * @param modelCode
     * @return
     */

    public static StatModel getStatModel(String modelCode) {
        if (modelCache == null) {
            inst().init();
        }
        return modelCache.get(modelCode);
    }

    /******************************************/
    private static class Inner {
        private static StatModelResolver r = new StatModelResolver();
    }

    private static StatModelResolver inst() {
        return StatModelResolver.Inner.r;
    }

}
