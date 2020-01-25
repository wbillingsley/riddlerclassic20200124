# A riddler Classic solution, 24 Jan 2020

This is a simple solution to [this puzzle](https://fivethirtyeight.com/features/how-many-pennies-should-you-pinch/)
on fivethirtyeight.com 

The puzzle is a game where a stack of coins is split into two. On your turn, you can:

* take *x* coins from one stack
* take *x* coins from the other stack
* take *(x, x)* coins from the stacks (i.e., the same number from both)

The winner is the player who takes the last coin.

You nominate the number of coins. Then Riddler splits them into stacks (however he likes). Then you go first.
What numbers of coins ensure you win?

## Solution

The code is in PennyPincher.scala

As the number of coins in the game always decreases, it's open to remembering which states win for which player.

The state `(0, 0)` is the "you've already lost" state, so mark that as a loser.

Now, on any state:
 
* if there's a move that makes your opponent always face a losing state, you'll win
* if every move would give your opponent a winning state, you'll lose

That lets us mark the states. Though we should add a check in case there are any that were indeterminate 
(there shouldn't be - the state space is small so it should be fully enumerable).

Now lets consider the splits. For a number *x*, do all possible splits produce a winning state? 
Those are the ones we're looking for.

```scala
splits(x).forall(wins)
```

But there are more guaranteed wins than losses, so instead let's look for the losing ones so we can print them.
Let's look at the first 30 results

```
For 1 coins, the first player always wins. Check: true
For 2 coins, the first player always wins. Check: true
For 3 coins, the first player loses. Split is (1,2)
For 4 coins, the first player always wins. Check: true
For 5 coins, the first player always wins. Check: true
For 6 coins, the first player always wins. Check: true
For 7 coins, the first player always wins. Check: true
For 8 coins, the first player loses. Split is (3,5)
For 9 coins, the first player always wins. Check: true
For 10 coins, the first player always wins. Check: true
For 11 coins, the first player loses. Split is (4,7)
For 12 coins, the first player always wins. Check: true
For 13 coins, the first player always wins. Check: true
For 14 coins, the first player always wins. Check: true
For 15 coins, the first player always wins. Check: true
For 16 coins, the first player loses. Split is (6,10)
For 17 coins, the first player always wins. Check: true
For 18 coins, the first player always wins. Check: true
For 19 coins, the first player always wins. Check: true
For 20 coins, the first player always wins. Check: true
For 21 coins, the first player loses. Split is (8,13)
For 22 coins, the first player always wins. Check: true
For 23 coins, the first player always wins. Check: true
For 24 coins, the first player loses. Split is (9,15)
For 25 coins, the first player always wins. Check: true
For 26 coins, the first player always wins. Check: true
For 27 coins, the first player always wins. Check: true
For 28 coins, the first player always wins. Check: true
For 29 coins, the first player loses. Split is (11,18)
For 30 coins, the first player always wins. Check: true
```

You'll notice the difference between them goes up by 1 each time.

That appears to be because `(1, 2)` is key. In that state, you're forced to produce `(0, x)`, `(x, 0)`, or `(x, x)` and
will lose. So, next we want to *stop* our opponent from producing `(1, 2)`, and he's trying to stop us from producing it.