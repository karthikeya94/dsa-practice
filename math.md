# 🌌 The Celestial Observatory: Math & Geometry 🌌
### *A Hero’s Journey from Counting Pebbles to Bending the Fabric of Space*

---

> *Alex climbed the highest peak of Algorithmia to the Celestial Observatory. The roof was open to the stars, which were connected by glowing lines forming massive triangles and polygons. A blind astronomer with a mechanical arm named **Archimedes Star-Chart** was drawing equations in the air.*
>
> *"Alex,"* Archimedes whispered, his voice echoing like a theorem. "You have mastered data structures and graphs. But you still think in `int` and `String`. The universe does not run on arrays. It runs on angles, primes, coordinates, and modulo arithmetic. If you cannot calculate the greatest common divisor or find the convex hull of a constellation, you cannot understand the cosmos."*
>
> *He handed Alex a compass and a protractor. "Let us calculate the universe."*

---

## 📖 Chapter 0: The Rules of the Cosmos (Modulo Arithmetic)

*"In the real world, numbers are infinite,"* Archimedes said. "But in a computer, an `int` is only 32 bits. If you multiply two massive numbers, they overflow into negative garbage. To prevent this, the universe asks us to calculate everything modulo `10^9 + 7` (a large prime)."

### 🧠 The Modulo Mental Model
The modulo operator (`%`) is the "remainder" operator. But it has magical properties that allow us to chain calculations:
1. `(a + b) % m = ((a % m) + (b % m)) % m`
2. `(a * b) % m = ((a % m) * (b % m)) % m`
3. ⚠️ **BUT Division is NOT like this!** `(a / b) % m` is NOT `(a%m / b%m) % m`.

> 🏷️ **Pattern Recognition:** "Return the answer modulo `10^9 + 7`". Apply `% MOD` at *every single addition and multiplication* to prevent overflow!

---

## 📖 Chapter 1: The Greatest Chopper (GCD & Euclid's Algorithm)

*"I have a rope of length 36 and a rope of length 24. I want to cut them into equal pieces with no leftover. What is the longest piece I can cut?"*

Alex thought: "I can just loop from 24 down to 1 and find the first number that divides both."
*"Too slow!"* Archimedes barked. "Use **Euclid’s Algorithm**. The greatest common divisor of `a` and `b` is the same as the GCD of `b` and `a % b`."

```java
long gcd(long a, long b) {
	if (b == 0) return a;
	return gcd(b, a % b);
}

// And LCM (Least Common Multiple) is just GCD in disguise!
long lcm(long a, long b) {
	return (a / gcd(a, b)) * b; // Divide first to prevent overflow!
}
```
> 🧠 **The Magic:** `gcd(36, 24)` -> `gcd(24, 12)` -> `gcd(12, 0)` -> returns `12`. It takes only 2 steps instead of 24! This is `O(log N)`.

---

## 📖 Chapter 2: The Prime Calendar (Sieve of Eratosthenes)

*"Count how many prime numbers exist below 1,000,000."*

Alex thought of checking every number one by one. `O(N * sqrt(N))`. Too slow for 1 million.
Archimedes handed him a giant calendar. **The Sieve of Eratosthenes.**
1. Assume every number is prime.
2. Start at 2. It is prime. Now, cross out all multiples of 2 (4, 6, 8, 10...).
3. Go to 3. It is prime. Cross out all multiples of 3 (9, 12, 15...).
4. Repeat up to `sqrt(N)`.

```java
int countPrimes(int n) {
	if (n <= 2) return 0;
	boolean[] isPrime = new boolean[n];
	Arrays.fill(isPrime, true);
	isPrime[0] = false; isPrime[1] = false;
	
	for (int i = 2; i * i < n; i++) {
		if (isPrime[i]) {
			// Cross out multiples! Start at i*i because smaller multiples were already crossed.
			for (int j = i * i; j < n; j += i) {
				isPrime[j] = false;
			}
		}
	}
	
	int count = 0;
	for (boolean p : isPrime) if (p) count++;
	return count;
}
```
> 🏷️ **Pattern Recognition:** "Count primes", "Find primes up to N", "Prime factorization of many numbers" → **Sieve of Eratosthenes**.

