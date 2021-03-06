package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.fmjava.core.dao.specification.SpecificationOptionDao;
import com.fmjava.core.dao.template.TypeTemplateDao;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.specification.SpecificationOption;
import com.fmjava.core.pojo.specification.SpecificationOptionQuery;
import com.fmjava.core.pojo.template.TypeTemplate;
import com.fmjava.core.pojo.template.TypeTemplateQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class TemplateServiceImpl implements TemplateService{
    @Autowired
    private TypeTemplateDao templateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult findPage(Integer page, Integer pageSize, TypeTemplate template) {
        //分页进行查询
        PageHelper.startPage(page,pageSize);
        TypeTemplateQuery query = new TypeTemplateQuery();
        if(template!=null){
            TypeTemplateQuery.Criteria criteria = query.createCriteria();
            if(template.getName()!=null&&!"".equals(template.getName())){
                criteria.andNameLike("%"+template.getName()+"%");
            }
        }
        Page<TypeTemplate> templates =(Page<TypeTemplate>) templateDao.selectByExample(query);
        PageResult pageResult = new PageResult(templates.getTotal(), templates.getResult());
        return pageResult;
    }

    @Override
    public void tempSave(TypeTemplate template) {
        templateDao.insertSelective(template);
    }

    @Override
    public void tempUpdate(TypeTemplate template) {
        templateDao.updateByPrimaryKeySelective(template);
    }

    @Override
    public void tempDelect(Long[] idx) {
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        criteria.andIdIn(Arrays.asList(idx));
        templateDao.deleteByExample(query);
    }

    @Override
    public List<TypeTemplate> selectAll() {
        return templateDao.selectByExample(null);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return templateDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Map> findSpecListBytempId(long id) {
        //查询出模板
        TypeTemplate template = templateDao.selectByPrimaryKey(id);
        //获取规格
        if (template!=null){
            String specIds = template.getSpecIds();
            //字符串转集合
            List<Map> specList = JSON.parseArray(specIds, Map.class);
            for (Map map : specList) {
                //获取规格id
                Object specIdObj = map.get("id");
                long specId = Long.parseLong(String.valueOf(specIdObj));
                //根据规格id获取规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                map.put("SpecificationOption",options);
            }
            return specList;
        }
        return null;
    }
}
