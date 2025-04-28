package com.myhousekeepingservice.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.entity.ServiceTempo;
import com.myhousekeepingservice.model.Result;
import com.myhousekeepingservice.service.IServiceTempoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.myhousekeepingservice.entity.Comment;
import com.myhousekeepingservice.service.ICommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final ICommentService commentService;
    private final IServiceTempoService serviceTempoService;

    @GetMapping("/list")
    public Result<List<Comment>> list() {
        return Result.success(commentService.list());
    }

    @GetMapping("/page")
    public Result<Page<Comment>> page(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize) {
        return Result.success(commentService.page(new Page<>(pageNum, pageSize)));
    }

    @PostMapping
    public Result<Void> save(@Validated @RequestBody Comment entity) {
        ServiceTempo serviceOne = serviceTempoService.getOne(new LambdaQueryWrapper<ServiceTempo>().eq(ServiceTempo::getProjectId, entity.getPreOrderId()).last("limit 1"));
        if (serviceOne == null) {
            throw new RuntimeException("预定单信息不存在");
        }
        if (serviceOne.getState() != 3) {
            throw new RuntimeException("只有已完成的订单才允许进行评价，请先完成订单！");
        }
        entity.setUserId(StpUtil.getLoginIdAsLong());
        commentService.save(entity);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Validated @RequestBody Comment entity) {
        commentService.updateById(entity);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam("id") Long id) {
        commentService.removeById(id);
        return Result.success();
    }
}
