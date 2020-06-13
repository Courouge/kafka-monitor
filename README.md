## Start docker platform
```bash
docker-compose up -d zookeeper-1 zookeeper-2 zookeeper-3
docker-compose up kafka-1 kafka-2 kafka-3
```
## Dev java kadmin
```bash
docker-compose up --build -d kadmin && docker-compose logs -f kadmin
```

## zookeeper
```bash
 docker-compose exec zookeeper-1 bash -c 'echo stat |nc zookeeper-1 2181'
```
## kafka
- List topics 
```bash
docker-compose exec kafka-1 bash -c 'KAFKA_OPTS="" kafka-topics --describe --zookeeper zookeeper-1:2181'
```
- Create `demo-perf-topic` with 4 partitions and 3 replicas.
```bash
docker-compose exec kafka-1 bash -c 'KAFKA_OPTS="" kafka-topics --create --partitions 4 --replication-factor 3 --topic demo-perf-topic --zookeeper zookeeper-1:2181'
```
- Produces random messages on `demo-perf-topic`
```bash
docker-compose exec kafka-1 bash -c 'KAFKA_OPTS="" kafka-producer-perf-test --throughput 500 --num-records 100000000 --topic demo-perf-topic --record-size 100 --producer-props bootstrap.servers=localhost:9092'
```
- Consumes random messages on `demo-perf-topic`
```bash
docker-compose exec kafka-1 bash -c 'KAFKA_OPTS="" kafka-consumer-perf-test --messages 100000000 --threads 1 --topic demo-perf-topic --broker-list localhost:9092 --timeout 60000'
```
- Add acls on `Test-topic`
```bash
docker-compose exec kafka-1 bash -c 'KAFKA_OPTS="" kafka-acls --authorizer kafka.security.auth.SimpleAclAuthorizer --authorizer-properties zookeeper.connect=zookeeper-1:2181 --add --allow-principal User:test --topic Test-topic'
```
- List acls on cluster
```bash
docker-compose exec kafka-1 bash -c 'KAFKA_OPTS="" kafka-acls --authorizer kafka.security.auth.SimpleAclAuthorizer --authorizer-properties zookeeper.connect=zookeeper-1:2181 --list'
```
- Produce random message kafka with sasl_plaintext
```bash
docker-compose exec kafka-1 bash -c 'KAFKA_OPTS="" kafka-producer-perf-test --throughput 500 --num-records 100000000 --topic demo-perf-topic --record-size 100 --producer-props bootstrap.servers=kafka-1:9092 --producer.config=/etc/kafka/secrets/producer.properties'
```

## Mongodb
- Connect to server
```bash
docker-compose exec mongodb bash -c 'mongo admin -u root -p rootpassword'
```
- Basics command
```bash
show dbs
show collections
db.topics.find()
```
- Data Structure Exemple
```bash
{ "_id" : "5e9c5e6007b9401756553735", "cluster" : "Cluster_100", "topic" : "__confluent.support.metrics", "size" : "11367", "time" : "2020-04-19T14:21:20.052Z" }
```