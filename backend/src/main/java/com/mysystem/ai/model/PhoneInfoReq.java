package com.mysystem.ai.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class PhoneInfoReq {

    @NotBlank
    private String name;
    @NotBlank
    private String config;
    @NotBlank
    private String features;
}