---

## 📖 Chapter 3: The Fast Multiplier (Binary Exponentiation)

*"Calculate $3^{1000000} \pmod{10^9+7}$."*

Alex panicked. "If I loop a million times multiplying 3, it will take too long!"
*"Use the Binary Exponentiation spell!"* Archimedes yelled. "If you need $3^{16}$, you don't multiply 3 sixteen times. You calculate $3^2$, then square it to get $3^4$, square it to get $3^8$, square it to get $3^{16}$! Only 4 steps!"

```java
long fastPower(long base, long exp, long MOD) {
	long result = 1;
	base = base % MOD;
	
	while (exp > 0) {
		// If the current bit of exp is 1, multiply the result by base
		if ((exp & 1) == 1) {
			result = (result * base) % MOD;
		}
		// Square the base for the next bit
		base = (base * base) % MOD;
		// Shift exp right (divide by 2)
		exp >>= 1;
	}
	return result;
}
```
> 🧠 **Time Complexity:** `O(log N)`. This is the exact same logic as the Binary Search on Answer Space and Bit Manipulation chapters. You are just squaring instead of adding!

---

## 📖 Chapter 4: The Grid of Manhattan (Coordinate Geometry Basics)

Archimedes pulled down a star map. Two stars were at `(x1, y1)` and `(x2, y2)`.
*"What is the distance between them?"*

### 1. Euclidean Distance (The Straight Line)
`sqrt((x2 - x1)^2 + (y2 - y1)^2)`
⚠️ **Master's Warning:** Floating-point numbers (`double`) are imprecise. `0.1 + 0.2 != 0.3` in a computer. **NEVER compare two doubles with `==`**. Always use `Math.abs(a - b) < 0.00001`.

### 2. Manhattan Distance (The Taxi Cab)
`abs(x2 - x1) + abs(y2 - y1)`
Used when you can only move horizontally and vertically (like in a grid or city blocks). No floating-point issues!

### 🧮 Checking Collinearity (Do 3 points form a straight line?)
Do NOT calculate the slope (division by zero if vertical!). Use the **Area of Triangle (Cross Product)** trick.
If the area of the triangle formed by points A, B, C is 0, they are collinear.
`Area = x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)`

---

## 📖 Chapter 5: The Magic Compass (Vector Cross Product)

This is the most powerful concept in computational geometry.
Given three points A, B, C. You are standing at A, looking at B. Where is C?
To find out, we calculate the **Cross Product** of vectors AB and AC.

`cross = (B.x - A.x) * (C.y - A.y) - (B.y - A.y) * (C.x - A.x)`

*   If `cross > 0`: C is to the **LEFT** of the line AB (Counter-Clockwise turn).
*   If `cross < 0`: C is to the **RIGHT** of the line AB (Clockwise turn).
*   If `cross == 0`: C is exactly **ON** the line AB (Collinear).

```java
// Returns positive for left turn, negative for right, 0 for straight
long crossProduct(long ax, long ay, long bx, long by, long cx, long cy) {
	return (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
}
```
> 🏷️ **Pattern Recognition:** "Do these line segments intersect?", "Is this polygon convex?", "Convex Hull" → **Vector Cross Product**. It avoids division and floating-point errors entirely!

---

## 📖 Chapter 6: The Celestial Shield (Convex Hull)

*"An asteroid field is approaching,"* Archimedes warned. "We must build a forcefield shield around the stars. But we have limited energy, so the shield must be the absolute smallest perimeter covering all stars. This is the **Convex Hull**."*

