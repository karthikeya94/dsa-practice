# 🧩 The Bit Manipulation Heist 🧩
### *A Hero's Journey from Confused Coder to Binary Master*

---

> *Alex stood before the Vault of the Ancients. The vault door wasn't locked with a key or a password. It had 32 massive stone tumblers, each glowing with either a blue light (0) or a red light (1).*
>
> *"Binary,"* a voice whispered from the shadows. A rogue engineer named **Maestro Bitwise** stepped out, adjusting his glowing goggles. *"Computers don't understand math or logic, kid. They only understand 1s and 0s. If you want to open this vault, you have to learn to speak their language."*
>
> *"Bit manipulation,"* Alex muttered.*
>
> *"Precisely. It's the closest you can get to the hardware. It's fast, it's dangerous, and if you shift the wrong way, you'll overflow into oblivion. Let's begin."*

---

## 📖 Chapter 0: The Binary Tongue — Basics & The 6 Operators

Maestro snapped his fingers, and a holographic table of operators appeared.

*Note: In Java, an `int` is **32 bits**, and a `long` is **64 bits**. They are stored in **Two's Complement** (which means the leftmost bit is the sign bit: 0 for positive, 1 for negative).*

| Operator | Name | What it does | Example (`a=5` (`0101`), `b=3` (`0011`)) |
|---|---|---|---|
| `&` | AND | 1 only if **both** are 1 | `0101 & 0011` = `0001` (1) |
| `\|` | OR | 1 if **either** is 1 | `0101 \| 0011` = `0111` (7) |
| `^` | XOR | 1 if bits are **different** | `0101 ^ 0011` = `0110` (6) |
| `~` | NOT | **Flips** all bits | `~0101` = `...1010` (-6) |
| `<<` | Left Shift | Shifts bits left, adds 0s. Multiplies by 2! | `0101 << 1` = `1010` (10) |
| `>>` | Right Shift | Shifts bits right, fills with sign bit. Divides by 2! | `0101 >> 1` = `0010` (2) |
| `>>>`| Unsigned Right | Shifts right, **always** fills with 0. | `-8 >>> 1` = huge positive number |

### ⚠️ Maestro's Warning: XOR is the Magic Spell
XOR (`^`) is the most powerful operator in bit manipulation. Memorize its 3 sacred rules:
1. `a ^ a = 0` (XORing a number with itself destroys it)
2. `a ^ 0 = a` (XORing with zero does nothing)
3. Order doesn't matter: `a ^ b ^ c = c ^ a ^ b`

---

## 📖 Chapter 1: The Basic Lockpicks (Core Tricks)

Before cracking the vault, Alex needed to learn the basic maneuvers. These are the fundamental building blocks of all bit problems.

### 1️⃣ Check if the $i$-th bit is set (1)
*"Is the 2nd bit of `5` a 1?"*
**Trick:** Create a mask with a 1 at the $i$-th position using Left Shift (`1 << i`). Use AND. If result != 0, it's a 1.

```java
boolean isSet(int num, int i) {
	return (num & (1 << i)) != 0;
}
```

### 2️⃣ Set the $i$-th bit (turn it to 1)
**Trick:** Use OR to force a 1 into that position.

```java
int setBit(int num, int i) {
	return num | (1 << i);
}
```

### 3️⃣ Clear the $i$-th bit (turn it to 0)
**Trick:** Create a mask that is all 1s EXCEPT at the $i$-th position. Use AND.
*How to make the mask?* `~(1 << i)`

```java
int clearBit(int num, int i) {
	return num & ~(1 << i);
}
```

### 4️⃣ Toggle the $i$-th bit (flip 0 to 1, or 1 to 0)
**Trick:** Use XOR.

```java
int toggleBit(int num, int i) {
	return num ^ (1 << i);
}
```

---

## 📖 Chapter 2: The Classic Parlor Tricks

Maestro smirked. *"Now let's show off. Every Bit Master needs to know these party tricks."*

### 🪙 1. Check if a number is Even or Odd
*"Forget modulo (`%`), kid. Modulo is for amateurs."*
**Trick:** The last bit of any number is 1 if it's odd, 0 if it's even.

