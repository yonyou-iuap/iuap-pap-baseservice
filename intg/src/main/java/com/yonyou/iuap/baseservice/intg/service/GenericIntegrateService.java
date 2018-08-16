package com.yonyou.iuap.baseservice.intg.service;

import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeatureHolder;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericMapper;
import com.yonyou.iuap.baseservice.persistence.support.DeleteFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.QueryFeatureExtension;
import com.yonyou.iuap.baseservice.persistence.support.SaveFeatureExtension;
import com.yonyou.iuap.baseservice.service.GenericService;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yonyou.iuap.baseservice.intg.support.ServiceFeature.*;

/**
 * 特性集成服务,用于GenericService所有服务接口集成多种组件特性
 * 默认支持
 *      <p>ATTACHMENT-附件,
 *      <p>MULTI_TENANT-多租户,
 *      <p>REFERENCE-参照,
 *      <p>LOGICAL_DEL-逻辑删除
 * @param <T>
 */
public  abstract class GenericIntegrateService<T extends Model> extends GenericService<T> {
    private static Logger log = LoggerFactory.getLogger(GenericIntegrateService.class);
    private static final String LOG_TEMPLATE="特性组件{}的未实现{}扩展" ;

    /***************************************************/
    /**
     * 查询前置条件处理
     * @param searchParams
     * @return
     */
    private SearchParams  prepareFeatSearchParam(SearchParams searchParams){

        for (ServiceFeature feat:feats){
           QueryFeatureExtension instance = ServiceFeatureHolder.getQueryExtension(feat.name());
           if (instance==null){
                log.info(LOG_TEMPLATE, feat,QueryFeatureExtension.class);
           }else{
               searchParams= instance.prepareQueryParam(searchParams);
           }
        }
        return searchParams;
    }

    /**
     * 查询后续处理
     * @param list
     * @return
     */
    private List fillListFeatAfterQuery(List list){

        for (ServiceFeature feat:feats){
            QueryFeatureExtension instance = ServiceFeatureHolder.getQueryExtension(feat.name());
            if (instance==null){
                log.info(LOG_TEMPLATE, feat,QueryFeatureExtension.class);
            }else{
                list= instance.afterListQuery(list);
            }
        }
        return list;
    }


    /**
     * 分页查询
     * @param pageRequest
     * @param searchParams
     * @return
     */
    @Override
    public Page<T> selectAllByPage(PageRequest pageRequest, SearchParams searchParams) {

        searchParams=prepareFeatSearchParam(searchParams);
        Page<T> page=super.selectAllByPage(pageRequest, searchParams);
        fillListFeatAfterQuery(page.getContent());
        return page;
    }

    /**
     * 查询所有数据
     * @return
     */
    @Override
    public List<T> findAll(){
        SearchParams searchParams=prepareFeatSearchParam(new SearchParams());
        return   fillListFeatAfterQuery(super.queryList(searchParams.getSearchMap()));
    }

    /**
     * 根据参数查询List
     * @param queryParams
     * @return
     */
    @Override
    public List<T> queryList(Map<String,Object> queryParams){
        SearchParams searchParams= new SearchParams();
        searchParams.setSearchMap(queryParams);
        searchParams=prepareFeatSearchParam(searchParams);
        return   fillListFeatAfterQuery(super.queryList(searchParams.getSearchMap()));
    }

    /**
     * 根据字段名查询List
     * @param name
     * @param value
     * @return
     */
    @Override
    public List<T> queryList(String name, Object value){
        SearchParams searchParams= new SearchParams();
        searchParams.addCondition(name,value);
        searchParams=prepareFeatSearchParam(searchParams);
        return fillListFeatAfterQuery(super.queryList(searchParams.getSearchMap()));
    }

    /**
     * 根据参数查询List【返回值为List<Map>】
     * @param params
     * @return
     */
    @Override
    public List<Map<String,Object>> queryListByMap(Map<String,Object> params){

//        return super.queryListByMap(params);
        SearchParams searchParams= new SearchParams();
        searchParams.setSearchMap(params);
        searchParams=prepareFeatSearchParam(searchParams);
        List<Map<String,Object> >list=super.queryListByMap(searchParams.getSearchMap());
        return   fillListFeatAfterQuery(list);
    }

    /**
     * 根据ID查询数据
     * @param id
     * @return
     */
    @Override
    public T findById(Serializable id) {
       return  this.findUnique("id",id);
    }

    /**
     * 查询唯一数据
     * @param name
     * @param value
     * @return
     */
    @Override
    public T findUnique(String name, Object value) {
        SearchParams searchParams= new SearchParams();
        searchParams.addCondition(name,value);
        searchParams=prepareFeatSearchParam(searchParams);
        List<T>listData=super.queryList(searchParams.getSearchMap());
        if(listData!=null && listData.size()==1) {
            return listData.get(0);
        }else {
            throw new RuntimeException("检索数据不唯一, "+name + ":" + value);
        }
    }

