package com.kmanage;

import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import java.lang.*;

public class LogDirTopic {
    private static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
    public static Map<String, String> bytopic(String logdirinfo) {
        String[] triple = logdirinfo.split("\\{")[1].split("\\}")[0].split("\\), |\\)");
        Map<String, String> res = new HashMap<String, String>();
        for (int i = 0; i < triple.length; i++) {
            String topic = triple[i].substring(0, triple[i].lastIndexOf("-"));
            String par = triple[i].substring(0+topic.length(), triple[i].lastIndexOf("-"));
            int size = Integer.parseInt(triple[i].split("size=")[1].split(",")[0]);
//            String [] bits  = triple[i].split("=")[0].split("-");
//            String partition = bits[bits.length-1];
//            res.put(topic+"-"+partition, String.valueOf(size));
            if (isNotNullOrEmpty(res.get(topic))) {
                res.put(topic, String.valueOf(Integer.parseInt(res.get(topic)) + size));
            } else {
                res.put(topic, String.valueOf(size));
            }
        }
        return res;
    }
}