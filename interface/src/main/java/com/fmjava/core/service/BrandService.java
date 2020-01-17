package com.fmjava.core.service;

import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<Brand> findAllBrands();

    /**
     * 分页查询品牌数据
     * @param page 当前页
     * @param pageSize 每页查询的记录数
     * @param brand
     * @return
     */
    PageResult findPage(Integer page, Integer pageSize, Brand brand);

    /**
     * 添加品牌
     * @param brand 要添加的品牌数据
     */
    void addBrand(Brand brand);

    /**
     * 更新品牌
     * @param brand 更新实体
     */
    void updateBrand(Brand brand);

    /**
     * 根据id删除品牌
     * @param idx
     */
    void deleteBrand(Long[] idx);
    /**
     * 加载品牌选项列表
     * @return
     */
    public List<Map> selectOptionList();
}
