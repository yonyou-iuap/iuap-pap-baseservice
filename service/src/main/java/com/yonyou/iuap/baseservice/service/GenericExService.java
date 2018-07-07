package com.yonyou.iuap.baseservice.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.yonyou.iuap.baseservice.entity.annotation.Reference;
import com.yonyou.iuap.mvc.type.SearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.yonyou.iuap.baseservice.entity.LogicDel;
import com.yonyou.iuap.baseservice.entity.Model;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.persistence.vo.pub.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 说明：基础Service扩展——支持逻辑删除
 * @author houlf
 * 2018年6月12日
 */
public abstract class GenericExService<T extends Model & LogicDel> extends GenericService<T>{
	
	private Logger log = LoggerFactory.getLogger(GenericExService.class);

	protected GenericExMapper<T> genericExMapper;

	public void setGenericMapper(GenericExMapper<T> mapper) {
		this.genericExMapper = mapper;
	}

    /**
     * 分页查询
     * @param pageRequest
     * @param searchParams
     * @return
     */
    @Override
    public Page<T> selectAllByPage(PageRequest pageRequest, SearchParams searchParams) {
        try {
        Page<T> page = genericMapper.selectAllByPage(pageRequest, searchParams).getPage();
        List<T> contentList = page.getContent();

        if (!contentList.isEmpty()) {
            Map<String, List<Map<String, Object>>> refContentMap = new HashMap<>();
            Field[] fields = contentList.get(0).getClass().getFields();
            for (Field field : fields) {
                Reference ref = field.getAnnotation(Reference.class);
                if (null != ref) {
                    List<Map<String, Object>> refContents = genericExMapper.selectRefTable(null, ref.table(), field.getName(), Arrays.asList(ref.srcProperties()), new HashMap<>()).getContent();
                    refContentMap.put(field.getName(), refContents);
                }
            }
            if (!refContentMap.isEmpty()) {
                for (Object item : contentList) {
                    Iterator<Map.Entry<String, List<Map<String, Object>>>> it = refContentMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, List<Map<String, Object>>> entry = it.next();
                        PropertyDescriptor pd = new PropertyDescriptor(entry.getKey(), item.getClass());
                        Method getMethod = pd.getReadMethod();
                        if (getMethod != null) {
                            getMethod.invoke(item);
                            for (Map<String, Object> refItem : entry.getValue()) {
                                Reference ref = item.getClass().getField(entry.getKey()).getAnnotation(Reference.class);
                                int i = 0;
                                for (String srcPro : ref.srcProperties()) {
                                    PropertyDescriptor itemPd = new PropertyDescriptor(ref.desProperties()[i], item.getClass());
                                    Method setMethod = itemPd.getWriteMethod();
                                    if (setMethod != null) {
                                        setMethod.invoke(item, refItem.get(srcPro));
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return page;
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

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
			throw new BusinessException("新增保存数据出错，对象为空!");
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
			throw new BusinessException("更新保存数据出错，对象为空!");
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
				log.error("删除数据出错,记录数="+count+"\r\n"+JSON.toJSONString(entity));
				throw new BusinessException();
			}
		}else {
			throw new BusinessException();
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

	public Page<Map<String, Object>> selectRefTable(PageRequest pageRequest,
													String tablename, String idfield, Map<String, String> condition, List<String> extColumns) {
		Page<Map<String,Object>> result = genericExMapper.selectRefTable(pageRequest, tablename, idfield, extColumns, condition).getPage();
		return result;
	}

	public Page<Map<String, Object>> selectRefTree(PageRequest pageRequest,
												   String tablename, String idfield, String pidfield,
												   String codefield, String namefield, Map<String, String> condition,List<String> extColumns) {

		Page<Map<String,Object>> result = genericExMapper.selectRefTree(pageRequest,tablename,idfield,pidfield,codefield,namefield, extColumns,condition).getPage();
		return result;
	}
	
	/***************************************************/
	protected GenericExMapper<T> genericMapperEx;

	public void setIbatisMapperEx(GenericExMapper<T> mapper) {
		this.genericMapperEx = mapper;
		super.setGenericMapper(mapper);
	}

}