    /***************************************************/
    /**
     * 保存前按特性初始化entity
     * @param entity
     */
    private void prepareFeatEntity(T entity){
        for (ServiceFeature feat:feats){
            SaveFeatureExtension instance = ServiceFeatureHolder.getSaveExtension(feat.name());
            if (instance==null){
                log.info(LOG_TEMPLATE, feat,SaveFeatureExtension.class);
            }else{
                instance.prepareEntityBeforeSave(entity);
            }
        }
    }

    private void addFeatAfterEntitySave(T entity){
        for (ServiceFeature feat:feats){
            SaveFeatureExtension instance = ServiceFeatureHolder.getSaveExtension(feat.name());
            if (instance==null){
                log.info(LOG_TEMPLATE, feat,SaveFeatureExtension.class);
            }else{
                instance.afterEntitySave(entity);
            }
        }
    }

    /**
     * 保存数据
     * @param entity
     * @return
     */
    @Override
    public T save(T entity) {
        prepareFeatEntity(entity);
        entity=super.save(entity);
        addFeatAfterEntitySave(entity);
        return entity;
    }

    /**
     * 批量保存
     * @param listEntity
     */
    @Override
    public void saveBatch(List<T> listEntity){
        for(int i=0; i<listEntity.size(); i++) {
            this.save(listEntity.get(i));
        }
    }

    /**
     * 新增保存数据
     * @param entity
     * @return
     */
    @Override
    public T insert(T entity) {
        prepareFeatEntity(entity);
        entity=super.insert(entity);
        addFeatAfterEntitySave(entity);
        return entity;
    }

    /**
     * 更新保存数据
     * @param entity
     * @return
     */
    @Override
    public T update(T entity) {
        prepareFeatEntity(entity);
        entity=super.update(entity);
        addFeatAfterEntitySave(entity);
        return entity;
    }
    /***************************************************/
    /**
     * 保存前按特性初始化entity
     * @param entity
     */
    private void prepareFeatDeleteParam(T entity,Map params){
        for (ServiceFeature feat:feats){
            DeleteFeatureExtension instance = ServiceFeatureHolder.getDeleteExtension(feat.name());
            if (instance==null){
                log.info(LOG_TEMPLATE, feat,DeleteFeatureExtension.class);
            }else{
                instance.prepareDeleteParams(entity,params);
            }
        }
    }
    private void runFeatAfterEntityDelete(T entity) {
        for (ServiceFeature feat : feats) {
            DeleteFeatureExtension instance = ServiceFeatureHolder.getDeleteExtension(feat.name());
            if (instance == null) {
                log.info(LOG_TEMPLATE, feat,DeleteFeatureExtension.class);
            } else {
                instance.afterDeteleEntity(entity);
            }
        }
    }
            /**
             * 删除数据
             */
    @Override
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
    @Override
    public int delete(T entity) {
        Map params = new  HashMap <>();
        params.put("id",entity.getId());
        prepareFeatDeleteParam(entity,params);
        int count= this.intgDelete(entity,params);
        runFeatAfterEntityDelete(entity);
        return count;
    }

    /**
     * 根据id删除数据
     * @param id
     * @return
     */
    @Override
    public int delete(Serializable id) {
        Map params = new  HashMap <>();
        params.put("id",id);
        List<T> ls = genericMapper.queryList(params);
        if (ls!=null&&ls.size()>0){
            return  this.delete(ls.get(0));
        }else{
            log.info("删除失败,无id为{}的数据",id);
//            throw new RuntimeException("删除失败,无id为{}的数据");
            return 0;
        }
    }

    /**
     * 物理删除:任意态
     * @param params
     * @return
     */
    public int intgDelete(T entity,Map params) {
        if (isLogicalDel()){
            super.update(entity);
            return 1;
        }else{
            return this.genericMapper.delete(params);
        }

    }

    private boolean isLogicalDel(){
        for (ServiceFeature feat:feats){
            if (feat==LOGICAL_DEL) return true;
        }
        return false;
    }

    /***************************************************/

    protected GenericMapper<T> genericMapper;

    protected ServiceFeature[] feats = new ServiceFeature[]{ATTACHMENT,MULTI_TENANT,LOGICAL_DEL,REFERENCE};//默认四特性都支持

    protected abstract void setFeats(ServiceFeature[] feats);

    public void setGenericMapper(GenericMapper<T> mapper) {
        this.genericMapper = mapper;
        super.setGenericMapper(mapper);
    }


}
