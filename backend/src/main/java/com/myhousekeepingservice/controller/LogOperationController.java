package com.myhousekeepingservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.entity.LogOperation;
import com.myhousekeepingservice.model.Result;
import com.myhousekeepingservice.service.LogOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogOperationController {
    @Autowired
    private LogOperationService logOperationService;

    @GetMapping
    public Result<Page<LogOperation>> page(@RequestParam("pageNo") Long pageNo, @RequestParam("pageSize") Long pageSize) {
        return Result.success(logOperationService.pager(pageNo, pageSize));
    }
}
