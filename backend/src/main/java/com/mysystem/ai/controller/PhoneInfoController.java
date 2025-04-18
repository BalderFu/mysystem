package com.mysystem.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.mysystem.ai.entity.PhoneInfo;
import com.mysystem.ai.model.PhoneInfoReq;
import com.mysystem.ai.model.Result;
import com.mysystem.ai.service.PhoneInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/phoneInfo")
public class PhoneInfoController {
    @Autowired
    private PhoneInfoService phoneInfoService;

    @PostMapping
    public Result<Void> save(@RequestBody @Validated PhoneInfoReq phoneInfoReq) {
        PhoneInfo phoneInfo = new PhoneInfo();
        BeanUtils.copyProperties(phoneInfoReq, phoneInfo);
        phoneInfo.setUserId(StpUtil.getLoginIdAsLong());
        phoneInfoService.save(phoneInfo);
        return Result.success();
    }
}
