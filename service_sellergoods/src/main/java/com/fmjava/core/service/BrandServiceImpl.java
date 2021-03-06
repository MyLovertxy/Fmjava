package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.fmjava.core.dao.good.BrandDao;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.good.Brand;
import com.fmjava.core.pojo.good.BrandQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService{
    @Autowired
    private BrandDao brandDao;
    @Override
    public List<Brand> findAllBrands() {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer page, Integer pageSize, Brand brand) {
        //分页进行查询
        PageHelper.startPage(page,pageSize);
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        if(brand!=null){
            if(brand.getFirstChar()!=null&&!"".equals(brand.getFirstChar())){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
            if(brand.getName()!=null&&!"".equals(brand.getName())){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
        }
        brandQuery.setOrderByClause("id desc");
        Page<Brand> brands =(Page<Brand>) brandDao.selectByExample(brandQuery);
        PageResult pageResult = new PageResult(brands.getTotal(), brands.getResult());
        return pageResult;
    }

    @Override
    public void addBrand(Brand brand) {
        brandDao.insertSelective(brand);
    }

    @Override
    public void updateBrand(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void deleteBrand(Long[] idx) {
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        criteria.andIdIn(Arrays.asList(idx));
        brandDao.deleteByExample(brandQuery);
    }

    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }
}
