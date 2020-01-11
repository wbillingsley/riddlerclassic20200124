# A riddler Classic solution, 10 Jan 2020

This is a simple solution to [this puzzle](https://fivethirtyeight.com/features/can-you-find-a-number-worth-its-weight-in-letters/)
on fivethirtyeight.com 

Suppose we write out a number in words (omitting "AND"). For example, 101 is "ONE HUNDRED ONE".

Suppose we now take the value of each letter in the name (A -> 1, B -> 2, etc.) and add them up.

So, 

* 1 is "ONE", with value 15 + 14 + 5 = 34
* 1,417 is "ONE THOUSAND FOUR HUNDRED SEVENTEEN", with value 379

The puzzle is to find the largest number that is smaller than the value of its letters.

We don't have many to try. By the time we reach 10,000, we'd need 384 letters in the name to have a value bigger than 
the number, even if they were all 'Z'. It's easily amenable to an exhaustive search within a reasonable range.

First, we need to translate the number into words. To keep the code short, let's recognise there's three different
kinds of word in number names:

* Ones like "THOUSAND", where we say how many there are. E.g, "TWO HUNDRED THOUSAND"
* Ones like "NINETY", where we state the number and then follow up with the remainder. E.g. "NINETY FOUR"
* Ones like "TWELVE", where there's nothing coming afterwards.

So, we define three function generators for these cases:

```scala
def xillion(i:Int, s:String):PartialFunction[Int, String] = {
  case number if (number >= i) => s"${stringify(number / i)} $s ${stringify(number % i)}"
}


def component(i:Int, s:String):PartialFunction[Int, String] = {
  case number if number >= i => s + ' ' + stringify(number - i)
}

def terminal(i:Int, s:String):PartialFunction[Int, String] = {
  case number if number >= i => s
}
```

Then we have a fairly boring list of what the words are:

```scala
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
    19 -> "NINETEEN", 18 -> "EIGHTEEN", 17 -> "SEVENTEEN", 16 -> "SIXTEEN", 15 -> "FIFTEEN", 14 -> "FOURTEEN", 
    13 -> "THIRTEEN", 12 -> "TWELVE", 11 -> "ELEVEN", 10 -> "TEN", 9 -> "NINE", 8 -> "EIGHT", 7 -> "SEVEN", 6 -> "SIX", 
    5 -> "FIVE", 4 -> "FOUR", 3 -> "THREE", 2 -> "TWO", 1 -> "ONE", 0 -> ""
).map { case (i, s) => terminal(i, s) }
```

Note that we need the `0 -> ""` at the end to cope with numbers like "THIRTY" that end with a component not a terminal.

Then we just compose those together into a stringify function:

```scala
val stringify:Stringer = wordFunctions.reduce[Stringer]({ case (a, b) => a.orElse(b) })
```

Going the other direction is much simpler. We have a string, we want to take all its letters and get corresponding 
values. By the power of ASCII, let's just subtract 'A' from each character and add 1 (because 'A' - 'A' is 0).

```scala
def stringValue(s:String):Int = s.filter(_.isLetter).map(_ - 'A' + 1).sum
```

Then, we just run through a range looking for the largest (last) number that matches the condition.

```scala
(1 to 10000).findLast(i => i < stringValue(stringify(i))) match {
  case Some(i) => println(s"The largest I could find was $i, ${stringify(i)} which is less than its value ${stringValue(stringify(i))} ")
}
```

The code is in `src/main/scala/riddlerpuzzle/Gematria.scala`
