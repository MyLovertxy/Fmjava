package com.fmjava.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.fmjava.consts.ResComConsts;
import com.fmjava.consts.ResComConsts.Search;
import com.fmjava.consts.ResComConsts.Filed;
import com.fmjava.core.pojo.item.Item;
import com.fmjava.core.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map paramMap) {
        //设置高亮查询
        Map<String, Object> map = this.setHighlightQuery(paramMap);
        //查询分类
        List category = this.findCategory(paramMap);

        map.put("categoryList",category);

        /*根据分类名称查询出品牌和规格
        * 先取出分类名称，如果没有就从查询出来的所有分类中取第一个
        *
        * */
        String categoryName = (String)paramMap.get("categoryName");
        boolean notEmpty = StringUtils.isNotEmpty(categoryName);
        if (StringUtils.isNotEmpty(categoryName)){
            Map brandAndSpec = this.findBrandAndSpecWithCategory(categoryName);
            map.putAll(brandAndSpec);
        }else {
            if (category != null && category.size()>0){
                String cate = (String) category.get(0);
                Map brandAndSpec = this.findBrandAndSpecWithCategory(cate);
                map.putAll(brandAndSpec);
            }

        }


        return map;
    }

    //根据分类名称查询出品牌和规格
    private Map findBrandAndSpecWithCategory(String categoryName) {
        //根据名称取出分类模板id
        Long tyid = (Long)redisTemplate.boundHashOps(ResComConsts.CATEGORY_LIST_REDIS).get(categoryName);
        //根据模板id取出品牌和规格

        if (null != tyid){
            BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(ResComConsts.BRAND_LIST_REDIS);
            List<Map> brandList = (List<Map>)redisTemplate.boundHashOps(ResComConsts.BRAND_LIST_REDIS).get(tyid);
            List<Map> specList = (List<Map>)redisTemplate.boundHashOps(ResComConsts.SPEC_LIST_REDIS).get(tyid);

            Map map = new HashMap();
            map.put("brandList",brandList);
            map.put("specList",specList);
            return map;
        }
        return null;
    }

    private Map<String,Object> setHighlightQuery(Map paramMap){
        //获取查询条件
        String keywords = String.valueOf(paramMap.get(Search.KEY_WORDS));
        Integer pageNo = Integer.parseInt(String.valueOf(paramMap.get(Search.PAGE_NOS)));
        Integer pageSize = Integer.parseInt(String.valueOf(paramMap.get(Search.PAGE_SIZE)));
        String price = String.valueOf(String.valueOf(paramMap.get(Search.PRICE)));

        //获取页面点击的分类过滤条件
        String category = String.valueOf(paramMap.get(Search.CATEGORY));
        //获取页面点击的品牌过滤条件
        String brand = String.valueOf(paramMap.get(Search.BRAND));
        //获取页面点击的规格过滤条件
        String spec = String.valueOf(paramMap.get(Search.SPEC));

        //条件查询
        SimpleHighlightQuery simpleQuery = new SimpleHighlightQuery();



        //根据分类过滤查询
        if (StringUtils.isNotEmpty(category)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria filterCriteria = new Criteria(Filed.CATEGORY).is(category);
            //将条件对象放入过滤对象中
            filterQuery.addCriteria(filterCriteria);
            //过滤对象放入查询对象中
            simpleQuery.addFilterQuery(filterQuery);
        }

        //根据品牌过滤查询
        if (StringUtils.isNotEmpty(brand)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria filterCriteria = new Criteria(Filed.BRAND).is(brand);
            //将条件对象放入过滤对象中
            filterQuery.addCriteria(filterCriteria);
            //过滤对象放入查询对象中
            simpleQuery.addFilterQuery(filterQuery);
        }

        //根据规格过滤查询 spec中的数据格式{网络:移动4G, 内存小大: 16G}
        if (StringUtils.isNotEmpty(spec)) {
            Map<String, String> speMap = JSON.parseObject(spec, Map.class);
            if (speMap != null && speMap.size() > 0) {
                Set<Map.Entry<String, String>> entries = speMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建条件对象
                    Criteria filterCriteria = new Criteria(Filed.SPEC + entry.getKey())
                            .is(entry.getValue());
                    //将条件对象放入过滤对象中
                    filterQuery.addCriteria(filterCriteria);
                    //过滤对象放入查询对象中
                    simpleQuery.addFilterQuery(filterQuery);
                }
            }
        }

        //根据价格过滤    0-500   500-1000    1000-1500   .....    3000 - *
        if (price != null && !"".equals(price)) {
            //切分价格, 这个素组中有最小值和最大值
            String[] split = price.split("-");
            if (split != null && split.length == 2) {
                //说明大于等于最小值, 如果第一个最小值为0, 进入不到这里
                if (!"0".equals(split[0])) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建条件对象
                    Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
                    //将条件对象放入过滤对象中
                    filterQuery.addCriteria(filterCriteria);
                    //过滤对象放入查询对象中
                    simpleQuery.addFilterQuery(filterQuery);
                }
                //说明小于等于最大值, 如果最后的元素也就是最大值为*, 进入不到这里
                if (!"*".equals(split[1])) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建条件对象
                    Criteria filterCriteria = new Criteria("item_price").lessThanEqual(split[1]);
                    //将条件对象放入过滤对象中
                    filterQuery.addCriteria(filterCriteria);
                    //过滤对象放入查询对象中
                    simpleQuery.addFilterQuery(filterQuery);
                }
            }
        }







        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField(Filed.TITLE);
        //设置前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        highlightOptions.setSimplePostfix("</em>");
        simpleQuery.setHighlightOptions(highlightOptions);
        Criteria criteria = new Criteria(Filed.KEY_WORDS).is(keywords);
        simpleQuery.addCriteria(criteria);

        //分页
        if (pageNo == null || pageNo<=0){
            pageNo = 1;
        }

        //计算当前查询位置
        Integer start = (pageNo-1)*pageSize;
        simpleQuery.setOffset(start);
        simpleQuery.setRows(pageSize);

        //查询，返回结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(simpleQuery, Item.class);

        //由于结果集封装的比较深，需要处理
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();

        List<Item> reslist = new ArrayList<>();
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //还没有高亮
            Item entity = itemHighlightEntry.getEntity();

            //获取高亮
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (null != highlights && highlights.size()>0){
                List<String> snipplets = highlights.get(0).getSnipplets();
                entity.setTitle(snipplets.get(0));
            }
            reslist.add(entity);

        }

        HashMap<String, Object> map = new HashMap<>();
        map.put(Search.ROWS,reslist);
        map.put(Search.TOTAL,items.getTotalElements());
        map.put(Search.TOTAL_PAGES,items.getTotalPages());
        return map;
    }

    //分类查询
    private List findCategory(Map paramMap) {

        String keywords = String.valueOf(paramMap.get(Search.KEY_WORDS));
        SimpleQuery simpleQuery = new SimpleQuery();
        Criteria criteria = new Criteria(Filed.KEY_WORDS).is(keywords);
        simpleQuery.addCriteria(criteria);

        //根据item_category分组  相当于去重
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField(Filed.CATEGORY);
        simpleQuery.setGroupOptions(groupOptions);

        List<String> list = new ArrayList<>();
        GroupPage<Item> items = solrTemplate.queryForGroupPage(simpleQuery, Item.class);
        GroupResult<Item> groupResult = items.getGroupResult(Filed.CATEGORY);
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            list.add(groupValue);
        }
        return list;

    }
}
