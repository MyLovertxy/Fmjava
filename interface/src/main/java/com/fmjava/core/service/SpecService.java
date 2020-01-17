package com.fmjava.core.service;

import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.specEntity;
import com.fmjava.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecService {
    PageResult findPage(Integer page, Integer pageSize, Specification specification);

    /**
     * 保存规格与规格选项
     * @param specEntity 存放规格与规格选项集合的实体
     */
    void addSpec(specEntity specEntity);

    /**
     * 根据id查找规格和规格选项
     * @param id
     * @return
     */
    specEntity findSpecWithId(Long id);
    /**
     * 根据id查找规格和规格选项
     * @param specEntity 存放规格与规格选项集合的实体
     */
    void specUpdate(specEntity specEntity);

    /**
     * 删除（一个或多个）
     * @param idx
     */
    void specDelect(Long[] idx);

    /**
     * 加载规格选项列表
     * @return
     */
    List<Map> selectOptionList();
}
