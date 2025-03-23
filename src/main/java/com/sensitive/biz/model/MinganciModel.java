package com.sensitive.biz.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MinganciModel {
    @NotNull(message = "关键字不能为空")
    private String keyword;

    @NotNull(message = "描述信息不能为空")
    private String description;
}
