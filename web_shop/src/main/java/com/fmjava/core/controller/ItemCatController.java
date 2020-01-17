package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.item.ItemCat;
import com.fmjava.core.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    @Reference
    private ItemCatService catService;
    /*根据parentid分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer pageSize, Long parentId, @RequestBody ItemCat itemCat) {
        PageResult result = catService.findPage(page,pageSize,parentId,itemCat);
        return result;
    }
    /*根据parentid查询*/
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId) {
        List<ItemCat> list = catService.findByParentId(parentId);
        return list;
    }

    /*添加分类*/
    @RequestMapping("/addItemCat")
    public Result addItemCat(@RequestBody ItemCat itemCat){
        try {
            catService.addItemCat(itemCat);
            return new Result(true, "添加成功");
        }catch (Exception e){
            return new Result(false, "添加失败");
        }

    }
    /*更新分类*/
    @RequestMapping("/updateItemCat")
    public Result updateItemCat(@RequestBody ItemCat itemCat){
        try {
            catService.updateItemCat(itemCat);
            return new Result(true, "更新成功");
        }catch (Exception e){
            return new Result(false, "更新失败");
        }

    }
    /*删除分类*/
    @RequestMapping("/delectItemCat")
    public Result delectItemCat(Long[] idx){
        try {
            int count = catService.delectItemCat(idx);
            return new Result(true, "删除成功, 成功记录数:"+count);
        }catch (Exception e){
            return new Result(false, "删除失败");
        }

    }

    /*根据parentId查询Parent*/
    @RequestMapping("/findParent")
    public ItemCat findParent(Long parentId) {
        ItemCat itemCat = catService.findParent(parentId);
        return itemCat;
    }

    /*查询所有*/
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return catService.findAll();
    }
}