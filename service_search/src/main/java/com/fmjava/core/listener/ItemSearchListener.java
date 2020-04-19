package com.fmjava.core.listener;

import com.alibaba.fastjson.JSON;
import com.fmjava.core.dao.item.ItemDao;
import com.fmjava.core.pojo.item.Item;
import com.fmjava.core.pojo.item.ItemQuery;
import com.fmjava.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener {
    @Autowired
    private SolrManagerService solrManagerService;
    @Autowired
    private ItemDao itemDao;
    @Override
    public void onMessage(Message message) {
        //为了方便获取文本消息, 将原生的消息对象转换成activeMq的文本消息对象
        ActiveMQObjectMessage activeMQObjectMessage = (ActiveMQObjectMessage)message;
        try {
            Long[] ids= (Long[])activeMQObjectMessage.getObject();
            for (Long id : ids) {
                saveItemToSolr(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveItemToSolr(Long goodsId){
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        //查询指定商品的库存数据
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> items = itemDao.selectByExample(query);
        if (items != null) {
            for (Item item : items) {
                //获取规格json格式字符串
                String specJsonStr = item.getSpec();
                Map map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
        }
        solrManagerService.saveItemToSolr(items);
    }
}
