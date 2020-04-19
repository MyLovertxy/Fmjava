package com.fmjava.core.util;

import com.alibaba.fastjson.JSON;
import com.fmjava.consts.ResComConsts;
import com.fmjava.core.dao.item.ItemDao;
import com.fmjava.core.pojo.item.Item;
import com.fmjava.core.pojo.item.ItemQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class DataImportToSolr {
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemToSolr(){
        //查询所有的库存数据（审核通过的）
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andStatusEqualTo(ResComConsts.AuditState.AUDIT_STATE_2);
        List<Item> items = itemDao.selectByExample(itemQuery);

        if (null != items){
            //处理规格
            for (Item item:items){
                String spec = item.getSpec();
                //string转map
                Map map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
            // 保存到索引库
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }


    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportToSolr dataImportToSolr = (DataImportToSolr)applicationContext.getBean("dataImportToSolr");
        dataImportToSolr.importItemToSolr();
    }
}
