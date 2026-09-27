# Bit Manipulation DSA — Ultimate Quick Revision Guide (Java)

> **How to use this guide:** Each topic follows the same pattern: *When to use → Nuance/Gotcha → Steps → Code.*  
> Before any interview, scan the **Master Cheat Sheet** at the bottom first, then drill whichever section you are shaky on.  
> Difficulty progresses from 🟢 Easy → 🟡 Medium → 🔴 Hard within each group.

---

## 🧠 The Mental Model — Think in Bits

```
Number:  13  →  Binary: 1101
                        ^^^^
                        |||└── bit 0  (2⁰ = 1)
                        ||└─── bit 1  (2¹ = 2)
                        |└──── bit 2  (2² = 4)
                        └───── bit 3  (2³ = 8)

Mask for bit i = (1 << i)
  i=0 → 0001
  i=1 → 0010
  i=2 → 0100
  i=3 → 1000
```

**Java integer size:** `int` = 32 bits, `long` = 64 bits. Bit 31 (or 63) = sign bit.

---

## 📦 Group 1: Bitwise Operators Reference

| Operator | Name | Rule | Example (4-bit) |
|---|---|---|---|
| `&` | AND | 1 only if BOTH are 1 | `1010 & 1100 = 1000` |
| `\|` | OR | 1 if EITHER is 1 | `1010 \| 0101 = 1111` |
| `^` | XOR | 1 if DIFFERENT | `1010 ^ 1100 = 0110` |
| `~` | NOT | Flips all bits | `~1010 = 0101` (sign changes too) |
| `<<` | Left Shift | Shift left, fill 0s on right | `0001 << 2 = 0100` (×4) |
| `>>` | Signed Right Shift | Shift right, fill with sign bit | `1000 >> 1 = 1100` (÷2, preserves sign) |
| `>>>` | Unsigned Right Shift | Shift right, fill with 0s always | `1000 >>> 1 = 0100` |

**Key identities to memorize:**
```
n & 0  = 0          n | 0  = n          n ^ 0  = n
n & n  = n          n | n  = n          n ^ n  = 0
n & ~0 = n          n | ~0 = -1         n ^ ~0 = ~n
~n     = -(n+1)     n << i = n × 2^i    n >> i = n / 2^i
```

**⚠️ Nuance:** `~n = -(n+1)` because Java uses **two's complement**. So `~0 = -1`, `~1 = -2`, `~(-1) = 0`.

```java
// Shift operators on byte/short: Java auto-promotes them to int first!
byte b = (byte) 0xFF;
int result = b >> 1;  // NOT 0x7F — b becomes -1 (int), result = -1 (all 1s)
int correct = (b & 0xFF) >> 1;  // 0x7F = 127 ✓

// Shifts by ≥ 32 on int wrap around:
int n = 1 << 32;  // ← Same as 1 << 0 = 1 (NOT 0!)
long fix = 1L << 32;  // 4294967296 ✓
```

---

## 🔧 Group 2: Core Bit Operations (Single Bit)

🟢 **These are the atomic building blocks — know them cold.**

```java
// ─── GET the i-th bit ─────────────────────────────────────────────────────
// Is bit i set to 1?
boolean getBit(int n, int i) {
    return ((n >> i) & 1) == 1;
    // Alt: (n & (1 << i)) != 0
}
// Example: n=10 (1010), i=1 → (1010 >> 1)=0101 → 0101 & 1 = 1 → true ✓

// ─── SET the i-th bit ─────────────────────────────────────────────────────
// Force bit i to 1 (leave others unchanged)
int setBit(int n, int i) {
    return n | (1 << i);
    // 1010 | 0100 (i=2) = 1110
}

// ─── CLEAR the i-th bit ───────────────────────────────────────────────────
// Force bit i to 0 (leave others unchanged)
int clearBit(int n, int i) {
    return n & ~(1 << i);
    // ~(0100) = ...11011, 1010 & ...11011 = 1010 (bit 2 was 0 already)
    // n=14 (1110), i=2: ~(0100)=...11011, 1110 & 11011 = 1010 ✓
}

// ─── TOGGLE the i-th bit ──────────────────────────────────────────────────
// Flip bit i (0→1, 1→0)
int toggleBit(int n, int i) {
    return n ^ (1 << i);
    // 1010 ^ 0100 (i=2) = 1110  (bit was 0, now 1)
    // 1110 ^ 0100 (i=2) = 1010  (bit was 1, now 0)
}

// ─── UPDATE the i-th bit to a specific value ──────────────────────────────
int updateBit(int n, int i, int val) { // val must be 0 or 1
    return (n & ~(1 << i)) | (val << i);
    // 1. Clear bit i: n & ~(1 << i)
    // 2. OR with val shifted to position i
}
```

