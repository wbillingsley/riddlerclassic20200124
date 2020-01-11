package riddlerpuzzle

object Gematria {

  type Stringer = PartialFunction[Int, String]

  /*
   * When we write a number, for some components we say how many there are (even if there's only one of them)
   * e.g. "TWELVE BILLION", "ONE MILLION", "TWENTY THOUSAND", "TWO HUNDRED"
   * whereas we don't say "ONE NINETY".
   *
   * xillion generates partial functions that can write millions, thousands, hundreds
   */
  def xillion(i:Int, s:String):PartialFunction[Int, String] = {
    case number if (number >= i) => s"${stringify(number / i)} $s ${stringify(number % i)}"
  }

  /*
   * For other components, we write out the component, and then process the remainder.
   * e.g. "SEVENTY five"
   */
  def component(i:Int, s:String):PartialFunction[Int, String] = {
    case number if number >= i => s + ' ' + stringify(number - i)
  }

  /*
   * Others are terminal. Once we've said "THIRTEEN", there's nothing comping after it.
   */
  def terminal(i:Int, s:String):PartialFunction[Int, String] = {
    case number if number >= i => s
  }

  /**
   * Together, these give us a list of partial functions that we can apply to numbers to transform them.
   */
  val wordFunctions:Seq[Stringer] = Seq(
    xillion(1000000, "MILLION"),
    xillion(1000, "THOUSAND"),
    xillion(100, "HUNDRED"),
  ) ++ Seq(
    90 -> "NINETY",
    80 -> "EIGHTY",
    70 -> "SEVENTY",
    60 -> "SIXTY",
    50 -> "FIFTY",
    40 -> "FORTY",
    30 -> "THIRTY",
    20 -> "TWENTY"
  ).map({ case (i, s) => component(i, s) }) ++ Seq(
    19 -> "NINETEEN", 18 -> "EIGHTEEN", 17 -> "SEVENTEEN", 16 -> "SIXTEEN", 15 -> "FIFTEEN", 14 -> "FOURTEEN", 13 -> "THIRTEEN",
    12 -> "TWELVE", 11 -> "ELEVEN",
    10 -> "TEN", 9 -> "NINE", 8 -> "EIGHT", 7 -> "SEVEN", 6 -> "SIX", 5 -> "FIVE", 4 -> "FOUR", 3 -> "THREE", 2 -> "TWO", 1 -> "ONE", 0 -> ""
  ).map { case (i, s) => terminal(i, s) }

  /**
   * Let's take that list of partial functions, and compose them all together into a single function that can write out
   * a number as a word.
   */
  val stringify:Stringer = wordFunctions.reduce[Stringer]({ case (a, b) => a.orElse(b) })

  /**
   * Next we need the number score for a string. This is fairly short, as characters have an ASCII value.
   * We just subtract 'A' and add 1 (because otherwise A is zero)
   */
  def stringValue(s:String):Int = s.filter(_.isLetter).map(_ - 'A' + 1).sum

  def main(args:Array[String]) = {
    // Let's test our stringify
    println(stringify(10234231))

    // And our value of
    println(stringValue("THREE MILLION ONE HUNDRED FORTY THOUSAND TWO HUNDRED SEVENTY FIVE"))

    // Now let's find the number.
    // We can certainly stop at 10,000 because by that point, we'd need 384 letters in the number's name even if they
    // were all Z.
    (1 to 10000).findLast(i => i < stringValue(stringify(i))) match {
      case Some(i) => println(s"The largest I could find was $i, ${stringify(i)} which is less than its value ${stringValue(stringify(i))} ")
    }
  }


}
