package com.kmanage;

import java.util.HashMap;
import java.util.Map;

import org.javatuples.*;
public class LogDirTopic {
    private static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static Map<String, Pair<String, Integer>> bytopic(String logdirinfo) {
        String[] triplet = logdirinfo.split("\\{")[1].split("\\}")[0].split("\\), |\\)");
        Map<String, Pair<String, Integer>> map_pair_res = new HashMap<String, Pair<String, Integer>>();

        for (int i = 0; i < triplet.length; i++) {
            String topic = triplet[i].substring(0, triplet[i].lastIndexOf("-"));
            int size = Integer.parseInt(triplet[i].split("size=")[1].split(",")[0]);
            String[] bits = triplet[i].split("=")[0].split("-");
            String partition = bits[bits.length - 1];
//            System.out.println("topic: " + topic+"."+partition + "size:"+ size);

            map_pair_res.put(topic + "-" + partition, Pair.with(String.valueOf(size), Integer.parseInt(partition)));

        }
        return map_pair_res;
    }
}

// {demo-perf-topic-2=529569, demo-perf-topic-3=528205, demo-perf-topic-0=513954, demo-perf-topic-1=521513, __confluent.support.metrics-0=15156}
// {demo-perf-topic=513954, __confluent.support.metrics=15156}