package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.ad.Content;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.service.ContentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    /*分页查询*/

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

    @RequestMapping("/findPage")
    public PageResult search(Integer page, Integer pageSize, @RequestBody Content content) {
        PageResult result = contentService.findPage(content, page, pageSize);
        return result;
    }
    /*添加广告*/
    @RequestMapping("/add")
    public Result add(@RequestBody Content content) {
        try {
            System.out.println("content="+content);
            contentService.add(content);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }
    /*修改广告*/
    @RequestMapping("/update")
    public Result update(@RequestBody Content content) {
        try {
            contentService.update(content);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }
    /*删除广告*/
    @RequestMapping("/delete")
    public Result delete(Long[] idx) {
        try {
            contentService.delete(idx,fileServerUrl);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

}
