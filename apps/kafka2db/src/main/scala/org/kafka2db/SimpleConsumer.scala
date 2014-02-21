package org.kafka2db

import java.util.Properties
import kafka.consumer._

object SimpleConsumer {

  def main(args: Array[String]): Unit = {
    println("[Start")

    val zookeeper = "localhost:2181"
    val topic = "test"

    val props = new Properties
    props.put("zookeeper.connect", zookeeper);
    props.put("group.id", "org.kafka2db.SimpleConsumer");
    props.put("zookeeper.session.timeout.ms", "6000");
    props.put("zookeeper.sync.time.ms", "200");
    props.put("auto.commit.interval.ms", "1000");
    props.put("autooffset.reset", "smallest");

    val config = new ConsumerConfig(props)

    val connector = Consumer.create(config)

    val msgStreams: Option[List[KafkaStream[Array[Byte],Array[Byte]]]] = connector.createMessageStreams(Map(topic -> 1)).get(topic)

    msgStreams.map(streamList => {
      println(s"Found ${streamList.size} streams")
      streamList.foreach(stream => {
        println(s"Fetching message with client id " + stream.clientId)
        for(e <- stream) {
          val message = new String(e.message)
          println(s"[${e.partition}/${e.offset}] => ${e.topic} -> ${message}")
        }
      })
    })

    println("Shutting down...")
    connector.shutdown
    println("End");
  }

}

