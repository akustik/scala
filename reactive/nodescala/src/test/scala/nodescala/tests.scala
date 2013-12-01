package nodescala

import scala.language.postfixOps
import scala.util.{ Try, Success, Failure }
import scala.collection._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.async.Async.{ async, await }
import org.scalatest._
import NodeScala._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NodeScalaSuite extends FunSuite {

  test("A Future should always be created") {
    val always = Future.always(517)

    assert(Await.result(always, 0 nanos) == 517)
  }

  test("A Future should never be created") {
    val never = Future.never[Int]

    try {
      Await.result(never, 1 second)
      assert(false)
    } catch {
      case t: TimeoutException => // ok!
    }
  }

  test("A list of futures should return the first one to end (delays)") {
    val f1 = Future.delay(2 seconds) continue(_ => "2")
    val f2 = Future.delay(1 seconds) continue(_ => "1")
    val l = Future.any(List(f1, f2))
    val result = Await.result(l, 5 second)
    assert(result === "1")
  }
  
  test("A list of futures should return the first one to end (delay and always)") {
    val l = Future.any(List(Future.delay(2 seconds), Future.always("always")))
    val result = Await.result(l, 5 second)
    assert(result === "always")
  }

  test("A list of futures should return every one") {
    val l = Future.all(List(Future { blocking { Thread.sleep(1000) }; "slow" },
      Future { blocking { Thread.sleep(500) }; "fast" }))
    l onComplete {
      case r: Try[List[String]] => assert(r === Success(List("slow", "fast")))
    }

    Await.result(l, 2 second)
  }

  test("A delay should allow other futures to be executed") {
    val d = Future.delay(1 seconds)
    val always = Future.always(517)
    assert(Await.result(always, 0 nanos) == 517)
  }

  test("Now should return the value if available") {
    val f = Future.always("here we are!")

    Try(f.now) match {
      case Success(e) => assert(e === "here we are!")
      case _ => fail()
    }
  }

  test("Now should trow an exception the value if available") {
    val f = future {
      blocking {
        Thread.sleep(500)
      }
      "here we are!"
    }

    try {
      f.now
    } catch {
      case t: NoSuchElementException => assert(t.getMessage() === "Element not available")
      case _: Throwable => fail()
    }
  }

  test("A future should continue with another") {
    val f = Future.always("here we are!") continueWith (_ => 123)
    assert(Await.result(f, 1 second) == 123)
  }

  test("A future should continue another") {
    val f = Future.always("here we are!") continue (_ match {
      case Success(x) => 123 + x
      case Failure(x) => x
    })
    assert(Await.result(f, 1 second) == "123here we are!")
  }

  test("A future should not continue with another if not completed") {
    val f = Future.never[String] continueWith (_ => 123)
    try {
      Await.result(f, 1 second)
      fail()
    } catch {
      case t: TimeoutException => // ok!
    }
  }

  test("CancellationTokenSource should allow stopping the computation") {
    val cts = CancellationTokenSource()
    val ct = cts.cancellationToken
    val p = Promise[String]()

    async {
      while (ct.nonCancelled) {
        // do work
      }

      p.success("done")
    }

    cts.unsubscribe()
    assert(Await.result(p.future, 1 second) == "done")
  }

  /*test("Future should run with a cancellable context") {
    val working = Future.run() { ct =>
      Future {
        while (ct.nonCancelled) {
          println("working")
        }
        println("done")
      }
    }
    Future.delay(5 seconds) onSuccess {
      case _ => working.unsubscribe()
    }
  }*/

  class DummyExchange(val request: Request) extends Exchange {
    @volatile var response = ""
    val loaded = Promise[String]()
    def write(s: String) {
      response += s
    }
    def close() {
      loaded.success(response)
    }
  }

  class DummyListener(val port: Int, val relativePath: String) extends NodeScala.Listener {
    self =>

    @volatile private var started = false
    var handler: Exchange => Unit = null

    def createContext(h: Exchange => Unit) = this.synchronized {
      assert(started, "is server started?")
      handler = h
    }

    def removeContext() = this.synchronized {
      assert(started, "is server started?")
      handler = null
    }

    def start() = self.synchronized {
      started = true
      new Subscription {
        def unsubscribe() = self.synchronized {
          started = false
        }
      }
    }

    def emit(req: Request) = {
      val exchange = new DummyExchange(req)
      if (handler != null) handler(exchange)
      exchange
    }
  }

  class DummyServer(val port: Int) extends NodeScala {
    self =>
    val listeners = mutable.Map[String, DummyListener]()

    def createListener(relativePath: String) = {
      val l = new DummyListener(port, relativePath)
      listeners(relativePath) = l
      l
    }

    def emit(relativePath: String, req: Request) = this.synchronized {
      val l = listeners(relativePath)
      l.emit(req)
    }
  }

  test("Listener should serve the next request as a future") {
    val dummy = new DummyListener(8191, "/test")
    val subscription = dummy.start()

    def test(req: Request) {
      val f = dummy.nextRequest()
      dummy.emit(req)
      val (reqReturned, xchg) = Await.result(f, 1 second)

      assert(reqReturned == req)
    }

    test(immutable.Map("StrangeHeader" -> List("StrangeValue1")))
    test(immutable.Map("StrangeHeader" -> List("StrangeValue2")))

    subscription.unsubscribe()
  }

  test("Server should serve requests") {
    val dummy = new DummyServer(8191)
    val dummySubscription = dummy.start("/testDir") {
      request => for (kv <- request.iterator) yield (kv + "\n").toString
    }

    // wait until server is really installed
    Thread.sleep(500)

    def test(req: Request) {
      val webpage = dummy.emit("/testDir", req)
      val content = Await.result(webpage.loaded.future, 1 second)
      val expected = (for (kv <- req.iterator) yield (kv + "\n").toString).mkString
      assert(content == expected, s"'$content' vs. '$expected'")
    }

    test(immutable.Map("StrangeRequest" -> List("Does it work?")))
    test(immutable.Map("StrangeRequest" -> List("It works!")))
    test(immutable.Map("WorksForThree" -> List("Always works. Trust me.")))

    dummySubscription.unsubscribe()
  }

}




