package com.adagio.autotask.util;

import java.util.UUID;

public class UUIDUtil {

    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
