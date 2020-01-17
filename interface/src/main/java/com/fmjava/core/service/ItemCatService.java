package com.fmjava.core.service;

import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    /**
     * 跟据parentid查询分类数据
     *  分页查询
     * @param page
     * @param pageSize
     * @param parentId
     * @param itemCat
     * @return
     */
    PageResult findPage(Integer page, Integer pageSize, Long parentId, ItemCat itemCat);

    /**
     * 根据父级分类查询(主要做下拉列表用）
     * @param parentId
     * @return
     */
    List<ItemCat> findByParentId(Long parentId);

    /**
     * 添加分类
     * @param itemCat 分类实体
     */
    void addItemCat(ItemCat itemCat);

    /**
     * 通过parentid查找父元素
     * @param parentId 父级id
     * @return
     */
    ItemCat findParent(Long parentId);

    /**
     * 通过主键更新
     * @param itemCat
     */
    void updateItemCat(ItemCat itemCat);

    /**
     * 通过主键批量删除
     * @param idx
     */
    int delectItemCat(Long[] idx);

    /**
     * 查询所有分类信息
     * @return
     */
    List<ItemCat> findAll();
}
