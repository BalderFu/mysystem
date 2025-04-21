package com.mysystem.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysystem.ai.entity.HashDict;
import com.mysystem.ai.model.DictResp;
import com.mysystem.ai.model.CrackReq;
import com.mysystem.ai.model.Result;
import com.mysystem.ai.service.HashArithmeticService;
import com.mysystem.ai.service.HashDictService;
import com.mysystem.ai.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dict")
public class HashDictController {
    @Autowired
    private HashDictService hashDictService;
    @Autowired
    private UserService userService;
    @Autowired
    private HashArithmeticService hashArithmeticService;

    @GetMapping("/page")
    public Result<Page<DictResp>> sessionPage(@RequestParam("page") Long page, @RequestParam("size") Long limit,
                                              @RequestParam(value = "userId",required = false) Long userId, @RequestParam(value = "arithmeticId",required = false) Long arithmeticId) {
        Page<HashDict> paged = hashDictService.page(new Page<>(page, limit),
                new LambdaQueryWrapper<HashDict>().eq(ObjectUtil.isNotNull(userId), HashDict::getUserId, userId).eq(ObjectUtil.isNotNull(arithmeticId), HashDict::getArithmeticId, arithmeticId));
        List<DictResp> list = paged.getRecords().stream().map(dict -> {
            DictResp dictResp = new DictResp();
            BeanUtils.copyProperties(dict, dictResp);
            dictResp.setArithmeticName(hashArithmeticService.getById(dict.getArithmeticId()).getName());
            dictResp.setUsername(userService.getById(dict.getUserId()).getUsername());
            return dictResp;
        }).collect(Collectors.toList());
        Page<DictResp> dictRespPage = new Page<>(page, limit, paged.getTotal());
        dictRespPage.setRecords(list);
        return Result.success(dictRespPage);
    }


    public static void main(String[] args) throws NoSuchMethodException {
        Method md5Hex = DigestUtil.class.getMethod("md5Hex",String.class);
        Object md5Hex1 = ReflectUtil.invokeStatic(md5Hex, "123456", DigestUtil.class);
        System.out.println(md5Hex1);
    }


    @PostMapping("/crack")
    public Result<List<String>> crack(@RequestBody CrackReq crackReq) {
        return Result.success(hashDictService.crack(crackReq));
    }

    @PostMapping
    public Result<Void> add(@RequestBody HashDict hashDict) {
        hashDictService.saveDict(hashDict);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam("id") Long id) {
        hashDictService.removeById(id);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody HashDict HashDict) {
        hashDictService.updateById(HashDict);
        return Result.success();
    }

    @GetMapping
    public Result<HashDict> detail(@RequestParam("id") Long id) {
        return Result.success(hashDictService.getById(id));
    }
}
