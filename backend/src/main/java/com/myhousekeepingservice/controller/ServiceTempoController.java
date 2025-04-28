package com.myhousekeepingservice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.model.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.myhousekeepingservice.entity.ServiceTempo;
import com.myhousekeepingservice.service.IServiceTempoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/serviceTempo")
public class ServiceTempoController {

    private final IServiceTempoService serviceTempoService;

    @GetMapping("/list")
    public Result<List<ServiceTempo>> list() {
        return Result.success(serviceTempoService.list());
    }

    @GetMapping("/page")
    public Result<Page<ServiceTempo>> page(@RequestParam("pageNum") Integer pageNum, @RequestParam(value = "projectId", required = false) Long projectId,
                                           @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "state", required = false) Integer state) {
        return Result.success(serviceTempoService.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<ServiceTempo>().eq(ObjectUtil.isNotNull(projectId), ServiceTempo::getProjectId, projectId).eq(ObjectUtil.isNotNull(state), ServiceTempo::getState, state)));
    }

    @PostMapping
    public Result<Void> save(@Validated @RequestBody ServiceTempo entity) {
        serviceTempoService.save(entity);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Validated @RequestBody ServiceTempo entity) {
        serviceTempoService.updateById(entity);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam("id") Long id) {
        serviceTempoService.removeById(id);
        return Result.success();
    }
}
