package com.kmanage;

import com.google.common.collect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.requests.DescribeLogDirsResponse.LogDirInfo;
import org.apache.kafka.common.requests.DescribeLogDirsResponse.ReplicaInfo;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;

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
    public Integer GetNumberOfPartitions(String topic) {
        try {
            Map<String,TopicDescription> describeTopicsResult = AdminClient.create(this.config).describeTopics(Collections.singleton(topic)).all().get();
            for (Map.Entry<String, TopicDescription> entry : describeTopicsResult.entrySet()) {
                return entry.getValue().partitions().size();
            }
        } catch (ExecutionException | InterruptedException e) { e.printStackTrace(); }
        return 0;
    }
    public Integer GetNumberOfReplicas(String topic) {
        try {
            int sum = 0;
            Map<String,TopicDescription> describeTopicsResult = AdminClient.create(this.config).describeTopics(Collections.singleton(topic)).all().get();
            for (TopicDescription desc: describeTopicsResult.values()) {
                List<TopicPartitionInfo> partitions = desc.partitions();
                for (TopicPartitionInfo info: partitions) {
                    sum += info.replicas().size();
                }
            }
            return (sum / GetNumberOfPartitions(topic));
        } catch (ExecutionException | InterruptedException e) { e.printStackTrace(); }
        return 0;
    }
}