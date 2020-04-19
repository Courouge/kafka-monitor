package com.kmanage;

import java.util.HashMap;
import java.util.Map;


public class LogDirTopic {
    private static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
    public static Map<String, String> bytopic(String logdirinfo) {
        String[] triplet = logdirinfo.split("\\{")[1].split("\\}")[0].split("\\), |\\)");
        Map<String, String> res = new HashMap<String, String>();
        for (int i = 0; i < triplet.length; i++) {
            String topic = triplet[i].substring(0, triplet[i].lastIndexOf("-"));
            int size = Integer.parseInt(triplet[i].split("size=")[1].split(",")[0]);
            if (isNotNullOrEmpty(res.get(topic))) {
                res.put(topic, String.valueOf(Integer.parseInt(res.get(topic)) + size));
            } else {
                res.put(topic, String.valueOf(size));
            }
        }
        return res;
    }
}