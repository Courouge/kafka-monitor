package com.kmanage;

import com.google.common.collect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.requests.DescribeLogDirsResponse.LogDirInfo;
import org.apache.kafka.common.requests.DescribeLogDirsResponse.ReplicaInfo;

public class TopicsDiskSize {
    Properties config = new Properties();
    HashMap<String, Long> MapTopicSize = new HashMap<String, Long>();

    public TopicsDiskSize(Properties props){
        for (Enumeration propertyNames = props.propertyNames(); propertyNames.hasMoreElements();) {
            Object key = propertyNames.nextElement();
            config.put(key, props.get(key));
        }
    }
    public String getbrokers(){
        return config.get("bootstrap.servers").toString();
    }
    public HashMap<String, Long> GetMapTopicSize() {
        try {
            Multimap<String, Long> MultimapTopicSize = ArrayListMultimap.create();
            // sum values by key (topic) on MultimapTopicSize
            for (int i=1; i < this.getbrokers().split(":").length; i++) {
                Map<TopicPartition, ReplicaInfo> MapPartitionSize = AdminClient.create(this.config).describeLogDirs(Arrays.asList(i)).all().get().get(i).get("/var/lib/kafka/data").replicaInfos;
                MapPartitionSize.forEach((key, value) -> MultimapTopicSize.put(key.toString().substring(0, key.toString().lastIndexOf("-")), value.size));
            }
            // sum values by key (topic) on MultimapTopicSize
            for(String key : MultimapTopicSize.keySet()){
                Collection<Long> coll = (Collection<Long>) MultimapTopicSize.get(key);
                long sum = 0;
                for(Long i : coll){
                    sum += i;
                }
                MapTopicSize.put(key, sum);
            }
        } catch (ExecutionException | InterruptedException e) { e.printStackTrace(); }
        return this.MapTopicSize;
    }
}