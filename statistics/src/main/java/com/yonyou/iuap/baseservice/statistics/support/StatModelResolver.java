package com.yonyou.iuap.baseservice.statistics.support;

import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.EntityUtil;
import com.yonyou.iuap.baseservice.persistence.mybatis.ext.utils.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 通过全局性扫描Entity及注解@StatisticField，得到整体的统计模型，后续可自动拼凑sql
 */
@Component
@Lazy(value = false)
public class StatModelResolver {
    private static Logger logger = LoggerFactory.getLogger(StatModelResolver.class);
    private static Map<String, StatModel> modelCache;

    /**
     * 优化性能，异步方式扫描
     */
    @PostConstruct
    public void init() {
        Thread thread = new Thread(){

          @Override
          public void run(){
              modelCache = new CaseInsensitiveMap<>();
              String basePackages = "com"; //TODO 改成更广泛的扫描范围
              Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackages, Table.class);

              for (Class clz : classes) {
                  logger.debug("Resolving model class:" + clz);
                  Field[] fields = ReflectUtil.getFields(clz);
                  StatModel statModel = null;
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
                      modelCache.put(clz.getSimpleName(), statModel);
                  }
              }

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
