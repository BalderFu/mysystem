package com.mysystem.ai.model;

import lombok.Data;

@Data
public class ResetPwsReq {
    private String originPassword;
    private String newPassword;
}
