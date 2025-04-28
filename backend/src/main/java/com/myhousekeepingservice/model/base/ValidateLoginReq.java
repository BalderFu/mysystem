package com.myhousekeepingservice.model.base;

import lombok.Data;

@Data
public class ValidateLoginReq {

    private String email;
    private String code;
}
