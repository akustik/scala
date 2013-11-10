package quickcheck

import common._
import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._
import scala.annotation.tailrec

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  @tailrec final def exists(element: Int, heap: H): Boolean = {
    if (isEmpty(heap)) false
    else if (element == findMin(heap)) true
    else if (element > findMin(heap)) exists(element, deleteMin(heap))
    else false
  }

  @tailrec final def truncate(f: Int => Boolean, heap: H): H = {
    if (isEmpty(heap)) heap
    else if (f(findMin(heap))) heap
    else truncate(f, deleteMin(heap))
  }
  
  @tailrec final def count(acc: Int, heap: H): Int = {
    if (isEmpty(heap)) acc
    else count(acc + 1, deleteMin(heap))
  }
  
  @tailrec final def toList(acc: List[Int], heap: H): List[Int] = {
    if (isEmpty(heap)) acc
    else toList(findMin(heap) :: acc, deleteMin(heap))
  }

  property("isEmpty") = {
    isEmpty(empty)
  }
  
  property("insert and deleteMin on empty") = {
    deleteMin(insert(0, empty)) == empty
  }

  property("findMin - heap keeps priority (== min)") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("findMin - heap keeps priority (> min)") = forAll { (h: H) =>
    if (!isEmpty(h) && findMin(h) < Int.MaxValue) {
      val m = findMin(h)
      val m2 = findMin(insert(m + 1, h))
      m == m2
    } else {
      //Not used
      true
    }
  }

  property("insert - elements inserted in heap can be recovered") = forAll { (h: H, a: Int, b: Int) =>
    val updated = insert(a, insert(b, h))
    exists(a, updated) && exists(b, updated)
  }
  
  property("insert - elements inserted in heap are recovered in orderly manner" ) = forAll { (h: H, a: Int, b: Int) =>
    if(!exists(a, h) && !exists(b, h)) {
      val updated = insert(a, insert(b, h))
      if(a < b) exists(b, truncate(_ > a, updated))
      else if(a > b) exists(a, truncate(_ > b, updated))
      else !exists(a, truncate(_ > a, updated))
    } else true
  }
  
  property("meld - the melded heap keeps the same minimum" ) = forAll { (h1: H, h2: H) =>
    val m1 = findMin(h1)
    val m2 = findMin(h2)
    val m = findMin(meld(h1, h2))
    if(m1 < m2) m == m1 else m == m2
  }
  
  property("meld - the melded heap contains the same number of elements" ) = forAll { (h1: H, h2: H) => 
    val m12 = count(count(0, h1), h2)
    val m = count(0, meld(h1, h2))
    m12 == m
  }
  
  property("meld - the melded heap contains the same elements" ) = forAll { (h1: H, h2: H) =>
    val l1 = toList(Nil, h1)
    val l2 = toList(Nil, h2)
    val l12 = (l1 ++ l2).sorted
    val lm = toList(Nil, meld(h1, h2)).reverse
    l12 == lm
  }
 

  lazy val genHeap: Gen[H] = for {
    v <- arbitrary[Int]
    h <- oneOf(empty, genHeap)
  } yield insert(v, h)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

}
