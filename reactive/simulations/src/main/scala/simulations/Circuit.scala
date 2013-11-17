package simulations

import common._
import scala.annotation.tailrec

class Wire {
  private var sigVal = false
  private var actions: List[Simulator#Action] = List()

  def getSignal: Boolean = sigVal

  def setSignal(s: Boolean) {
    if (s != sigVal) {
      sigVal = s
      actions.foreach(action => action())
    }
  }

  def addAction(a: Simulator#Action) {
    actions = a :: actions
    a()
  }
}

abstract class CircuitSimulator extends Simulator {

  val InverterDelay: Int
  val AndGateDelay: Int
  val OrGateDelay: Int

  def probe(name: String, wire: Wire) {
    wire addAction {
      () =>
        afterDelay(0) {
          println(
            "  " + currentTime + ": " + name + " -> " + wire.getSignal)
        }
    }
  }

  def inverter(input: Wire, output: Wire) {
    def invertAction() {
      val inputSig = input.getSignal
      afterDelay(InverterDelay) { output.setSignal(!inputSig) }
    }
    input addAction invertAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) {
    def andAction() {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) { output.setSignal(a1Sig & a2Sig) }
    }
    a1 addAction andAction
    a2 addAction andAction
  }

  //
  // to complete with orGates and demux...
  //

  def orGate(a1: Wire, a2: Wire, output: Wire) {
    def orAction() {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(OrGateDelay) { output.setSignal(a1Sig | a2Sig) }
    }
    a1 addAction orAction
    a2 addAction orAction
  }

  def orGate2(a1: Wire, a2: Wire, output: Wire) {
    val notA1, notA2, notOut = new Wire
    inverter(a1, notA1)
    inverter(a2, notA2)
    andGate(notA1, notA2, notOut)
    inverter(notOut, output)
  }

  /*
   * As long as you implement it in terms of 2-1 AND gates, I don't see why not. Though I don't think it would help. 
   * I think you ought to think of the problem in terms of dividing the outputs in halves for each control wire. 
   * The most significant control bit (wire) should select the first or second half of the outputs. The next bit 
   * should select the first or second half of each of the previous halves. Repeat until there are no more control
   * bits, at which point you should have exactly one output to activate.
   */

  def demux(in: Wire, c: List[Wire], out: List[Wire]) {
    
    @tailrec
    def addAndsToOutputs(msb: Wire, out: List[Wire], accIns: List[Wire]): List[Wire] = {
      out match {
        case Nil => accIns.reverse
        case x :: xs => {
          val in = new Wire
          andGate(msb, in, x)
          //probe(s"in ${out.size} ${accIns.size}", in)
          addAndsToOutputs(msb, xs, in :: accIns)
        }
      }
    }
    
    c match {
      case Nil => {
        //TODO: Maybe use the base case with one bit to avoid using
        //an and gate with the same input?
        andGate(in, in, out.head)
      }
      case msb :: bs => {
        val notMsb = new Wire
        inverter(msb, notMsb)
        val msHalf = out.slice(0, out.size / 2)
        val lsHalf = out.slice(out.size / 2, out.size)        
        val msIn = addAndsToOutputs(msb, msHalf, Nil)
        val lsIn = addAndsToOutputs(notMsb, lsHalf, Nil)        
        demux(in, bs, msIn)
        demux(in, bs, lsIn)
      }
    }
  }

}

object Circuit extends CircuitSimulator {
  val InverterDelay = 1
  val AndGateDelay = 3
  val OrGateDelay = 5

  def andGateExample {
    val in1, in2, out = new Wire
    andGate(in1, in2, out)
    probe("in1", in1)
    probe("in2", in2)
    probe("out", out)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    in1.setSignal(true)
    run

    in2.setSignal(true)
    run
  }

  //
  // to complete with orGateExample and demuxExample...
  //
  
  def orGateExample {
    val in1, in2, out = new Wire
    orGate2(in1, in2, out)
    probe("in1", in1)
    probe("in2", in2)
    probe("out", out)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    in1.setSignal(true)
    in2.setSignal(false)
    run

    in1.setSignal(true)
    in2.setSignal(true)
    run
  }
  
  def demuxExample {
    val in, c0, o0, o1 = new Wire
    demux(in, List(c0), List(o1, o0))
    probe("in", in)
    probe("c0", c0)
    probe("o0", o0)
    probe("o1", o1)
    c0.setSignal(false)
    in.setSignal(false)
    run
    
    c0.setSignal(false)
    in.setSignal(true)
    run
    
    c0.setSignal(true)
    in.setSignal(true)
    run
  }
}

object CircuitMain extends App {
  // You can write tests either here, or better in the test class CircuitSuite.
  Circuit.demuxExample
}
