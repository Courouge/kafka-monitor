from time import sleep
from json import dumps
from kafka import KafkaProducer
import random
import string

def randomString(stringLength=10):
    """Generate a random string of fixed length """
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(stringLength))

producer = KafkaProducer(bootstrap_servers='0.0.0.0:9092')
for e in range(1000000):
    sleep(1)
    producer.send('demo-perf-topic', randomString(100))