```java
boolean isOdd(int n) {
	return (n & 1) == 1;
}
```

### 🔄 2. Swap Two Numbers Without a Temp Variable
**Trick:** The XOR swap.

```java
void swap(int a, int b) {
	a = a ^ b;
	b = a ^ b; // (a^b) ^ b = a
	a = a ^ b; // (a^b) ^ a = b
	// Now a and b are swapped!
}
```

### 🧮 3. Multiply or Divide by 2
**Trick:** Left shift multiplies, right shift divides.

```java
int multiplyBy2(int n) { return n << 1; }
int divideBy2(int n) { return n >> 1; }
```

---

## 📖 Chapter 3: The Power of Two Mystery

*"The vault's engines only run on powers of two,"* Maestro explained. *"Can you tell me if a number is a power of two instantly?"*

Alex thought about binary.
* 1 is `0001`
* 2 is `0010`
* 4 is `0100`
* 8 is `1000`

*"They all have exactly ONE bit set to 1!"* Alex exclaimed.

*"Brilliant. Now, what happens if you subtract 1 from a power of two?"*
* 4 (`0100`) - 1 = 3 (`0011`)

*"The single 1 turns to 0, and all the 0s to its right turn to 1s! So, `n & (n - 1)` will ALWAYS be 0 for a power of two!"*

```java
boolean isPowerOfTwo(int n) {
	// Must be > 0 to handle n=0, because 0 & -1 is 0!
	return n > 0 && (n & (n - 1)) == 0;
}
```

---

## 📖 Chapter 4: Brian Kernighan’s Masterpiece (Counting Set Bits)

*"How many 1s are in the number 29?"* Maestro asked.

*"I can loop through all 32 bits and check each one,"* Alex offered.

*"Too slow. Let me teach you **Brian Kernighan's Algorithm**. It only runs as many times as there are 1s."*

**The Magic of `n & (n - 1)`**: This operation **always clears the lowest set bit (the rightmost 1)**.
*Example:* `29` is `11101`.
* `29 & 28` -> `11101 & 11100` = `11100` (28). (The lowest 1 is gone!)
* `28 & 27` -> `11100 & 11011` = `11000` (24). (Next lowest 1 is gone!)

```java
int countSetBits(int n) {
	int count = 0;
	while (n != 0) {
		n = n & (n - 1); // Destroys the lowest set bit
		count++;
	}
	return count;
}
```

---

## 📖 Chapter 5: The Lone Wolf — Single Number

*"The vault's scanner detected an anomaly,"* Maestro said, pulling up a screen. *"An array of numbers where every number appears exactly TWICE, except for ONE number which appears ONCE. Find the lone wolf."*

Alex remembered the sacred XOR rules: `a ^ a = 0`.
*"If I XOR the entire array, the duplicates will destroy each other, leaving only the lone wolf!"*

```java
int singleNumber(int[] nums) {
	int ans = 0;
	for (int num : nums) {
		ans = ans ^ num;
	}
	return ans;
}
```

### 🐺 The Two Lone Wolves (Advanced)
*"What if TWO numbers appear once, and the rest appear twice?"*

1. XOR everything. The result is `xor2 = A ^ B` (the two lone wolves XORed).
2. Since A and B are different, `xor2` has at least one bit set to 1. Find the **rightmost set bit** using `xor2 & -xor2` (this isolates the lowest 1).
3. This bit is different in A and B! Use this bit to partition the array into two groups, and XOR them separately.

```java
int[] singleNumberTwo(int[] nums) {
	long xor2 = 0;
	for (int n : nums) xor2 ^= n;
	
	// Get rightmost set bit (difference bit)
	long diffBit = xor2 & -xor2; 
	
	int a = 0, b = 0;
	for (int n : nums) {
		if ((n & diffBit) != 0) a ^= n;
		else b ^= n;
	}
	return new int[]{a, b};
}
```

---

## 📖 Chapter 6: The Missing Twin

*"I have an array of numbers from 0 to N. One is missing. Find it."*

Alex could have summed them up and subtracted from the expected sum. But Maestro wanted bits.
*"XOR the array indices with the array values. The missing one will be left behind!"*

