package com.myhousekeepingservice.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.myhousekeepingservice.entity.User;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class UserUtils {

    /**
     * 获取用户的角色权限信息
     *
     * @param user
     * @return
     */
    public static List<String> getUserRoles(User user) {
        if (ObjectUtil.isEmpty(user) || StrUtil.isBlank(user.getRole())) {
            return Collections.emptyList();
        }
        if (user.getRole().equals("admin")) {
            return Arrays.asList("admin","normal");
        }
        if (user.getRole().equals("normal")) {
            return Collections.singletonList(user.getRole());
        }
        return Collections.emptyList();
    }
}
