package com.mysystem.ai.utils;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DigestUtils {

    public static String MD5(String input) {
        return DigestUtil.md5Hex(input);
    }

    public static String SHA1(String input) {
        return DigestUtil.sha1Hex(input);
    }

    public static String SHA256(String input) {
        return DigestUtil.sha256Hex(input);
    }

    public static String SHA512(String input) {
        return DigestUtil.sha512Hex(input);
    }
}
