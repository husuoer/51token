package com.sharex.token.api.util;

import java.util.Random;

public class RandomUtil {

    public static String get() {

        String sources = "0123456789"; // 加上一些字母，就可以生成pc站的验证码了
        Random rand = new Random();
        StringBuffer flag = new StringBuffer();
        for (int j = 0; j < 6; j++)
        {
            flag.append(sources.charAt(rand.nextInt(9)) + "");
        }
        return flag.toString();
    }
}