---

## ⚡ Group 3: Bit Shift Tricks

🟢 **Shift = multiply/divide by 2. Know all three shift types.**

```java
// ─── Left Shift: × 2^i ────────────────────────────────────────────────────
int n = 5;       // 0000 0101
n << 1;          // 0000 1010  = 10   (5 × 2)
n << 3;          // 0010 1000  = 40   (5 × 8)

// ─── Signed Right Shift: ÷ 2^i (rounds toward -∞) ────────────────────────
int a = -20;     // 1111...101100
a >> 2;          // 1111...111011  = -5    (-20 / 4 = -5) ✓
a >> 1;          // 1111...110110  = -10   (-20 / 2 = -10) ✓

// ─── Unsigned Right Shift: always fills 0 on left ────────────────────────
int b = -1;      // 1111...1111
b >>> 1;         // 0111...1111  = 2147483647  (Integer.MAX_VALUE)
// Use case: finding mid index without overflow:
int mid = (lo + hi) >>> 1;  // Safe! Even if lo+hi overflows int.
// vs (lo + hi) / 2 which can overflow

// ─── Generate mask for bit i ──────────────────────────────────────────────
int mask = 1 << i;                  // Single-bit mask at position i
int maskRange = ((1 << k) - 1);     // k low bits all set to 1
// k=3: (1<<3)-1 = 1000-1 = 0111

// ─── Clear last i bits ────────────────────────────────────────────────────
int clearLast(int n, int i) {
    return n & (~0 << i);           // ~0 = all 1s; shift left pushes i zeros on right
    // n=15 (1111), i=2: ~0<<2 = ...11100, 1111 & 11100 = 1100 = 12
}

// ─── Get last i bits ──────────────────────────────────────────────────────
int getLast(int n, int i) {
    return n & ((1 << i) - 1);
    // n=13 (1101), i=3: (1<<3)-1=0111, 1101 & 0111 = 0101 = 5
}
```

---

## 🟰 Group 4: Classic Single-Number Tricks (XOR)

🟢 **XOR properties are the key — internalize them.**

```
A ^ A = 0     (same number cancels)
A ^ 0 = A     (identity)
A ^ B ^ A = B (pair elimination)
XOR is commutative and associative — order doesn't matter.
```

---

### 1. 🟢 Check Odd / Even
**Use:** Faster than `n % 2` — single CPU instruction.
```java
boolean isOdd(int n)  { return (n & 1) == 1; }
boolean isEven(int n) { return (n & 1) == 0; }
// 10 (1010): last bit 0 → even ✓
// 7  (0111): last bit 1 → odd ✓
```

---

### 2. 🟢 Check Power of 2
**Use:** Frequent in heap, tree size, and interval problems.  
**⚠️ Nuance:** 0 is NOT a power of 2 — always guard with `n > 0`.

**Key insight:** A power of 2 has exactly one bit set: `8 = 1000`. Subtracting 1 flips all lower bits: `7 = 0111`. Their AND is always 0.

```java
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
    // 8 (1000) & 7 (0111) = 0 → true ✓
    // 6 (0110) & 5 (0101) = 4 → false ✓
    // 0 → fails n > 0 guard ✓
}

// Generalization: check if n is a power of k using same idea with logs
// But for power-of-2, the bit trick is always preferred.
```

---

### 3. 🟢 Single Number (One unique, rest appear twice)
**Use:** Classic interview trick. O(n) time, O(1) space.  
**⚠️ Nuance:** Only works when all duplicates appear **exactly twice**. XOR-ing all numbers cancels pairs.

```java
int singleNumber(int[] nums) {
    int result = 0;
    for (int n : nums) result ^= n;
    return result;
    // [4,1,2,1,2]: 4^1^2^1^2 = (1^1)^(2^2)^4 = 0^0^4 = 4 ✓
}
```

---

### 4. 🟡 Two Single Numbers (Two unique, rest appear twice)
**Use:** Extension of single number. Requires lowest-set-bit isolation trick.

**Steps:**
1. XOR all → get `xorResult = X ^ Y`.
2. Find lowest set bit of `xorResult` → this bit differs between X and Y.
3. Partition array into two groups by that bit; XOR each group → X and Y.

