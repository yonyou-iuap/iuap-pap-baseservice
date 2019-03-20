package com.yonyou.iuap.baseservice.ref.service;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.pap.base.ref.utils.RefIdToNameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 将远程和本地统一进行处理,按照新版参照的规范进行反写处理
 *
 * @author leon
 * @date 2019/3/20
 * @since 3.5.6
 */
public class RefUnionService<T extends Model> implements QueryFeatureExtension<T> {
    private static Logger logger = LoggerFactory.getLogger(RefUnionService.class);
    private Class modelClass;

    @Override
    public SearchParams prepareQueryParam(SearchParams searchParams, Class modelClass) {
        this.modelClass = modelClass;
        return searchParams;
    }

    /**
     * 参照特性反写执行方法,弱化反写优先级,当反写失败时不影响正常list返回
     * @param list 查询结果
     * @return 查询结果-已被执行id-to-name参照反写
     */
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
     * 参照数据id-to-name加载<p>根据@Reference注解定义的参照参数,
     * 将参照数据中的内容逐个反写
     *
     * @param list 未装填参照的原始list
     * @return 重新装填后的结果
     * @see com.yonyou.iuap.baseservice.entity.annotation.Reference
     */
    public List<T> fillListWithRef(List<T> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        //所有缓存的参照反写上下文,key为refCode
        Map<String, RefContext> allCache = new HashMap<>();
        /*
         * @Step 1
         * 解析参照配置(仅处理远程参照服务),获取参照字段id集合,用于后续参照远程调用
         */
        //记录所有@Reference注解
        Field[] fields = ReflectUtil.getFields(modelClass);
        for (Field field : fields) {
            Reference ref = field.getAnnotation(Reference.class);
            if (null != ref && !allCache.containsKey(ref.code())) {
                    logger.debug("caching remote Reference:" + ref.code());
                    RefContext cache = new RefContext(ref);
                    cache.getFieldCache().add(field);
                    allCache.put(ref.code(), cache);
            }else if(null != ref){
                allCache.get(ref.code()).getFieldCache().add(field);
            }
        }
        //记录下list里所有的参照id
        for (Object entity : list) {
            for (String refCode : allCache.keySet()) {
                //从缓存的field中遍历,跳过没有@Reference的field
                for (Field field : allCache.get(refCode).getFieldCache()) {
                    Object refIds = ReflectUtil.getFieldValue(entity, field);
                    if (null != refIds) {
                        //兼容参照多选
                        String[] fieldIds = refIds.toString().split(",");
                        //收集同一refCode下的所有id
                        allCache.get(refCode).getIdCache().addAll(Arrays.asList(fieldIds));
                        //缓存list里每条数据每个field里的参照ids,反写时使用
                        allCache.get(refCode).cacheRefIdsInEntityField(entity.hashCode() +""+ field.hashCode(), fieldIds);
                    }
                }
            }

        }

        /*
         * @Step 2解析参照配置, 一次按需(idCache)加载参照数据
         */
        Map<String, List<String>> refParams = new HashMap<>();
        for (String refCode : allCache.keySet()) {
            List<String> ids = new ArrayList<>(allCache.get(refCode).getIdCache());
            refParams.put(refCode,ids);
        }
        try {
            Map<String, List<Map<String, Object>>> queryResult = RefIdToNameUtil.convertIdToName(refParams);
            if (null != queryResult && queryResult.size() > 0) {
                for (String refCode :allCache.keySet()){
                    allCache.get(refCode).setRefDataCache(queryResult.get(refCode));//将所有参照查询结果按refCode分组缓存
                    allCache.get(refCode).putRefDataInOrderOfFieldIds();//按缓存分组整理这个refContents，为反写准备好一切
                }
            }
        } catch (Exception e) {
            logger.error("ref-id2name util calling error："+refParams.toString(), e);
        }

        /*
         * @Step 3
         * 第2次遍历结果集,执行反写
         */
        for (Object entity : list) {
            for (String refCode : allCache.keySet()) {
                if (allCache.get(refCode).hasNoneRefData()) {
                    //没有参照数据queryResult,就不用后面的反写了,直接下一个refCode
                    continue;
                }
                RefContext refContext = allCache.get(refCode);
                //从缓存的field中遍历,跳过没有@Reference的field
                for (Field refField : refContext.getFieldCache()) {
                    String cacheKey=entity.hashCode() +""+ refField.hashCode();
                    //从step2中整理好的结果中拿到半成品
                    List<Map<String, Object>> fieldRefData = refContext.getFieldRefData(cacheKey);
                    //参照配置@Reference多字段参照时需严格对应大小
                    int loopSize = Math.min(
                            refContext.getRef().srcProperties().length,
                            refContext.getRef().desProperties().length);
                    for (int i = 0; i < loopSize; i++) {
                        List<String> reflectValues = new ArrayList<>();  //装载待反写field的值
                        for (Map<String, Object> refData : fieldRefData) {//取出参照缓存数据集
                            reflectValues.add( String.valueOf(refData.get(refContext.getRef().srcProperties()[i]  ))  )  ;
                        }
                        String fieldValue = ArrayUtil.join(reflectValues.toArray(), ",");
                        ReflectUtil.setFieldValue(entity, refContext.getRef().desProperties()[i], fieldValue); //执行反写
                    }
                }
            }
        }
        return list;
    }

