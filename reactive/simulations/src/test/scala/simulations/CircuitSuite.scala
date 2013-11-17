package simulations

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CircuitSuite extends CircuitSimulator with FunSuite {
  val InverterDelay = 1
  val AndGateDelay = 3
  val OrGateDelay = 5

  test("andGate example") {
    val in1, in2, out = new Wire
    andGate(in1, in2, out)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    assert(out.getSignal === false, "and 1")

    in1.setSignal(true)
    run

    assert(out.getSignal === false, "and 2")

    in2.setSignal(true)
    run

    assert(out.getSignal === true, "and 3")
  }

  //
  // to complete with tests for orGate, demux, ...
  //

  test("orGate") {
    val in1, in2, out = new Wire
    orGate(in1, in2, out)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    assert(out.getSignal === false, "or 1")

    in1.setSignal(true)
    run

    assert(out.getSignal === true, "or 2")

    in2.setSignal(true)
    run

    assert(out.getSignal === true, "or 3")
  }

  test("orGate2") {
    val in1, in2, out = new Wire
    orGate2(in1, in2, out)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    assert(out.getSignal === false, "or 1")

    in1.setSignal(true)
    run

    assert(out.getSignal === true, "or 2")

    in2.setSignal(true)
    run

    assert(out.getSignal === true, "or 3")
  }

  test("demux1") {
    val in, out = new Wire
    demux(in, List(), List(out))

    in.setSignal(false)
    run

    assert(out.getSignal === false, "demux1 1")

    in.setSignal(true)
    run

    assert(out.getSignal === true, "demux1 2")
  }

  test("demux2") {
    val in, c0, o0, o1 = new Wire
    demux(in, List(c0), List(o1, o0))

    c0.setSignal(false)
    in.setSignal(false)
    run

    assert(o0.getSignal === false, "demux2 1")
    assert(o1.getSignal === false, "demux2 2")

    in.setSignal(true)
    run

    assert(o0.getSignal === true, "demux2 3")
    assert(o1.getSignal === false, "demux2 4")

    c0.setSignal(true)
    run

    assert(o0.getSignal === false, "demux2 5")
    assert(o1.getSignal === true, "demux2 6")
  }

  test("demux4") {
    val in, c1, c0, o0, o1, o2, o3 = new Wire
    demux(in, List(c1, c0), List(o3, o2, o1, o0))

    c0.setSignal(false)
    c1.setSignal(false)
    in.setSignal(false)
    run

    assert(o0.getSignal === false, "demux4 1")
    assert(o1.getSignal === false, "demux4 2")
    assert(o2.getSignal === false, "demux4 3")
    assert(o3.getSignal === false, "demux4 4")

    in.setSignal(true)
    run

    assert(o0.getSignal === true, "demux4 5")
    assert(o1.getSignal === false, "demux4 6")
    assert(o2.getSignal === false, "demux4 7")
    assert(o3.getSignal === false, "demux4 8")

    c0.setSignal(true)
    c1.setSignal(false)
    run

    assert(o0.getSignal === false, "demux4 9")
    assert(o1.getSignal === true, "demux4 10")
    assert(o2.getSignal === false, "demux4 11")
    assert(o3.getSignal === false, "demux4 12")

    c0.setSignal(false)
    c1.setSignal(true)
    run

    assert(o0.getSignal === false, "demux4 17")
    assert(o1.getSignal === false, "demux4 18")
    assert(o2.getSignal === true, "demux4 19")
    assert(o3.getSignal === false, "demux4 20")

    c0.setSignal(true)
    c1.setSignal(true)
    run

    assert(o0.getSignal === false, "demux4 13")
    assert(o1.getSignal === false, "demux4 14")
    assert(o2.getSignal === false, "demux4 15")
    assert(o3.getSignal === true, "demux4 16")

  }

}
