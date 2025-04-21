package com.mysystem.ai.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrackReq {
    @NotBlank(message = "密文不能为空")
    private String hashText;

    @NotBlank(message = "算法ID不能为空")
    private Long id;
}
