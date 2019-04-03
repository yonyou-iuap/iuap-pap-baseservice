package com.yonyou.iuap.baseservice.intg.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeatureHolder;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.pap.base.i18n.MessageSourceUtil;
import com.yonyou.iuap.ucf.common.entity.Identifier;
import com.yonyou.iuap.ucf.common.rest.SearchParams;
import com.yonyou.iuap.ucf.dao.BaseDAO;
import com.yonyou.iuap.ucf.dao.BasePO;
import com.yonyou.iuap.ucf.dao.description.Persistence;
import com.yonyou.iuap.ucf.dao.support.UcfSearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * 继承ucf持久层的服务集成基类
 *
 * @author leon
 * @date 2019/4/3
 * @since UCF1.0
 */
public abstract class GenericUcfService <T  extends Persistence & Identifier<ID>, ID extends Serializable>{

    private static Logger log = LoggerFactory.getLogger(GenericUcfService.class);
    private static final String LOG_TEMPLATE= MessageSourceUtil.getMessage("ja.int.ser2.0001", "特性组件{}的未实现{}扩展") ;


    protected BaseDAO<T,ID> genericMapper;
    protected String[] feats = new String[]{"UNI_REFERENCE"};
    protected Set<QueryFeatureExtension> customQueryExts;     //客户自定义的查询特性服务扩展点
    protected Set<SaveFeatureExtension> customSaveExts;       //客户自定义的保存特性服务扩展点
    protected Set<DeleteFeatureExtension> customDeleteExts;   //客户自定义的删除特性服务扩展点


    protected abstract ServiceFeature[] getFeats();

    public void setGenericMapper(BaseDAO<T,ID> mapper ) {
        setGenericMapper(mapper,new String[0]);
    }

    public void setGenericMapper(BaseDAO<T,ID> mapper,String... extensions ) {
        this.feats=combineFeats(extensions);
        this.genericMapper = mapper;
    }

    private String[] combineFeats(String[] extensions){
        List<String> allFeat = new ArrayList<>();
        for (ServiceFeature feature:getFeats()){
            allFeat.add(feature.name());
        }
        Collections.addAll(allFeat, extensions);
        return  allFeat.toArray(extensions);
    }