```java
int missingNumber(int[] nums) {
	int n = nums.length;
	int ans = n;
	for (int i = 0; i < n; i++) {
		ans ^= i ^ nums[i];
	}
	return ans;
}
```

---

## 📖 Chapter 7: The Masked Ball — Subsets via Bitmasking

*"Generate all subsets of an array."*
Alex remembered this from his DP Quest! If an array has `n` elements, there are `2^n` subsets. Each subset can be represented by an integer from `0` to `(1 << n) - 1`, where a 1 bit means "include" and a 0 bit means "exclude".

```java
List<List<Integer>> subsets(int[] nums) {
	int n = nums.length;
	List<List<Integer>> result = new ArrayList<>();
	
	int totalMasks = 1 << n; // 2^n
	for (int mask = 0; mask < totalMasks; mask++) {
		List<Integer> subset = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			// If i-th bit in mask is 1, include nums[i]
			if ((mask & (1 << i)) != 0) {
				subset.add(nums[i]);
			}
		}
		result.add(subset);
	}
	return result;
}
```

---

## 📖 Chapter 8: Isolating the Weakness (Rightmost Bit)

*"How do you isolate just the rightmost 1-bit?"*
**Trick:** `n & -n` (Two's Complement magic).
*How it works:* `-n` is the same as `~n + 1`. When you AND `n` with `-n`, all bits cancel out except the lowest 1.
*Example:* `n = 12` (`1100`). `-n` is `-12` (in binary, `...10100`). `12 & -12` = `0100` (4).

```java
int getLowestSetBit(int n) {
	return n & -n;
}
```
*(This is the core logic behind **Fenwick Trees / Binary Indexed Trees**!)*

---

## 📖 Chapter 9: Reversing the Tumblers (Reverse Bits)

The vault required a 32-bit integer to be completely reversed.
*Example:* `1100` becomes `0011`.

Maestro showed Alex the **Divide and Conquer Bit Swap**:
1. Swap odd and even bits.
2. Swap consecutive pairs.
3. Swap nibbles (4 bits).
4. Swap bytes.
5. Swap 2-byte halves.

```java
int reverseBits(int n) {
	// Swap odd and even bits
	n = ((n & 0xAAAAAAAA) >>> 1) | ((n & 0x55555555) << 1);
	// Swap consecutive pairs
	n = ((n & 0xCCCCCCCC) >>> 2) | ((n & 0x33333333) << 2);
	// Swap nibbles (4 bits)
	n = ((n & 0xF0F0F0F0) >>> 4) | ((n & 0x0F0F0F0F) << 4);
	// Swap bytes
	n = ((n & 0xFF00FF00) >>> 8) | ((n & 0x00FF00FF) << 8);
	// Swap 2-byte halves
	return (n >>> 16) | (n << 16);
}
```
> *Note: Use `>>>` (unsigned shift) to avoid bringing the sign bit down!*

---

## 📖 Chapter 10: The Gray Code Enigma

*"The ancients used Gray Code,"* Maestro said, *"where two successive numbers differ by only ONE bit."*

The magic formula to convert a standard number `n` to Gray Code is:
`gray = n ^ (n >> 1)`

```java
int toGrayCode(int n) {
	return n ^ (n >> 1);
}
```

---

## 📖 Chapter 11: The Repeating Curse (Single Number II)

*"Every number appears THREE times, except ONE number appears ONCE. Find it."*

This is the ultimate bit manipulation test. Alex needed to count the number of 1s at every bit position modulo 3.
Instead of counting in arrays, Maestro taught him to use **Two Bitmasks (`ones` and `twos`)** to simulate a state machine.

* If a bit appears for the 1st time, set it in `ones`.
* If it appears for the 2nd time, move it to `twos` and clear from `ones`.
* If it appears for the 3rd time, clear it from both!

```java
int singleNumberThree(int[] nums) {
	int ones = 0, twos = 0;
	for (int num : nums) {
		// Add to ones only if not in twos
		ones = (ones ^ num) & ~twos;
		// Add to twos only if not in ones (the NEW ones)
		twos = (twos ^ num) & ~ones;
	}
	return ones; // The number appearing once remains in 'ones'
}
```

---

## 📖 Chapter 12: The Range Bitwise AND

*"Find the bitwise AND of all numbers in the range [m, n]."*

Alex tried a loop, but `n` could be 2 billion. Too slow!
*"Think, Alex! As you go from m to n, the rightmost bits will flip between 0 and 1 constantly. The only bits that survive the AND operation are the **common prefix** of m and n!"*

**Trick:** Shift both `m` and `n` to the right until they are equal. Then shift them back left to restore the common prefix.

```java
int rangeBitwiseAnd(int m, int n) {
	int shift = 0;
	while (m != n) {
		m >>= 1;
		n >>= 1;
		shift++;
	}
	return m << shift; // Common prefix restored with zeros
}
```

---

## 📖 Final Chapter: The Master’s Schematic — Bit Manipulation Cheat Sheet

The vault door clicked open, red and blue lights swirling harmoniously. Maestro Bitwise handed Alex a worn leather notebook.

*"Memorize this schematic. It contains every bit trick known to the ancients."*

### 🗺️ The Grand Bit Trick Table

| Goal | Trick / Code | Notes |
|---|---|---|
| **Check $i$-th bit** | `(n & (1 << i)) != 0` | |
| **Set $i$-th bit** | `n \| (1 << i)` | |
| **Clear $i$-th bit** | `n & ~(1 << i)` | |
| **Toggle $i$-th bit** | `n ^ (1 << i)` | |
| **Isolate lowest set 1** | `n & -n` | Two's complement magic! |
| **Clear lowest set 1** | `n & (n - 1)` | Brian Kernighan's algorithm |
| **Is Power of Two?** | `n > 0 && (n & (n - 1)) == 0` | |
| **Is Even?** | `(n & 1) == 0` | Faster than `% 2` |
| **Swap variables** | `a ^= b; b ^= a; a ^= b;` | No temp variable! |
| **Count set bits** | Loop with `n = n & (n - 1)` | O(number of 1s) |
| **Missing Number** | `ans ^= i ^ nums[i]` | |
| **Find two unique** | `xor2 & -xor2` | Find partition bit |
| **Gray Code** | `n ^ (n >> 1)` | |
| **Remove last bit** | `n >> 1` | Integer division by 2 |

### 🧠 The Bit-Master's Final Checklist

Before writing any bit manipulation code, ask yourself:
1. **Am I dealing with powers of 2?** (Use `1 << i` instead of `Math.pow(2, i)`)
2. **Am I checking states/combinations?** (Use Bitmasks, 1 means ON, 0 means OFF)
3. **Is there a pairing/duplicate problem?** (Use XOR! `a ^ a = 0`)
4. **Am I overflowing?** (Java `int` is 32 bits. `1 << 31` is negative! Use `1L << 31` for longs)
5. **Am I using the right right-shift?** (`>>` keeps the sign, `>>>` fills with 0s)

### 🏆 Practice Quests — From Padawan to Bit-Master

**Level 1 (Apprentice):**
- Number of 1 Bits, Reverse Bits, Power of Two, Missing Number

**Level 2 (Squire):**
- Single Number, Subsets, Sum of Two Integers (without + or -), Find the Difference

**Level 3 (Knight):**
- Single Number II (appears 3 times), Single Number III (two unique), Binary Watch, Gray Code

**Level 4 (Champion):**
- Counting Bits (O(n) DP approach), Range Bitwise AND, Maximum XOR of Two Numbers in an Array

**Level 5 (Grandmaster):**
- Minimum XOR Sum of Two Arrays (Bitmask DP + Bits), Find the Shortest Superstring (Bitmask DP)

---

> *Alex stepped into the Vault of the Ancients. The 32 tumblers aligned perfectly, glowing a soft blue. He had done it. He hadn't just used numbers; he had touched the very fabric of machine logic.*
>
> *Maestro Bitwise tipped his hat. "You've got the bits now, kid. Remember, in the end, everything in a computer is just 1s and 0s. Master the bits, and you master the machine."*
>
> *Alex smiled, ready for whatever algorithm challenge came next. Because he knew, down at the very bottom, he could always just XOR his way out.*

**The Binary Journey Concludes.** 🔢
