package org.kafka2db

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.consumer.Consumer;
import java.util.Properties

object SimpleConsumer {

  def main(args: Array[String]): Unit = {
    println("[Simple Consumer] Start")

    val zookeeper = "localhost:2181"
    val topic = "test"

    val props = new Properties
    props.put("zookeeper.connect", zookeeper);
    props.put("group.id", "org.kafka2db.SimpleConsumer");
    props.put("zookeeper.session.timeout.ms", "400");
    props.put("zookeeper.sync.time.ms", "200");
    props.put("auto.commit.interval.ms", "1000");
    
    val config = new ConsumerConfig(props)

    val connector = Consumer.create(config)

    var stream = connector.createMessageStreams(Map(topic -> 1)).get(topic)

    println(stream)


    connector.shutdown
    println("[Simple Consumer] Ended")
  }

}