    /**
     * 内部参照计算缓存对象,每个对象对应一类Reference(以code区别)注解
     */
    class RefContext {

        private Reference ref; //同一个refcode所有@Reference内容都在这里
        private Set<String> idCache = new HashSet<>(); //缓存每套refcode下用到的所有参照表id值
        private Set<Field> fieldCache = new HashSet<>();//缓存每套refcode下打注解的field
        private List<Map<String, Object>> refDataCache = new ArrayList<>(); //缓存每套refCode下的id到name的远程查询结果
        private Map<String, String[]> fieldIds = new HashMap<>(); //缓存实体中每个field对应的id值,key为hashcode
        private Map<String, List<Map<String, Object>>> fieldRefData = new HashMap<>(); //缓存实体中每个field的ids对应的refDataCache里面的值,key为hashcode


        List<Map<String, Object>> getFieldRefData(String hasCode) {
            return fieldRefData.get(hasCode);
        }

        void cacheRefIdsInEntityField(String hasCode, String[] refIds) {
            fieldIds.put(hasCode, refIds);

        }

        String[] getFieldIds(String hasCode) {
            return fieldIds.get(hasCode);
        }

        RefContext(Reference ref) {
            this.ref = ref;
        }

        Reference getRef() {
            return ref;
        }

        void setRef(Reference ref) {
            this.ref = ref;
        }

        Set<String> getIdCache() {
            return idCache;
        }

        void setIdCache(Set<String> idCache) {
            this.idCache = idCache;
        }

        Set<Field> getFieldCache() {
            return fieldCache;
        }

        void setFieldCache(Set<Field> fieldCache) {
            this.fieldCache = fieldCache;
        }

        List<Map<String, Object>> getRefDataCache() {
            return refDataCache;
        }

        void setRefDataCache(List<Map<String, Object>> refDataCache) {
            this.refDataCache = refDataCache;
        }

        /**
         * 一次性遍历refDataCache内缓存的参照获取结果集，按照fieldIdCache的分组方式准备好，可简化后续反写使用时的复杂度
         */
        void putRefDataInOrderOfFieldIds() {
            if (refDataCache==null || refDataCache.size()==0){
                return;
            }
            for (Map<String, Object> refData : refDataCache) {//取出参照缓存数据集，进行分组归纳，以每个entity.field为分组依据,
                for (String fkey : fieldIds.keySet()) {
                    if (fieldIds.get(fkey)==null){
                        continue;
                    }
                    List<Map<String,Object>> refDatas = new ArrayList<>();//新参照标准中返回数据都有id字段,旧的则只有refpk
                    for (String id : fieldIds.get(fkey)) {
                        if (id.equals( refData.get("ID"))||id.equals( refData.get("id"))||id.equals( refData.get("refpk") )){
                            refDatas.add(refData);
                        }
                    }
                    if (fieldRefData.containsKey(fkey)){//防止缓存的正确值被覆盖掉
                        fieldRefData.get(fkey).addAll(refDatas);
                    }else{
                        fieldRefData.put(fkey, refDatas);
                    }
                }
            }
        }

        boolean hasNoneRefData() {
            return refDataCache == null || refDataCache.size() == 0;
        }
    }
}