The most famous algorithm for this is **Graham Scan** (or Andrew's Monotone Chain).
1. **Sort the points** by X-coordinate, then Y-coordinate.
2. **Build the Lower Hull:** Iterate left to right. Use a stack. If adding a new point makes a "Right Turn" (Clockwise), the middle point is inside the hull. Pop it!
3. **Build the Upper Hull:** Iterate right to left. Same logic.

```java
// Andrew's Monotone Chain for Convex Hull
List<int[]> convexHull(int[][] points) {
	Arrays.sort(points, (a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);
	int n = points.length;
	List<int[]> hull = new ArrayList<>();
	
	// Build lower hull
	for (int i = 0; i < n; i++) {
		// While hull has at least 2 points, and we make a non-left turn (cross <= 0)
		while (hull.size() >= 2 && cross(hull.get(hull.size()-2), hull.get(hull.size()-1), points[i]) <= 0) {
			hull.remove(hull.size() - 1); // Pop the middle point!
		}
		hull.add(points[i]);
	}
	
	// Build upper hull
	int lowerSize = hull.size() + 1; // +1 for the last point of lower hull
	for (int i = n - 2; i >= 0; i--) {
		while (hull.size() >= lowerSize && cross(hull.get(hull.size()-2), hull.get(hull.size()-1), points[i]) <= 0) {
			hull.remove(hull.size() - 1);
		}
		hull.add(points[i]);
	}
	
	hull.remove(hull.size() - 1); // Remove duplicate start point
	return hull;
}

long cross(int[] a, int[] b, int[] c) {
	return (long) (b[0] - a[0]) * (c[1] - a[1]) - (long) (b[1] - a[1]) * (c[0] - a[0]);
}
```
> 🧠 **Mental Model:** The stack maintains a strictly "Left Turning" path. The moment you turn right, you are dipping inward, so we chop off that vertex. `O(N log N)` due to sorting.

---

## 📖 Final Chapter: The Astronomer’s Blueprint

Archimedes closed the observatory roof. The stars dimmed, but Alex's mind was alight with numbers.

### 🗺️ The Grand Math & Geometry Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Modulo Arithmetic** | "Return answer % 10^9+7" | Apply `% MOD` at every step of addition/multiplication. |
| **GCD / LCM** | "Divide equally", "Smallest multiple" | **Euclid's Algorithm** `gcd(b, a % b)`. |
| **Find Primes < N** | "Count primes up to N" | **Sieve of Eratosthenes**. Cross out multiples of `i` starting at `i*i`. |
| **Huge Powers** | "Calculate $A^B$" | **Binary Exponentiation**. Square the base, shift the exponent. `O(log N)`. |
| **Distance in Grid** | "Taxi distance", "Shortest path in matrix" | **Manhattan Distance** `abs(x1-x2) + abs(y1-y2)`. |
| **Line Intersections / Polygon** | "Do lines cross?", "Convex Hull" | **Vector Cross Product**. Left/Right turn logic. |

### 🧠 The Mathematician's Final Checklist

1. **Am I overflowing?** (If multiplying two `int`s, cast to `long`! `long result = (long) a * b;`)
2. **Am I using `double`?** (If yes, NEVER use `==`. Use `Math.abs(a - b) < 1e-5`).
3. **Can I avoid division?** (Division creates floats. Use Cross Product or GCD to compare ratios like `a/b == c/d` by doing `a*d == c*b`).
4. **Did I sort the points?** (For Geometry algorithms like Convex Hull, sorting by X then Y is always step 1).
5. **Is my loop `i * i < N`?** (When finding prime factors or crossing out multiples, only loop up to `sqrt(N)`).

### 🏆 Practice Quests — From Pebble Counter to Star Chart Master

**Level 1 (Apprentice):**
- Roman to Integer, Happy Number, Power of Three, Excel Sheet Column Number.

**Level 2 (Squire):**
- Count Primes (Sieve), Greatest Common Divisor of Strings, Powers of Two (Bitwise).

**Level 3 (Knight):**
- Pow(x, n) (Binary Exponentiation), Plus One, Add Binary, Detect Squares (HashMap + Geometry).

**Level 4 (Champion):**
- Max Points on a Line (Cross Product / GCD for slopes), Erect the Fence (Convex Hull), Continuous Subarray Sum (Modulo + Prefix Sum).

**Level 5 (Grandmaster):**
- Convex Polygon (Cross Product sign check), Minimum Area Rectangle II, Super Palindromes (Math + String generation), Number of Submatrices That Sum to Target.

---

> *Alex looked down at the world of Algorithmia. He had started his journey struggling to climb 100 stairs. Now, he was calculating the trajectories of comets, bending space with modulo arithmetic, and shielding the stars with convex hulls. He was no longer just a coder. He was a Master of the Algorithmic Universe.*

**The Celestial Observatory Heist is Complete.** 🌌
