package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.consts.ResComConsts;
import com.fmjava.core.pojo.entity.GoodsEntity;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.good.Goods;
import com.fmjava.core.service.GoodsService;
import com.fmjava.core.service.SolrManagerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private SolrManagerService managerService;

    @RequestMapping("/addGoods")
    public Result addGoods(@RequestBody GoodsEntity goodsEntity){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(name);
            goodsService.addGoods(goodsEntity);
            return new Result(true, "添加成功");
        }catch (Exception e){
            return new Result(false, "添加失败");
        }
    }
    /*更新商品*/
    @RequestMapping("/updateGoods")
    public Result updateGoods(@RequestBody GoodsEntity goodsEntity) {
        try {
            //获取当前登录用户的用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取这个商品的所有者
            String sellerId = goodsEntity.getGoods().getSellerId();
            if (!userName.equals(sellerId)) {
                return new Result(false, "您没有权限修改此商品!");
            }
            goodsService.updateGoods(goodsEntity);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }
    /*删除商品*/
    @RequestMapping("/goodsDelete")
    public Result goodsDelete(Long[] ids) {
        try {
            goodsService.goodsDelete(ids);
            managerService.deleteItemByGoodsId(Arrays.asList(ids));
            return  new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "删除失败!");
        }
    }
    /*提交商品*/
    @RequestMapping("/auditSub")
    public Result auditSub(Long[] ids) {
        try {
            goodsService.auditSub(ids);
            return  new Result(true, "提交成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "提交失败!");
        }
    }

    /*分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer pageSize, @RequestBody Goods goods){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);
        PageResult pageResult=goodsService.findPage(page,pageSize,goods, ResComConsts.IsOperate.NO);
        return pageResult;
    }
    /*根据id查询一个商品*/
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id) {
        return goodsService.findOne(id);
    }
}
