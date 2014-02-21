package org.kafka2db

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

@RunWith(classOf[JUnitRunner])
class SimpleConsumerFlatSpec extends FlatSpec with ShouldMatchers {
  
  "A simple consumer" should "work" in {
    SimpleConsumer.main(Array("test"))
  }
}