package com.myhousekeepingservice.model.base;

import lombok.Data;

@Data
public class ResetPwsReq {
    private String originPassword;
    private String newPassword;
}
