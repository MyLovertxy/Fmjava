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
import java.util.Map;

@RestController
@RequestMapping("/temp")
public class TemplateController {

    @Reference
    private TemplateService templateService;

    /*单个查询*/
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return templateService.findOne(id);
    }

    /*根据模板id查询规格及其对应规格选项*/
    @RequestMapping("findSpecListBytempId")
    public List<Map> findSpecListBytempId(long id){
        List<Map> list=templateService.findSpecListBytempId(id);
        return list;
    }
}
