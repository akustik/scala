package org.kafka2db

import scala.annotation.tailrec
import scala.util.{Failure, Success}

import java.util.Properties
import kafka.consumer._
import kafka.message._

import reactivemongo.api._
import reactivemongo.bson._
import play.api.libs.iteratee.Iteratee

import scala.concurrent.ExecutionContext.Implicits.global

trait MessageRepository {
  def store(topic: String, msg: String)
  def dumpByTopic(topic: String)
  def dumpByTopicAndMessage(topic: String, msg: String)
  def close
}

class DummyMessageRepository extends MessageRepository {
  def store(topic: String, msg: String) = {
    println(s"store(${topic}, ${msg})")
  }
  def dumpByTopic(topic: String) = {
    println(s"dumpByTopic(${topic})")
  }
  def dumpByTopicAndMessage(topic: String, msg: String) = {
    println(s"dumpByTopicAndMessage(${topic}, ${msg})")
  }
  def close = {
    println("close")
  }
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
        println("inserted, last error = " + lastError)
      }
    }
  }

  def dumpByTopicAndMessage(topic: String, msg: String) = {
    val regexp = ""
    val query = BSONDocument(
      "topic" -> topic,
      "msg" -> msg)
    
    val filter = BSONDocument(
      "msg" -> 1,
      "_id" -> 1)

    collection.
      find(query, filter).
      cursor[BSONDocument].
      enumerate().apply(Iteratee.foreach { doc =>
        println(s"dumpByTopicAndMessage(${topic}, ${msg}): " + BSONDocument.pretty(doc))
      })
  }

  def dumpByTopic(topic: String)= {
    val query = BSONDocument(
      "topic" -> topic)
    
    val filter = BSONDocument(
      "msg" -> 1,
      "_id" -> 1)

    collection.
      find(query, filter).
      cursor[BSONDocument].
      enumerate().apply(Iteratee.foreach { doc =>
        println(s"dumpByTopic(${topic}): " + BSONDocument.pretty(doc))
      })
  }

  def close = {
    println("Closing repository...")
    //connection.close
    driver.close
  }
}

trait MessageSource {
  def topic: String
  def subscribe(fn: (Iterable[MessageAndMetadata[Array[Byte],Array[Byte]]]) => Unit)
  def close: Unit
}

class KafkaMessageSource(val hostAndPort: String, val topic: String) extends MessageSource {
  val config = new ConsumerConfig(buildConfigProperties(hostAndPort))
  val connector = Consumer.create(config)

  def subscribe(fn: (Iterable[MessageAndMetadata[Array[Byte],Array[Byte]]]) => Unit) = {
    val msgStreams: Option[List[KafkaStream[Array[Byte],Array[Byte]]]] = connector.createMessageStreams(Map(topic -> 1)).get(topic)
    msgStreams.map(streamList => {
      println(s"Found ${streamList.size} streams")
      streamList.foreach(stream => {
        println(s"Fetching message with client id " + stream.clientId)
        fn(stream)
      })
    })
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

  def close = {
    println("Closing source...")
    connector.shutdown
  }
}

class SimpleConsumer(implicit val repo: MessageRepository, implicit val src: MessageSource) {
  
  val filterPattern = "get:(.*)".r

  @tailrec
  final def readMessage(messages: Iterable[MessageAndMetadata[Array[Byte],Array[Byte]]]): Unit = {
    val e = messages.head
    val msg = new String(e.message)
    msg match {
      case "dump" => {
        repo.dumpByTopic(src.topic)
        readMessage(messages)
      }
      case filterPattern(m) => {
        repo.dumpByTopicAndMessage(src.topic, m)
        readMessage(messages)
      }
      case "shutdown" => {
        println("Shutdown signal received!")
      }
      case _ => {
        println(s"[${e.partition}/${e.offset}] => ${e.topic} -> ${msg}")
        repo.store(src.topic, msg)
        readMessage(messages)
      }
    }
  }

  def listenTo() = {
    src.subscribe(readMessage(_));
  }

  def shutdown() = {
    println("Shutting down...")
    src.close
    repo.close
  }
}

object SimpleConsumer {

  def main(args: Array[String]): Unit = {
    println("[Start")

    val zookeeper = "localhost:2181"
    val topic = "test"

    //implicit val repo: MessageRepository = new DummyMessageRepository
    implicit val src: MessageSource = new KafkaMessageSource(zookeeper, topic)
    implicit val repo: MessageRepository = new MongoMessageRepository
    val consumer = new SimpleConsumer
    consumer.listenTo()
    consumer.shutdown

    println("Waiting for db to stop...")
    Thread.sleep(5000)

    println("End");
  }

}

