package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.template.TypeTemplate;
import com.fmjava.core.service.TemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/temp")
public class TemplateController {

    @Reference
    private TemplateService templateService;

    /*分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer pageSize, @RequestBody TypeTemplate template){
        PageResult pageResult=templateService.findPage(page,pageSize,template);
        return pageResult;
    }
    /*添加模板*/
    @RequestMapping("/tempSave")
    public Result tempSave(@RequestBody TypeTemplate template) {
        try {
            templateService.tempSave(template);
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }
    }
    /*更新模板*/
    @RequestMapping("/tempUpdate")
    public Result tempUpdate(@RequestBody TypeTemplate template) {
        try {
            templateService.tempUpdate(template);
            return new Result(true, "更新成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败!");
        }
    }
    /*删除模板*/
    @RequestMapping("/tempDelect")
    public Result tempDelect(Long[] idx) {
        try {
            templateService.tempDelect(idx);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }
    /*回显数据*/
    @RequestMapping("/tempEdit")
    public TypeTemplate tempEdit(@RequestBody TypeTemplate template) {
        return template;
    }

    /*查询所有*/
    @RequestMapping("/selectAll")
    public List<TypeTemplate> selectAll(){
        List<TypeTemplate> list=templateService.selectAll();
        return list;
    }
}
