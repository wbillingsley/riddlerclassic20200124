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

So at the core of our algorithm we have a function `test(s)` for testing if a state is a winner or a loser for the next player.
Let's be a bit verbose in case you're new to Scala.

```scala
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
```

That lets us mark the states. 

Now lets consider the splits. For a number *x*, do all possible splits produce a winning state? 
Those are the ones we're looking for.

```scala
splits(x).forall(wins)
```

But there are more guaranteed wins than losses, so instead let's look for the losing ones so we can print them.
This can look for the first 300 results:

```scala
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
```

Because we're searching for *definite losses*, I also add a check making sure the other states are all *definite winners* 
in case there were any that were indeterminate. Tthere shouldn't be - the state space is small so it should be fully enumerable.

Let's paste below the first 300. You'll notice the difference between them goes up by 1 each time.
                                 
That appears to be because `(1, 2)` is key. In that state, you're forced to produce `(0, x)`, `(x, 0)`, or `(x, x)` and
will lose. So, next we want to *stop* our opponent from producing `(1, 2)`, and he's trying to stop us from producing it.

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
For 31 coins, the first player always wins. Check: true
For 32 coins, the first player loses. Split is (12,20)
For 33 coins, the first player always wins. Check: true
For 34 coins, the first player always wins. Check: true
For 35 coins, the first player always wins. Check: true
For 36 coins, the first player always wins. Check: true
For 37 coins, the first player loses. Split is (14,23)
For 38 coins, the first player always wins. Check: true
For 39 coins, the first player always wins. Check: true
For 40 coins, the first player always wins. Check: true
For 41 coins, the first player always wins. Check: true
For 42 coins, the first player loses. Split is (16,26)
For 43 coins, the first player always wins. Check: true
For 44 coins, the first player always wins. Check: true
For 45 coins, the first player loses. Split is (17,28)
For 46 coins, the first player always wins. Check: true
For 47 coins, the first player always wins. Check: true
For 48 coins, the first player always wins. Check: true
For 49 coins, the first player always wins. Check: true
For 50 coins, the first player loses. Split is (19,31)
For 51 coins, the first player always wins. Check: true
For 52 coins, the first player always wins. Check: true
For 53 coins, the first player always wins. Check: true
For 54 coins, the first player always wins. Check: true
For 55 coins, the first player loses. Split is (21,34)
For 56 coins, the first player always wins. Check: true
For 57 coins, the first player always wins. Check: true
For 58 coins, the first player loses. Split is (22,36)
For 59 coins, the first player always wins. Check: true
For 60 coins, the first player always wins. Check: true
For 61 coins, the first player always wins. Check: true
For 62 coins, the first player always wins. Check: true
For 63 coins, the first player loses. Split is (24,39)
For 64 coins, the first player always wins. Check: true
For 65 coins, the first player always wins. Check: true
For 66 coins, the first player loses. Split is (25,41)
For 67 coins, the first player always wins. Check: true
For 68 coins, the first player always wins. Check: true
For 69 coins, the first player always wins. Check: true
For 70 coins, the first player always wins. Check: true
For 71 coins, the first player loses. Split is (27,44)
For 72 coins, the first player always wins. Check: true
For 73 coins, the first player always wins. Check: true
For 74 coins, the first player always wins. Check: true
For 75 coins, the first player always wins. Check: true
For 76 coins, the first player loses. Split is (29,47)
For 77 coins, the first player always wins. Check: true
For 78 coins, the first player always wins. Check: true
For 79 coins, the first player loses. Split is (30,49)
For 80 coins, the first player always wins. Check: true
For 81 coins, the first player always wins. Check: true
For 82 coins, the first player always wins. Check: true
For 83 coins, the first player always wins. Check: true
For 84 coins, the first player loses. Split is (32,52)
For 85 coins, the first player always wins. Check: true
For 86 coins, the first player always wins. Check: true
For 87 coins, the first player loses. Split is (33,54)
For 88 coins, the first player always wins. Check: true
For 89 coins, the first player always wins. Check: true
For 90 coins, the first player always wins. Check: true
For 91 coins, the first player always wins. Check: true
For 92 coins, the first player loses. Split is (35,57)
For 93 coins, the first player always wins. Check: true
For 94 coins, the first player always wins. Check: true
For 95 coins, the first player always wins. Check: true
For 96 coins, the first player always wins. Check: true
For 97 coins, the first player loses. Split is (37,60)
For 98 coins, the first player always wins. Check: true
For 99 coins, the first player always wins. Check: true
For 100 coins, the first player loses. Split is (38,62)
For 101 coins, the first player always wins. Check: true
For 102 coins, the first player always wins. Check: true
For 103 coins, the first player always wins. Check: true
For 104 coins, the first player always wins. Check: true
For 105 coins, the first player loses. Split is (40,65)
For 106 coins, the first player always wins. Check: true
For 107 coins, the first player always wins. Check: true
For 108 coins, the first player always wins. Check: true
For 109 coins, the first player always wins. Check: true
For 110 coins, the first player loses. Split is (42,68)
For 111 coins, the first player always wins. Check: true
For 112 coins, the first player always wins. Check: true
For 113 coins, the first player loses. Split is (43,70)
For 114 coins, the first player always wins. Check: true
For 115 coins, the first player always wins. Check: true
For 116 coins, the first player always wins. Check: true
For 117 coins, the first player always wins. Check: true
For 118 coins, the first player loses. Split is (45,73)
For 119 coins, the first player always wins. Check: true
For 120 coins, the first player always wins. Check: true
For 121 coins, the first player loses. Split is (46,75)
For 122 coins, the first player always wins. Check: true
For 123 coins, the first player always wins. Check: true
For 124 coins, the first player always wins. Check: true
For 125 coins, the first player always wins. Check: true
For 126 coins, the first player loses. Split is (48,78)
For 127 coins, the first player always wins. Check: true
For 128 coins, the first player always wins. Check: true
For 129 coins, the first player always wins. Check: true
For 130 coins, the first player always wins. Check: true
For 131 coins, the first player loses. Split is (50,81)
For 132 coins, the first player always wins. Check: true
For 133 coins, the first player always wins. Check: true
For 134 coins, the first player loses. Split is (51,83)
For 135 coins, the first player always wins. Check: true
For 136 coins, the first player always wins. Check: true
For 137 coins, the first player always wins. Check: true
For 138 coins, the first player always wins. Check: true
For 139 coins, the first player loses. Split is (53,86)
For 140 coins, the first player always wins. Check: true
For 141 coins, the first player always wins. Check: true
For 142 coins, the first player always wins. Check: true
For 143 coins, the first player always wins. Check: true
For 144 coins, the first player loses. Split is (55,89)
For 145 coins, the first player always wins. Check: true
For 146 coins, the first player always wins. Check: true
For 147 coins, the first player loses. Split is (56,91)
For 148 coins, the first player always wins. Check: true
For 149 coins, the first player always wins. Check: true
For 150 coins, the first player always wins. Check: true
For 151 coins, the first player always wins. Check: true
For 152 coins, the first player loses. Split is (58,94)
For 153 coins, the first player always wins. Check: true
For 154 coins, the first player always wins. Check: true
For 155 coins, the first player loses. Split is (59,96)
For 156 coins, the first player always wins. Check: true
For 157 coins, the first player always wins. Check: true
For 158 coins, the first player always wins. Check: true
For 159 coins, the first player always wins. Check: true
For 160 coins, the first player loses. Split is (61,99)
For 161 coins, the first player always wins. Check: true
For 162 coins, the first player always wins. Check: true
For 163 coins, the first player always wins. Check: true
For 164 coins, the first player always wins. Check: true
For 165 coins, the first player loses. Split is (63,102)
For 166 coins, the first player always wins. Check: true
For 167 coins, the first player always wins. Check: true
For 168 coins, the first player loses. Split is (64,104)
For 169 coins, the first player always wins. Check: true
For 170 coins, the first player always wins. Check: true
For 171 coins, the first player always wins. Check: true
For 172 coins, the first player always wins. Check: true
For 173 coins, the first player loses. Split is (66,107)
For 174 coins, the first player always wins. Check: true
For 175 coins, the first player always wins. Check: true
For 176 coins, the first player loses. Split is (67,109)
For 177 coins, the first player always wins. Check: true
For 178 coins, the first player always wins. Check: true
For 179 coins, the first player always wins. Check: true
For 180 coins, the first player always wins. Check: true
For 181 coins, the first player loses. Split is (69,112)
For 182 coins, the first player always wins. Check: true
For 183 coins, the first player always wins. Check: true
For 184 coins, the first player always wins. Check: true
For 185 coins, the first player always wins. Check: true
For 186 coins, the first player loses. Split is (71,115)
For 187 coins, the first player always wins. Check: true
For 188 coins, the first player always wins. Check: true
For 189 coins, the first player loses. Split is (72,117)
For 190 coins, the first player always wins. Check: true
For 191 coins, the first player always wins. Check: true
For 192 coins, the first player always wins. Check: true
For 193 coins, the first player always wins. Check: true
For 194 coins, the first player loses. Split is (74,120)
For 195 coins, the first player always wins. Check: true
For 196 coins, the first player always wins. Check: true
For 197 coins, the first player always wins. Check: true
For 198 coins, the first player always wins. Check: true
For 199 coins, the first player loses. Split is (76,123)
For 200 coins, the first player always wins. Check: true
For 201 coins, the first player always wins. Check: true
For 202 coins, the first player loses. Split is (77,125)
For 203 coins, the first player always wins. Check: true
For 204 coins, the first player always wins. Check: true
For 205 coins, the first player always wins. Check: true
For 206 coins, the first player always wins. Check: true
For 207 coins, the first player loses. Split is (79,128)
For 208 coins, the first player always wins. Check: true
For 209 coins, the first player always wins. Check: true
For 210 coins, the first player loses. Split is (80,130)
For 211 coins, the first player always wins. Check: true
For 212 coins, the first player always wins. Check: true
For 213 coins, the first player always wins. Check: true
For 214 coins, the first player always wins. Check: true
For 215 coins, the first player loses. Split is (82,133)
For 216 coins, the first player always wins. Check: true
For 217 coins, the first player always wins. Check: true
For 218 coins, the first player always wins. Check: true
For 219 coins, the first player always wins. Check: true
For 220 coins, the first player loses. Split is (84,136)
For 221 coins, the first player always wins. Check: true
For 222 coins, the first player always wins. Check: true
For 223 coins, the first player loses. Split is (85,138)
For 224 coins, the first player always wins. Check: true
For 225 coins, the first player always wins. Check: true
For 226 coins, the first player always wins. Check: true
For 227 coins, the first player always wins. Check: true
For 228 coins, the first player loses. Split is (87,141)
For 229 coins, the first player always wins. Check: true
For 230 coins, the first player always wins. Check: true
For 231 coins, the first player loses. Split is (88,143)
For 232 coins, the first player always wins. Check: true
For 233 coins, the first player always wins. Check: true
For 234 coins, the first player always wins. Check: true
For 235 coins, the first player always wins. Check: true
For 236 coins, the first player loses. Split is (90,146)
For 237 coins, the first player always wins. Check: true
For 238 coins, the first player always wins. Check: true
For 239 coins, the first player always wins. Check: true
For 240 coins, the first player always wins. Check: true
For 241 coins, the first player loses. Split is (92,149)
For 242 coins, the first player always wins. Check: true
For 243 coins, the first player always wins. Check: true
For 244 coins, the first player loses. Split is (93,151)
For 245 coins, the first player always wins. Check: true
For 246 coins, the first player always wins. Check: true
For 247 coins, the first player always wins. Check: true
For 248 coins, the first player always wins. Check: true
For 249 coins, the first player loses. Split is (95,154)
For 250 coins, the first player always wins. Check: true
For 251 coins, the first player always wins. Check: true
For 252 coins, the first player always wins. Check: true
For 253 coins, the first player always wins. Check: true
For 254 coins, the first player loses. Split is (97,157)
For 255 coins, the first player always wins. Check: true
For 256 coins, the first player always wins. Check: true
For 257 coins, the first player loses. Split is (98,159)
For 258 coins, the first player always wins. Check: true
For 259 coins, the first player always wins. Check: true
For 260 coins, the first player always wins. Check: true
For 261 coins, the first player always wins. Check: true
For 262 coins, the first player loses. Split is (100,162)
For 263 coins, the first player always wins. Check: true
For 264 coins, the first player always wins. Check: true
For 265 coins, the first player loses. Split is (101,164)
For 266 coins, the first player always wins. Check: true
For 267 coins, the first player always wins. Check: true
For 268 coins, the first player always wins. Check: true
For 269 coins, the first player always wins. Check: true
For 270 coins, the first player loses. Split is (103,167)
For 271 coins, the first player always wins. Check: true
For 272 coins, the first player always wins. Check: true
For 273 coins, the first player always wins. Check: true
For 274 coins, the first player always wins. Check: true
For 275 coins, the first player loses. Split is (105,170)
For 276 coins, the first player always wins. Check: true
For 277 coins, the first player always wins. Check: true
For 278 coins, the first player loses. Split is (106,172)
For 279 coins, the first player always wins. Check: true
For 280 coins, the first player always wins. Check: true
For 281 coins, the first player always wins. Check: true
For 282 coins, the first player always wins. Check: true
For 283 coins, the first player loses. Split is (108,175)
For 284 coins, the first player always wins. Check: true
For 285 coins, the first player always wins. Check: true
For 286 coins, the first player always wins. Check: true
For 287 coins, the first player always wins. Check: true
For 288 coins, the first player loses. Split is (110,178)
For 289 coins, the first player always wins. Check: true
For 290 coins, the first player always wins. Check: true
For 291 coins, the first player loses. Split is (111,180)
For 292 coins, the first player always wins. Check: true
For 293 coins, the first player always wins. Check: true
For 294 coins, the first player always wins. Check: true
For 295 coins, the first player always wins. Check: true
For 296 coins, the first player loses. Split is (113,183)
For 297 coins, the first player always wins. Check: true
For 298 coins, the first player always wins. Check: true
For 299 coins, the first player loses. Split is (114,185)
For 300 coins, the first player always wins. Check: true
```

