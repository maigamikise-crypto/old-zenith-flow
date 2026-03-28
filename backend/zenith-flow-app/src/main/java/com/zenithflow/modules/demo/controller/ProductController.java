package com.zenithflow.modules.demo.controller;

import com.zenithflow.common.annotation.LogOperation;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.utils.Result;
import com.zenithflow.common.validator.AssertUtils;
import com.zenithflow.common.validator.ValidatorUtils;
import com.zenithflow.common.validator.group.AddGroup;
import com.zenithflow.common.validator.group.DefaultGroup;
import com.zenithflow.common.validator.group.UpdateGroup;
import com.zenithflow.modules.demo.dto.ProductDTO;
import com.zenithflow.modules.demo.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


/**
 * 产品管理
 *
 *
 */
@RestController
@RequestMapping("demo/product")
@Api(tags="产品管理")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("demo:product:page")
    public Result<PageData<ProductDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ProductDTO> page = productService.page(params);

        return new Result<PageData<ProductDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("demo:product:info")
    public Result<ProductDTO> get(@PathVariable("id") Long id){
        ProductDTO data = productService.get(id);

        return new Result<ProductDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("demo:product:save")
    public Result save(@RequestBody ProductDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        productService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("demo:product:update")
    public Result update(@RequestBody ProductDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        productService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("demo:product:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        productService.delete(ids);

        return new Result();
    }

}
