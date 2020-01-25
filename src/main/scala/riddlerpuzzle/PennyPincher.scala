package riddlerpuzzle

import scala.collection.mutable


/**
 * The number of coins always decreases, so if we memoize which states win and lose, we can just search for whether
 * this state has a move that makes the other player always lose, or whether all this state's move let the other player
 * always win.
 */
object PennyPincher {

  /** A game state has two stacks of coins */
  type State = (Int, Int)

  /** A move goes from one state to another. We make it a case class so it prints well. */
  case class Move(x:Int, y:Int) {
    def apply(s:State):State = {
      val (a, b) = s
      (a - x, b - y)
    }
  }

  /** For a given state, we can generate what every available move is */
  def availableMoves(s:State):Seq[Move] = {
    val (a, b) = s
    val min = Math.min(a, b)

    val takeFromBoth:Seq[Move] = for { i <- 1 to min } yield Move(i, i)
    val takeFromA:Seq[Move] = for { i <- 1 to a } yield Move(i, 0)
    val takeFromB:Seq[Move] = for { i <- 1 to b } yield Move(0, i)

    takeFromBoth ++ takeFromA ++ takeFromB
  }

  // If a state is in the map with true, it is known to win. With false, it is known to lose.
  // Anything not in the map isn't known yet.
  // For (0, 0) the last coin has been taken, so you've already lost, so that is our seed.
  val knownStates:mutable.Map[State, Boolean] = mutable.Map((0, 0) -> false)

  // Tests if a state wins or loses for the current player, caching it for performance reasons
  def test(s:State):Option[Boolean] = {

    // If we already know it, return it
    if (knownStates.contains(s)) knownStates.get(s) else {
      // Otherwise, work out if this state wins or loses (or if we can't tell yet)

      val moves = availableMoves(s)

      // If there exists a move that makes the next player lose, we win
      if (moves.exists(m => test(m(s)).contains(false))) {
        knownStates(s) = true
        Some(true)

      // If all moves make the next player win, we lose
      } else if (moves.forall(m => test(m(s)).contains(true))) {
        knownStates(s) = false
        Some(false)

      // Otherwise we don't know yet.
      } else None
    }
  }

  // Shorthand for if this state is known to lose for the next player
  def loses(s:State):Boolean = test(s).contains(false)

  // Shorthand for if this state is known to win for the next player
  def wins(s:State):Boolean = test(s).contains(true)


  // All possible ways to set up the game, for a given number of coins
  def splits(i:Int):Seq[State] = {
    for { j <- 0 to i } yield (j, i - j)
  }


  def main(args:Array[String]):Unit = {

    for {
      x <- (1 to 300)
    } {
      // Find one that loses for the first player
      splits(x).find(loses) match {
        case Some(l) =>
          println(s"For $x coins, the first player loses. Split is $l")
        case None =>
          println(s"For $x coins, the first player always wins. Check: ${splits(x).forall(wins)}")
      }


    }
  }
}
