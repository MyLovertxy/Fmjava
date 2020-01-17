package com.fmjava.core.service;

import com.fmjava.core.pojo.ad.ContentCategory;
import com.fmjava.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentCatService {
    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param category
     * @return
     */
    PageResult findPage(Integer page, Integer pageSize, ContentCategory category);

    /**
     * 修改广告分类
     * @param category 分类实体
     */
    void update(ContentCategory category);

    /**
     * 添加广告分类
     * @param category
     */
    void add(ContentCategory category);

    /**
     * 删除广告分类
     * @param idx
     */
    void delete(Long[] idx);

    /**
     * 查询所有
     * @return
     */
    List<ContentCategory> findAll();

}
