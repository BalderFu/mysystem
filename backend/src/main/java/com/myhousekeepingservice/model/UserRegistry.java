package com.myhousekeepingservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistry {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String phone;
    private String email;
    private String avatar;
    private String nickname;


}
