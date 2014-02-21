package org.kafka2db

import scala.annotation.tailrec
import scala.util.{Failure, Success}

import java.util.Properties
import kafka.consumer._

import reactivemongo.api._
import reactivemongo.bson._
import play.api.libs.iteratee.Iteratee

import scala.concurrent.ExecutionContext.Implicits.global

trait MessageRepository {
  def store(topic: String, msg: String)
  def dumpByTopic(topic: String)
  def close
}

class MongoMessageRepository extends MessageRepository {
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db = connection("kafka")
  val collection = db("msgcoll")

  def store(topic: String, msg: String) = {
    val document = BSONDocument(
      "topic" -> topic,
      "msg" -> msg)

    val future = collection.insert(document)

    future.onComplete {
      case Failure(e) => println(e)
      case Success(lastError) => {
        println("successfully inserted document with lastError = " + lastError)
      }
    }
  }

  def dumpByTopic(topic: String)= {
    val query = BSONDocument("topic" -> topic)
    
    val filter = BSONDocument(
      "msg" -> 1,
      "_id" -> 1)

    collection.
      find(query, filter).
      cursor[BSONDocument].
      enumerate().apply(Iteratee.foreach { doc =>
        println("found document: " + BSONDocument.pretty(doc))
      })
  }

  def close = {
    connection.close
    driver.close
  }
}

class SimpleConsumer(val hostAndPort: String, val topic: String) {

  val repo: MessageRepository = new MongoMessageRepository
  val config = new ConsumerConfig(buildConfigProperties(hostAndPort))
  val connector = Consumer.create(config)

  @tailrec
  final def readMessage(stream: KafkaStream[Array[Byte],Array[Byte]]): Unit = {
    val e = stream.head
    val msg = new String(e.message)
    msg match {
      case "dump" => {
        repo.dumpByTopic(topic)
        readMessage(stream)
      }
      case "shutdown" => {
        println("Shutdown signal received!")
      }
      case _ => {
        println(s"[${e.partition}/${e.offset}] => ${e.topic} -> ${msg}")
        repo.store(topic, msg)
        readMessage(stream)
      }
    }
  }

  def buildConfigProperties(hostAndPort: String): Properties = {
    val props = new Properties
    props.put("zookeeper.connect", hostAndPort);
    props.put("group.id", "org.kafka2db.SimpleConsumer");
    props.put("zookeeper.session.timeout.ms", "6000");
    props.put("zookeeper.sync.time.ms", "200");
    props.put("auto.commit.interval.ms", "1000");
    props.put("autooffset.reset", "smallest");
    props
  }

  def listenTo() = {
    val msgStreams: Option[List[KafkaStream[Array[Byte],Array[Byte]]]] = connector.createMessageStreams(Map(topic -> 1)).get(topic)
    msgStreams.map(streamList => {
      println(s"Found ${streamList.size} streams")
      streamList.foreach(stream => {
        println(s"Fetching message with client id " + stream.clientId)
        readMessage(stream)
      })
    })
  }

  def shutdown() = {
    println("Shutting down...")
    connector.shutdown
    repo.close
  }
}

object SimpleConsumer {

  def main(args: Array[String]): Unit = {
    println("[Start")

    val zookeeper = "localhost:2181"
    val topic = "test"

    val consumer = new SimpleConsumer(zookeeper, topic)
    consumer.listenTo()
    consumer.shutdown

    println("End");
  }

}

