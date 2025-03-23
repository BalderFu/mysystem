package com.sensitive.biz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sensitive.biz.model.Result;
import com.sensitive.biz.entity.Minganci;
import com.sensitive.biz.model.MinganciModel;
import com.sensitive.biz.model.Statistic;
import com.sensitive.biz.model.Text;
import com.sensitive.biz.service.MInganciService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/minganci")
public class MinGanCiController {
    @Autowired
    private MInganciService minganciService;

    @PostMapping("add")
    public Result<Void> addMinganci(@Validated @RequestBody MinganciModel minganciModel) {
        minganciService.addMinganci(minganciModel);
        return Result.success();
    }

    @GetMapping("/pager")
    public Result<Page<Minganci>> pager(@RequestParam("n") Integer n, @RequestParam("s") Integer s) {
        return Result.success(minganciService.pager(n, s));
    }

    @PutMapping("/update")
    public Result<Void> updateMinganci(@Validated @RequestBody MinganciModel minganciModel) {
        if (minganciService.update(minganciModel) < 1) {
            throw new RuntimeException("更新失败");
        }
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<Void> deleteMinganci(@RequestParam("id") Long id) {
        if (!minganciService.removeById(id)) {
            throw new RuntimeException("删除失败");
        }
        return Result.success();
    }

    @PostMapping("/check")
    public Result<Statistic> check(@Validated @RequestBody Text text) {
        return Result.success(minganciService.check(text.getContent(), "", text.getType(), "innerApi"));
    }

}
