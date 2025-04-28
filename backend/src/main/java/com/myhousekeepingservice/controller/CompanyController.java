package com.myhousekeepingservice.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.model.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.myhousekeepingservice.entity.Company;
import com.myhousekeepingservice.service.ICompanyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final ICompanyService companyService;

    @GetMapping("/list")
    public Result<List<Company>> list() {
        return Result.success(companyService.list());
    }

    @GetMapping("/page")
    public Result<Page<Company>> page(@RequestParam("pageNum") Integer pageNum, @RequestParam(value = "name", required = false) String name,
                                      @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "state", required = false) Boolean state) {
        return Result.success(companyService.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Company>().like(StrUtil.isNotBlank(name), Company::getName, name).eq(ObjectUtil.isNotNull(state), Company::getState, state)));
    }

    @PostMapping
    public Result<Void> save(@Validated @RequestBody Company entity) {
        companyService.save(entity);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Validated @RequestBody Company entity) {
        companyService.updateById(entity);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam("id") Long id) {
        companyService.removeById(id);
        return Result.success();
    }
}
