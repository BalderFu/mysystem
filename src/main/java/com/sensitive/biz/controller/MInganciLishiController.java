package com.sensitive.biz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sensitive.biz.model.Result;
import com.sensitive.biz.entity.MinganciLishi;
import com.sensitive.biz.service.MinganciLishiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lishi")
public class MInganciLishiController {
    @Autowired
    private MinganciLishiService minganciLishiService;

    @GetMapping("/pagger")
    public Result<Page<MinganciLishi>> pager(@RequestParam("n") Integer n, @RequestParam("s") Integer s, @RequestParam("t") String title) {
        return Result.success(minganciLishiService.pager(n, s, title));
    }
}
