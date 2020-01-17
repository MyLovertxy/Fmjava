package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.seller.Seller;
import com.fmjava.core.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService service;

    @Autowired
    private PasswordEncoder passwordEncoder;
    /*注册商家*/
    @RequestMapping("/addSeller")
    public Result addSeller(@RequestBody Seller seller){
        try {
            String password = seller.getPassword();
            String encode = passwordEncoder.encode(password);
            seller.setPassword(encode);
            service.addSeller(seller);
            return new Result(true, "注册成功");
        }catch (Exception e){
            return new Result(false, "注册失败");
        }

    }
}
