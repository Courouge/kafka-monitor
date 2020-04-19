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

public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092,kafka-2:9092,kafka-3:9092");
        AdminClient admin = AdminClient.create(config);

        // get log size from admin client
        String describeLogDirs = admin.describeLogDirs(Arrays.asList(1)).all().get().get(1).get("/var/lib/kafka/data").toString();
        LogDirTopic LogSizeTopic = new LogDirTopic();
        Map<String, String> res =  LogSizeTopic.bytopic(describeLogDirs);
//        System.out.println(res);

        char[] password = { 'r', 'o', 'o', 't', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
        MongoCredential credential = MongoCredential.createCredential("root", "admin", password);
        MongoClient mongoClient = new MongoClient(new ServerAddress("mongodb", 27017), Arrays.asList(credential));
        MongoDatabase database = mongoClient.getDatabase("test");

        // insert a document
        for (String key : res.keySet()) {
            MongoCollection<Document> collection = database.getCollection("mycoll");
            String Cluster = "MUTU_HP-100";
            String json = "{_id : '" + ObjectId.get() + "', cluster : '" + Cluster + "', topic : '" + key + "', size : '" + res.get(key) + "', time : '" + new Date().toInstant() +"'}";
            collection.insertOne(new Document(BasicDBObject.parse(json)));
        }

    }
}
