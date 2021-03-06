package com.fmjava.core.service;

import com.fmjava.Exception.ImageException;
import com.fmjava.core.pojo.ad.Content;
import com.fmjava.core.pojo.entity.PageResult;

public interface ContentService {
    /**
     * 分页查询
     * @param content
     * @param page
     * @param pageSize
     * @return
     */
    PageResult findPage(Content content, Integer page, Integer pageSize);

    /**
     * 添加广告
     * @param content
     */
    void add(Content content);

    /**
     * 修改广告
     * @param content
     */
    void update(Content content);

    /**
     * 删除广告
     * @param idx
     */
    void delete(Long[] idx,String fileServerUrl) throws ImageException;
}
