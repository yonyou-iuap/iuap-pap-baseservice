package com.yonyou.iuap.baseservice.ref.service;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.RefParamVO;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.utils.RefXMLParse;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.pap.base.ref.utils.RefIdToNameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 远程参照反写服务,作为可插拔ServiceFeature#REMOTE_REFERENCE特性的默认实现，用于多参照微服务之间RPC调用，解决id转name问题，继续依赖Reference注解
 * <br>
 * 数据库集中存储式请参考com.yonyou.iuap.pap.base.ref.service.RefBaseCommonService
 *
 * @author leon
 * @date 2018-12-11
 * @see com.yonyou.iuap.pap.base.ref.service.RefBaseCommonService
 * @see com.yonyou.iuap.baseservice.entity.annotation.Reference
 */
@SuppressWarnings("ALL")
@Service
public class RefRemoteService<T extends Model> implements QueryFeatureExtension<T> {
    private static Logger logger = LoggerFactory.getLogger(RefRemoteService.class);
    private Class modelClass;

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams, Class modelClass) {
        this.modelClass = modelClass;// 加载到业务实体的class，后续有用
        return searchParams;
    }

    @Override
    public List<T> afterListQuery(List<T> list) {
        try{
            return fillListWithRef(list);
        }catch (Exception e){
            logger.error("refField reflect error!",e);
        }

        return list;
    }

    /**
     * 参照数据加载<p>根据Reference
     * 中定义的参照参数,将参照表中检索出来的值反写到对应的entity指定得属性中,直接返回给前端,省却跨库join的麻烦
     *
     * @param list 未装填参照的原始list
     * @return 重新装填后的结果
     * @see com.yonyou.iuap.baseservice.entity.annotation.Reference
     */
    public List<T> fillListWithRef(List<T> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        Map<String, ReferenceCache> allCache = new HashMap<>(); //所有缓存的临时变量,key为refCode
        /**
         * @Step 1
         * 解析参照配置(仅处理远程参照服务),获取参照字段id集合,用于后续参照远程调用
         */
        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field field : fields) {
            Reference ref = field.getAnnotation(Reference.class);
            if (null != ref && !allCache.containsKey(ref.code())) {
                RefParamVO refParamVO = RefXMLParse.getInstance().getReParamConfig(ref.code());
                if (refParamVO == null) { //ref.xml中解析不到的一律视为远程参照
                    logger.debug("caching remote Reference:" + ref.code());
                    ReferenceCache cache = new ReferenceCache(ref);
                    cache.getFieldCache().add(field);
                    allCache.put(ref.code(), cache);
                }
            }
        }
        for (Object entity : list) {
            for (String refCode : allCache.keySet()) {
                for (Field field : allCache.get(refCode).getFieldCache()) {
                    Object refIds = ReflectUtil.getFieldValue(entity, field);
                    if (null != refIds) {
                        String[] fieldIds = refIds.toString().split(",");//兼容参照多选
                        allCache.get(refCode).getIdCache().addAll(Arrays.asList(fieldIds));
                        allCache.get(refCode).cacheRefIdsInEntityField(entity.hashCode() +""+ field.hashCode(), fieldIds); //缓存list里每条数据没个field里的参照ids
                    }
                }
            }

        }

        /**
         * @Step 2解析参照配置, 一次按需(idCache)加载参照数据
         */
        for (String refCode : allCache.keySet()) {
            List<Map<String, Object>> refContents = null;
            try {
                refContents = RefIdToNameUtil.convertIdToName(refCode, allCache.get(refCode).getIdCache());//调用pap——base——ref的工具类进行远程调用
            } catch (Exception e) {
                logger.error("remote ref-id2name service calling error：" + refCode, e);
            }
            if (null != refContents && refContents.size() > 0) {
                allCache.get(refCode).setRefDataCache(refContents);//将所有参照数据集和field的关系缓存起来后续使用
                allCache.get(refCode).putRefDataInOrderOfFieldIds();//按缓存分组整理这个refContents，备用
            }
        }

        /**
         * @Step 3 逐条遍历业务结果集,向entity参照指定属性写入参照查询结果值
         */
        for (Object entity : list) { //遍历结果集
            for (String refCode : allCache.keySet()) {
                if (allCache.get(refCode).hasNoRefDataCache()) {
                    continue;//没有参照数据集缓存,就不用后面的反写了,直接下一个refcode
                }
                ReferenceCache cache = allCache.get(refCode);
                for (Field refField : cache.getFieldCache()) {//直接从缓存的field中遍历,省却没有@Reference的field
                    String hasCodeKey=entity.hashCode() +""+ refField.hashCode();
                    String[] fieldIds = cache.getCachedFieldIds(hasCodeKey);
                    if (fieldIds == null) {
                        continue; //实体field内id值为空,则跳过本field数据解析
                    }
                    List<Map<String, Object>> fieldRefData = cache.getFieldRefDataCache(hasCodeKey);//从step2中整理好的结果中拿到半成品
                    int loopSize = Math.min(
                            cache.getRef().srcProperties().length,
                            cache.getRef().desProperties().length);//参照配置多字段参照时需严格对应大小
                    for (int i = 0; i < loopSize; i++) {                //遍历参照中配置的多个srcPro和desPro 进行值替换
                        List<String> reflectValues = new ArrayList<>(fieldIds.length);  //装载待反写field的值
                        for (Map<String, Object> refData : fieldRefData) {//取出参照缓存数据集
                            reflectValues.add( String.valueOf(refData.get(cache.getRef().srcProperties()[i]  ))  )  ;
                        }
                        String fieldValue = ArrayUtil.join(reflectValues.toArray(), ",");
                        ReflectUtil.setFieldValue(entity, cache.getRef().desProperties()[i], fieldValue); //执行反写
                    }
                }
            }
        }
        return list;

    }

    /**
     * 内部参照计算缓存对象,每个对象对应一类Reference(以code区别)注解
     */
    class ReferenceCache {

        private Reference ref; //同一个refcode所有@Reference内容都在这里
        private List<String> idCache = new ArrayList<>(); //缓存每套refcode下用到的所有参照表id值
        private Set<Field> fieldCache = new HashSet<>();//缓存每套refcode下打注解的field
        private List<Map<String, Object>> refDataCache = new ArrayList<>(); //缓存每套refCode下的id到name的远程查询结果
        private Map<String, String[]> fieldIdCache = new HashMap<>(); //缓存实体中每个field对应的id值,key为hashcode
        private Map<String, List<Map<String, Object>>> fieldRefDataCache = new HashMap<>(); //缓存实体中每个field的ids对应的refDataCache里面的值,key为hashcode


        public  List<Map<String, Object>>  getFieldRefDataCache(String hasCode) {
            return fieldRefDataCache.get(hasCode);
        }

        public void cacheRefIdsInEntityField(String hasCode, String[] refIds) {
            fieldIdCache.put(hasCode, refIds);

        }

        public String[] getCachedFieldIds(String hasCode) {
            return fieldIdCache.get(hasCode);
        }

        public ReferenceCache(Reference ref) {
            this.ref = ref;
        }

        public Reference getRef() {
            return ref;
        }

        public void setRef(Reference ref) {
            this.ref = ref;
        }

        public List<String> getIdCache() {
            return idCache;
        }

        public void setIdCache(List<String> idCache) {
            this.idCache = idCache;
        }

        public Set<Field> getFieldCache() {
            return fieldCache;
        }

        public void setFieldCache(Set<Field> fieldCache) {
            this.fieldCache = fieldCache;
        }

        public List<Map<String, Object>> getRefDataCache() {
            return refDataCache;
        }

        public void setRefDataCache(List<Map<String, Object>> refDataCache) {
            this.refDataCache = refDataCache;
        }

        /**
         * 一次性遍历refDataCache内缓存的参照获取结果集，按照fieldIdCache的分组方式准备好，可简化后续使用时的复杂度
         */
        public void putRefDataInOrderOfFieldIds() {
            if (refDataCache==null || refDataCache.size()==0){
                return;
            }
            for (Map<String, Object> refData : refDataCache) {//取出参照缓存数据集，进行分组归纳，以每个entity.field为分组依据,
                for (String fkey : fieldIdCache.keySet()) {
                    if (fieldIdCache.get(fkey)==null){
                        continue;
                    }
                    List<Map<String,Object>> refDatas = new ArrayList<>();//新参照标准中返回数据都有id字段,旧的则只有refpk
                    for (String id : fieldIdCache.get(fkey)) {
                        if (id.equals( refData.get("ID"))||id.equals( refData.get("id"))||id.equals( refData.get("refpk") )){
                            refDatas.add(refData);
                        }
                    }
                    if (fieldRefDataCache.containsKey(fkey)){//防止缓存的正确值被覆盖掉
                        fieldRefDataCache.get(fkey).addAll(refDatas);
                    }else{
                        fieldRefDataCache.put(fkey, refDatas);
                    }
                }
            }
        }

        public boolean hasNoRefDataCache() {
            return refDataCache == null || refDataCache.size() == 0;
        }
    }
}