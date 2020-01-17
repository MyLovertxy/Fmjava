package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.ad.ContentCategory;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.service.ContentCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCat")
public class ContentCatController {

    @Reference
    private ContentCatService contentCatService;

    /*分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer pageSize, @RequestBody ContentCategory category){
        PageResult pageResult=contentCatService.findPage(page,pageSize,category);
        return pageResult;
    }
    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory category){
        try {
            contentCatService.update(category);
            return new Result(true, "修改成功");
        }catch (Exception e){
            return new Result(false, "修改失败");
        }
    }
    /*添加*/
    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory category){
        try {
            contentCatService.add(category);
            return new Result(true, "添加成功");
        }catch (Exception e){
            return new Result(false, "添加失败");
        }
    }
    /*删除*/
    @RequestMapping("/delete")
    public Result delete(Long[] idx){
        try {
            contentCatService.delete(idx);
            return new Result(true, "删除成功");
        }catch (Exception e){
            return new Result(false, "删除失败");
        }
    }

    /*查询所有*/
    @RequestMapping("/findAll")
    public List<ContentCategory> findAll(){
        return contentCatService.findAll();
    }
}
