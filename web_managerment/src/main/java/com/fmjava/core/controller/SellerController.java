package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.seller.Seller;
import com.fmjava.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;

    /*分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer pageSize, @RequestBody Seller seller){
        PageResult pageResult=sellerService.findPage(page,pageSize,seller);
        return pageResult;
    }
    /*数据回显*/
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        Seller seller=sellerService.findOne(id);
        return seller;
    }
    /*商家审核*/
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true, "操作成功");
        }catch (Exception e){
            return new Result(true, "操作失败");
        }
    }
}
