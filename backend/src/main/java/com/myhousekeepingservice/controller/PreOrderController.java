package com.myhousekeepingservice.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.entity.ServiceTempo;
import com.myhousekeepingservice.model.Result;
import com.myhousekeepingservice.service.IServiceTempoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.myhousekeepingservice.entity.PreOrder;
import com.myhousekeepingservice.service.IPreOrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/preOrder")
public class PreOrderController {

    private final IPreOrderService preOrderService;
    private final IServiceTempoService serviceTempoService;

    @GetMapping("/list")
    public Result<List<PreOrder>> list() {
        return Result.success(preOrderService.list());
    }

    @GetMapping("/page")
    public Result<Page<PreOrder>> page(@RequestParam("pageNum") Integer pageNum, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "phone", required = false) String phone,
                                       @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "projectId", required = false) Long projectId) {
        return Result.success(preOrderService.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<PreOrder>().like(StrUtil.isNotBlank(name),PreOrder::getName, name).eq(StrUtil.isNotBlank(phone), PreOrder::getPhone, phone).eq(ObjectUtil.isNotNull(projectId),PreOrder::getProjectId, projectId)));
    }

    @PostMapping
    @Transactional
    public Result<Void> save(@Validated @RequestBody PreOrder entity) {
        preOrderService.save(entity);
        ServiceTempo tempo = new ServiceTempo();
        tempo.setProjectId(entity.getId());
        tempo.setState(1);
        serviceTempoService.save(tempo);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Validated @RequestBody PreOrder entity) {
        preOrderService.updateById(entity);
        serviceTempoService.update(new LambdaUpdateWrapper<ServiceTempo>().eq(ServiceTempo::getProjectId, entity.getId()).set(ServiceTempo::getState, 1));
        return Result.success();
    }

    @DeleteMapping
    @Transactional
    public Result<Void> delete(@RequestParam("id") Long id) {
        preOrderService.removeById(id);
        serviceTempoService.remove(new LambdaQueryWrapper<ServiceTempo>().eq(ServiceTempo::getProjectId, id));
        return Result.success();
    }
}
