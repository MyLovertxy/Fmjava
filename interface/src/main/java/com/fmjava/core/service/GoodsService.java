package com.fmjava.core.service;

import com.fmjava.core.pojo.entity.GoodsEntity;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.good.Goods;
import com.fmjava.core.pojo.item.Item;

import java.util.List;

public interface GoodsService {
    /**
     * 商品添加
     * @param goodsEntity 自定义实体（存放商品基本信息，详情信息以及库存信息）
     */
    void addGoods(GoodsEntity goodsEntity);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param goods
     * @param isOperate
     * @return
     */
    PageResult findPage(Integer page, Integer pageSize, Goods goods, String isOperate);

    /**
     * 根据id查询商品的基本信息，详细信息以及库存信息
     * @param id
     * @return
     */
    GoodsEntity findOne(Long id);
    /**
     * 商品更新
     * @param goodsEntity 自定义实体（存放商品基本信息，详情信息以及库存信息）
     */
    void updateGoods(GoodsEntity goodsEntity);

    /**
     * 删除商品
     * @param ids 商品id集合
     */
    void goodsDelete(Long[] ids);

    /**
     * 提交商品交给运营商审核
     * @param ids
     */
    void auditSub(Long[] ids);

    /**
     * 运营商审核商品 （审核通过、驳回)
     * @param ids
     */
    void updateStatus(Long[] ids,String status);

    /**
     * 根据goodsid和审核状态查询
     * @param ids
     */
    List<Item> findItemByGoodsIdAndState (Long[] ids ,String status);
}
