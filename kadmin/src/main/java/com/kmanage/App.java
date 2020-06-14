package com.kmanage;

import com.kmanage.TopicsDiskSize;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import org.bson.Document;
import org.bson.types.ObjectId;

public class App {
    public static void main(String[] args) {
        // Kafka config
        Properties config = new Properties();
        String jaasTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
        String brokers = "kafka-1:9092,kafka-2:9092,kafka-3:9092";
        config.put("bootstrap.servers", brokers);
        config.put("security.protocol", "SASL_PLAINTEXT");
        config.put("sasl.mechanism", "PLAIN");
        config.put("sasl.jaas.config", String.format(jaasTemplate, "test", "test"));

        TopicsDiskSize TopicsSize = new TopicsDiskSize(config);
        HashMap<String, Long> topicsSize = TopicsSize.GetMapTopicSize();

        for(HashMap.Entry<String, Long> entry : topicsSize.entrySet() ){
            String nice_to_show = "Topic: " + entry.getKey() + ", nb_partitions: " + TopicsSize.GetNumberOfPartitions(entry.getKey()) + ", nb_replica: "+ TopicsSize.GetNumberOfReplicas(entry.getKey())+", Disk: " + entry.getValue() + ", Data: " + topicsSize.get(entry.getKey())/TopicsSize.GetNumberOfReplicas(entry.getKey());
            System.out.println(nice_to_show);
        }

        // Mongodb connect
        String password = "rootpassword";
        MongoCredential credential = MongoCredential.createCredential("root", "admin", password.toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress("mongodb", 27017), Arrays.asList(credential));
        MongoDatabase database = mongoClient.getDatabase("kafkamonitor");

        // insert a document
        for (String key : topicsSize.keySet()) {
            MongoCollection<Document> collection = database.getCollection("topics");
            String Cluster = "Cluster_100";
            String json = "{cluster: '" + Cluster + "',topic: '" + key + "',size: '" + topicsSize.get(key) + "',time: '" + new Date().toInstant() +"'}";
            collection.insertOne(new Document(BasicDBObject.parse(json)));
        }
    }
}
