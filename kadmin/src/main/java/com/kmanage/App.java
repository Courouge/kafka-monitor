package com.kmanage;

import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.DescribeLogDirsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.clients.admin.DescribeReplicaLogDirsResult;
import org.apache.kafka.common.TopicPartitionReplica;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.Node;
import org.apache.kafka.clients.admin.DescribeReplicaLogDirsResult.ReplicaLogDirInfo;
import org.apache.kafka.common.requests.DescribeLogDirsResponse.LogDirInfo;
import org.apache.kafka.clients.admin.DescribeLogDirsOptions;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.message.DescribeLogDirsResponseData;

import com.kmanage.LogDirTopic;

public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.0.1:9092,172.17.0.1:9093");
        AdminClient admin = AdminClient.create(config);
        // Print topics list
//        admin.listTopics().names().get().forEach(System.out::println);
        // Print topics config
//        admin.describeCluster().nodes().get().forEach(System.out::println);

//        List<String> list = Arrays.asList("test4");
//        System.out.println(admin.describeTopics(list).all().get());

        String Topic = "Hello-Kafka-2";
        DescribeTopicsResult describeTopicsResult = admin.describeTopics(Collections.singletonList(Topic));
        KafkaFuture<Map<String, TopicDescription>> all = describeTopicsResult.all();
        try {
            Map<String, TopicDescription> topicDescriptions = all.get(30, TimeUnit.SECONDS);
            TopicDescription topicDescription = topicDescriptions.get(Topic);

            List<TopicPartitionInfo> topic_partitions_info = topicDescription.partitions();
//            System.out.println("topic: " + topicDescription.name());
//            System.out.println("partition: " + topic_partitions_info.get(0).partition());
//            System.out.println("leader: " + topic_partitions_info.get(0).leader().id());

//            System.out.println(all);
//            KafkaFuture{value={Hello-Kafka-2=(name=Hello-Kafka-2, internal=false, partitions=(partition=0, leader=127.0.0.1:9092 (id: 1 rack: null), replicas=127.0.0.1:9092 (id: 1 rack: null), 127.0.0.1:9093 (id: 2 rack: null), isr=127.0.0.1:9092 (id: 1 rack: null), 127.0.0.1:9093 (id: 2 rack: null)),(partition=1, leader=127.0.0.1:9093 (id: 2 rack: null), replicas=127.0.0.1:9093 (id: 2 rack: null), 127.0.0.1:9092 (id: 1 rack: null), isr=127.0.0.1:9093 (id: 2 rack: null), 127.0.0.1:9092 (id: 1 rack: null)),(partition=2, leader=127.0.0.1:9092 (id: 1 rack: null), replicas=127.0.0.1:9092 (id: 1 rack: null), 127.0.0.1:9093 (id: 2 rack: null), isr=127.0.0.1:9092 (id: 1 rack: null), 127.0.0.1:9093 (id: 2 rack: null)), authorizedOperations=[])},exception=null,done=true}

            List<Integer> replica = new ArrayList<Integer>();
            List<Integer> isr = new ArrayList<Integer>();

            for(int i=0;i<topic_partitions_info.get(0).replicas().size();i++){
                replica.add(topic_partitions_info.get(0).replicas().get(i).id());
            }

            for(int i=0;i<topic_partitions_info.get(0).isr().size();i++){
                isr.add(topic_partitions_info.get(0).replicas().get(i).id());
            }
//            System.out.println("replicas: " + replica);
//            System.out.println("isr: " + isr);

        }
        catch (Exception e) {
            System.out.println("exception in kafka => " + e);
        }
        // get log size from admin client
        String describeLogDirs = admin.describeLogDirs(Arrays.asList(1)).all().get().get(1).get("/var/lib/kafka/data").toString();
        LogDirTopic LogSizeTopic = new LogDirTopic();
        Map<String, String> res =  LogSizeTopic.bytopic(describeLogDirs);
        System.out.println(res);
    }
}

// Hello-Kafka-2-1=7844602
// sur disque      7892992 (soit 48390 de plus) 8K size of directory
