package spellingbee

import scala.io.Source

object Solver {

  // A selection has a middle letter and 6 outer letters. Remember the middle letter and ALL the letters
  // (We don't worry that we're recording the middle letter twice)
  type Selection = (Char, Set[Char])


  // Load up the words
  val words:Seq[String] = Source.fromResource("words.txt").getLines().filterNot({ w =>
    // We can drop anything less than 4 letters
    // And anything with more than 7 unique letters
    // And anything containing 'S' - eliminated by Riddler
    w.length < 4 || w.contains('s') || w.toSeq.distinct.length > 7
  }).toSeq

  println(s"There are ${words.length} words being considered!")

  // Start with the letters of the alphabet. Riddler removed 'S'
  val letters = "abcdefghijklmnopqrtuvwxyz"

  // All possible ways of choosing 7 letters
  val subsets:Iterator[Set[Char]] = letters.toSet.subsets(7)

  // From those, we need to consider the 7 different cases for which is the centre letter.
  // All up, we should have (26 choose 7) * 7 possibilities. About 4.6 million.
  val selections:Iterator[Selection] = subsets.flatMap { s =>
    for {
      centre <- s
    } yield (centre, s)
  }

  def score(word:String, sel:Selection):Int = {
    val (centre, all) = sel
    if (word.contains(centre) && word.forall { c => all.contains(c) }) {

      if (all.forall(c => word.contains(c))) {
        // Must have more than 4 letters!
        word.length + 7
      } else {
        if (word.length == 4) 1 else word.length
      }

    } else 0
  }

  var tried:Int = 0
  var biggest:(Selection, Int, String) = (('a', Set.empty[Char]), 0, "")

  // All the words that could score
  def wordsForSelection(sel:Selection):Seq[String] = words.filter { w =>
    val (centre, all) = sel
    w.contains(centre) && w.forall(c => all.contains(c))
  }

  def pangram(sel:Selection, w:String):Boolean = sel._2.forall(c => w.contains(c))

  def logTried(selection: Selection, score: Int):Unit = {
    tried = tried + 1
    if (tried % 1000 == 0) {
      println(s"Try ${tried}. $selection scores $score.  Biggest is $biggest")
    }

    if (score > biggest._2) {
      val pan = wordsForSelection(selection).find(w => pangram(selection, w))
      pan match {
        case Some(w) =>
          biggest = (selection, score, w)
          println(s"Biggest: $selection scores $score with pangram $w")
        case None =>
          println(s"Nearly had a biggest, but no pangram! $selection $score")
      }
    }
  }

  def maxScore(selection:Selection):Int = {
    val s = words.foldLeft(0) { case (total, word) => total + score(word, selection) }
    logTried(selection, s)
    s
  }

  def main(args:Array[String]):Unit = {
    println(selections.maxBy(maxScore))
    println(biggest)
  }

}