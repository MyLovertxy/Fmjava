package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.fmjava.core.dao.ad.ContentCategoryDao;
import com.fmjava.core.pojo.ad.ContentCategory;
import com.fmjava.core.pojo.ad.ContentCategoryQuery;
import com.fmjava.core.pojo.entity.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ContentCatServiceImpl implements ContentCatService{
    @Autowired
    private ContentCategoryDao categoryDao;

    @Override
    public PageResult findPage(Integer page, Integer pageSize,ContentCategory category) {
        PageHelper.startPage(page, pageSize);
        ContentCategoryQuery query = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = query.createCriteria();
        if (category != null) {
            if (category.getName() != null && !"".equals(category.getName())) {
                criteria.andNameLike("%"+category.getName()+"%");
            }
        }
        Page<ContentCategory> categoryList = (Page<ContentCategory>)categoryDao.selectByExample(query);
        return new PageResult(categoryList.getTotal(), categoryList.getResult());
    }

    @Override
    public void update(ContentCategory category) {
        categoryDao.updateByPrimaryKeySelective(category);
    }

    @Override
    public void add(ContentCategory category) {
        categoryDao.insertSelective(category);
    }

    @Override
    public void delete(Long[] idx) {
        List<Long> list = Arrays.asList(idx);
        ContentCategoryQuery query = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = query.createCriteria();
        criteria.andIdIn(list);
        categoryDao.deleteByExample(query);
    }

    @Override
    public List<ContentCategory> findAll() {
        return categoryDao.selectByExample(null);
    }

}
