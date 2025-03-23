package com.sensitive.biz.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sensitive.biz.utils.SensitiveWordUtil2;
import com.sensitive.biz.entity.Minganci;
import com.sensitive.biz.entity.MinganciLishi;
import com.sensitive.biz.mapper.MinGanCiMapper;
import com.sensitive.biz.mapper.MinganciLishiMapper;
import com.sensitive.biz.model.MinganciModel;
import com.sensitive.biz.model.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MInganciService extends ServiceImpl<MinGanCiMapper, Minganci> {

    @Autowired
    MinGanCiMapper minGanCiMapper;
    @Autowired
    MinganciLishiService minganciLishiService;
    @Autowired
    private MinganciLishiMapper minganciLishiMapper;

    public void addMinganci(MinganciModel minganciModel) {
        if (minGanCiMapper.selectCount(new LambdaQueryWrapper<Minganci>().eq(Minganci::getKeyword, minganciModel.getKeyword())) > 0) {
            throw new RuntimeException("敏感词已存在,无需重复添加");
        }
        Minganci entity = new Minganci();
        entity.setKeyword(minganciModel.getKeyword());
        entity.setDescription(minganciModel.getDescription());
        entity.setUserId(Long.valueOf((String) StpUtil.getLoginId()));
        minGanCiMapper.insert(entity);
    }


    public Page<Minganci> pager(int c, int i) {
        Page<Minganci> page = new Page<>(c, i);
        return minGanCiMapper.selectPage(page, new LambdaQueryWrapper<Minganci>().eq(Minganci::getUserId, StpUtil.getLoginId()));
    }

    public Integer update(MinganciModel minganciModel) {
        return minGanCiMapper.update(new LambdaUpdateWrapper<Minganci>().eq(Minganci::getKeyword, minganciModel.getKeyword())
                .eq(Minganci::getUserId, StpUtil.getLoginId())
                .set(Minganci::getDescription, minganciModel.getDescription()));
    }

    public Statistic check(String content, String replaceChar, String type, String source) {
        Statistic statistic = new Statistic();
        try {
            Set<String> words = minGanCiMapper.selectList(new LambdaQueryWrapper<Minganci>().select(Minganci::getKeyword))
                    .stream().map(Minganci::getKeyword).collect(Collectors.toSet());
            SensitiveWordUtil2.init(words);
            Set<String> sensitiveWord = SensitiveWordUtil2.getSensitiveWord(content);
            statistic.setWords(sensitiveWord);
            statistic.setNums(sensitiveWord.size());
            String replaced = StrUtil.isNotBlank(replaceChar) ? SensitiveWordUtil2.replaceSensitiveWord(content, replaceChar) : content;
            statistic.setContent(replaced);

            MinganciLishi entity = new MinganciLishi();
            entity.setUserId(Long.valueOf((String) StpUtil.getLoginId()));
            entity.setType(type);
            entity.setSource(source);
            entity.setOrigin(content);
            entity.setContent(replaced);
            minganciLishiMapper.insert(entity);
        } catch (Exception e) {
            log.error("检测出错 ", e);
        } finally {
            SensitiveWordUtil2.clear();
        }
        return statistic;
    }
}
