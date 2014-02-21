package org.kafka2db

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

import kafka.message._

@RunWith(classOf[JUnitRunner])
class SimpleConsumerFlatSpec extends FlatSpec with ShouldMatchers {
  
  "A simple consumer" should "read messages from a source" in {
  	val repo = new InMemoryMessageRepository
  	val src = new FixedMessageSource("t", MessageAndMetadata("t".getBytes, "m".getBytes, "t", 0, 0L) :: Nil)
    val consumer = new SimpleConsumer()(repo, src)
    consumer.listenTo
    consumer.shutdown

    repo.dumpByTopic("t") should be(List[(String, String)](("t", "m")))
  }
}