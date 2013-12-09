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
import rx.lang.scala.{ Observable, Observer, Subscription }
import rx.lang.scala.subscriptions.BooleanSubscription

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
    var res = 0
    var msg = ""
    var completed = false
    val seq = Observable(1, 2, 3, 4)
    val rec = seq.map(x => {
      if (x == 3) throw new IOException("OOPS")
      else x
    }).recovered subscribe (
      value => {
        value match {
          case Success(x) => res = res + x
          case Failure(t) => msg = t.toString()
        }
      },
      t => assert(false, s"stream error $t"),
      () => completed = true)
    assert(res === 3)
    assert(completed == true)
    assert(msg === new IOException("OOPS").toString)
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

  /*
  test("WikipediaApi should correctly use concatRecovered mine") {
    val obs = Observable((observer: Observer[String]) => {
      observer.onNext("1")
      observer.onNext("2")
      observer.onNext("3")
      BooleanSubscription {

      }
    }) concatRecovered (s => {
      if (s == "1") {
        Observable("1")
      } else {
        Observable((obs2: Observer[String]) => {
          obs2.onError(new IOException)
          BooleanSubscription {

          }
        })
      }
    })

    val res = obs.toBlockingObservable.toList.toSet
    assert(res.toString === Set(Success("1"), Failure(new IOException)).toString)
    
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