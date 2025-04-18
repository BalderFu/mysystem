package com.mysystem.ai.model;

import lombok.Data;

@Data
public class ValidateLoginReq {

    private String email;
    private String code;
}
