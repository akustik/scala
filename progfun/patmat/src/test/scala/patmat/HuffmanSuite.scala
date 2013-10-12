package patmat

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import patmat.Huffman._

@RunWith(classOf[JUnitRunner])
class HuffmanSuite extends FunSuite {
  trait TestTrees {
    val t1 = Fork(Leaf('a', 2), Leaf('b', 3), List('a', 'b'), 5)
    val t2 = Fork(Fork(Leaf('a', 2), Leaf('b', 3), List('a', 'b'), 5), Leaf('d', 4), List('a', 'b', 'd'), 9)
  }
  
  trait TestText {
    val en = "onaltur any is mot havion em to to fand themar ine he urain ortudit offigum his hiscio to to tort trear therse to be or tance dic gooked fal fort offiel mir harway den be of suries sted an reard phre prearge postrip a bell thad waske forthe was morcho ely the ticial ther creffe ciscludie commot a crepro whatic oanden thouse fronce shang ters asew trecia the ablic and examic cors this ounted abial comet to thany lorseli accomagm theops sigh to a comear to prithe whimpar scond the fist oped spardis or by mes corly dis thentrus gingin is bery ch revent the spostic canes a the let to pial diclaw of tromor whe aterel por genct offerien al cogiel ithe be sucle extbor ses fic for tated rayeta de proveng ation theys ortury licaugh treate acklem whiscust whis fultia forldly fran the darst ougurim woreet to the expent cis theynize ocke of actian oulatio sublion of anates to tomars of the be wits of now monion der binst me exte mountme goll of cond ther go nowdes morinfloo usencie dol oposecon munt not the theter notake of hempat to the ung not win a the iturge theralf hanke sentric brome sweady as comaccia sich proll her tal ines the andesse a my of belard ped everce se jurk wasigh the men douseas oremse mes hers cong dedly whill graddy ortion expotte thuman infelin a dat mactur linis a contal modeve by cal the of wins onampt astat artud tolic levidge invern thrich ase the hileat and hateme th ther witts a gese ot bot thic the forat prons unew cal makist im to to use biannut ford t blembro youspot to bustecon peram to butchiel gion couldris nallar ing of intent she perver humbet sion a con by angerg thente apains costs the st own active assess th a cran thentin othe ins as pectia make joymbrieg antles appy whow ficen have foce they listan end re sevice wherat twe th hill yo ategra nown donly whin ofians fival to ited pulical speream of minins by staked ding a lany dial per etteses on is ot hilexplie vighty the of moname connot tempec a millow to wilizon on elinglog ving the ch the to printle ney ing this untour doento exence se thus quens disfet to robt ressibul teren prommor is whisit exatize des hin teritie al opoter foce ininot cal pret prople wention uppecar to grent wout the durbse mation cee tentou geslog alcobjes thousna now upprog makerjus dentier artursac lissan ter cone inuest labou wood the te aremay wition to the is of herns and isuctio cands ments caugs fork shaver offecrit the nommon tooded evan their id be sticiew deperi ant hig ifir sion a lient by of lion ise fouree hic ithal thears of whe magation for a consed or labodis it ideris belso for caugh dragan ineren fals ty of thatic ina fitegin beentien torifign ent thow to fit hicure th the ands lestan or orld hered the bal a borlex to he cur vany muces anivel the to heirdis of earchi reavis thenday tridid of ish a forapir a slogion reld tagence lity des ablecur oran cherte essour givear ded ank commen thesse on nothed sur ealig pin by a sakel dis wholop tions to shis oppleas peasirt themen withe heris."
  }

  test("weight of a larger tree") {
    new TestTrees {
      assert(weight(t1) === 5)
    }
  }

  test("chars of a larger tree") {
    new TestTrees {
      assert(chars(t2) === List('a', 'b', 'd'))
    }
  }

  test("string2chars(\"hello, world\")") {
    assert(string2Chars("hello, world") === List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'))
  }

  test("times of an empty list") {
    assert(times(List()) == List())
  }

  test("times of a list with duplicates") {
    assert(times(List('a', 'b', 'a', 'd')) == List(('d', 1), ('b', 1), ('a', 2)))
  }

  test("makeOrderedLeafList for some frequency table") {
    assert(makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))) === List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 3)))
  }

  test("combine of some leaf list") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(combine(leaflist) === List(Fork(Leaf('e', 1), Leaf('t', 2), List('e', 't'), 3), Leaf('x', 4)))
  }

  test("combine of some leaf list until singleton") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(until(singleton, combine)(leaflist) ===
      List(Fork(Fork(Leaf('e', 1), Leaf('t', 2), List('e', 't'), 3), Leaf('x', 4), List('e', 't', 'x'), 7)))
  }
  
  test("createCodeTree of a long english text and encode with slow encoder") {
    new TestText {
      val codeTree = createCodeTree(string2Chars(en))
      encode(codeTree)(string2Chars("hello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello world"))
    }
  }
  
  test("createCodeTree of a long english text and encode with quick encoder") {
    new TestText {
      val codeTree = createCodeTree(string2Chars(en))
      quickEncode(codeTree)(string2Chars("hello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello world"))
    }
  }

  test("print the secret") {
    assert(decodedSecret === List('h', 'u', 'f', 'f', 'm', 'a', 'n', 'e', 's', 't', 'c', 'o', 'o', 'l'))
  }

  test("encode texts with t2") {
    new TestTrees {
      val encoder = encode(t2)(_)
      val quickEncoder = quickEncode(t2)(_)
      assert(encoder(List('d', 'b', 'a')) === List(1, 0, 1, 0, 0))
      assert(quickEncoder(List('d', 'b', 'a')) === List(1, 0, 1, 0, 0))
    }
  }

  test("decode and encode a very short text should be identity") {
    new TestTrees {
      val encoder = encode(t1)(_)
      assert(decode(t1, encoder("ab".toList)) === "ab".toList)
    }
  }

  test("convert a tree in a code table") {
	  new TestTrees {
	    assert(convert(t1) === List(('a', List(0)), ('b', List(1))))
	    assert(convert(t2) === List(('a', List(0,0)), ('b', List(0,1)), ('d', List(1))))
	  }
  }
}
