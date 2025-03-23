package com.sensitive.biz.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sensitive.biz.entity.MinganciLishi;
import com.sensitive.biz.entity.Yonghu;
import com.sensitive.biz.mapper.MinganciLishiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinganciLishiService extends ServiceImpl<MinganciLishiMapper, MinganciLishi> {
    @Autowired
    private MinganciLishiMapper minganciLishiMapper;
    @Autowired
    private YonghuService yonghuService;

    public Page<MinganciLishi> pager(Integer n, Integer s, String title) {
        String loginId = ((String) StpUtil.getLoginId());
        Yonghu yonghu = yonghuService.getById(loginId);
        if (null == yonghu) {
            throw new RuntimeException("用户不存在");
        }
        if (yonghu.getRole().equalsIgnoreCase("admin")) {
            loginId = null;
        }
        Page<MinganciLishi> page = new Page<>(n, s);
        Page<MinganciLishi> selectedPage = minganciLishiMapper.selectPage(page, new LambdaQueryWrapper<MinganciLishi>()
                .eq(StrUtil.isNotBlank(loginId), MinganciLishi::getUserId, loginId)
                .eq(StrUtil.isNotBlank(title), MinganciLishi::getSource, title)
                .orderByDesc(MinganciLishi::getCreateTime));
        if (CollectionUtil.isNotEmpty(selectedPage.getRecords())) {
            selectedPage.getRecords().forEach(record -> {
                Yonghu y;
                record.setUsername(null != (y = yonghuService.getById(record.getUserId())) ?
                        y.getUsername() : record.getUserId().toString());
                record.setSource(record.getSource().equals("innerApi") ? "内部接口" : "外部开放接口");
                record.setType(record.getType().equals("text") ? "文本" : "文件");
            });
        }
        return selectedPage;
    }
}
