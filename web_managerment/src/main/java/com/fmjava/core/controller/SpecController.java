package com.fmjava.core.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.entity.specEntity;
import com.fmjava.core.pojo.good.Brand;
import com.fmjava.core.pojo.specification.Specification;
import com.fmjava.core.service.SpecService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spec")
public class SpecController {
    @Reference
    private SpecService specService;
    /*分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer pageSize, @RequestBody Specification specification){
        PageResult pageResult=specService.findPage(page,pageSize,specification);
        return pageResult;
    }
    /*添加规格*/
    @RequestMapping("/specSave")
    public Result specSave(@RequestBody specEntity specEntity){
        try {
            specService.addSpec(specEntity);
            return new Result(true, "添加成功");
        }catch (Exception e){
            return new Result(false, "添加失败");
        }

    }
    /*更新规格及对应规格选项*/
    @RequestMapping("/specUpdate")
    public Result specUpdate(@RequestBody specEntity specEntity){
        try {
            specService.specUpdate(specEntity);
            return new Result(true, "更新成功");
        }catch (Exception e){
            return new Result(false, "更新失败");
        }

    }
    /*删除规格及对应规格选项*/
    @RequestMapping("/specDelect")
    public Result specDelect(Long[] idx){
        try {
            specService.specDelect(idx);
            return new Result(true, "删除成功");
        }catch (Exception e){
            return new Result(false, "删除失败");
        }

    }
    /*根据id查找规格和规格选项*/
    @RequestMapping("/findSpecWithId")
    public specEntity findSpecWithId(Long id){
        return specService.findSpecWithId(id);
    }

    /*加载规格选项列表*/
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specService.selectOptionList();
    }
}
