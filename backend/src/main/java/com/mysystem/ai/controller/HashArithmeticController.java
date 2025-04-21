package com.mysystem.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysystem.ai.entity.HashArithmetic;
import com.mysystem.ai.entity.Usage;
import com.mysystem.ai.model.Result;
import com.mysystem.ai.service.HashArithmeticService;
import com.mysystem.ai.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/hashArithmetic")
public class HashArithmeticController {
    @Autowired
    private HashArithmeticService hashArithmeticService;
    @Autowired
    private UsageService usageService;


    @GetMapping("/page")
    public Result<Page<HashArithmetic>> sessionPage(@RequestParam("page") Long page, @RequestParam("size") Long limit,
                                                    @RequestParam("name") String name) {
        return Result.success(hashArithmeticService.page(new Page<>(page, limit),
                new LambdaQueryWrapper<HashArithmetic>().like(StrUtil.isNotBlank(name), HashArithmetic::getName, name)));
    }

    @GetMapping("/list")
    public Result<Map<Long, String>> list() {
        return Result.success(hashArithmeticService.list().stream().collect(Collectors.toMap(HashArithmetic::getId, HashArithmetic::getName)));
    }

    @PostMapping
    public Result<Void> add(@RequestBody HashArithmetic hashArithmetic) {
        hashArithmetic.setKeyword(hashArithmetic.getKeyword().toUpperCase(Locale.ROOT));
        hashArithmetic.setUserId(StpUtil.getLoginIdAsLong());
        hashArithmeticService.getOneOpt(new LambdaQueryWrapper<HashArithmetic>().eq(HashArithmetic::getKeyword, hashArithmetic.getKeyword())).ifPresent(l->{
            throw new RuntimeException("算法已存在");
        });
        hashArithmeticService.save(hashArithmetic);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam("id") Long id) {
        hashArithmeticService.removeById(id);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody HashArithmetic hashArithmetic) {
        hashArithmeticService.updateById(hashArithmetic);
        return Result.success();
    }

    @GetMapping
    public Result<HashArithmetic> detail(@RequestParam("id") Long id) {
        return Result.success(hashArithmeticService.getById(id));
    }
}
