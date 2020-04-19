package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.fmjava.core.dao.good.GoodsDao;
import com.fmjava.core.dao.good.GoodsDescDao;
import com.fmjava.core.dao.item.ItemCatDao;
import com.fmjava.core.dao.item.ItemDao;
import com.fmjava.core.pojo.good.Goods;
import com.fmjava.core.pojo.good.GoodsDesc;
import com.fmjava.core.pojo.item.Item;
import com.fmjava.core.pojo.item.ItemCat;
import com.fmjava.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;


import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao descDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;


    @Autowired
    private ItemCatDao itemCatDao;

    private ServletContext servletContext;

    @Override
    public void createStaticPage(Long goodsID) throws Exception {
        //根据goodsid获取商品数据

        Map map = getGoodsDataWithGoodsId(goodsID);

        //创建静态页面

        //获取模板初始话对象
        Configuration configuration = freeMarkerConfig.getConfiguration();
        //获取模板对象
        Template template = configuration.getTemplate("item.ftl");

        String realPath = servletContext.getRealPath(goodsID + ".html");
//        String replace = realPath.replace("\\", "/");

        //创建输出流
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "utf-8");
        //生成静态页面
        template.process(map,out);

        out.close();
    }

    /**
     * 根据goodsid获取商品数据
     * @Author wuyong
     * @param goodsID
     */
    private Map getGoodsDataWithGoodsId(Long goodsID){
        HashMap<String, Object> map = new HashMap<>();
        //1根据id获取商品的数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsID);
        //查看商品的详情数据
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(goodsID);
        //查询库存数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsID);
        List<Item> itemList = itemDao.selectByExample(query);
        //获取分类数据
        if(null !=goods){
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());

            map.put("itemCat1",itemCat1.getName());
            map.put("itemCat2",itemCat2.getName());
            map.put("itemCat3",itemCat3.getName());

            //将查询的商品数据 添加到map中

            map.put("goods",goods);
            map.put("goodsDesc",goodsDesc);
            map.put("itemList",itemList);

            System.out.println(map);
        }
        return map;
    }


    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