    /**
     * 在执行查询方法前，仅能通过泛型来拿到实体的真实类型
     * @return 泛型指定的业务实体Class
     */
    public Class<? extends Identifier>  getModelClass(){
        Type superclassType = this.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            log.warn("泛型解析失败，可能影响特性扩展服务");
            return BasePO.class ;
        }
        Type[] t = ((ParameterizedType) superclassType).getActualTypeArguments();
        if (null!=t&& t.length>0){
            return (Class) t[0];
        }else
        {
            return BasePO.class ;
        }
    }


    /**
     * 查询前置条件处理埋点
     * @param searchParams
     * @return
     */
    private SearchParams prepareFeatSearchParam(SearchParams searchParams){

        for (String feat:feats){
            QueryFeatureExtension instance = ServiceFeatureHolder.getQueryExtension(feat);
            if (instance==null){
                log.debug(LOG_TEMPLATE, feat,QueryFeatureExtension.class.getSimpleName());
            }else{
                searchParams= instance.prepareQueryParam(searchParams,getModelClass());
            }
        }
        if ( customQueryExts == null){
            customQueryExts = ServiceFeatureHolder.getModelExtensions(getModelClass(), QueryFeatureExtension.class);
        }
        for (QueryFeatureExtension qService : customQueryExts){
            searchParams= qService.prepareQueryParam(searchParams,getModelClass());
        }
        return searchParams;
    }

    /**
     * 查询后续处理埋点
     * @param list
     * @return
     */
    private List<T> fillListFeatAfterQuery(List<T> list){
        for (String feat:feats){
            QueryFeatureExtension instance = ServiceFeatureHolder.getQueryExtension(feat);
            if (instance==null){
                log.debug(LOG_TEMPLATE, feat,QueryFeatureExtension.class);
            }else{
                list= instance.afterListQuery(list);
            }
        }
        if ( customQueryExts == null){
            customQueryExts = ServiceFeatureHolder.getModelExtensions(getModelClass(), QueryFeatureExtension.class);
        }
        for (QueryFeatureExtension qService : customQueryExts){
            list= qService.afterListQuery(list);
        }
        return list;
    }

    /**
     * 分页查询
     * @param pageRequest
     * @param searchParams
     * @return
     */
    public Page<T> selectAllByPage(PageRequest pageRequest, SearchParams searchParams) {

        searchParams=prepareFeatSearchParam(searchParams);
        Page<T> page=genericMapper.listPage(pageRequest, searchParams).getPage();
        fillListFeatAfterQuery(page.getContent());
        return page;
    }

    /**
     * 查询所有数据
     * @return
     */
    public List<T> findAll(){
        SearchParams searchParams=prepareFeatSearchParam(UcfSearchParams.of(getModelClass()));
        return   queryList(searchParams.getSearchMap());
    }

    /**
     * 根据参数查询List
     * @param queryParams
     * @return
     */
    public List<T> queryList(Map<String,Object> queryParams){
        SearchParams searchParams= UcfSearchParams.of(getModelClass());
        searchParams.setSearchMap(queryParams);
        searchParams=prepareFeatSearchParam(searchParams);
        return   fillListFeatAfterQuery(genericMapper.list(searchParams));
    }


    /**
     * 查询唯一数据
     * @param name 查询参数名
     * @param value 查询匹配值
     * @return 带特性加工的业务实体
     */
    public T findUnique(String name, Object value) {
        UcfSearchParams searchParams= UcfSearchParams.of(getModelClass());
        searchParams.addEqualCondition(name,value);
        prepareFeatSearchParam(searchParams);
        List<T>listData=genericMapper.list(searchParams);
        listData=fillListFeatAfterQuery(listData);
        if(listData!=null && listData.size()==1) {
            return listData.get(0);
        }else {
            if (listData==null||listData.size()==0){
                throw new RuntimeException(MessageSourceUtil.getMessage("ja.int.ser2.0003", "检索结果为空,")+name + ":" + value);
            }else{
                throw new RuntimeException(MessageSourceUtil.getMessage("ja.int.ser2.0004", "检索数据不唯一,")+name + ":" + value);
            }
        }

    }


    /**
     * 保存前按特性初始化entity
     * @param entity 保存前的业务实体
     */
    private void prepareFeatEntity(T entity){
        for (String feat:feats){
            SaveFeatureExtension instance = ServiceFeatureHolder.getSaveExtension(feat);
            if (instance==null){
                log.debug(LOG_TEMPLATE, feat,SaveFeatureExtension.class);
            }else{
                instance.prepareEntityBeforeSave(entity);
            }
        }
        if ( customSaveExts == null){
            customSaveExts = ServiceFeatureHolder.getModelExtensions(getModelClass(), SaveFeatureExtension.class);
        }
        for (SaveFeatureExtension sService : customSaveExts){
            sService.prepareEntityBeforeSave(entity);
        }
    }

    /**
     * 保存实体之后的特性扩展埋点
     * @param entity
     */
    private void addFeatAfterEntitySave(T entity){
        for (String feat:feats){
            SaveFeatureExtension instance = ServiceFeatureHolder.getSaveExtension(feat);
            if (instance==null){
                log.debug(LOG_TEMPLATE, feat,SaveFeatureExtension.class);
            }else{
                instance.afterEntitySave(entity);
            }
        }
        if ( customSaveExts == null){
            customSaveExts = ServiceFeatureHolder.getModelExtensions(getModelClass(), SaveFeatureExtension.class);
        }
        for (SaveFeatureExtension sService : customSaveExts){
            sService.afterEntitySave(entity);
        }
    }


    /**
     *  批量全量插入
     * @param listEntity 待插入保存业务实体列表
     * @return
     */
    public int insertBatch(List<T> listEntity){
        for(T entity: listEntity ){
            prepareFeatEntity(entity);
        }
        int savedCnt=  genericMapper.insertBatch(listEntity);
        if (savedCnt!= listEntity.size()){
            throw new RuntimeException(MessageSourceUtil.getMessage("ja.int.ser2.0007", "batch insert error!"));
        }
        for(T entity: listEntity ){
            addFeatAfterEntitySave(entity);
        }
        return savedCnt;
    }

    /**
     *  批量保存
     * @param listEntity 待插入保存业务实体列表
     * @return
     */
    public int saveBatch(List<T> listEntity){
        int savedCnt=0;
        for(T entity: listEntity ){
            prepareFeatEntity(entity);
            save(entity);
            savedCnt++;
            addFeatAfterEntitySave(entity);
        }
        if (savedCnt!= listEntity.size()){
            throw new RuntimeException(MessageSourceUtil.getMessage("ja.int.ser2.0007", "batch insert error!"));
        }
        return savedCnt;
    }


    /**
     * 保存数据,将GenericService.save中的全值保存方式切换为selective方式
     * 不需埋点,因为其后续会调用executeInsert或executeUpdate的埋点
     * @param entity  入参转化后的业务实体,需至少实现model接口
     * @return 保存后的完整实体信息
     */
    public T save(T entity) {
        boolean isNew = false;					//是否新增数据
        if(entity instanceof Model) {
            if(entity.getId()==null) {
                isNew = true;
            }else {
                isNew = StrUtil.isEmptyIfStr(entity.getId());
            }
        }
        if(isNew) {
             genericMapper.insertSelective(entity);
        }else {
             genericMapper.updateSelective(entity);
        }
        return entity;
    }
    /**
     * 在GenericService#executeInsert()上进行重载+埋点,
     * 埋点后,无论执行的时insert还是insertSelective,所有集成的特性便都会生效
     * @param entity 待插入实体
     * @param isSelective  是否可选性插入标识
     * @return 插入保存后实体
     */
    protected T executeInsert(T entity,boolean isSelective) {
        prepareFeatEntity(entity);
        if (entity != null) {
            if (isSelective) {
                this.genericMapper.insertSelective(entity);
                Map<String, Object> queryParams = new HashMap<>();
                if(entity.getId()==null){
                    queryParams.put(entity.getDescription().getVersion().getName(),
                            ReflectUtil.getFieldValue(entity,entity.getDescription().getVersion().getName()));
                }else{
                    queryParams.put("id",entity.getId());
                }
                SearchParams params = UcfSearchParams.of(getModelClass()).setSearchMap(queryParams);
                List<T> refreshed = genericMapper.list(params);
                //insertSelective之后的信息完整化回传
                if (refreshed.size()>0){
                    if (entity.getId()==null){
                        BeanUtils.copyProperties(refreshed.get(refreshed.size()-1) , entity);
                    }else {
                        BeanUtils.copyProperties(refreshed.get(0) , entity);
                    }
                }
            } else
                this.genericMapper.insert(entity);
            log.info("新增保存数据：\r\n" + JSON.toJSONString(entity));
        } else {
            throw new RuntimeException(MessageSourceUtil.getMessage("ja.bas.ser2.0003", "新增保存数据出错，对象为空!"));
        }
        addFeatAfterEntitySave(entity);
        return entity;
    }

    /**
     * 在GenericService#executeUpdate()上进行重载+埋点,
     * 埋点后,无论执行的时update还是updateSelective,所有集成的特性便都会生效
     * @param entity 入参转化后的业务实体,需至少实现model接口
     * @param isSelective 是否启用selective方式的标识
     * @return 保存后的完整实体信息
     */
    protected T executeUpdate(T entity,boolean isSelective) {
        int count ;
        prepareFeatEntity(entity);
        if(entity!=null) {
            String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS");
            if (isSelective){
                count = genericMapper.updateSelective(entity);
            }else{
                count = genericMapper.update(entity);
            }
            if(count != 1) {
                String msg=MessageSourceUtil.getMessage("ja.bas.ser2.0004", "更新保存数据出错，更新记录数=")+count+"\r\n"+JSON.toJSONString(entity);
                log.error(msg);
                throw new RuntimeException(msg);
            }else if (isSelective){
                BeanUtils.copyProperties(
                        genericMapper.getById(entity.getId()),entity
                         );//updateSelective之后的信息完整化回传
            }
        }else {
            String msg=MessageSourceUtil.getMessage("ja.bas.ser2.0005", "更新保存数据出错，输入参数对象为空!");
            log.error(msg);
            throw new RuntimeException(msg);
        }
        addFeatAfterEntitySave(entity);
        return entity;
    }
    /**
     * 删除前操作业务实体entity,或查询条件 params
     * @param entity 单实体删除时传参
     * @param params 批量删除时的查询条件
     */
    private void prepareFeatDeleteParam(T entity,Map params){
        for (String feat:feats){
            DeleteFeatureExtension instance = ServiceFeatureHolder.getDeleteExtension(feat);
            if (instance==null){
                log.debug(LOG_TEMPLATE, feat,DeleteFeatureExtension.class);
            }else{
                instance.prepareDeleteParams(entity,params);
            }
        }
        if ( customDeleteExts == null){
            customDeleteExts = ServiceFeatureHolder.getModelExtensions(getModelClass(), DeleteFeatureExtension.class);
        }
        for (DeleteFeatureExtension dService : customDeleteExts){
            dService.prepareDeleteParams(entity,params);
        }
    }

    /**
     * 删除后特性扩展埋点
     * @param entity 被删除的业务实体
     */
    private void runFeatAfterEntityDelete(T entity) {
        for (String feat : feats) {
            DeleteFeatureExtension instance = ServiceFeatureHolder.getDeleteExtension(feat);
            if (instance == null) {
                log.debug(LOG_TEMPLATE, feat,DeleteFeatureExtension.class);
            } else {
                instance.afterDeteleEntity(entity);
            }
        }
        if ( customDeleteExts == null){
            customDeleteExts = ServiceFeatureHolder.getModelExtensions(getModelClass(), DeleteFeatureExtension.class);
        }
        for (DeleteFeatureExtension dService : customDeleteExts){
            dService.afterDeteleEntity(entity);
        }
    }

    /**
     * 删除数据
     */
    public int deleteBatch(List<T> list) {
        int count = 0;
        for(T entity: list) {
            count += this.delete(entity.getId());
        }
        return count;
    }
    /**
     * 删除数据:核心集成点
     * @param entity
     * @return
     */
    public int delete(T entity) {
        Map params = new  HashMap <>();
        params.put("id",entity.getId());
        prepareFeatDeleteParam(entity,params);
        int count= genericMapper.delete(params);
        runFeatAfterEntityDelete(entity);
        return count;
    }

    /**
     * 根据id删除数据
     * @param id
     * @return
     */
    public int delete(ID id) {
        if (id == null) {
            log.info(" input parameter[id] is null,deleting nothing", id);
            return 0;
        }
        T entity = genericMapper.getById(id);
        if (entity!=null){
            return  this.delete(entity);
        }else{
            log.info("删除失败,无id为{}的数据",id);
//            throw new RuntimeException("删除失败,无id为{}的数据");
            return 0;
        }
    }





}
