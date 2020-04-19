## Start docker platform
```bash
docker-compose up -d jmx-exporter zookeeper-1 zookeeper-2 zookeeper-3 && docker-compose up kafka-1 kafka-2 kafka-3
```
## Dev java kadmin
```bash
docker-compose up --build -d kadmin && docker-compose logs kadmin
```
## kafka
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
{ "_id" : "5e9c5e6007b9401756553735", "cluster" : "MUTU_HP-100", "topic" : "__confluent.support.metrics", "size" : "11367", "time" : "2020-04-19T14:21:20.052Z" }
```