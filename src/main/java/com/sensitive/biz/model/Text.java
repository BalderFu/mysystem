package com.sensitive.biz.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Text {
    @NotNull(message = "检测内容不能为空")
    private String content;
    @NotNull(message = "检测类型不能为空")
    private String type;
}
