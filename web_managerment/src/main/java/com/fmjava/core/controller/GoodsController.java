package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.consts.ResComConsts;
import com.fmjava.core.pojo.entity.GoodsEntity;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.good.Goods;
import com.fmjava.core.pojo.item.Item;
import com.fmjava.core.service.GoodsService;
import com.fmjava.core.service.SolrManagerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private SolrManagerService managerService;
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
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status) {
        try {
            goodsService.updateStatus(ids,status);

            if (ResComConsts.AuditState.AUDIT_STATE_2.equals(status)){
                List<Item> items = goodsService.findItemByGoodsIdAndState(ids, status);

                //保存索引库
                managerService.saveItemToSolr(items);
            }
            return  new Result(true, "操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, "操作失败!");
        }
    }

    /*分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer pageSize, @RequestBody Goods goods){
        PageResult pageResult=goodsService.findPage(page,pageSize,goods, ResComConsts.IsOperate.YES);
        return pageResult;
    }
    /*根据id查询一个商品*/
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id) {
        return goodsService.findOne(id);
    }
}
