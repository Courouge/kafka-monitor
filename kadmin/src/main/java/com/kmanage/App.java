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
import java.util.HashMap;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.AbstractConfig;
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
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.resource.ResourceFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import com.kmanage.LogDirTopic;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClient;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.BasicDBObject;
import org.bson.BsonString;
import java.nio.charset.Charset;
import org.bson.types.ObjectId;
import java.util.Date;
import org.javatuples.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;

import com.google.common.collect.*;

import org.apache.kafka.clients.admin.DescribeAclsResult;

public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Kafka config
        Properties config = new Properties();
//        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
        String brokers = "kafka-1:9092,kafka-2:9092,kafka-3:9092";
        config.put("bootstrap.servers", brokers);
        config.put("security.protocol", "SASL_PLAINTEXT");
        config.put("sasl.mechanism", "PLAIN");
        config.put("sasl.jaas.config", String.format(jaasTemplate, "test", "test"));
        AdminClient admin = AdminClient.create(config);

        // Mongodb connect
        char[] password = { 'r', 'o', 'o', 't', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
        MongoCredential credential = MongoCredential.createCredential("root", "admin", password);
        MongoClient mongoClient = new MongoClient(new ServerAddress("mongodb", 27017), Arrays.asList(credential));
        MongoDatabase database = mongoClient.getDatabase("kafkamonitor");

        // declare vars
        LogDirTopic LogSizeTopic = new LogDirTopic();
        Multimap<String, Long> multimap = ArrayListMultimap.create();

        // loop on brokers id to get log size from kafka admin client
        for (int i=1; i < brokers.split(":").length; i++)
        {
            Map<String, Long> res  =  LogSizeTopic.bytopic(admin.describeLogDirs(Arrays.asList(i)).all().get().get(i).get("/var/lib/kafka/data").toString());
            res.forEach((key, value) ->   multimap.put(key, value));
        }
        System.out.println(multimap);

        // sum values by key (partition) on multimap
        HashMap<String, Long> resultMap = new HashMap<String, Long>();
        for(String key : multimap.keySet()){
            Collection<Long> coll = (Collection<Long>) multimap.get(key);
            long sum = 0;
            for(Long i : coll){
                sum += i;
            }
            resultMap.put(key, sum);
        }
        System.out.println(resultMap);

        Multimap<String, Long> testmap = LinkedHashMultimap.create();
        for (String key : resultMap.keySet()) {
            testmap.put(key.substring(0, key.lastIndexOf("-")), resultMap.get(key));
        }
        System.out.println(testmap);
        // sum values by key (topic) on multimap
        HashMap<String, Long> resultMap1 = new HashMap<String, Long>();
        for(String key : testmap.keySet()){
            Collection<Long> coll = (Collection<Long>) testmap.get(key);
            long sum = 0;
            for(Long i : coll){
                sum += i;
            }
            resultMap1.put(key, sum);
        }
        System.out.println(resultMap1);

        // ################################# En cours ... ##################################################################"

        //System.out.println(admin.describeAcls(AclBindingFilter.ANY).values().get());

        // insert a document
//        for (String key : res.keySet()) {
//            MongoCollection<Document> collection = database.getCollection("topics");
//            String Cluster = "Cluster_100";
//            String json = "{_id : '" + ObjectId.get() + "', cluster : '" + Cluster + "', topic : '" + key + "', size : '" + res.get(key) + "', time : '" + new Date().toInstant() +"'}";
//            collection.insertOne(new Document(BasicDBObject.parse(json)));
//        }

    }
}
