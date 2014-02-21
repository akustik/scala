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
  def dumpByTopic(topic: String): List[(String, String)]
  def dumpByTopicAndMessage(topic: String, msg: String): List[(String, String)]
  def close
}

trait MessageSource {
  def topic: String
  def subscribe(fn: (Iterator[MessageAndMetadata[Array[Byte],Array[Byte]]]) => Unit)
  def close: Unit
}

class InMemoryMessageRepository() extends MessageRepository {
  var values = List[(String, String)]()

  def store(topic: String, msg: String) = {
    values = (topic, msg) :: values
  }
  def dumpByTopic(topic: String): List[(String, String)] = values.filter(_._1 == topic)
  def dumpByTopicAndMessage(topic: String, msg: String): List[(String, String)] = values.filter(e => e._1 == topic && e._2 == msg)
  def close = {}
}

class FixedMessageSource(val topic: String, values: List[MessageAndMetadata[Array[Byte],Array[Byte]]]) extends MessageSource {

  private class OneTimeListIterator(var values: List[MessageAndMetadata[Array[Byte],Array[Byte]]])  
    extends Iterator[MessageAndMetadata[Array[Byte],Array[Byte]]] with java.util.Iterator[MessageAndMetadata[Array[Byte],Array[Byte]]]{
      def hasNext = values != Nil
      def next = {
        val res = values.head
        values = values.tail
        res
      }
      def remove = {}
  }

  private class OneTimeListIterable(values: List[MessageAndMetadata[Array[Byte],Array[Byte]]]) 
    extends Iterable[MessageAndMetadata[Array[Byte],Array[Byte]]] with java.lang.Iterable[MessageAndMetadata[Array[Byte],Array[Byte]]] {

    def iterator() = new OneTimeListIterator(values)
  }

  def subscribe(fn: (Iterator[MessageAndMetadata[Array[Byte],Array[Byte]]]) => Unit) = fn(values.iterator)
  def close = {}
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

  def dumpByTopicAndMessage(topic: String, msg: String): List[(String, String)] = {
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

    Nil
  }

  def dumpByTopic(topic: String): List[(String, String)] = {
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

    Nil
  }

  def close = {
    println("Closing repository...")
    //connection.close
    driver.close
  }
}

class KafkaMessageSource(val hostAndPort: String, val topic: String) extends MessageSource {
  val config = new ConsumerConfig(buildConfigProperties(hostAndPort))
  val connector = Consumer.create(config)

  def subscribe(fn: (Iterator[MessageAndMetadata[Array[Byte],Array[Byte]]]) => Unit) = {
    val msgStreams: Option[List[KafkaStream[Array[Byte],Array[Byte]]]] = connector.createMessageStreams(Map(topic -> 1)).get(topic)
    msgStreams.map(streamList => {
      println(s"Found ${streamList.size} streams")
      streamList.foreach(stream => {
        println(s"Fetching message with client id " + stream.clientId)
        fn(stream.iterator)
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
  final def readMessage(messages: Iterator[MessageAndMetadata[Array[Byte],Array[Byte]]]): Unit = {
    if(messages.hasNext) {
      val e = messages.next
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
  }

  final def read(messages: Iterable[MessageAndMetadata[Array[Byte],Array[Byte]]]): Unit = {
    readMessage(messages.iterator)
  }

  def listenTo() = {
    src.subscribe(readMessage);
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

