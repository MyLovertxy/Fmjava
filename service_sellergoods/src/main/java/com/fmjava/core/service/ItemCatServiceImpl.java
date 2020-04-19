package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.fmjava.consts.ResComConsts;
import com.fmjava.core.dao.item.ItemCatDao;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.item.ItemCat;
import com.fmjava.core.pojo.item.ItemCatQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

import static com.fmjava.consts.ResComConsts.CATEGORY_LIST_REDIS;

@Service
public class ItemCatServiceImpl implements ItemCatService{
    @Autowired
    private ItemCatDao catDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult findPage(Integer page, Integer pageSize, Long parentId, ItemCat itemCat) {
        //分页进行查询
        PageHelper.startPage(page,pageSize);
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        if(itemCat!=null){
            if(itemCat.getName()!=null&&!"".equals(itemCat.getName())){
                criteria.andNameLike("%"+itemCat.getName()+"%");
            }
        }

        criteria.andParentIdEqualTo(parentId);
        Page<ItemCat> itemCats =(Page<ItemCat>) catDao.selectByExample(query);
        return new PageResult(itemCats.getTotal(),itemCats.getResult());
    }

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        //缓存数据
        if (!redisTemplate.hasKey(CATEGORY_LIST_REDIS)){
            List<ItemCat> itemCatsAll = catDao.selectByExample(null);
            for (ItemCat itemCat : itemCatsAll) {
                redisTemplate.boundHashOps(CATEGORY_LIST_REDIS).put(itemCat.getName(),itemCat.getTypeId());
            }
        }



        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<ItemCat> itemCats =catDao.selectByExample(query);
        return itemCats;
    }

    @Override
    public void addItemCat(ItemCat itemCat) {
        catDao.insertSelective(itemCat);
    }

    @Override
    public ItemCat findParent(Long parentId) {
        ItemCat itemCat = catDao.selectByPrimaryKey(parentId);
        return itemCat;
    }

    @Override
    public void updateItemCat(ItemCat itemCat) {
        catDao.updateByPrimaryKeySelective(itemCat);
    }

    @Override
    public int delectItemCat(Long[] idx) {
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andIdIn(Arrays.asList(idx));
        int count = catDao.deleteByExample(query);
        return count;
    }

    @Override
    public List<ItemCat> findAll() {
        return catDao.selectByExample(null);
    }
}
