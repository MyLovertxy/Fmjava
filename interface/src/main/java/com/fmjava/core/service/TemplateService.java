package com.fmjava.core.service;

import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    /**
     * 分页查询
     * @param page 当前页
     * @param pageSize 一页查询的数量
     * @param template 模板实体(搜索框内容）
     * @return
     */
    PageResult findPage(Integer page, Integer pageSize, TypeTemplate template);

    /**
     * 添加模板
     * @param template 模板实体
     */
    void tempSave(TypeTemplate template);

    /**
     * 更新模板
     * @param template 模板实体
     */
    void tempUpdate(TypeTemplate template);

    /**
     * 删除模板
     * @param idx 存放多个模板id的集合
     */
    void tempDelect(Long[] idx);

    /**
     * 查询所有
     * @return
     */
    List<TypeTemplate> selectAll();

    /**
     * 根据主键查询
     * @return
     */
    TypeTemplate findOne(Long id);

    /**
     * 根据模板id查询出规格及规格选项
     * @param id 模板id
     * @return
     */
    List<Map> findSpecListBytempId(long id);
}
