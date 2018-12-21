package com.yonyou.iuap.baseservice.service;

import com.yonyou.iuap.i18n.MessageSourceUtil;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;

/**
 * 说明：基础Service扩展——支持逻辑删除
 * @author houlf
 * 2018年6月12日
 */
public abstract class GenericExService<T extends Model & LogicDel> extends GenericService<T>{

    private Logger log = LoggerFactory.getLogger(GenericExService.class);

    /**
     * 新增保存数据
     * @param entity
     * @return
     */
    public T insert(T entity) {
        if(entity != null) {
            entity.setDr(LogicDel.NORMAL);
            return super.insert(entity);
        }else {
            throw new BusinessException(MessageSourceUtil.getMessage("ja.bas.ser1.0001", "新增保存数据出错，对象为空!"));
        }
    }

    /**
     * 更新保存数据
     * @param entity
     * @return
     */
    @Override
    public T update(T entity) {
        if(entity != null) {
            entity.setDr(LogicDel.NORMAL);
            return super.update(entity);
        }else {
            throw new BusinessException(MessageSourceUtil.getMessage("ja.bas.ser1.0002", "更新保存数据出错，对象为空!"));
        }
    }

    /**
     * 逻辑删除
     * @param entity
     * @return
     */
    public int update4LogicDel(T entity) {
        if(entity != null) {
            entity.setDr(LogicDel.DELETED);
            int count = this.genericMapperEx.update(entity);
            if(count == 1) {
                return count;
            }else {
                log.error(MessageSourceUtil.getMessage("ja.bas.ser1.0003", "删除数据出错,记录数=")+count+"\r\n"+JSON.toJSONString(entity));
                throw new BusinessException();
            }
        }else {
            throw new BusinessException(MessageSourceUtil.getMessage("ja.bas.ser1.0004", "数据对象为空,无法删除!"));
        }
    }

    /**
     * 覆盖父方法，调整为逻辑删除
     * @param entity
     * @return
     */
    @Override
    public int delete(T entity) {
        return this.update4LogicDel(entity);
    }

    /**
     * 覆盖父方法，调整为逻辑删除
     * @param id
     */
    @Override
    public int delete(Serializable id) {
        T entity = this.findById(id);
        return this.delete(entity);
    }


    /***************************************************/
    protected GenericExMapper<T> genericMapperEx;

    public void setIbatisMapperEx(GenericExMapper<T> mapper) {
        this.genericMapperEx = mapper;
        super.setGenericMapper(mapper);
    }

}
