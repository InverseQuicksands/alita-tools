package com.alita.framework.job.utils;

import java.util.Map;

public class MapUtils {

    public static boolean isEmpty(final Map<?,?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(final Map<?,?> map) {
        return !MapUtils.isEmpty(map);
    }

}
