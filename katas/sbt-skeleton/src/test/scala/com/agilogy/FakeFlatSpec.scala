package com.agilogy

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FakeFlatSpec extends FlatSpec with ShouldMatchers {

  "A Fake" should "return true when required to do something" in {
    new Fake().something should be (true)
  }

}