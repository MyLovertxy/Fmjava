package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.fmjava.core.dao.specification.SpecificationDao;
import com.fmjava.core.dao.specification.SpecificationOptionDao;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.specEntity;
import com.fmjava.core.pojo.specification.Specification;
import com.fmjava.core.pojo.specification.SpecificationOption;
import com.fmjava.core.pojo.specification.SpecificationOptionQuery;
import com.fmjava.core.pojo.specification.SpecificationQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl implements SpecService{
    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult findPage(Integer page, Integer pageSize, Specification specification) {
        //分页进行查询
        PageHelper.startPage(page,pageSize);
        SpecificationQuery query = new SpecificationQuery();
        if(specification!=null){
            SpecificationQuery.Criteria criteria = query.createCriteria();
            if(specification.getSpecName()!=null&&!"".equals(specification.getSpecName())){
                criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
            }
        }
        Page<Specification> specifications =(Page<Specification>) specificationDao.selectByExample(query);
        PageResult pageResult = new PageResult(specifications.getTotal(), specifications.getResult());
        return pageResult;
    }

    @Override
    public void addSpec(specEntity specEntity) {
        //1.保存规格对象  获取id
        specificationDao.insertSelective(specEntity.getSpecification());

        for (SpecificationOption option : specEntity.getSpecOptionList()) {
            //2.设置规格选项对应的规格id
            option.setSpecId(specEntity.getSpecification().getId());
            // 3.保存规格选项
            specificationOptionDao.insertSelective(option);
        }

    }

    @Override
    public specEntity findSpecWithId(Long id) {
        //1.查询出规格
        Specification specification = specificationDao.selectByPrimaryKey(id);
        //2.根据规格id查询出规格选项
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());
        List<SpecificationOption> list = specificationOptionDao.selectByExample(query);
        //3.封装成实体返回
        specEntity entity = new specEntity();
        entity.setSpecification(specification);
        entity.setSpecOptionList(list);
        return entity;
    }

    @Override
    public void specUpdate(specEntity specEntity) {
        //更新规格对象
        specificationDao.updateByPrimaryKeySelective(specEntity.getSpecification());
        //打破之前的关系，根据specid删除规格选项
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);
        //重新建立关系
        for (SpecificationOption option : specEntity.getSpecOptionList()) {
            option.setSpecId(specEntity.getSpecification().getId());
            specificationOptionDao.insertSelective(option);
        }
    }

    @Override
    public void specDelect(Long[] idx) {
        if(idx!=null){

            for (Long id : idx) {
                //删除规格
                specificationDao.deleteByPrimaryKey(id);
                //删除规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(id);
                specificationOptionDao.deleteByExample(query);
            }
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
