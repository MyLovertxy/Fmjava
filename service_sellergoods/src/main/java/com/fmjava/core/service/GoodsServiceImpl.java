package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.fmjava.consts.ResComConsts.*;
import com.fmjava.core.dao.good.BrandDao;
import com.fmjava.core.dao.good.GoodsDao;
import com.fmjava.core.dao.good.GoodsDescDao;
import com.fmjava.core.dao.item.ItemCatDao;
import com.fmjava.core.dao.item.ItemDao;
import com.fmjava.core.dao.seller.SellerDao;
import com.fmjava.core.pojo.entity.GoodsEntity;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.good.Brand;
import com.fmjava.core.pojo.good.Goods;
import com.fmjava.core.pojo.good.GoodsDesc;
import com.fmjava.core.pojo.good.GoodsQuery;
import com.fmjava.core.pojo.item.Item;
import com.fmjava.core.pojo.item.ItemCat;
import com.fmjava.core.pojo.item.ItemQuery;
import com.fmjava.core.pojo.seller.Seller;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao catDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SellerDao sellerDao;

    @Override
    public void addGoods(GoodsEntity goodsEntity) {
        Goods goods=goodsEntity.getGoods();
        //保存商品
        goods.setAuditStatus(AuditState.AUDIT_STATE_0);
        goodsDao.insertSelective(goods);

        //保存商品详情

        GoodsDesc goodsDesc = goodsEntity.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);

        //保存库存

        insertItem(goodsEntity);
    }

    @Override
    public PageResult findPage(Integer page, Integer pageSize, Goods goods, String isOperate) {
        PageHelper.startPage(page, pageSize);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        if (goods != null){
            if (goods.getGoodsName()!=null && !"".equals(goods.getGoodsName())){
                criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (goods.getAuditStatus()!=null && !"".equals(goods.getAuditStatus())){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getSellerId() != null && !"".equals(goods.getSellerId())){
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        criteria.andIsDeleteEqualTo(IsDelete.ISDElEtE_N);
        if (IsOperate.YES.equals(isOperate)){
            criteria.andAuditStatusNotEqualTo(AuditState.AUDIT_STATE_0);
        }
        Page<Goods> list =(Page<Goods>) goodsDao.selectByExample(query);
        PageResult result = new PageResult(list.getTotal(), list.getResult());
        return result;
    }

    @Override
    public GoodsEntity findOne(Long id) {
        //1. 根据商品id查询商品对象
        Goods goods = goodsDao.selectByPrimaryKey(id);
        //2. 根据商品id查询商品详情对象
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);

        //3. 根据商品id查询库存集合对象
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);

        //4. 将以上查询到的对象封装到GoodsEntity中返回
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setGoods(goods);
        goodsEntity.setGoodsDesc(goodsDesc);
        goodsEntity.setItemList(items);
        return goodsEntity;
    }

    @Override
    public void updateGoods(GoodsEntity goodsEntity) {
        //1. 修改商品对象
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        //2. 修改商品详情对象
        goodsDescDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        //3. 根据商品id删除对应的库存集合数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(query);
        //4. 添加库存集合数据
        insertItem(goodsEntity);
    }

    @Override
    public void goodsDelete(Long[] ids) {
        if (ids != null) {
            ItemQuery query = new ItemQuery();
            for (Long id : ids) {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setIsDelete(IsDelete.ISDElEtE_Y);
                goodsDao.updateByPrimaryKeySelective(goods);
                Item item = new Item();
                item.setStatus(State.DELETE);
                ItemQuery.Criteria criteria = query.createCriteria();
                criteria.andGoodsIdEqualTo(id);
                itemDao.updateByExampleSelective(item, query);
            }
        }
    }

    @Override
    public void auditSub(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setAuditStatus(AuditState.AUDIT_STATE_1);
                goodsDao.updateByPrimaryKeySelective(goods);
            }
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null) {
            for (Long id : ids) {
                //1. 根据商品id修改商品对象状态码
                Goods goods  = new Goods();
                goods.setId(id);
                goods.setAuditStatus(status);
                goodsDao.updateByPrimaryKeySelective(goods);

                //修改item表状态
                itemDao.updateStatusByGoodsId(id,status);
            }
        }
    }

    @Override
    public List<Item> findItemByGoodsIdAndState(Long[] ids, String status) {
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andGoodsIdIn(Arrays.asList(ids));
        List<Item> items = itemDao.selectByExample(itemQuery);
        return items;
    }

    /**
     * 保存库存信息
     * @param goodsEntity
     */
    private void insertItem(GoodsEntity goodsEntity){
        Goods goods=goodsEntity.getGoods();
        List<Item> itemList=goodsEntity.getItemList();
        //判断是否启用库存信息
        if ("1".equals(goods.getIsEnableSpec())){
            if (itemList != null){
                for (Item item : itemList) {
                    /*设置标题，由于是用来给别人搜索，所以这里我们以(商品名称+商品规格名称作为标题)*/
                    String title = goods.getGoodsName();
                    String spec = item.getSpec();
                    Map specMap = JSON.parseObject(spec);
                    Collection values = specMap.values();
                    for (Object value : values) {
                        title += value+"";
                    }
                    item.setTitle(title);
                    Item item1 = initItemValue(goodsEntity, item);
                    itemDao.insertSelective(item1);
                }
            }else {
                //没有勾选复选框, 没有库存数据, 但是我们需要初始化一条, 不然前端有可能报错
                Item item = new Item();
                //价格
                item.setPrice(new BigDecimal("99999999999"));
                //库存量
                item.setNum(0);
                //初始化规格
                item.setSpec("{}");
                //标题
                item.setTitle(goodsEntity.getGoods().getGoodsName());
                //设置库存对象的属性值
                initItemValue(goodsEntity, item);
                itemDao.insertSelective(item);
            }

        }
    }

    /**
     *
     * @param goodsEntity
     * @param item
     * @return
     */
    private Item initItemValue(GoodsEntity goodsEntity,Item item){
        Goods goods=goodsEntity.getGoods();

        //商品id
        item.setGoodsId(goods.getId());
        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(new Date());
        //库存状态, 默认为0, 未审核
        item.setStatus(AuditState.AUDIT_STATE_0);
        //分类id, 库存使用商品的第三级分类最为库存分类
        item.setCategoryid(goods.getCategory3Id());
        //分类名称
        ItemCat itemCat = catDao.selectByPrimaryKey(goods.getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌名称
        Brand brand = brandDao.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand.getName());
        //卖家名称
        Seller seller = sellerDao.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller.getName());
        //示例图片
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if (maps != null && maps.size() > 0) {
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }
}
