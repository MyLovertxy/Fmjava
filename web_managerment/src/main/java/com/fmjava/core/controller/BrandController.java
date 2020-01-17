package com.fmjava.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.core.pojo.entity.Result;
import com.fmjava.core.pojo.good.Brand;
import com.fmjava.core.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;
    @RequestMapping("/findAllBrands")
    public List<Brand> getBrands(){
        return brandService.findAllBrands();
    }

    /*分页查询*/
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page,Integer pageSize,@RequestBody Brand brand){
        PageResult pageResult=brandService.findPage(page,pageSize,brand);
        return pageResult;
    }
    /*解决数据回显双向绑定问题*/
    @RequestMapping("/removeBind")
    public Brand removeBind(@RequestBody Brand brand){
        return brand;
    }
    /*添加品牌*/
    @RequestMapping("/addBrand")
    public Result addBrand(@RequestBody Brand brand){
        try {
            brandService.addBrand(brand);
           return new Result(true, "添加成功");
        }catch (Exception e){
            return new Result(false, "添加失败");
        }

    }
    /*更新品牌*/
    @RequestMapping("/updateBrand")
    public Result updateBrand(@RequestBody Brand brand){
        try {
            brandService.updateBrand(brand);
           return new Result(true, "修改成功");
        }catch (Exception e){
            return new Result(false, "修改失败");
        }

    }
    /*删除品牌*/
    @RequestMapping("/deleteBrand")
    public Result deleteBrand(Long[] idx){
        try {
            brandService.deleteBrand(idx);
           return new Result(true, "删除成功");
        }catch (Exception e){
            return new Result(false, "删除失败");
        }

    }
    /*加载品牌选项列表*/
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
