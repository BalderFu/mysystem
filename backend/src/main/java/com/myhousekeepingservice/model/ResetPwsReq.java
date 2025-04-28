package com.myhousekeepingservice.model;

import lombok.Data;

@Data
public class ResetPwsReq {
    private String originPassword;
    private String newPassword;
}
