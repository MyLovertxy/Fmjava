package com.fmjava.core.service;

import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.seller.Seller;

public interface SellerService {
    /**
     * 商家注册
     * @param seller 商家实体
     */
    void addSeller(Seller seller);


    /**
     * 查询商家列表
     * @param page
     * @param pageSize
     * @param seller
     * @return
     */
    PageResult findPage(Integer page, Integer pageSize, Seller seller);

    /**
     * 根据id拆查询
     * @param id
     * @return
     */
    Seller findOne(String id);

    /**
     * 商家审核
     * @param sellerId 商家id
     * @param status 审核后状态
     */
    void updateStatus(String sellerId, String status);
}