```java
int[] twoSingleNumbers(int[] nums) {
    int xorAll = 0;
    for (int n : nums) xorAll ^= n;       // xorAll = X ^ Y

    int diff = xorAll & (-xorAll);         // Isolate lowest set bit
    // -n = ~n + 1 in two's complement, so n & -n = lowest set bit

    int x = 0, y = 0;
    for (int n : nums) {
        if ((n & diff) != 0) x ^= n;      // Group A: has that bit
        else                 y ^= n;      // Group B: doesn't
    }
    return new int[]{x, y};
    // [1,2,1,3,2,5]: xorAll=3^5=6(110), diff=6&-6=2(010)
    // Group A (bit1 set): 2,3,2 → 0^2^3^2=3 ✓
    // Group B (bit1 clear): 1,1,5 → 0^1^1^5=5 ✓
}
```

---

### 5. 🟡 Single Number III (One unique, rest appear three times)
**Use:** XOR no longer works (3 copies don't cancel). Use 32-bit counting trick.

**Steps:** For each bit position, sum all bits across all numbers. If `sum % 3 != 0`, the unique number has that bit set.

```java
int singleNumberIII(int[] nums) {
    int result = 0;
    for (int i = 0; i < 32; i++) {
        int sum = 0;
        for (int n : nums) sum += (n >> i) & 1;
        if (sum % 3 != 0) result |= (1 << i);
    }
    return result;
}
```

---

## 🧮 Group 5: Counting & Inspecting Bits

---

### 6. 🟢 Count Set Bits (Hamming Weight / popcount)

**Approach A — Brian Kernighan's Algorithm:** `n & (n-1)` removes the lowest set bit each time.  
**⚠️ Nuance:** Runs in O(# set bits), not O(32). Fastest when bits are sparse.

```java
int countBits(int n) {
    int count = 0;
    while (n != 0) {
        n = n & (n - 1);  // Remove lowest set bit
        count++;
    }
    return count;
    // n=12 (1100): 1100→1000→0000, count=2 ✓
}

// Approach B — Java built-in (always prefer in interviews if allowed)
Integer.bitCount(n);   // O(1), uses hardware popcount

// Approach C — Classic loop for teaching
int countLoop(int n) {
    int count = 0;
    while (n != 0) {
        count += n & 1;  // Add last bit
        n >>>= 1;        // Unsigned shift right (handles negatives correctly)
    }
    return count;
}
```

---

### 7. 🟢 Hamming Distance (Count differing bits)
**Use:** Compare two numbers bit by bit.

```java
int hammingDistance(int x, int y) {
    return Integer.bitCount(x ^ y);  // XOR isolates differing bits; count them
    // x=1 (001), y=4 (100): XOR=101, bitCount=2 ✓
}
```

---

### 8. 🟡 Count Bits for 0 to N (DP)
**Use:** Produce `result[i]` = popcount of `i`, for all `i` in `[0, n]`. O(n) — no loops per number.

**Key insight:** `bits[i] = bits[i >> 1] + (i & 1)`.  
`i >> 1` is `i` with last bit removed (we already know its count); just add the last bit.

```java
int[] countBits(int n) {
    int[] dp = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        dp[i] = dp[i >> 1] + (i & 1);
    }
    return dp;
    // i=6 (110): dp[3]+0 = dp[1]+1+0 = 2 ✓
    // i=7 (111): dp[3]+1 = 2+1 = 3 ✓
}
```

---

### 9. 🟡 Lowest Set Bit (LSB) — Isolate and Remove

```java
// Isolate the lowest set bit
int lsb = n & (-n);       // n & two's complement of n
// n=12 (1100): -12 = ...0100, 1100 & ...0100 = 0100 ✓

// Remove the lowest set bit
int removed = n & (n - 1);
// n=12 (1100): n-1=1011, 1100 & 1011 = 1000 ✓

// Position of lowest set bit (0-indexed)
int pos = Integer.numberOfTrailingZeros(n);  // O(1)
// n=12 (1100): 2 trailing zeros → pos=2 ✓
```

---

### 10. 🟡 Highest Set Bit (MSB) — Position

```java
int highestSetBit(int n) {
    return 31 - Integer.numberOfLeadingZeros(n);  // 0-indexed position
    // n=12 (1100): 28 leading zeros → pos = 31-28 = 3 ✓
}

// Alternatively — log2 floor:
int pos2 = (int)(Math.log(n) / Math.log(2));

// Round up to next power of 2:
int nextPow2(int n) {
    if (n <= 0) return 1;
    n--;
    n |= n >> 1;
    n |= n >> 2;
    n |= n >> 4;
    n |= n >> 8;
    n |= n >> 16;
    return n + 1;
}
```

---

## 🔀 Group 6: Bit Swapping & Reversal

---

### 11. 🟢 Swap Two Variables (No Temp)
**⚠️ Nuance:** Only works when `a` and `b` are different memory locations. `swap(a, a)` breaks it.

```java
void swapXOR(int[] arr, int i, int j) {
    if (i == j) return;     // ← Must guard this!
    arr[i] ^= arr[j];
    arr[j] ^= arr[i];
    arr[i] ^= arr[j];
}
```

---

### 12. 🟡 Reverse Bits of an Integer
**Use:** Common in problems involving bit mirroring, gray codes, or checksums.

```java
int reverseBits(int n) {
    int result = 0;
    for (int i = 0; i < 32; i++) {
        result = (result << 1) | (n & 1);  // Append last bit of n to result
        n >>>= 1;                          // Unsigned shift (handles negatives)
    }
    return result;
}

// If called many times — cache with HashMap
Map<Integer, Integer> cache = new HashMap<>();
int reverseBitsCached(int n) {
    if (cache.containsKey(n)) return cache.get(n);
    int res = reverseBits(n);
    cache.put(n, res);
    return res;
}
```

---

### 13. 🟡 Reverse Bits via Masking (Divide and Conquer)
**Time:** O(1) — 5 parallel operations instead of 32 serial ones.

```java
int reverseBitsO1(int n) {
    n = ((n & 0xFFFF0000) >>> 16) | ((n & 0x0000FFFF) << 16); // Swap 16-bit halves
    n = ((n & 0xFF00FF00) >>> 8)  | ((n & 0x00FF00FF) << 8);  // Swap 8-bit chunks
    n = ((n & 0xF0F0F0F0) >>> 4)  | ((n & 0x0F0F0F0F) << 4);  // Swap 4-bit nibbles
    n = ((n & 0xCCCCCCCC) >>> 2)  | ((n & 0x33333333) << 2);  // Swap 2-bit pairs
    n = ((n & 0xAAAAAAAA) >>> 1)  | ((n & 0x55555555) << 1);  // Swap individual bits
    return n;
}
```

---

## 📐 Group 7: Subset Enumeration & Bitmask DP

🟡🔴 **Bitmask DP is a medium-to-hard pattern. State = which elements are included.**

---

### 14. 🟡 Enumerate All Subsets of a Set
**Use:** When you need to process every combination of a small set (n ≤ 20).  
**⚠️ Nuance:** There are 2ⁿ subsets. For n=20, that's ~1M — fine. For n=30, it's ~1B — too slow.

```java
void allSubsets(int[] arr) {
    int n = arr.length;
    for (int mask = 0; mask < (1 << n); mask++) {
        List<Integer> subset = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((mask >> i & 1) == 1) {    // Is element i in this subset?
                subset.add(arr[i]);
            }
        }
        System.out.println(subset);
    }
    // arr=[1,2,3], n=3 → 8 subsets (mask 000 to 111)
}

// Enumerate all subsets of a given mask m (iterate sub-masks):
void subMasks(int m) {
    for (int sub = m; sub > 0; sub = (sub - 1) & m) {
        // process sub
        // (sub-1) & m removes lowest set bit of sub while staying within m
    }
    // Don't forget to also process sub=0 (empty subset) if needed
}
```

---

### 15. 🟡 Sum of All Subset XORs
**Use:** Power set problems involving XOR.

```java
int subsetXORSum(int[] nums) {
    int n = nums.length;
    int total = 0;
    for (int mask = 0; mask < (1 << n); mask++) {
        int xor = 0;
        for (int i = 0; i < n; i++)
            if ((mask >> i & 1) == 1) xor ^= nums[i];
        total += xor;
    }
    return total;
    // Trick shortcut: OR all elements, then result = orAll << (n-1)
}
```

---

### 16. 🔴 Bitmask DP — Travelling Salesman Problem (TSP)
**Use:** Visit all n cities exactly once, return to start, minimize cost.  
**State:** `dp[mask][i]` = min cost to have visited exactly the cities in `mask`, ending at city `i`.  
**Time:** O(2ⁿ × n²) | **Space:** O(2ⁿ × n)  
**⚠️ Nuance:** Only feasible for n ≤ 20. Base case is `dp[1][0] = 0` (visited only city 0, at city 0).

```java
int tsp(int[][] dist) {
    int n = dist.length;
    int FULL = (1 << n) - 1;  // All cities visited
    int[][] dp = new int[1 << n][n];
    for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE / 2);

    dp[1][0] = 0;  // Start at city 0, only city 0 visited (mask = 000...001)

    for (int mask = 1; mask <= FULL; mask++) {
        for (int u = 0; u < n; u++) {
            if ((mask >> u & 1) == 0) continue;           // u not in current mask
            if (dp[mask][u] == Integer.MAX_VALUE / 2) continue;
            for (int v = 0; v < n; v++) {
                if ((mask >> v & 1) == 1) continue;       // v already visited
                int next = mask | (1 << v);
                dp[next][v] = Math.min(dp[next][v], dp[mask][u] + dist[u][v]);
            }
        }
    }

    // Return to start: find min over all ending cities
    int ans = Integer.MAX_VALUE;
    for (int u = 1; u < n; u++) {
        if (dp[FULL][u] < Integer.MAX_VALUE / 2)
            ans = Math.min(ans, dp[FULL][u] + dist[u][0]);
    }
    return ans;
}
```

---

### 17. 🔴 Bitmask DP — Minimum Cost to Cover All States
**Pattern:** Common in "assign tasks to workers" / "match items with constraints" problems.  
**Classic Example:** [Leetcode 1125] Smallest Sufficient Team

```java
// dp[mask] = minimum team to cover skills in mask
int[] smallestSufficientTeam(String[] reqSkills, List<List<String>> people) {
    int n = reqSkills.length;
    Map<String, Integer> index = new HashMap<>();
    for (int i = 0; i < n; i++) index.put(reqSkills[i], i);

    int FULL = (1 << n) - 1;
    long[] dp = new long[1 << n];   // Encode team as bitmask of person indices (using long)
    Arrays.fill(dp, Long.MAX_VALUE);
    dp[0] = 0;

    for (int p = 0; p < people.size(); p++) {
        int personSkill = 0;
        for (String s : people.get(p))
            if (index.containsKey(s)) personSkill |= (1 << index.get(s));

        // Try adding person p to every existing team
        for (int mask = FULL; mask >= 0; mask--) {
            if (dp[mask] == Long.MAX_VALUE) continue;
            int newMask = mask | personSkill;
            if (Long.bitCount(dp[mask | personSkill]) > Long.bitCount(dp[mask]) + 1) {
                dp[newMask] = dp[mask] | (1L << p);
            }
        }
    }

    long encoded = dp[FULL];
    List<Integer> team = new ArrayList<>();
    for (int p = 0; p < people.size(); p++)
        if ((encoded >> p & 1) == 1) team.add(p);
    return team.stream().mapToInt(i -> i).toArray();
}
```

---

## 🎯 Group 8: Classic Interview Problems

---

### 18. 🟢 Missing Number in [0, n]
**Use:** One number missing from 0 to n. XOR approach: no overflow unlike sum method.

```java
int missingNumber(int[] nums) {
    int xor = 0;
    for (int i = 0; i <= nums.length; i++) xor ^= i;   // XOR all expected
    for (int n : nums)                     xor ^= n;   // XOR all actual
    return xor;  // Only the missing number survives (everything else cancels)
}

// Alt — arithmetic (can overflow for large n; use long if needed):
int missingSum(int[] nums) {
    int n = nums.length;
    int expected = n * (n + 1) / 2;
    int actual = 0;
    for (int x : nums) actual += x;
    return expected - actual;
}
```

---

### 19. 🟡 Add Two Numbers Without Arithmetic Operators
**Use:** Classic bit-tricks interview warm-up.

```java
int addBits(int a, int b) {
    while (b != 0) {
        int carry = (a & b) << 1;  // Carry: bits where both are 1, shifted left
        a = a ^ b;                  // Sum without carry
        b = carry;
    }
    return a;
    // a=5 (101), b=3 (011):
    // carry=010<<1=010, a=110, b=010
    // carry=010<<1=100, a=100, b=100
    // carry=100<<1=1000, a=000, b=1000
    // carry=0, a=1000=8 ✓
}
```

---

### 20. 🟡 Find Duplicate in Array [1, n] — No Extra Space
**Use:** Array has n+1 elements, values 1..n, exactly one duplicate.  
XOR approach: XOR all indices 1..n and all values. Duplicate survives.

```java
int findDuplicate(int[] nums) {
    int xor = 0;
    for (int i = 1; i < nums.length; i++) xor ^= i;   // XOR 1..n
    for (int n : nums)                    xor ^= n;   // XOR all values
    return xor;
    // Only the duplicate appears an odd number of times → survives
}
// Note: If the problem allows Floyd's cycle detection, that also works in O(n)/O(1).
```

---

### 21. 🟡 XOR Queries on Array (Range XOR)
**Use:** Prefix XOR to answer range queries in O(1) each.

```java
// Build prefix XOR array
int[] prefixXOR(int[] arr) {
    int[] pre = new int[arr.length + 1];
    for (int i = 0; i < arr.length; i++)
        pre[i + 1] = pre[i] ^ arr[i];
    return pre;
}

// Query XOR of arr[l..r] (0-indexed)
int rangeXOR(int[] pre, int l, int r) {
    return pre[r + 1] ^ pre[l];
    // pre[r+1] ^ pre[l] cancels all elements before l → only [l..r] remain
}
```

---

### 22. 🟡 Power Set (All Unique Subsets)
**Use:** Combine subset enumeration with deduplication for problems like LeetCode 90.

```java
List<List<Integer>> subsetsWithDup(int[] nums) {
    Arrays.sort(nums);  // Sort first to group duplicates
    Set<List<Integer>> result = new HashSet<>();
    int n = nums.length;
    for (int mask = 0; mask < (1 << n); mask++) {
        List<Integer> sub = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if ((mask >> i & 1) == 1) sub.add(nums[i]);
        result.add(sub);
    }
    return new ArrayList<>(result);
}
```

---

### 23. 🟡 Divide Two Integers Without Division
**Use:** Test understanding of bit shifts as arithmetic.  
**Steps:** Keep subtracting `divisor << k` (largest possible k) from dividend, recording bits.

```java
int divide(int dividend, int divisor) {
    if (dividend == Integer.MIN_VALUE && divisor == -1) return Integer.MAX_VALUE;  // Overflow
    int sign = (dividend > 0) == (divisor > 0) ? 1 : -1;
    long dvd = Math.abs((long) dividend);
    long dvs = Math.abs((long) divisor);
    int result = 0;
    while (dvd >= dvs) {
        long temp = dvs, multiple = 1;
        while (dvd >= (temp << 1)) {
            temp <<= 1;
            multiple <<= 1;
        }
        dvd -= temp;
        result += multiple;
    }
    return sign == 1 ? result : -result;
}
```

---

## ⚡ Group 9: Advanced Patterns

---

### 24. 🟡 Fast Power (Binary Exponentiation)
**Use:** Compute `base^exp % mod` in O(log exp). Essential for modular arithmetic in contests.  
**⚠️ Nuance:** Process bits of `exp` from LSB to MSB. If current bit is 1, multiply result by current base power.

```java
long fastPow(long base, long exp, long mod) {
    long result = 1;
    base %= mod;
    while (exp > 0) {
        if ((exp & 1) == 1) result = result * base % mod;  // Current bit is set
        base = base * base % mod;                          // Square the base
        exp >>= 1;                                         // Move to next bit
    }
    return result;
    // base=2, exp=10 (1010): bits processed right-to-left → 2^10=1024 ✓
}
```

---

### 25. 🟡 Bitwise AND of Numbers Range [left, right]
**Use:** LeetCode 201. AND of all numbers in [left, right].  
**⚠️ Nuance:** Keep removing LSBs of `right` until `right <= left`. The remaining bits are the common prefix.

```java
int rangeBitwiseAnd(int left, int right) {
    int shift = 0;
    while (left != right) {
        left >>= 1;
        right >>= 1;
        shift++;
    }
    return left << shift;
    // [5,7]: 101,110,111 → left=right=1 after 2 shifts → 1<<2=4(100) ✓
}
```

---

### 26. 🔴 Gray Code — Binary to Gray and Back

**Use:** Generate sequences where consecutive values differ in exactly 1 bit.  
**⚠️ Nuance:** Gray code of `n` = `n ^ (n >> 1)`. Reverse: peel off bits from MSB.

```java
// Binary → Gray Code
int toGray(int n) {
    return n ^ (n >> 1);
    // 4 (100) → 100^010 = 110 = 6 ✓
}

// Gray Code → Binary
int fromGray(int gray) {
    int n = 0;
    while (gray != 0) {
        n ^= gray;
        gray >>= 1;
    }
    return n;
    // 6 (110): n=110, gray=011; n=101, gray=001; n=100, gray=000 → 4 ✓
}

// Generate all n-bit gray codes in order
List<Integer> grayCodes(int n) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < (1 << n); i++) result.add(i ^ (i >> 1));
    return result;
}
```

---

### 27. 🔴 Fenwick Tree (Binary Indexed Tree — BIT)
**Use:** Point updates + prefix sum queries in O(log n). Uses `i & (-i)` to navigate the tree.  
**⚠️ Nuance:** 1-indexed! `i & (-i)` gives the responsible range length at index `i`.

```java
static class FenwickTree {
    int[] tree;
    int n;

    FenwickTree(int n) {
        this.n = n;
        tree = new int[n + 1];  // 1-indexed
    }

    // Point update: add delta at position i (1-indexed)
    void update(int i, int delta) {
        for (; i <= n; i += i & (-i))  // Move to next responsible node
            tree[i] += delta;
    }

    // Prefix sum: sum of [1..i] (1-indexed)
    int query(int i) {
        int sum = 0;
        for (; i > 0; i -= i & (-i))   // Move to parent node
            sum += tree[i];
        return sum;
    }

    // Range sum: sum of [l..r] (1-indexed)
    int rangeQuery(int l, int r) {
        return query(r) - query(l - 1);
    }
}
// i & (-i) magic:
// i=6 (110): -6=...11010(2), 110 & ...010 = 010 = 2 → responsible for 2 elements
// i=4 (100): -4=...1100, 100 & 100 = 100 = 4 → responsible for 4 elements
```

---

### 28. 🔴 Bit Manipulation with Hex Masks (ARGB Color Extraction)
**Use:** Whenever a 32-bit int encodes multiple fields (color channels, flags, protocols).

```java
// Color stored as int: 0xAARRGGBB
// AA=Alpha (bits 24-31), RR=Red (bits 16-23), GG=Green (bits 8-15), BB=Blue (bits 0-7)

int color = 0xFF336699;

int alpha = (color >> 24) & 0xFF;    // Shift down 24, mask 8 bits → 0xFF = 255
int red   = (color >> 16) & 0xFF;    // → 0x33 = 51
int green = (color >> 8)  & 0xFF;    // → 0x66 = 102
int blue  = (color)       & 0xFF;    // No shift needed → 0x99 = 153

// Reconstruct color from channels
int rebuilt = (alpha << 24) | (red << 16) | (green << 8) | blue;

// Swap Red and Blue channels
int swapped = (color & 0xFF00FF00)          // Keep Alpha and Green
            | ((color & 0x000000FF) << 16)  // Blue → Red position
            | ((color & 0x00FF0000) >> 16); // Red → Blue position
```

---

### 29. 🔴 BitSet for Sieve of Eratosthenes
**Use:** Memory-efficient prime finding. `boolean[]` uses ~8× more RAM than `BitSet`.

```java
import java.util.BitSet;

BitSet sieve(int limit) {
    BitSet isComposite = new BitSet(limit + 1);
    isComposite.set(0); isComposite.set(1);         // 0 and 1 are not prime
    for (int p = 2; (long) p * p <= limit; p++) {
        if (!isComposite.get(p)) {
            for (int i = p * p; i <= limit; i += p) {
                isComposite.set(i);                 // Mark multiples composite
            }
        }
    }
    return isComposite;  // isComposite.get(n) == false → n is prime
}

// Iterate primes: use nextClearBit for O(1) skip vs manual scan
BitSet primes = sieve(100);
for (int p = primes.nextClearBit(2); p <= 100; p = primes.nextClearBit(p + 1)) {
    System.out.print(p + " ");
}
// Memory: sieve up to 100M → BitSet uses ~12MB vs boolean[] ~100MB
```

---

## 🧠 Master Cheat Sheet — Bit Manipulation Selection Guide

| Problem / Pattern | Key Operation | Trick | Time |
|---|---|---|---|
| Check odd/even | `n & 1` | Last bit = 0 (even) or 1 (odd) | O(1) |
| Check power of 2 | `n > 0 && (n & n-1) == 0` | Power of 2 has exactly 1 bit set | O(1) |
| Get i-th bit | `(n >> i) & 1` | Shift bit to position 0, mask | O(1) |
| Set i-th bit | `n \| (1 << i)` | OR with 1-mask | O(1) |
| Clear i-th bit | `n & ~(1 << i)` | AND with 0-mask | O(1) |
| Toggle i-th bit | `n ^ (1 << i)` | XOR flips | O(1) |
| Lowest set bit | `n & (-n)` | Two's complement trick | O(1) |
| Remove lowest set bit | `n & (n - 1)` | Brian Kernighan step | O(1) |
| Count set bits | `Integer.bitCount(n)` | Or Brian Kernighan loop | O(k) |
| Next power of 2 | Smear then +1 | OR with right shifts | O(1) |
| Safe midpoint | `(lo + hi) >>> 1` | Unsigned shift avoids overflow | O(1) |
| Single unique (pairs) | XOR all | Pairs cancel: `A^A=0` | O(n) |
| Two uniques (pairs) | XOR then split on LSB | Partition by differing bit | O(n) |
| Single unique (triples) | Bit-count mod 3 | Sum each bit position | O(32n) |
| Missing number | XOR [0..n] vs array | All others cancel | O(n) |
| Range XOR [l..r] | Prefix XOR array | `pre[r+1] ^ pre[l]` | O(1) query |
| All subsets of n elements | Enumerate 0..(1<<n)-1 | Bit i set = element i included | O(2ⁿ×n) |
| Sub-masks of mask m | `sub=(sub-1)&m` | Iterate submasks in O(3ⁿ) total | O(3ⁿ) |
| DP over subsets | `dp[mask][i]` | State = which elements used | O(2ⁿ×n²) |
| Prefix sum / range sum | Fenwick Tree | `i & -i` for tree navigation | O(log n) |
| Modular exponentiation | Binary exp | Process bits of exponent | O(log exp) |
| Range AND [left..right] | Shift until equal | Find common prefix | O(log n) |
| Reverse bits | Loop 32 times or parallel | `result=(result<<1)\|(n&1)` | O(32) |
| Gray code | `n ^ (n >> 1)` | Consecutive values differ by 1 bit | O(1) |
| Color channel extraction | Shift + `& 0xFF` mask | Each channel = 8 bits | O(1) |
| Prime sieve (memory) | BitSet | 8× smaller than boolean[] | O(n log log n) |

---

## ⚡ Key Nuances to Remember in Interviews

**Operator Precedence Traps**
- `&`, `|`, `^` have LOWER precedence than `==` and `!=`. Always parenthesize:
  - `if ((n & 1) == 0)` ← correct. `if (n & 1 == 0)` ← wrong (parsed as `n & (1 == 0)`)
- Similarly: `(n >> i) & 1` — shift first, then AND.

**Sign Bit Pitfalls**
- `>>` fills with sign bit — avoid for bit extraction on negative numbers. Use `>>>`.
- `int` has 32 bits; bit 31 is the sign bit. `1 << 31` = `Integer.MIN_VALUE` (negative!).
- Use `1L << i` when shifting beyond bit 30 to avoid sign confusion.

**The `n & (n-1)` Trick**
- Removes the lowest set bit of `n`. Core of: power-of-2 check, count bits (Brian Kernighan), check if only 1 bit set.
- Mental model: `n-1` flips all bits at and below the lowest set bit.

**XOR Properties (memorize all three)**
- `A ^ A = 0` → pairs cancel.
- `A ^ 0 = A` → identity.
- `A ^ B ^ A = B` → XOR is its own inverse; order doesn't matter.

**Bitmask DP Sizing**
- n=20 → 2²⁰ ≈ 1M states → feasible.
- n=25 → 2²⁵ ≈ 33M → borderline.
- n=30 → 2³⁰ ≈ 1B → too slow for O(2ⁿ×n).
- If n is large but elements are small values (e.g., digits 0-9), think column-bitmask DP instead.

**Java-Specific**
- `Integer.bitCount(n)` — popcount, O(1).
- `Integer.numberOfTrailingZeros(n)` — position of lowest set bit, O(1).
- `Integer.numberOfLeadingZeros(n)` — position of highest set bit, O(1).
- `Integer.reverse(n)` — reverses all 32 bits, O(1).
- `Integer.highestOneBit(n)` — returns value with only highest bit set, O(1).
- For negative shift amounts: Java masks shift by `& 31` for int, `& 63` for long.

**Hex Masks Quick Reference**
```
0xFF       = 0000 0000 0000 0000 0000 0000 1111 1111  (low byte)
0xFF00     = 0000 0000 0000 0000 1111 1111 0000 0000  (second byte)
0x0F0F0F0F = nibble pattern
0x55555555 = 0101... (odd bits)
0xAAAAAAAA = 1010... (even bits)
0x33333333 = 0011 0011... (pairs)
0xCCCCCCCC = 1100 1100... (pairs)
```

**Fenwick Tree**
- Always 1-indexed. `tree[0]` is unused.
- `i & (-i)` = responsible range = how many elements `i` covers.
- Update: `i += i & (-i)` (move to next responsible ancestor).
- Query: `i -= i & (-i)` (move to parent).

---

*Covers all standard interview bit manipulation topics from basics through bitmask DP. Revise the Master Cheat Sheet last, then drill whichever group feels shaky.*
