package com.rzt.util;

import java.util.UUID;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.util
 * @Author: liuze
 * @date: 2017-12-7 20:12
 */
public class UUIDTool {
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }
}
