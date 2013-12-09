package suggestions

import language.postfixOps
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Try, Success, Failure }
import rx.lang.scala._
import org.scalatest._
import gui._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.concurrent.TimeUnit
import rx.lang.scala.Notification.{ OnCompleted, OnError, OnNext }
import java.io.IOException

@RunWith(classOf[JUnitRunner])
class WikipediaApiTest extends FunSuite {

  object mockApi extends WikipediaApi {
    def wikipediaSuggestion(term: String) = Future {
      if (term.head.isLetter) {
        for (suffix <- List(" (Computer Scientist)", " (Footballer)")) yield term + suffix
      } else {
        List(term)
      }
    }
    def wikipediaPage(term: String) = Future {
      "Title: " + term
    }
  }

  import mockApi._

  test("WikipediaApi should make the stream valid using sanitized") {
    val notvalid = Observable("erik", "erik meijer", "martin")
    val valid = notvalid.sanitized

    var count = 0
    var completed = false

    val sub = valid.subscribe(
      term => {
        assert(term.forall(_ != ' '))
        count += 1
      },
      t => assert(false, s"stream error $t"),
      () => completed = true)
    assert(completed && count == 3, "completed: " + completed + ", event count: " + count)
  }

  test("WikipediaApi should make use of recovered") {
    val seq = Observable(1, 2, 3, 4)
    val rec = seq.map(x => {
      if (x == 3) throw new IOException("OOPS")
      else x
    }).recovered
    var completed = false
    var actual = rec.toBlockingObservable.toList;
    assert(actual(0) === Success(1))
    assert(actual(1) === Success(2))
    assert(actual(2).toString === Failure(new IOException("OOPS")).toString)
  }

  /*test("WikipediaApi should correctly use timeout"){
    var count = 0
    var completed = false
    val freq = 200
    val timer = Observable.interval(Duration(freq, TimeUnit.MILLISECONDS))
    timer timedOut(1) subscribe (
      time => {
        count += 1
      },
      t => assert(false, s"stream error $t"),
      () => completed = true     
    )
    Thread.sleep(1200)
    assert(completed && count == 5, "completed: " + completed + ", event count: " + count)
  }*/

  test("WikipediaApi should correctly use concatRecovered") {
    val requests = Observable(1, 2, 3)
    val remoteComputation = (n: Int) => Observable(0 to n)
    val responses = requests concatRecovered remoteComputation
    val sum = responses.foldLeft(0) { (acc, tn) =>
      tn match {
        case Success(n) => acc + n
        case Failure(t) => throw t
      }
    }
    var total = -1
    val sub = sum.subscribe {
      s => total = s
    }
    assert(total == (1 + 1 + 2 + 1 + 2 + 3), s"Sum: $total")
  }
}