# DSA Complete Interview Playbook — Java (v1.1)

> **What's new in v1.1:** Z-Algorithm, SCC (Tarjan's + Kosaraju's), Articulation Points, Lazy Propagation Segment Tree, Closest Pair of Points, LFU Cache, Skip List, Morris Traversal, 0/1 BFS, A* Search, Network Flow (Dinic's), Suffix Arrays, Aho-Corasick, Digit DP, Matrix Exponentiation, Sparse Table, Bloom Filter, Count-Min Sketch, Treap, Euler Tour, Reservoir Sampling, Fisher-Yates, Java Concurrency section, and 30+ more topics.
>
> **How to use:** Each topic follows: *Identify (keywords/patterns) → Nuance/Gotcha → Steps → Code.*
> Before any interview, scan the **Quick-Reference Index** first, then drill the sections you are shaky on.
> Difficulty: 🟢 Easy → 🟡 Medium → 🔴 Hard within each group.

---

## 📋 Quick-Reference Index

| # | Topic | Key Signal Words |
|---|-------|-----------------|
| 1 | [Arrays & Strings](#1-arrays--strings) | subarray, prefix, rotation, anagram, difference array, sparse table |
| 2 | [Two Pointers](#2-two-pointers) | sorted, pair sum, palindrome, remove duplicates |
| 3 | [Sliding Window](#3-sliding-window) | subarray/substring of length k, longest, shortest, at most k |
| 4 | [Binary Search](#4-binary-search) | sorted, rotated, find minimum/maximum, first/last position |
| 5 | [Recursion & Backtracking](#5-recursion--backtracking) | all combinations, permutations, subsets, generate all |
| 6 | [Dynamic Programming](#6-dynamic-programming) | optimal, count ways, min/max cost, digit DP, matrix exp |
| 7 | [Linked Lists](#7-linked-lists) | reverse, cycle, LRU, LFU, skip list |
| 8 | [Stacks & Queues](#8-stacks--queues) | next greater, balanced brackets, monotonic, BFS |
| 9 | [Trees & Binary Trees](#9-trees--binary-trees) | path sum, LCA, diameter, Morris traversal, Euler tour |
| 10 | [Binary Search Trees](#10-binary-search-trees) | kth smallest, validate BST, AVL, Red-Black |
| 11 | [Heaps / Priority Queues](#11-heaps--priority-queues) | kth largest, top-k, median, merge k lists |
| 12 | [Graphs](#12-graphs) | connected, shortest path, 0/1 BFS, A*, network flow, Euler path |
| 13 | [Tries](#13-tries) | prefix, search words, autocomplete, word board |
| 14 | [Sorting & Searching](#14-sorting--searching) | sort by custom criteria, k-th element, closest pair |
| 15 | [Greedy](#15-greedy) | minimum steps, scheduling, intervals, fractional knapsack |
| 16 | [Divide & Conquer](#16-divide--conquer) | split, merge, closest pair, Karatsuba |
| 17 | [Bit Manipulation](#17-bit-manipulation) | XOR, single number, power of 2, subset mask |
| 18 | [Math & Number Theory](#18-math--number-theory) | prime, GCD, extended Euclid, CRT, FFT, Josephus |
| 19 | [Monotonic Stack / Queue](#19-monotonic-stack--queue) | next greater/smaller, largest rectangle, sliding max |
| 20 | [Union-Find (DSU)](#20-union-find-dsu) | connected components, cycle detection, dynamic connectivity |
| 21 | [Advanced Data Structures](#21-advanced-data-structures) | lazy seg tree, sparse table, treap, Bloom filter, Count-Min |
| 22 | [String Algorithms](#22-string-algorithms) | KMP, Z-algo, Aho-Corasick, suffix array, Manacher |
| 23 | [Advanced Graph Algorithms](#23-advanced-graph-algorithms) | Dijkstra, SCC, bridges, articulation, HLD, centroid |
| 24 | [Java Concurrency](#24-java-concurrency) | thread-safe, CAS, ConcurrentHashMap, BlockingQueue |
| 25 | [Interview Meta & Randomized Algorithms](#25-interview-meta--randomized-algorithms) | reservoir, Fisher-Yates, external sort, amortized |
| 26 | [Master Cheat Sheet](#26-master-cheat-sheet) | — |

---

## 1. Arrays & Strings

### 🧠 Identify this pattern when you see:
- "subarray", "contiguous", "prefix sum", "rotation"
- "anagram", "permutation of string", "window"
- "range update, point query" → Difference Array
- "range min/max in O(1)" → Sparse Table
- "next permutation", "longest consecutive"
- "stock profit with cooldown/fee"

---

### 1.1 🟢 Prefix Sum

**Use:** Answer multiple range sum queries in O(1) after O(n) preprocessing.

**⚠️ Nuance:** `prefix[i]` = sum of `nums[0..i-1]`. Range `[l, r]` = `prefix[r+1] - prefix[l]`.

```java
int[] buildPrefix(int[] nums) {
    int n = nums.length;
    int[] prefix = new int[n + 1];
    for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
    return prefix;
}
int rangeSum(int[] prefix, int l, int r) { return prefix[r + 1] - prefix[l]; }
```

**2D Prefix Sum:**
```java
int[][] buildPrefix2D(int[][] mat) {
    int m = mat.length, n = mat[0].length;
    int[][] p = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            p[i][j] = mat[i-1][j-1] + p[i-1][j] + p[i][j-1] - p[i-1][j-1];
    return p;
}
int subMatSum(int[][] p, int r1, int c1, int r2, int c2) {
    return p[r2+1][c2+1] - p[r1][c2+1] - p[r2+1][c1] + p[r1][c1];
}
```

---

### 1.2 🟢 Difference Array (Range Update, Point Query)

**Identify:** "add value V to every element in range [l, r]", multiple range updates then read final array.

**Key insight:** Prefix sum of difference array = original array after updates. Update is O(1); reconstruct is O(n).

**⚠️ Nuance:** This is the **complement** of prefix sums — prefix sums do range query; difference array does range update.

```java
// diff[i] = nums[i] - nums[i-1]
int[] buildDiff(int[] nums) {
    int n = nums.length;
    int[] diff = new int[n + 1]; // +1 to handle right boundary safely
    diff[0] = nums[0];
    for (int i = 1; i < n; i++) diff[i] = nums[i] - nums[i - 1];
    return diff;
}

// Range update: add val to all elements in [l, r]
void rangeAdd(int[] diff, int l, int r, int val) {
    diff[l] += val;
    diff[r + 1] -= val; // Cancel effect after r
}

// Reconstruct final array from diff
int[] reconstruct(int[] diff, int n) {
    int[] result = new int[n];
    result[0] = diff[0];
    for (int i = 1; i < n; i++) result[i] = result[i - 1] + diff[i];
    return result;
}

// Classic problem: flight booking — seats occupied in ranges
// [2, 4, 10] means add 10 seats for flights 2..4
int[] corpFlightBookings(int[][] bookings, int n) {
    int[] diff = new int[n + 2];
    for (int[] b : bookings) { diff[b[0]] += b[2]; diff[b[1] + 1] -= b[2]; }
    int[] result = new int[n];
    result[0] = diff[1];
    for (int i = 1; i < n; i++) result[i] = result[i-1] + diff[i+1];
    return result;
}
```

---

### 1.3 🟢 Kadane's Algorithm (Maximum Subarray Sum)

**Identify:** "maximum sum contiguous subarray", "largest sum subarray"

**⚠️ Nuance:** Initialize `maxSum` to `nums[0]` (not 0) to handle all-negative arrays.

```java
int maxSubarraySum(int[] nums) {
    int maxSum = nums[0], current = 0;
    for (int n : nums) {
        current = Math.max(n, current + n);
        maxSum = Math.max(maxSum, current);
    }
    return maxSum;
}

// Max product subarray — track both max and min (negatives flip sign)
int maxProduct(int[] nums) {
    int maxP = nums[0], minP = nums[0], result = nums[0];
    for (int i = 1; i < nums.length; i++) {
        int n = nums[i];
        int tempMax = Math.max(n, Math.max(maxP * n, minP * n));
        minP = Math.min(n, Math.min(maxP * n, minP * n));
        maxP = tempMax;
        result = Math.max(result, maxP);
    }
    return result;
}
```

---

### 1.4 🟢 HashMap for O(1) Lookup

```java
// Two Sum
int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (map.containsKey(complement)) return new int[]{map.get(complement), i};
        map.put(nums[i], i);
    }
    return new int[]{};
}

// Subarray sum equals k — prefix sum + HashMap
int subarrayCount(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    freq.put(0, 1);
    int count = 0, sum = 0;
    for (int n : nums) {
        sum += n;
        count += freq.getOrDefault(sum - k, 0);
        freq.merge(sum, 1, Integer::sum);
    }
    return count;
}
```

---

### 1.5 🟡 Longest Consecutive Sequence

**Identify:** "find the longest sequence of consecutive integers", O(n) required.

**⚠️ Nuance:** Only start counting from the beginning of a sequence (`n-1` not in set). This avoids re-counting.

```java
int longestConsecutive(int[] nums) {
    Set<Integer> set = new HashSet<>();
    for (int n : nums) set.add(n);
    int maxLen = 0;
    for (int n : set) {
        if (!set.contains(n - 1)) { // Start of a sequence
            int cur = n, len = 1;
            while (set.contains(cur + 1)) { cur++; len++; }
            maxLen = Math.max(maxLen, len);
        }
    }
    return maxLen;
}
```

---

### 1.6 🟡 Next Permutation

**Identify:** "next lexicographically greater permutation", "rearrange numbers"

**Steps:**
1. Find rightmost index `i` where `nums[i] < nums[i+1]` (scan right-to-left).
2. Find rightmost `j > i` where `nums[j] > nums[i]`.
3. Swap `nums[i]` and `nums[j]`.
4. Reverse everything from `i+1` to end.

**⚠️ Nuance:** If no such `i` exists, array is fully descending — just reverse the whole thing.

```java
void nextPermutation(int[] nums) {
    int n = nums.length, i = n - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) i--; // Step 1
    if (i >= 0) {
        int j = n - 1;
        while (nums[j] <= nums[i]) j--;             // Step 2
        swap(nums, i, j);                            // Step 3
    }
    reverse(nums, i + 1, n - 1);                    // Step 4
}
void swap(int[] a, int i, int j) { int t=a[i]; a[i]=a[j]; a[j]=t; }
void reverse(int[] a, int l, int r) { while(l<r) swap(a,l++,r--); }
```

---

### 1.7 🟡 Stock Buy/Sell Variants

**Identify:** "best time to buy and sell stock", "maximum profit", "with cooldown / fee / k transactions"

```java
// Single transaction — max(prices[j] - prices[i]) for i < j
int maxProfit1(int[] prices) {
    int minPrice = Integer.MAX_VALUE, maxProfit = 0;
    for (int p : prices) { minPrice = Math.min(minPrice, p); maxProfit = Math.max(maxProfit, p - minPrice); }
    return maxProfit;
}

// Unlimited transactions
int maxProfitUnlimited(int[] prices) {
    int profit = 0;
    for (int i = 1; i < prices.length; i++)
        if (prices[i] > prices[i-1]) profit += prices[i] - prices[i-1];
    return profit;
}

// With cooldown (can't buy day after sell)
int maxProfitCooldown(int[] prices) {
    int hold = Integer.MIN_VALUE, sold = 0, rest = 0;
    for (int p : prices) {
        int prevSold = sold;
        sold = hold + p;         // sell today
        hold = Math.max(hold, rest - p); // buy today (only if was resting)
        rest = Math.max(rest, prevSold); // rest today
    }
    return Math.max(sold, rest);
}

// With transaction fee
int maxProfitWithFee(int[] prices, int fee) {
    int hold = -prices[0], cash = 0;
    for (int p : prices) {
        cash = Math.max(cash, hold + p - fee);
        hold = Math.max(hold, cash - p);
    }
    return cash;
}

// At most k transactions — DP
int maxProfitK(int k, int[] prices) {
    int n = prices.length;
    if (k >= n / 2) return maxProfitUnlimited(prices); // Treat as unlimited
    int[] buy = new int[k + 1], sell = new int[k + 1];
    Arrays.fill(buy, Integer.MIN_VALUE);
    for (int p : prices)
        for (int t = 1; t <= k; t++) {
            buy[t]  = Math.max(buy[t],  sell[t-1] - p);
            sell[t] = Math.max(sell[t], buy[t] + p);
        }
    return sell[k];
}
```

---

### 1.8 🟡 Array Rotation

```java
void rotate(int[] nums, int k) {
    int n = nums.length;
    k %= n;
    reverse(nums, 0, n - 1);
    reverse(nums, 0, k - 1);
    reverse(nums, k, n - 1);
}
```

---

### 1.9 🟡 Dutch National Flag

```java
void sortColors(int[] nums) {
    int lo = 0, mid = 0, hi = nums.length - 1;
    while (mid <= hi) {
        if      (nums[mid] == 0) swap(nums, lo++, mid++);
        else if (nums[mid] == 1) mid++;
        else                     swap(nums, mid, hi--);
    }
}
```

---

### 1.10 🔴 Trapping Rain Water

```java
int trap(int[] height) {
    int lo = 0, hi = height.length - 1, maxL = 0, maxR = 0, water = 0;
    while (lo <= hi) {
        if (height[lo] <= height[hi]) {
            if (height[lo] >= maxL) maxL = height[lo];
            else water += maxL - height[lo];
            lo++;
        } else {
            if (height[hi] >= maxR) maxR = height[hi];
            else water += maxR - height[hi];
            hi--;
        }
    }
    return water;
}
```

---

## 2. Two Pointers

### 🧠 Identify this pattern when you see:
- Array is **sorted** or can be sorted
- "pair with sum", "three sum", "palindrome check"
- "remove duplicates", "squeeze from both ends"

---

### 2.1 🟢 Opposite-End Pointers

```java
int[] twoSumSorted(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l < r) {
        int sum = nums[l] + nums[r];
        if      (sum == target) return new int[]{l, r};
        else if (sum < target)  l++;
        else                    r--;
    }
    return new int[]{-1, -1};
}
```

---

### 2.2 🟡 Three Sum

**⚠️ Nuances:** Sort first. Fix `i`, two pointers for rest. Skip duplicates at every level.

```java
List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;
        int l = i + 1, r = nums.length - 1;
        while (l < r) {
            int sum = nums[i] + nums[l] + nums[r];
            if (sum == 0) {
                result.add(Arrays.asList(nums[i], nums[l], nums[r]));
                while (l < r && nums[l] == nums[l + 1]) l++;
                while (l < r && nums[r] == nums[r - 1]) r--;
                l++; r--;
            } else if (sum < 0) l++;
            else r--;
        }
    }
    return result;
}
```

---

### 2.3 🟡 Container With Most Water

```java
int maxWater(int[] height) {
    int l = 0, r = height.length - 1, max = 0;
    while (l < r) {
        max = Math.max(max, Math.min(height[l], height[r]) * (r - l));
        if (height[l] < height[r]) l++;
        else r--;
    }
    return max;
}
```

---

## 3. Sliding Window

### 🧠 Identify this pattern when you see:
- "subarray/substring of **length k**" → Fixed window
- "**longest**" or "**shortest**" subarray with condition → Variable window
- "**at most k** distinct", "**exactly k**" → `exactly(k) = atMost(k) - atMost(k-1)`

---

### 3.1 🟢 Fixed-Size Window

```java
double maxAvg(int[] nums, int k) {
    double sum = 0;
    for (int i = 0; i < k; i++) sum += nums[i];
    double max = sum;
    for (int i = k; i < nums.length; i++) {
        sum += nums[i] - nums[i - k];
        max = Math.max(max, sum);
    }
    return max / k;
}
```

---

### 3.2 🟡 Longest Without Repeating

```java
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> map = new HashMap<>();
    int maxLen = 0;
    for (int left = 0, right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (map.containsKey(c)) left = Math.max(left, map.get(c) + 1);
        map.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

---

### 3.3 🟡 Minimum Window Substring

```java
String minWindow(String s, String t) {
    int[] need = new int[128];
    for (char c : t.toCharArray()) need[c]++;
    int left = 0, minLen = Integer.MAX_VALUE, minStart = 0, formed = t.length();
    for (int right = 0; right < s.length(); right++) {
        if (need[s.charAt(right)]-- > 0) formed--;
        while (formed == 0) {
            if (right - left + 1 < minLen) { minLen = right - left + 1; minStart = left; }
            if (++need[s.charAt(left++)] > 0) formed++;
        }
    }
    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
```

---

### 3.4 🟡 Exactly K → AtMost K Trick

```java
int subarraysWithKDistinct(int[] nums, int k) {
    return atMost(nums, k) - atMost(nums, k - 1);
}
int atMost(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    int left = 0, count = 0;
    for (int right = 0; right < nums.length; right++) {
        freq.merge(nums[right], 1, Integer::sum);
        while (freq.size() > k) {
            freq.merge(nums[left], -1, Integer::sum);
            if (freq.get(nums[left]) == 0) freq.remove(nums[left]);
            left++;
        }
        count += right - left + 1;
    }
    return count;
}
```

---

## 4. Binary Search

### 🧠 Identify this pattern when you see:
- "sorted array", "rotated sorted", "find first/last position"
- "minimize the maximum", "maximize the minimum"
- Answer is **monotonic** (binary search on answer space)

---

### 4.1 🟢 Templates

```java
// Exact match
int binarySearch(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if      (nums[mid] == target) return mid;
        else if (nums[mid] < target)  lo = mid + 1;
        else                          hi = mid - 1;
    }
    return -1;
}

// Leftmost occurrence
int searchLeft(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) { result = mid; hi = mid - 1; }
        else if (nums[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return result;
}
```

---

### 4.2 🟡 Binary Search on Answer

```java
// Minimize: smallest feasible answer
int bsMinimize(int lo, int hi) {
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (feasible(mid)) hi = mid;
        else               lo = mid + 1;
    }
    return lo;
}

// Split Array Largest Sum
int splitArray(int[] nums, int k) {
    int lo = Arrays.stream(nums).max().getAsInt(), hi = Arrays.stream(nums).sum();
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (canSplit(nums, k, mid)) hi = mid; else lo = mid + 1;
    }
    return lo;
}
boolean canSplit(int[] nums, int k, int limit) {
    int parts = 1, sum = 0;
    for (int n : nums) { if (sum + n > limit) { parts++; sum = 0; } sum += n; }
    return parts <= k;
}
```

---

### 4.3 🟡 Search in Rotated Sorted Array

```java
int searchRotated(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[lo] <= nums[mid]) {
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else {
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
```

---

## 5. Recursion & Backtracking

### 🧠 Identify this pattern when you see:
- "generate **all** combinations / permutations / subsets"
- "can we place / choose / assign?" — constraint satisfaction

---

### 5.1 🟢 Subsets (Power Set)

```java
List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}
void backtrack(int[] nums, int start, List<Integer> curr, List<List<Integer>> res) {
    res.add(new ArrayList<>(curr));
    for (int i = start; i < nums.length; i++) {
        curr.add(nums[i]);
        backtrack(nums, i + 1, curr, res);
        curr.remove(curr.size() - 1);
    }
}
```

---

### 5.2 🟡 Permutations

```java
List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrackPerm(nums, new boolean[nums.length], new ArrayList<>(), result);
    return result;
}
void backtrackPerm(int[] nums, boolean[] used, List<Integer> curr, List<List<Integer>> res) {
    if (curr.size() == nums.length) { res.add(new ArrayList<>(curr)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        used[i] = true; curr.add(nums[i]);
        backtrackPerm(nums, used, curr, res);
        curr.remove(curr.size() - 1); used[i] = false;
    }
}
```

---

### 5.3 🟡 Combination Sum (Unlimited Reuse)

```java
List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<>();
    Arrays.sort(candidates);
    btCombSum(candidates, target, 0, new ArrayList<>(), result);
    return result;
}
void btCombSum(int[] nums, int rem, int start, List<Integer> curr, List<List<Integer>> res) {
    if (rem == 0) { res.add(new ArrayList<>(curr)); return; }
    for (int i = start; i < nums.length; i++) {
        if (nums[i] > rem) break;
        curr.add(nums[i]);
        btCombSum(nums, rem - nums[i], i, curr, res); // i not i+1 → allow reuse
        curr.remove(curr.size() - 1);
    }
}
```

---

### 5.4 🔴 N-Queens

```java
List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    boolean[] cols = new boolean[n], d1 = new boolean[2*n], d2 = new boolean[2*n];
    solveNQ(0, n, new int[n], cols, d1, d2, result);
    return result;
}
void solveNQ(int row, int n, int[] queens, boolean[] cols, boolean[] d1, boolean[] d2, List<List<String>> res) {
    if (row == n) { res.add(buildBoard(queens, n)); return; }
    for (int col = 0; col < n; col++) {
        if (cols[col] || d1[row-col+n] || d2[row+col]) continue;
        queens[row] = col; cols[col] = d1[row-col+n] = d2[row+col] = true;
        solveNQ(row + 1, n, queens, cols, d1, d2, res);
        cols[col] = d1[row-col+n] = d2[row+col] = false;
    }
}
List<String> buildBoard(int[] queens, int n) {
    List<String> board = new ArrayList<>();
    for (int r = 0; r < n; r++) {
        char[] line = new char[n]; Arrays.fill(line, '.'); line[queens[r]] = 'Q';
        board.add(new String(line));
    }
    return board;
}
```

---

## 6. Dynamic Programming

### 🧠 Identify this pattern when you see:
- "number of ways", "min cost", "max profit", "can we achieve?"
- "overlapping subproblems" in recursion
- "count integers with property up to N" → Digit DP
- "Fibonacci-like recurrence in O(log n)" → Matrix Exponentiation

---

### 6.1 🟢 Fibonacci / Climbing Stairs

```java
int climbStairs(int n) {
    if (n <= 2) return n;
    int prev2 = 1, prev1 = 2;
    for (int i = 3; i <= n; i++) { int cur = prev1 + prev2; prev2 = prev1; prev1 = cur; }
    return prev1;
}
```

---

### 6.2 🟢 0/1 Knapsack

```java
int knapsack1D(int[] weights, int[] values, int W) {
    int[] dp = new int[W + 1];
    for (int i = 0; i < weights.length; i++)
        for (int w = W; w >= weights[i]; w--) // Reverse prevents item reuse
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
    return dp[W];
}
```

---

### 6.3 🟡 Coin Change (Unbounded)

```java
int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);
    dp[0] = 0;
    for (int w = 1; w <= amount; w++)
        for (int c : coins) if (c <= w) dp[w] = Math.min(dp[w], dp[w - c] + 1);
    return dp[amount] > amount ? -1 : dp[amount];
}
```

---

### 6.4 🟡 LCS / Edit Distance

```java
int lcs(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            dp[i][j] = s1.charAt(i-1) == s2.charAt(j-1) ? dp[i-1][j-1] + 1 : Math.max(dp[i-1][j], dp[i][j-1]);
    return dp[m][n];
}

int editDistance(String s, String t) {
    int m = s.length(), n = t.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            dp[i][j] = s.charAt(i-1) == t.charAt(j-1) ? dp[i-1][j-1]
                      : 1 + Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]));
    return dp[m][n];
}
```

---

### 6.5 🟡 LIS — O(n log n)

```java
int lis(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    for (int n : nums) {
        int pos = Collections.binarySearch(tails, n);
        if (pos < 0) pos = -(pos + 1);
        if (pos == tails.size()) tails.add(n);
        else tails.set(pos, n);
    }
    return tails.size();
}
```

---

### 6.6 🟡 Longest Bitonic / Alternating Subsequence

**Identify:** "first increasing then decreasing", "alternating up-down subsequence"

```java
// Longest Bitonic: LIS from left + LIS from right - 1
int longestBitonic(int[] nums) {
    int n = nums.length;
    int[] lis = new int[n], lds = new int[n];
    Arrays.fill(lis, 1); Arrays.fill(lds, 1);
    for (int i = 1; i < n; i++)
        for (int j = 0; j < i; j++)
            if (nums[j] < nums[i]) lis[i] = Math.max(lis[i], lis[j] + 1);
    for (int i = n-2; i >= 0; i--)
        for (int j = i+1; j < n; j++)
            if (nums[j] < nums[i]) lds[i] = Math.max(lds[i], lds[j] + 1);
    int max = 0;
    for (int i = 0; i < n; i++) max = Math.max(max, lis[i] + lds[i] - 1);
    return max;
}

// Longest Alternating Subsequence — O(n) greedy DP
int longestAlternating(int[] nums) {
    int up = 1, down = 1;
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] > nums[i-1]) up = down + 1;
        else if (nums[i] < nums[i-1]) down = up + 1;
    }
    return Math.max(up, down);
}
```

---

### 6.7 🟡 House Robber

```java
int rob(int[] nums) {
    int prev2 = 0, prev1 = 0;
    for (int n : nums) { int cur = Math.max(prev1, prev2 + n); prev2 = prev1; prev1 = cur; }
    return prev1;
}
int robCircular(int[] nums) {
    if (nums.length == 1) return nums[0];
    return Math.max(robRange(nums, 0, nums.length-2), robRange(nums, 1, nums.length-1));
}
int robRange(int[] nums, int lo, int hi) {
    int p2 = 0, p1 = 0;
    for (int i = lo; i <= hi; i++) { int c = Math.max(p1, p2 + nums[i]); p2 = p1; p1 = c; }
    return p1;
}
```

---

### 6.8 🟡 Word Break

```java
boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;
    for (int i = 1; i <= s.length(); i++)
        for (int j = 0; j < i; j++)
            if (dp[j] && dict.contains(s.substring(j, i))) { dp[i] = true; break; }
    return dp[s.length()];
}
```

---

### 6.9 🔴 Burst Balloons (Interval DP)

```java
int maxCoins(int[] nums) {
    int n = nums.length;
    int[] arr = new int[n + 2];
    arr[0] = arr[n + 1] = 1;
    for (int i = 1; i <= n; i++) arr[i] = nums[i - 1];
    int[][] dp = new int[n + 2][n + 2];
    for (int len = 1; len <= n; len++)
        for (int left = 1; left <= n - len + 1; left++) {
            int right = left + len - 1;
            for (int k = left; k <= right; k++)
                dp[left][right] = Math.max(dp[left][right],
                    dp[left][k-1] + arr[left-1]*arr[k]*arr[right+1] + dp[k+1][right]);
        }
    return dp[1][n];
}
```

---

### 6.10 🔴 Digit DP

**Identify:** "count integers in range [1, N] with property X", "count numbers with digit sum divisible by K"

**Template:** Build the number digit by digit. Track: current position, whether we're still tight against the bound, leading zeros, accumulated state.

```java
// Count numbers from 1..n where digits don't repeat (no repeated digit)
int countNumbersWithUniqueDigits(int n) {
    // Classic DP: dp[i] = count with exactly i digits, all unique
    if (n == 0) return 1;
    int result = 10, available = 9;
    for (int i = 2; i <= n && available > 0; i++) {
        result += result * available; // but simpler approach:
        available--;
    }
    return result;
}

// General digit DP template
int[] digits;
int[][][] memo;

int digitDP(int n) {
    digits = Integer.toString(n).chars().map(c -> c - '0').toArray();
    int len = digits.length;
    memo = new int[len][2][/* state size */10]; // adjust state size
    for (int[][] a : memo) for (int[] b : a) Arrays.fill(b, -1);
    return solve(0, true, 0);
}

// pos = current digit position
// tight = are we still bounded by N's digits?
// state = problem-specific accumulated value (e.g., digit sum, mask of used digits)
int solve(int pos, boolean tight, int state) {
    if (pos == digits.length) return /* base case check on state */1;
    int key = state; // encode tight + state into memo key
    if (memo[pos][tight ? 1 : 0][key] != -1) return memo[pos][tight ? 1 : 0][key];
    int limit = tight ? digits[pos] : 9;
    int result = 0;
    for (int d = 0; d <= limit; d++) {
        boolean newTight = tight && (d == limit);
        int newState = /* update state with digit d */state;
        result += solve(pos + 1, newTight, newState);
    }
    return memo[pos][tight ? 1 : 0][key] = result;
}
```

---

### 6.11 🔴 Matrix Exponentiation

**Identify:** "compute F(n) where F(n) = aF(n-1) + bF(n-2) + ... for very large n", "Fibonacci mod 10^9"

**Key:** Represent recurrence as matrix multiply. Use fast power → O(k³ log n) where k = matrix size.

```java
// Matrix multiply
long[][] matMul(long[][] A, long[][] B, long mod) {
    int n = A.length;
    long[][] C = new long[n][n];
    for (int i = 0; i < n; i++)
        for (int k = 0; k < n; k++)
            for (int j = 0; j < n; j++)
                C[i][j] = (C[i][j] + A[i][k] * B[k][j]) % mod;
    return C;
}

// Matrix fast power
long[][] matPow(long[][] M, long p, long mod) {
    int n = M.length;
    long[][] result = new long[n][n];
    for (int i = 0; i < n; i++) result[i][i] = 1; // Identity
    while (p > 0) {
        if ((p & 1) == 1) result = matMul(result, M, mod);
        M = matMul(M, M, mod);
        p >>= 1;
    }
    return result;
}

// Fibonacci(n) mod m in O(log n)
long fib(long n, long mod) {
    if (n <= 1) return n;
    long[][] M = {{1, 1}, {1, 0}};
    long[][] result = matPow(M, n - 1, mod);
    return result[0][0]; // F(n)
}
// Generalization: [F(n+1), F(n)] = M^n * [F(1), F(0)]
// For recurrence F(n) = a*F(n-1) + b*F(n-2): M = [[a, b], [1, 0]]
```

---

### 6.12 🔴 Bitmask DP

```java
int assignTasks(int[][] cost) {
    int n = cost.length;
    int[] dp = new int[1 << n];
    Arrays.fill(dp, Integer.MAX_VALUE); dp[0] = 0;
    for (int mask = 0; mask < (1 << n); mask++) {
        if (dp[mask] == Integer.MAX_VALUE) continue;
        int worker = Integer.bitCount(mask);
        if (worker == n) continue;
        for (int task = 0; task < n; task++)
            if ((mask & (1 << task)) == 0)
                dp[mask | (1 << task)] = Math.min(dp[mask | (1 << task)], dp[mask] + cost[worker][task]);
    }
    return dp[(1 << n) - 1];
}
```

---

## 7. Linked Lists

### 🧠 Identify this pattern when you see:
- "reverse", "cycle", "nth from end", "LRU", "LFU"

---

### 7.1 🟢 Reverse

```java
ListNode reverse(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) { ListNode next = curr.next; curr.next = prev; prev = curr; curr = next; }
    return prev;
}
```

---

### 7.2 🟢 Floyd's Cycle Detection

```java
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next; fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}

ListNode cycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next; fast = fast.next.next;
        if (slow == fast) { slow = head; while (slow != fast) { slow = slow.next; fast = fast.next; } return slow; }
    }
    return null;
}
```

---

### 7.3 🟡 Merge K Sorted Lists

```java
ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
    for (ListNode node : lists) if (node != null) pq.offer(node);
    ListNode dummy = new ListNode(0), cur = dummy;
    while (!pq.isEmpty()) {
        ListNode node = pq.poll(); cur.next = node; cur = cur.next;
        if (node.next != null) pq.offer(node.next);
    }
    return dummy.next;
}
```

---

### 7.4 🟡 LRU Cache

```java
class LRUCache {
    private final int cap;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(), tail = new Node();
    LRUCache(int cap) { this.cap = cap; head.next = tail; tail.prev = head; }
    int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node n = map.get(key); remove(n); addFront(n); return n.val;
    }
    void put(int key, int value) {
        if (map.containsKey(key)) remove(map.get(key));
        if (map.size() == cap) { Node lru = tail.prev; remove(lru); map.remove(lru.key); }
        addFront(new Node(key, value));
    }
    void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; map.remove(n.key); }
    void addFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; map.put(n.key, n); }
    static class Node { int key, val; Node prev, next; Node() {} Node(int k, int v) { key=k; val=v; } }
}
```

---

### 7.5 🔴 LFU Cache

**Identify:** "least frequently used eviction", "evict lowest frequency, then LRU among ties"

**Structure:** HashMap(key → node) + HashMap(freq → DLL of keys) + `minFreq` tracker.

```java
class LFUCache {
    int capacity, minFreq;
    Map<Integer, Integer> keyVal = new HashMap<>();   // key → value
    Map<Integer, Integer> keyFreq = new HashMap<>();  // key → frequency
    Map<Integer, LinkedHashSet<Integer>> freqKeys = new HashMap<>(); // freq → ordered set of keys

    LFUCache(int capacity) { this.capacity = capacity; }

    int get(int key) {
        if (!keyVal.containsKey(key)) return -1;
        updateFreq(key);
        return keyVal.get(key);
    }

    void put(int key, int value) {
        if (capacity <= 0) return;
        if (keyVal.containsKey(key)) { keyVal.put(key, value); updateFreq(key); return; }
        if (keyVal.size() == capacity) {
            // Evict LFU key (and LRU among min-freq ties — first in LinkedHashSet)
            int evict = freqKeys.get(minFreq).iterator().next();
            freqKeys.get(minFreq).remove(evict);
            keyVal.remove(evict); keyFreq.remove(evict);
        }
        keyVal.put(key, value); keyFreq.put(key, 1);
        freqKeys.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFreq = 1; // New key always starts at freq 1
    }

    void updateFreq(int key) {
        int freq = keyFreq.get(key);
        keyFreq.put(key, freq + 1);
        freqKeys.get(freq).remove(key);
        if (freqKeys.get(freq).isEmpty()) {
            freqKeys.remove(freq);
            if (minFreq == freq) minFreq++;
        }
        freqKeys.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
    }
}
```

---

### 7.6 🔴 Skip List (Probabilistic Data Structure)

**Identify:** "O(log n) insert/search/delete in sorted linked list", "alternative to balanced BST"

**Concept:** Multiple layers of linked lists. Bottom = full list. Each higher level skips more elements (promoted with probability 1/2). Expected O(log n) for all operations.

```java
class SkipList {
    static final int MAX_LEVEL = 16;
    static final double P = 0.5;
    Node head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
    int level = 1;

    boolean search(int target) {
        Node cur = head;
        for (int i = level - 1; i >= 0; i--)
            while (cur.next[i] != null && cur.next[i].val < target) cur = cur.next[i];
        cur = cur.next[0];
        return cur != null && cur.val == target;
    }

    void insert(int num) {
        Node[] update = new Node[MAX_LEVEL];
        Arrays.fill(update, head);
        Node cur = head;
        for (int i = level - 1; i >= 0; i--)
            while (cur.next[i] != null && cur.next[i].val < num) cur = cur.next[i];
            update[i] = cur; // Where new node will be inserted at level i
        int newLevel = randomLevel();
        if (newLevel > level) { for (int i = level; i < newLevel; i++) update[i] = head; level = newLevel; }
        Node node = new Node(num, newLevel);
        for (int i = 0; i < newLevel; i++) { node.next[i] = update[i].next[i]; update[i].next[i] = node; }
    }

    boolean erase(int num) {
        Node[] update = new Node[MAX_LEVEL];
        Node cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < num) cur = cur.next[i];
            update[i] = cur;
        }
        cur = cur.next[0];
        if (cur == null || cur.val != num) return false;
        for (int i = 0; i < level; i++) {
            if (update[i].next[i] != cur) break;
            update[i].next[i] = cur.next[i];
        }
        while (level > 1 && head.next[level - 1] == null) level--;
        return true;
    }

    int randomLevel() {
        int lvl = 1;
        while (Math.random() < P && lvl < MAX_LEVEL) lvl++;
        return lvl;
    }

    static class Node { int val; Node[] next; Node(int v, int l) { val = v; next = new Node[l]; } }
}
```

---

## 8. Stacks & Queues

### 8.1 🟢 Valid Parentheses

```java
boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c=='(' || c=='[' || c=='{') stack.push(c);
        else {
            if (stack.isEmpty()) return false;
            char top = stack.pop();
            if (c==')' && top!='(') return false;
            if (c==']' && top!='[') return false;
            if (c=='}' && top!='{') return false;
        }
    }
    return stack.isEmpty();
}
```

---

### 8.2 🟡 Next Greater Element (Monotonic Stack)

```java
int[] nextGreater(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) result[stack.pop()] = nums[i];
        stack.push(i);
    }
    return result;
}
```

---

### 8.3 🟡 Largest Rectangle in Histogram

```java
int largestRectangleArea(int[] heights) {
    int n = heights.length, maxArea = 0;
    int[] h = new int[n + 2];
    System.arraycopy(heights, 0, h, 1, n);
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(0);
    for (int i = 1; i < h.length; i++) {
        while (h[i] < h[stack.peek()]) {
            int height = h[stack.pop()];
            maxArea = Math.max(maxArea, height * (i - stack.peek() - 1));
        }
        stack.push(i);
    }
    return maxArea;
}
```

---

### 8.4 🟡 Min Stack

```java
class MinStack {
    Deque<Integer> stack = new ArrayDeque<>(), minStack = new ArrayDeque<>();
    void push(int val) { stack.push(val); minStack.push(minStack.isEmpty() ? val : Math.min(val, minStack.peek())); }
    void pop() { stack.pop(); minStack.pop(); }
    int top() { return stack.peek(); }
    int getMin() { return minStack.peek(); }
}
```

---

### 8.5 🟡 Sliding Window Maximum

```java
int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        if (!deque.isEmpty() && deque.peekFirst() < i - k + 1) deque.pollFirst();
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast();
        deque.offerLast(i);
        if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
    }
    return result;
}
```

---

## 9. Trees & Binary Trees

### 🧠 Identify this pattern when you see:
- "level order" → Queue (BFS)
- "path sum", "LCA", "diameter" → DFS
- "O(1) space traversal" → Morris Traversal
- "subtree queries" → Euler Tour + BIT/Sparse Table

---

### 9.1 🟢 DFS Traversals

```java
// Iterative inorder
List<Integer> inorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode cur = root;
    while (cur != null || !stack.isEmpty()) {
        while (cur != null) { stack.push(cur); cur = cur.left; }
        cur = stack.pop(); result.add(cur.val); cur = cur.right;
    }
    return result;
}
```

---

### 9.2 🟢 BFS Level Order

```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode n = q.poll(); level.add(n.val);
            if (n.left != null)  q.offer(n.left);
            if (n.right != null) q.offer(n.right);
        }
        result.add(level);
    }
    return result;
}
```

---

### 9.3 🟡 LCA

```java
TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left = lca(root.left, p, q), right = lca(root.right, p, q);
    return (left != null && right != null) ? root : (left != null ? left : right);
}
```

---

### 9.4 🟡 Diameter of Binary Tree

```java
int diameter;
int diameterOfBinaryTree(TreeNode root) { diameter = 0; height(root); return diameter; }
int height(TreeNode node) {
    if (node == null) return 0;
    int l = height(node.left), r = height(node.right);
    diameter = Math.max(diameter, l + r);
    return 1 + Math.max(l, r);
}
```

---

### 9.5 🟡 Path Sum (Any Start/End)

```java
int pathSumCount(TreeNode root, int target) {
    Map<Long, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0L, 1);
    return dfsCount(root, 0L, target, prefixCount);
}
int dfsCount(TreeNode node, long curr, int target, Map<Long, Integer> map) {
    if (node == null) return 0;
    curr += node.val;
    int count = map.getOrDefault(curr - target, 0);
    map.merge(curr, 1, Integer::sum);
    count += dfsCount(node.left, curr, target, map) + dfsCount(node.right, curr, target, map);
    map.merge(curr, -1, Integer::sum);
    return count;
}
```

---

### 9.6 🟡 Serialize / Deserialize

```java
String serialize(TreeNode root) {
    if (root == null) return "#";
    return root.val + "," + serialize(root.left) + "," + serialize(root.right);
}
TreeNode deserialize(String data) {
    Queue<String> q = new LinkedList<>(Arrays.asList(data.split(",")));
    return buildTree(q);
}
TreeNode buildTree(Queue<String> q) {
    String val = q.poll();
    if (val.equals("#")) return null;
    TreeNode node = new TreeNode(Integer.parseInt(val));
    node.left = buildTree(q); node.right = buildTree(q);
    return node;
}
```

---

### 9.7 🔴 Morris Traversal (O(1) Space Inorder)

**Identify:** "inorder traversal without recursion or stack", "O(1) extra space traversal"

**Concept:** Temporarily thread the rightmost node of the left subtree to point back to current node. Restore after visiting.

```java
List<Integer> morrisInorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    TreeNode cur = root;
    while (cur != null) {
        if (cur.left == null) {
            result.add(cur.val); // Visit
            cur = cur.right;
        } else {
            // Find inorder predecessor (rightmost of left subtree)
            TreeNode pred = cur.left;
            while (pred.right != null && pred.right != cur) pred = pred.right;

            if (pred.right == null) {
                pred.right = cur; // Thread: create temporary link
                cur = cur.left;
            } else {
                pred.right = null; // Unthread: restore
                result.add(cur.val); // Visit
                cur = cur.right;
            }
        }
    }
    return result;
}
```

---

### 9.8 🔴 Euler Tour / DFS Timestamps (Subtree Queries)

**Identify:** "sum of values in subtree", "count nodes in subtree", "LCA with sparse table"

**Concept:** Flatten tree into array. Entry time `in[v]` and exit time `out[v]`. Subtree of `v` = array range `[in[v], out[v]]`.

```java
int[] in, out, order;
int timer = 0;

void eulerTour(int node, int parent, List<List<Integer>> adj, int[] vals) {
    in[node] = timer;
    order[timer++] = node; // Euler tour array
    for (int child : adj.get(node)) {
        if (child != parent) eulerTour(child, node, adj, vals);
    }
    out[node] = timer - 1;
}

// Now: subtree of node = range [in[node], out[node]] in `order` array
// Use BIT or Sparse Table on this range for O(1)/O(log n) subtree queries

// LCA via Euler Tour:
// Record every node visited in DFS (including backtracks) → 2n-1 length array
// LCA(u, v) = node with minimum depth between first[u] and first[v] in the tour
// Answer with RMQ (Sparse Table → O(1) LCA after O(n log n) preprocessing)
```

---

## 10. Binary Search Trees

### 10.1 🟢 Validate BST

```java
boolean isValidBST(TreeNode root) { return validate(root, Long.MIN_VALUE, Long.MAX_VALUE); }
boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) && validate(node.right, node.val, max);
}
```

---

### 10.2 🟡 Kth Smallest (Inorder)

```java
int kthSmallestBST(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode cur = root;
    while (cur != null || !stack.isEmpty()) {
        while (cur != null) { stack.push(cur); cur = cur.left; }
        cur = stack.pop();
        if (--k == 0) return cur.val;
        cur = cur.right;
    }
    return -1;
}
```

---

### 10.3 🔴 AVL Tree — Rotations

**Identify:** "self-balancing BST", "guaranteed O(log n) height", "rotation"

**Balance Factor** = height(left) - height(right). Must be in {-1, 0, 1}.

```java
class AVLTree {
    class Node { int val, height; Node left, right; Node(int v) { val=v; height=1; } }

    int height(Node n) { return n == null ? 0 : n.height; }
    int bf(Node n) { return n == null ? 0 : height(n.left) - height(n.right); }
    void updateHeight(Node n) { n.height = 1 + Math.max(height(n.left), height(n.right)); }

    // Right rotation (fix left-heavy)
    Node rotateRight(Node y) {
        Node x = y.left, T2 = x.right;
        x.right = y; y.left = T2;
        updateHeight(y); updateHeight(x);
        return x;
    }

    // Left rotation (fix right-heavy)
    Node rotateLeft(Node x) {
        Node y = x.right, T2 = y.left;
        y.left = x; x.right = T2;
        updateHeight(x); updateHeight(y);
        return y;
    }

    Node insert(Node node, int val) {
        if (node == null) return new Node(val);
        if (val < node.val)      node.left  = insert(node.left,  val);
        else if (val > node.val) node.right = insert(node.right, val);
        else return node; // Duplicate
        updateHeight(node);
        int balance = bf(node);
        // 4 cases:
        if (balance > 1 && val < node.left.val)  return rotateRight(node);     // Left-Left
        if (balance < -1 && val > node.right.val) return rotateLeft(node);     // Right-Right
        if (balance > 1 && val > node.left.val)  { node.left = rotateLeft(node.left); return rotateRight(node); }  // Left-Right
        if (balance < -1 && val < node.right.val) { node.right = rotateRight(node.right); return rotateLeft(node); } // Right-Left
        return node;
    }
}
// Red-Black Trees: Similar guarantee but rotations + recoloring; used in TreeMap/TreeSet internally
// Key property: No red node has a red parent; every path has same black-height
```

---

## 11. Heaps / Priority Queues

### 11.1 🟢 Kth Largest

```java
int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int n : nums) { minHeap.offer(n); if (minHeap.size() > k) minHeap.poll(); }
    return minHeap.peek();
}
```

---

### 11.2 🔴 Median Finder

```java
class MedianFinder {
    PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder()); // Max-heap
    PriorityQueue<Integer> upper = new PriorityQueue<>(); // Min-heap
    void addNum(int n) {
        lower.offer(n); upper.offer(lower.poll());
        if (lower.size() < upper.size()) lower.offer(upper.poll());
    }
    double findMedian() {
        return lower.size() > upper.size() ? lower.peek() : (lower.peek() + upper.peek()) / 2.0;
    }
}
```

---

### 11.3 🟡 Task Scheduler

```java
int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char c : tasks) freq[c - 'A']++;
    int maxFreq = Arrays.stream(freq).max().getAsInt();
    int maxCount = 0;
    for (int f : freq) if (f == maxFreq) maxCount++;
    return Math.max(tasks.length, (maxFreq - 1) * (n + 1) + maxCount);
}
```

---

## 12. Graphs

### 🧠 Identify this pattern when you see:
- "connected components" → BFS/DFS or DSU
- "0 or 1 edge weights" → 0/1 BFS (deque)
- "heuristic shortest path" → A*
- "Euler path/circuit" → Hierholzer
- "max flow, min cut" → Dinic's algorithm
- "bipartite matching" → Hopcroft-Karp

---

### 12.1 🟢 BFS (Shortest Path)

```java
int bfs(int start, int target, Map<Integer, List<Integer>> graph) {
    Queue<Integer> q = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    q.offer(start); visited.add(start);
    int dist = 0;
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            int node = q.poll();
            if (node == target) return dist;
            for (int nb : graph.getOrDefault(node, Collections.emptyList()))
                if (visited.add(nb)) q.offer(nb);
        }
        dist++;
    }
    return -1;
}
```

---

### 12.2 🟢 DFS (Number of Islands)

```java
int numIslands(char[][] grid) {
    int count = 0, m = grid.length, n = grid[0].length;
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            if (grid[i][j] == '1') { dfsIsland(grid, i, j, m, n); count++; }
    return count;
}
void dfsIsland(char[][] grid, int i, int j, int m, int n) {
    if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] != '1') return;
    grid[i][j] = '0';
    dfsIsland(grid, i+1, j, m, n); dfsIsland(grid, i-1, j, m, n);
    dfsIsland(grid, i, j+1, m, n); dfsIsland(grid, i, j-1, m, n);
}
```

---

### 12.3 🟡 Topological Sort (Kahn's)

```java
int[] topoSort(int n, int[][] edges) {
    List<List<Integer>> adj = new ArrayList<>();
    int[] inDegree = new int[n];
    for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    for (int[] e : edges) { adj.get(e[1]).add(e[0]); inDegree[e[0]]++; }
    Queue<Integer> q = new LinkedList<>();
    for (int i = 0; i < n; i++) if (inDegree[i] == 0) q.offer(i);
    int[] order = new int[n]; int idx = 0;
    while (!q.isEmpty()) {
        int node = q.poll(); order[idx++] = node;
        for (int next : adj.get(node)) if (--inDegree[next] == 0) q.offer(next);
    }
    return idx == n ? order : new int[]{};
}
```

---

### 12.4 🟡 0/1 BFS (Deque BFS for Edge Weights 0 or 1)

**Identify:** "shortest path where edges have weight 0 or 1", "minimum cost grid with two types of moves"

**⚠️ Nuance:** Weight-0 edges → push front (priority). Weight-1 edges → push back. O(V+E) vs Dijkstra's O((V+E)log V).

```java
int[] zeroOneBFS(int src, int n, List<int[]>[] adj) { // adj[u] = {v, weight (0 or 1)}
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    Deque<Integer> deque = new ArrayDeque<>();
    deque.offerFirst(src);
    while (!deque.isEmpty()) {
        int u = deque.pollFirst();
        for (int[] edge : adj[u]) {
            int v = edge[0], w = edge[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                if (w == 0) deque.offerFirst(v);  // 0-weight → front (higher priority)
                else        deque.offerLast(v);   // 1-weight → back
            }
        }
    }
    return dist;
}
// Example: "minimum number of obstacles to remove" in a grid
// obstacle cell: weight 1, empty cell: weight 0 → 0/1 BFS
```

---

### 12.5 🟡 Bipartite Check

```java
boolean isBipartite(int[][] graph) {
    int n = graph.length;
    int[] color = new int[n];
    for (int i = 0; i < n; i++) {
        if (color[i] != 0) continue;
        Queue<Integer> q = new LinkedList<>();
        q.offer(i); color[i] = 1;
        while (!q.isEmpty()) {
            int node = q.poll();
            for (int nb : graph[node]) {
                if (color[nb] == 0) { color[nb] = -color[node]; q.offer(nb); }
                else if (color[nb] == color[node]) return false;
            }
        }
    }
    return true;
}
```

---

### 12.6 🔴 A* Search (Heuristic Shortest Path)

**Identify:** "shortest path in grid with heuristic", "navigate from A to B on a map"

**Key:** f(n) = g(n) + h(n). g = actual cost from start. h = admissible heuristic (never overestimate). Use Manhattan distance for grids.

```java
int aStarGrid(int[][] grid, int[] start, int[] end) {
    int m = grid.length, n = grid[0].length;
    int[][] dist = new int[m][n];
    for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
    dist[start[0]][start[1]] = 0;
    // PQ ordered by f = g + h
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]); // {row, col, f}
    pq.offer(new int[]{start[0], start[1], heuristic(start, end)});
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int r = cur[0], c = cur[1], g = cur[2] - heuristic(new int[]{r,c}, end);
        if (r == end[0] && c == end[1]) return g;
        if (g > dist[r][c]) continue; // Stale
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || grid[nr][nc] == 1) continue;
            int ng = g + 1;
            if (ng < dist[nr][nc]) {
                dist[nr][nc] = ng;
                pq.offer(new int[]{nr, nc, ng + heuristic(new int[]{nr,nc}, end)});
            }
        }
    }
    return -1;
}
int heuristic(int[] a, int[] b) { return Math.abs(a[0]-b[0]) + Math.abs(a[1]-b[1]); } // Manhattan
```

---

### 12.7 🔴 Eulerian Path / Circuit

**Identify:** "traverse all edges exactly once", "reconstruct itinerary"

**Conditions:**
- **Eulerian Circuit:** All vertices have even degree (undirected), or all have equal in/out degree (directed).
- **Eulerian Path:** Exactly 0 or 2 vertices with odd degree (undirected), or exactly 1 vertex with out-degree - in-degree = 1 (start) and 1 with in-degree - out-degree = 1 (end) in directed.

```java
// Hierholzer's Algorithm — O(E)
List<String> findItinerary(List<List<String>> tickets) {
    Map<String, PriorityQueue<String>> adj = new HashMap<>();
    for (List<String> t : tickets)
        adj.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).add(t.get(1));
    LinkedList<String> result = new LinkedList<>();
    dfsEuler("JFK", adj, result);
    return result;
}
void dfsEuler(String airport, Map<String, PriorityQueue<String>> adj, LinkedList<String> result) {
    PriorityQueue<String> pq = adj.get(airport);
    while (pq != null && !pq.isEmpty()) dfsEuler(pq.poll(), adj, result);
    result.addFirst(airport); // Add to front after exhausting all outgoing edges
}
```

---

### 12.8 🔴 Network Flow — Dinic's Algorithm

**Identify:** "max flow", "min cut", "bipartite matching" (which reduces to max flow)

**Complexity:** O(V² × E). For unit-capacity graphs (bipartite matching): O(E√V).

```java
class Dinic {
    static class Edge { int to, rev; long cap; Edge(int to, long cap, int rev) { this.to=to; this.cap=cap; this.rev=rev; } }
    List<Edge>[] graph;
    int[] level, iter;
    int n;

    @SuppressWarnings("unchecked")
    Dinic(int n) { this.n=n; graph=new List[n]; for(int i=0;i<n;i++) graph[i]=new ArrayList<>(); level=new int[n]; iter=new int[n]; }

    void addEdge(int from, int to, long cap) {
        graph[from].add(new Edge(to, cap, graph[to].size()));
        graph[to].add(new Edge(from, 0, graph[from].size()-1)); // Reverse edge (cap=0 for directed)
    }

    boolean bfs(int s, int t) {
        Arrays.fill(level, -1); level[s] = 0;
        Queue<Integer> q = new LinkedList<>(); q.offer(s);
        while (!q.isEmpty()) {
            int v = q.poll();
            for (Edge e : graph[v]) if (e.cap > 0 && level[e.to] < 0) { level[e.to] = level[v]+1; q.offer(e.to); }
        }
        return level[t] >= 0;
    }

    long dfs(int v, int t, long f) {
        if (v == t) return f;
        for (; iter[v] < graph[v].size(); iter[v]++) {
            Edge e = graph[v].get(iter[v]);
            if (e.cap > 0 && level[v] < level[e.to]) {
                long d = dfs(e.to, t, Math.min(f, e.cap));
                if (d > 0) { e.cap -= d; graph[e.to].get(e.rev).cap += d; return d; }
            }
        }
        return 0;
    }

    long maxFlow(int s, int t) {
        long flow = 0;
        while (bfs(s, t)) {
            Arrays.fill(iter, 0);
            long d;
            while ((d = dfs(s, t, Long.MAX_VALUE)) > 0) flow += d;
        }
        return flow;
    }
}
// Bipartite Matching: add source → left nodes (cap 1), right nodes → sink (cap 1), left→right edges (cap 1)
// maxFlow = max bipartite matching
```

---

## 13. Tries

### 13.1 🟢 Trie Implementation

```java
class Trie {
    TrieNode root = new TrieNode();
    void insert(String word) {
        TrieNode cur = root;
        for (char c : word.toCharArray()) { cur.children.putIfAbsent(c, new TrieNode()); cur = cur.children.get(c); }
        cur.isEnd = true;
    }
    boolean search(String word) { TrieNode n = find(word); return n != null && n.isEnd; }
    boolean startsWith(String prefix) { return find(prefix) != null; }
    private TrieNode find(String s) {
        TrieNode cur = root;
        for (char c : s.toCharArray()) { if (!cur.children.containsKey(c)) return null; cur = cur.children.get(c); }
        return cur;
    }
    static class TrieNode { Map<Character, TrieNode> children = new HashMap<>(); boolean isEnd; }
}
```

---

## 14. Sorting & Searching

### 14.1 🟡 QuickSelect

```java
int quickSelect(int[] nums, int k) {
    return qsHelper(nums, 0, nums.length - 1, nums.length - k);
}
int qsHelper(int[] nums, int lo, int hi, int k) {
    if (lo == hi) return nums[lo];
    int pivot = partition(nums, lo, hi);
    if      (pivot == k) return nums[pivot];
    else if (pivot  < k) return qsHelper(nums, pivot + 1, hi, k);
    else                 return qsHelper(nums, lo, pivot - 1, k);
}
int partition(int[] nums, int lo, int hi) {
    int pivot = nums[hi], i = lo;
    for (int j = lo; j < hi; j++) if (nums[j] <= pivot) { int t=nums[i]; nums[i++]=nums[j]; nums[j]=t; }
    int t=nums[i]; nums[i]=nums[hi]; nums[hi]=t; return i;
}
```

---

### 14.2 🟡 Merge Intervals

```java
int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    List<int[]> merged = new ArrayList<>();
    for (int[] cur : intervals) {
        if (merged.isEmpty() || merged.get(merged.size()-1)[1] < cur[0]) merged.add(cur);
        else merged.get(merged.size()-1)[1] = Math.max(merged.get(merged.size()-1)[1], cur[1]);
    }
    return merged.toArray(new int[0][]);
}
```

---

### 14.3 🔴 Closest Pair of Points

**Identify:** "minimum distance between any two points", "closest pair"

**Approach:** Divide & Conquer, O(n log n). Split at median x. Recursively find min in each half. Check strip within `delta` of dividing line.

```java
double closestPair(int[][] points) {
    Arrays.sort(points, (a, b) -> a[0] - b[0]); // Sort by x
    return closestRec(points, 0, points.length - 1);
}

double closestRec(int[][] pts, int lo, int hi) {
    if (hi - lo < 3) { // Brute force for small sets
        double d = Double.MAX_VALUE;
        for (int i = lo; i <= hi; i++)
            for (int j = i + 1; j <= hi; j++) d = Math.min(d, dist(pts[i], pts[j]));
        Arrays.sort(pts, lo, hi + 1, (a, b) -> a[1] - b[1]); // Sort by y for merge step
        return d;
    }
    int mid = (lo + hi) / 2;
    double midX = pts[mid][0];
    double d = Math.min(closestRec(pts, lo, mid), closestRec(pts, mid + 1, hi));

    // Merge step: check strip points within delta of dividing line
    List<int[]> strip = new ArrayList<>();
    // pts is now sorted by y after recursive calls
    List<int[]> merged = mergeSortY(pts, lo, mid, hi);
    for (int i = 0; i < merged.size(); i++) { // Merge by y completed in sub-calls
        int[] p = merged.get(i);
        if (Math.abs(p[0] - midX) < d) strip.add(p);
    }
    // Check at most 7 points after each point in strip
    for (int i = 0; i < strip.size(); i++)
        for (int j = i + 1; j < strip.size() && strip.get(j)[1] - strip.get(i)[1] < d; j++)
            d = Math.min(d, dist(strip.get(i), strip.get(j)));
    return d;
}

double dist(int[] a, int[] b) {
    double dx = a[0] - b[0], dy = a[1] - b[1];
    return Math.sqrt(dx*dx + dy*dy);
}

List<int[]> mergeSortY(int[][] pts, int lo, int mid, int hi) {
    // Merge pts[lo..mid] and pts[mid+1..hi] both sorted by y
    List<int[]> result = new ArrayList<>();
    int i = lo, j = mid + 1;
    while (i <= mid && j <= hi) {
        if (pts[i][1] <= pts[j][1]) result.add(pts[i++]);
        else result.add(pts[j++]);
    }
    while (i <= mid) result.add(pts[i++]);
    while (j <= hi)  result.add(pts[j++]);
    for (int k = lo; k <= hi; k++) pts[k] = result.get(k - lo);
    return result;
}
```

---

### 14.4 🔴 Merge Sort (Count Inversions)

```java
long mergeSort(int[] arr, int lo, int hi) {
    if (lo >= hi) return 0;
    int mid = lo + (hi - lo) / 2;
    long count = mergeSort(arr, lo, mid) + mergeSort(arr, mid + 1, hi);
    int[] temp = new int[hi - lo + 1]; int i = lo, j = mid + 1, k = 0;
    while (i <= mid && j <= hi) {
        if (arr[i] <= arr[j]) temp[k++] = arr[i++];
        else { count += (mid - i + 1); temp[k++] = arr[j++]; }
    }
    while (i <= mid) temp[k++] = arr[i++];
    while (j <= hi)  temp[k++] = arr[j++];
    System.arraycopy(temp, 0, arr, lo, temp.length);
    return count;
}
```

---

## 15. Greedy

### 15.1 🟢 Jump Game

```java
boolean canJump(int[] nums) {
    int maxReach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false;
        maxReach = Math.max(maxReach, i + nums[i]);
    }
    return true;
}
```

---

### 15.2 🟡 Meeting Rooms

```java
int minMeetingRooms(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    PriorityQueue<Integer> endTimes = new PriorityQueue<>();
    for (int[] i : intervals) {
        if (!endTimes.isEmpty() && endTimes.peek() <= i[0]) endTimes.poll();
        endTimes.offer(i[1]);
    }
    return endTimes.size();
}
```

---

### 15.3 🟡 Fractional Knapsack

**Identify:** "maximize value with weight limit, items can be split", "greedily pick highest value/weight ratio"

**⚠️ Nuance:** Greedy works here (unlike 0/1 knapsack) because we can take fractional items.

```java
double fractionalKnapsack(int[][] items, int W) { // items[i] = {value, weight}
    Arrays.sort(items, (a, b) -> Double.compare((double)b[0]/b[1], (double)a[0]/a[1])); // Sort by value/weight desc
    double totalValue = 0;
    for (int[] item : items) {
        if (W <= 0) break;
        int take = Math.min(item[1], W);
        totalValue += (double) take / item[1] * item[0];
        W -= take;
    }
    return totalValue;
}
```

---

### 15.4 🟡 Gas Station

```java
int canCompleteCircuit(int[] gas, int[] cost) {
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        tank += gas[i] - cost[i]; total += gas[i] - cost[i];
        if (tank < 0) { start = i + 1; tank = 0; }
    }
    return total >= 0 ? start : -1;
}
```

---

### 15.5 🟡 Josephus Problem

**Identify:** "n people in circle, every kth person eliminated, who survives?"

```java
// O(n) — recursive formula: J(n, k) = (J(n-1, k) + k) % n
// J(1, k) = 0 (0-indexed position)
int josephus(int n, int k) {
    int pos = 0; // Position of survivor in sub-problem of size 1
    for (int size = 2; size <= n; size++)
        pos = (pos + k) % size;
    return pos; // 0-indexed; add 1 for 1-indexed answer
}
// For k=2 (every 2nd person): O(log n) formula exists using highest bit
int josephusK2(int n) {
    // Find highest bit, place it at lowest position
    int highestBit = Integer.highestOneBit(n);
    return 2 * (n - highestBit) + 1; // 1-indexed
}
```

---

## 16. Divide & Conquer

### 16.1 🟢 Fast Power

```java
long power(long base, long exp, long mod) {
    long result = 1; base %= mod;
    while (exp > 0) {
        if ((exp & 1) == 1) result = result * base % mod;
        base = base * base % mod; exp >>= 1;
    }
    return result;
}
```

---

### 16.2 🟡 Majority Element (Boyer-Moore)

```java
int majorityElement(int[] nums) {
    int candidate = nums[0], count = 1;
    for (int i = 1; i < nums.length; i++) count += nums[i] == candidate ? 1 : -1;
    if (count == 0) { candidate = nums[nums.length/2]; /* fallback */ }
    return candidate;
}
```

---

### 16.3 🔴 Karatsuba Multiplication

**Identify:** "multiply two large numbers faster than O(n²)", competitive/systems interviews

**Key:** Split each number in half, make 3 (not 4) recursive multiplications.

```java
// Multiply two non-negative BigInteger-style numbers represented as long
// Standard: a*b where a = a1*10^m + a0, b = b1*10^m + b0
// Naive: a1b1, a0b0, a1b0, a0b1 (4 mults)
// Karatsuba: a1b1, a0b0, (a1+a0)(b1+b0) - a1b1 - a0b0 (3 mults)
long karatsuba(long x, long y) {
    if (x < 10 || y < 10) return x * y;
    int n = Math.max(Long.toString(x).length(), Long.toString(y).length());
    int m = n / 2;
    long p = (long) Math.pow(10, m);
    long x1 = x / p, x0 = x % p;
    long y1 = y / p, y0 = y % p;
    long z2 = karatsuba(x1, y1);
    long z0 = karatsuba(x0, y0);
    long z1 = karatsuba(x1 + x0, y1 + y0) - z2 - z0;
    return z2 * (long)Math.pow(10, 2*m) + z1 * p + z0;
    // Real implementation uses BigInteger or string-based arithmetic to avoid overflow
}
```

---

## 17. Bit Manipulation

### 17.1 🟢 Core Operations

```java
boolean getBit(int n, int i)  { return ((n >> i) & 1) == 1; }
int setBit(int n, int i)      { return n | (1 << i); }
int clearBit(int n, int i)    { return n & ~(1 << i); }
int toggleBit(int n, int i)   { return n ^ (1 << i); }
boolean isOdd(int n)          { return (n & 1) == 1; }
boolean isPowerOfTwo(int n)   { return n > 0 && (n & (n - 1)) == 0; }
int countSetBits(int n)       { int c = 0; while (n != 0) { n &= n-1; c++; } return c; }
int lowestSetBit(int n)       { return n & (-n); }
```

---

### 17.2 🟢 XOR Tricks

```java
int singleNumber(int[] nums) { int r = 0; for (int n : nums) r ^= n; return r; }
int missingNumber(int[] nums) {
    int xor = nums.length;
    for (int i = 0; i < nums.length; i++) xor ^= i ^ nums[i];
    return xor;
}
```

---

### 17.3 🟡 Two Single Numbers

```java
int[] twoSingleNumbers(int[] nums) {
    int xorAll = 0;
    for (int n : nums) xorAll ^= n;
    int diff = xorAll & (-xorAll);
    int x = 0, y = 0;
    for (int n : nums) { if ((n & diff) != 0) x ^= n; else y ^= n; }
    return new int[]{x, y};
}
```

---

### 17.4 🟡 Bitmask Subsets

```java
List<List<Integer>> subsetsViaBit(int[] nums) {
    int n = nums.length;
    List<List<Integer>> result = new ArrayList<>();
    for (int mask = 0; mask < (1 << n); mask++) {
        List<Integer> subset = new ArrayList<>();
        for (int i = 0; i < n; i++) if ((mask & (1 << i)) != 0) subset.add(nums[i]);
        result.add(subset);
    }
    return result;
}
```

---

## 18. Math & Number Theory

### 18.1 🟢 GCD / LCM

```java
int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }
int lcm(int a, int b) { return a / gcd(a, b) * b; }
```

---

### 18.2 🟡 Extended Euclidean Algorithm

**Identify:** "find x, y such that ax + by = gcd(a,b)", "modular inverse when modulus is not prime"

```java
// Returns gcd and sets Bezout coefficients x, y: a*x + b*y = gcd
int[] extGcd(int a, int b) {
    if (b == 0) return new int[]{a, 1, 0}; // gcd=a, x=1, y=0
    int[] r = extGcd(b, a % b);
    int gcd = r[0], x = r[2], y = r[1] - (a / b) * r[2];
    return new int[]{gcd, x, y};
}

// Modular inverse: a^-1 mod m when gcd(a,m) = 1
int modInverseExtGcd(int a, int m) {
    int[] r = extGcd(a, m);
    if (r[0] != 1) return -1; // No inverse (gcd != 1)
    return ((r[1] % m) + m) % m; // Ensure positive
}
// vs Fermat: modInverse = power(a, m-2, m) — only works when m is prime
```

---

### 18.3 🟡 Chinese Remainder Theorem (CRT)

**Identify:** "find x such that x ≡ r1 (mod m1), x ≡ r2 (mod m2), ..." (moduli coprime)

```java
// x ≡ r1 (mod m1) and x ≡ r2 (mod m2)
long crt(long r1, long m1, long r2, long m2) {
    // Extended Euclid: find p such that m1*p ≡ 1 (mod m2)
    long[] ext = extGcdL(m1, m2);
    long p = ext[1]; // m1*p + m2*q = 1 → m1*p ≡ 1 (mod m2)
    long lcm = m1 * m2; // m1, m2 coprime
    long x = (r1 + m1 * ((r2 - r1) % m2 * p % m2)) % lcm;
    return (x + lcm) % lcm;
}
long[] extGcdL(long a, long b) {
    if (b == 0) return new long[]{a, 1, 0};
    long[] r = extGcdL(b, a % b);
    return new long[]{r[0], r[2], r[1] - (a/b)*r[2]};
}
// For multiple congruences: apply CRT pairwise
```

---

### 18.4 🟡 Sieve of Eratosthenes

```java
boolean[] sieve(int n) {
    boolean[] isComposite = new boolean[n + 1];
    isComposite[0] = isComposite[1] = true;
    for (int i = 2; i * i <= n; i++)
        if (!isComposite[i])
            for (int j = i * i; j <= n; j += i) isComposite[j] = true;
    return isComposite;
}
```

---

### 18.5 🟡 Modular Arithmetic

```java
long addMod(long a, long b, long m) { return ((a%m)+(b%m))%m; }
long mulMod(long a, long b, long m) { return (a%m)*(b%m)%m; }
long modInverse(long a, long m) { return power(a, m-2, m); } // m must be prime
long nCr(int n, int r, int p) {
    if (r > n) return 0;
    long[] fact = new long[n+1]; fact[0]=1;
    for (int i=1;i<=n;i++) fact[i]=fact[i-1]*i%p;
    return fact[n]*modInverse(fact[r],p)%p*modInverse(fact[n-r],p)%p;
}
```

---

### 18.6 🔴 Fast Fourier Transform (FFT) / NTT

**Identify:** "multiply two polynomials in O(n log n)", "large integer multiplication", "count substring matches"

**NTT (Number Theoretic Transform):** FFT over a prime field — avoids floating-point errors. Use mod = 998244353 (NTT-friendly prime, primitive root = 3).

```java
// Simplified iterative FFT (for reference — competitive programming)
static final int MOD = 998244353, g = 3;
void ntt(long[] a, boolean invert) {
    int n = a.length;
    for (int i = 1, j = 0; i < n; i++) {
        int bit = n >> 1;
        for (; (j & bit) != 0; bit >>= 1) j ^= bit;
        j ^= bit;
        if (i < j) { long t = a[i]; a[i] = a[j]; a[j] = t; }
    }
    for (int len = 2; len <= n; len <<= 1) {
        long w = invert ? power(power(g, MOD-1-( (MOD-1)/len ), MOD), 1, MOD) : power(g, (MOD-1)/len, MOD);
        for (int i = 0; i < n; i += len) {
            long wn = 1;
            for (int j = 0; j < len/2; j++) {
                long u = a[i+j], v = a[i+j+len/2] * wn % MOD;
                a[i+j] = (u+v) % MOD; a[i+j+len/2] = (u-v+MOD) % MOD;
                wn = wn * w % MOD;
            }
        }
    }
    if (invert) { long inv = power(n, MOD-2, MOD); for (int i=0;i<n;i++) a[i]=a[i]*inv%MOD; }
}
long[] multiply(long[] a, long[] b) {
    int result_len = a.length + b.length;
    int n = 1; while (n < result_len) n <<= 1;
    long[] fa = Arrays.copyOf(a, n), fb = Arrays.copyOf(b, n);
    ntt(fa, false); ntt(fb, false);
    for (int i=0;i<n;i++) fa[i]=fa[i]*fb[i]%MOD;
    ntt(fa, true);
    return fa;
}
```

---

## 19. Monotonic Stack / Queue

### 19.1 🟡 Next Greater / Smaller

```java
int[] nextSmaller(int[] nums) {
    int n = nums.length; int[] result = new int[n]; Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[i] < nums[stack.peek()]) result[stack.pop()] = nums[i];
        stack.push(i);
    }
    return result;
}
```

---

### 19.2 🔴 Maximal Rectangle in Binary Matrix

```java
int maximalRectangle(char[][] matrix) {
    if (matrix.length == 0) return 0;
    int n = matrix[0].length, maxArea = 0;
    int[] heights = new int[n];
    for (char[] row : matrix) {
        for (int j = 0; j < n; j++) heights[j] = row[j]=='1' ? heights[j]+1 : 0;
        maxArea = Math.max(maxArea, largestRectangleArea(heights));
    }
    return maxArea;
}
```

---

## 20. Union-Find (DSU)

### 20.1 🟡 DSU with Path Compression + Union by Rank

```java
class DSU {
    int[] parent, rank;
    DSU(int n) { parent = new int[n]; rank = new int[n]; for (int i=0;i<n;i++) parent[i]=i; }
    int find(int x) { if (parent[x]!=x) parent[x]=find(parent[x]); return parent[x]; }
    boolean union(int x, int y) {
        int px=find(x), py=find(y); if (px==py) return false;
        if (rank[px]<rank[py]) { int t=px; px=py; py=t; }
        parent[py]=px; if (rank[px]==rank[py]) rank[px]++;
        return true;
    }
    boolean connected(int x, int y) { return find(x)==find(y); }
}
```

---

## 21. Advanced Data Structures

### 🧠 Identify this pattern when you see:
- "range update AND range query" → Lazy Propagation Segment Tree
- "range min/max in O(1), static array" → Sparse Table
- "versioned queries / persistent structure" → Persistent Segment Tree
- "kth smallest in range" → Merge Sort Tree
- "ordered insertions with random access" → Treap
- "approximate membership" → Bloom Filter
- "frequency estimation in streams" → Count-Min Sketch

---

### 21.1 🟡 Binary Indexed Tree (Fenwick Tree)

```java
class BIT {
    int[] tree; int n;
    BIT(int n) { this.n=n; tree=new int[n+1]; }
    void update(int i, int delta) { for (; i<=n; i+=i&(-i)) tree[i]+=delta; }
    int query(int i) { int sum=0; for (; i>0; i-=i&(-i)) sum+=tree[i]; return sum; }
    int rangeQuery(int l, int r) { return query(r)-query(l-1); }
}
```

---

### 21.2 🔴 Segment Tree (Point Update, Range Query)

```java
class SegTree {
    int[] tree; int n;
    SegTree(int[] nums) { n=nums.length; tree=new int[4*n]; build(nums,0,0,n-1); }
    void build(int[] nums, int node, int s, int e) {
        if (s==e) { tree[node]=nums[s]; return; }
        int mid=(s+e)/2;
        build(nums,2*node+1,s,mid); build(nums,2*node+2,mid+1,e);
        tree[node]=tree[2*node+1]+tree[2*node+2];
    }
    void update(int node, int s, int e, int idx, int val) {
        if (s==e) { tree[node]=val; return; }
        int mid=(s+e)/2;
        if (idx<=mid) update(2*node+1,s,mid,idx,val);
        else          update(2*node+2,mid+1,e,idx,val);
        tree[node]=tree[2*node+1]+tree[2*node+2];
    }
    int query(int node, int s, int e, int l, int r) {
        if (r<s || e<l) return 0;
        if (l<=s && e<=r) return tree[node];
        int mid=(s+e)/2;
        return query(2*node+1,s,mid,l,r)+query(2*node+2,mid+1,e,l,r);
    }
}
```

---

### 21.3 🔴 Lazy Propagation Segment Tree (Range Update, Range Query)

**Identify:** "add value V to all elements in range [l,r]", "set all elements in range to V", then query range sum/min/max.

**⚠️ Nuance:** Lazy tag stores pending operation for children. Push down before accessing children.

```java
class LazySegTree {
    long[] tree, lazy;
    int n;

    LazySegTree(int[] nums) {
        n = nums.length;
        tree = new long[4 * n];
        lazy = new long[4 * n]; // Lazy: pending add for children
        build(nums, 0, 0, n - 1);
    }

    void build(int[] nums, int node, int s, int e) {
        if (s == e) { tree[node] = nums[s]; return; }
        int mid = (s + e) / 2;
        build(nums, 2*node+1, s, mid); build(nums, 2*node+2, mid+1, e);
        tree[node] = tree[2*node+1] + tree[2*node+2];
    }

    void pushDown(int node, int s, int e) {
        if (lazy[node] != 0) {
            int mid = (s + e) / 2;
            int leftLen = mid - s + 1, rightLen = e - mid;
            // Apply lazy to children
            tree[2*node+1] += lazy[node] * leftLen;
            tree[2*node+2] += lazy[node] * rightLen;
            lazy[2*node+1] += lazy[node];
            lazy[2*node+2] += lazy[node];
            lazy[node] = 0; // Clear parent's lazy
        }
    }

    // Range add: add val to all elements in [l, r]
    void rangeAdd(int node, int s, int e, int l, int r, long val) {
        if (r < s || e < l) return;
        if (l <= s && e <= r) { // Fully within range
            tree[node] += val * (e - s + 1);
            lazy[node] += val;
            return;
        }
        pushDown(node, s, e); // Push pending operations to children
        int mid = (s + e) / 2;
        rangeAdd(2*node+1, s, mid, l, r, val);
        rangeAdd(2*node+2, mid+1, e, l, r, val);
        tree[node] = tree[2*node+1] + tree[2*node+2];
    }

    // Range sum query
    long rangeQuery(int node, int s, int e, int l, int r) {
        if (r < s || e < l) return 0;
        if (l <= s && e <= r) return tree[node];
        pushDown(node, s, e);
        int mid = (s + e) / 2;
        return rangeQuery(2*node+1, s, mid, l, r) + rangeQuery(2*node+2, mid+1, e, l, r);
    }

    // Public wrappers
    void add(int l, int r, long val) { rangeAdd(0, 0, n-1, l, r, val); }
    long sum(int l, int r) { return rangeQuery(0, 0, n-1, l, r); }
}
```

---

### 21.4 🔴 Sparse Table (Static Range Minimum/Maximum in O(1))

**Identify:** "range minimum/maximum queries on static (non-updated) array", "RMQ in O(1)"

**⚠️ Nuance:** Preprocessing O(n log n), query O(1). Works because min/max are idempotent (overlapping ranges OK).

```java
class SparseTable {
    int[][] table;
    int[] log2;
    int n;

    SparseTable(int[] nums) {
        n = nums.length;
        int LOG = (int)(Math.log(n) / Math.log(2)) + 1;
        table = new int[LOG][n];
        log2 = new int[n + 1];

        // Precompute log2 values
        log2[1] = 0;
        for (int i = 2; i <= n; i++) log2[i] = log2[i/2] + 1;

        // Build sparse table: table[j][i] = min of nums[i..i+2^j-1]
        table[0] = nums.clone();
        for (int j = 1; j < LOG; j++)
            for (int i = 0; i + (1 << j) <= n; i++)
                table[j][i] = Math.min(table[j-1][i], table[j-1][i + (1 << (j-1))]);
    }

    // Query min in [l, r] — O(1)
    int queryMin(int l, int r) {
        int j = log2[r - l + 1];
        return Math.min(table[j][l], table[j][r - (1 << j) + 1]);
    }
    // For max: replace Math.min with Math.max throughout
}
```

---

### 21.5 🔴 Persistent Segment Tree (Versioned Queries)

**Identify:** "kth smallest in range [l, r] of original array", "queries on past versions of array"

**Concept:** Each update creates a new root, sharing unchanged nodes with previous version.

```java
class PersistentSegTree {
    int[] left, right, sum;
    int nodeCount = 0;
    int n;

    PersistentSegTree(int maxNodes) {
        left = new int[maxNodes]; right = new int[maxNodes]; sum = new int[maxNodes];
    }

    int newNode(int l, int r, int s) { left[nodeCount]=l; right[nodeCount]=r; sum[nodeCount]=s; return nodeCount++; }

    int build(int s, int e) {
        if (s == e) return newNode(0, 0, 0);
        int mid=(s+e)/2, node=newNode(0,0,0);
        left[node]=build(s,mid); right[node]=build(mid+1,e);
        return node;
    }

    // Returns new root after inserting value at position pos
    int update(int prev, int s, int e, int pos) {
        int node = newNode(left[prev], right[prev], sum[prev]+1);
        if (s == e) return node;
        int mid=(s+e)/2;
        if (pos<=mid) left[node]=update(left[prev],s,mid,pos);
        else         right[node]=update(right[prev],mid+1,e,pos);
        sum[node]=sum[left[node]]+sum[right[node]];
        return node;
    }

    // kth smallest between versions: roots[r] - roots[l-1]
    int kthSmallest(int leftRoot, int rightRoot, int s, int e, int k) {
        if (s==e) return s;
        int mid=(s+e)/2;
        int leftCount = sum[left[rightRoot]] - sum[left[leftRoot]];
        if (k<=leftCount) return kthSmallest(left[leftRoot],left[rightRoot],s,mid,k);
        else              return kthSmallest(right[leftRoot],right[rightRoot],mid+1,e,k-leftCount);
    }
}
```

---

### 21.6 🔴 Treap (Randomized BST)

**Identify:** "fast insert/delete/split/merge on sorted sequence", "implicit treap for array with O(log n) operations"

**Concept:** Each node has a key (BST property) and a random priority (heap property). Expected O(log n) height.

```java
class Treap {
    Random rand = new Random();
    class Node { int key, priority, size; Node left, right;
        Node(int k) { key=k; priority=rand.nextInt(); size=1; } }

    int size(Node n) { return n==null ? 0 : n.size; }
    void update(Node n) { if (n!=null) n.size=1+size(n.left)+size(n.right); }

    // Split into (< key) and (>= key)
    Node[] split(Node n, int key) {
        if (n==null) return new Node[]{null, null};
        if (n.key < key) { Node[] r=split(n.right,key); n.right=r[0]; update(n); return new Node[]{n, r[1]}; }
        else             { Node[] r=split(n.left, key); n.left =r[1]; update(n); return new Node[]{r[0], n}; }
    }

    Node merge(Node l, Node r) {
        if (l==null) return r; if (r==null) return l;
        if (l.priority > r.priority) { l.right=merge(l.right,r); update(l); return l; }
        else                         { r.left=merge(l,r.left);   update(r); return r; }
    }

    Node insert(Node root, int key) {
        Node[] parts=split(root, key);
        return merge(merge(parts[0], new Node(key)), parts[1]);
    }

    Node delete(Node root, int key) {
        Node[] parts=split(root, key);
        Node[] parts2=split(parts[1], key+1);
        return merge(parts[0], parts2[1]); // Discard exactly-key node
    }
}
```

---

### 21.7 🟡 Bloom Filter

**Identify:** "check if element is in set with no false negatives", "space-efficient membership test", "web crawl already-seen URLs", "distributed cache lookup"

**⚠️ Nuance:** False positives possible; no false negatives. Cannot delete (use Counting Bloom Filter for deletes). More bits / more hash functions → lower false positive rate.

```java
class BloomFilter {
    BitSet bits;
    int[] seeds; // Different seeds for k hash functions
    int size;

    BloomFilter(int size, int numHashes) {
        this.size = size;
        bits = new BitSet(size);
        seeds = new int[numHashes];
        Random rand = new Random(42);
        for (int i = 0; i < numHashes; i++) seeds[i] = rand.nextInt();
    }

    int hash(String item, int seed) {
        int h = seed;
        for (char c : item.toCharArray()) h = h * 31 + c;
        return Math.abs(h % size);
    }

    void add(String item) {
        for (int seed : seeds) bits.set(hash(item, seed));
    }

    // Returns false → definitely NOT in set
    // Returns true  → POSSIBLY in set (may be false positive)
    boolean mightContain(String item) {
        for (int seed : seeds) if (!bits.get(hash(item, seed))) return false;
        return true;
    }
}
// False positive rate ≈ (1 - e^(-kn/m))^k
// k = num hash functions, n = num elements, m = num bits
// Optimal k = (m/n) * ln(2)
// Used by: Cassandra, HBase, Redis (BF module), Chrome (safe browsing)
```

---

### 21.8 🟡 Count-Min Sketch (Frequency Estimation in Streams)

**Identify:** "estimate frequency of elements in a stream with limited memory", "heavy hitters", "top-k with approximate counts"

**⚠️ Nuance:** Always overestimates (adds noise from collisions), never underestimates. Width × depth determines accuracy.

```java
class CountMinSketch {
    int[][] table;
    int[] seeds;
    int width, depth;

    CountMinSketch(int width, int depth) {
        this.width = width; this.depth = depth;
        table = new int[depth][width];
        seeds = new int[depth];
        Random rand = new Random(42);
        for (int i = 0; i < depth; i++) seeds[i] = rand.nextInt();
    }

    int hash(String item, int seed) {
        int h = seed;
        for (char c : item.toCharArray()) h = h * 31 + c;
        return Math.abs(h % width);
    }

    void add(String item, int count) {
        for (int i = 0; i < depth; i++) table[i][hash(item, seeds[i])] += count;
    }

    // Returns estimate — always >= true count
    int estimate(String item) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < depth; i++) min = Math.min(min, table[i][hash(item, seeds[i])]);
        return min;
    }
}
// Error bound: with probability 1 - δ, error ≤ ε * (sum of all counts)
// width = ceil(e/ε), depth = ceil(ln(1/δ))
// Used by: network routers (traffic analysis), streaming analytics, Redis (TOPK module)
```

---

## 22. String Algorithms

### 🧠 Identify this pattern when you see:
- "pattern in text" → KMP
- "pattern in text (multiple patterns)" → Aho-Corasick
- "repeated substring, period of string" → Z-Algorithm / KMP failure function
- "longest palindromic substring" → Manacher's
- "number of distinct substrings, longest common substring" → Suffix Array

---

### 22.1 🟡 KMP

```java
int[] computeLPS(String pattern) {
    int m = pattern.length();
    int[] lps = new int[m];
    int len = 0, i = 1;
    while (i < m) {
        if (pattern.charAt(i)==pattern.charAt(len)) { lps[i++]=++len; }
        else if (len!=0) { len=lps[len-1]; }
        else { lps[i++]=0; }
    }
    return lps;
}

List<Integer> kmpSearch(String text, String pattern) {
    int[] lps = computeLPS(pattern);
    List<Integer> result = new ArrayList<>();
    int i=0, j=0;
    while (i < text.length()) {
        if (text.charAt(i)==pattern.charAt(j)) { i++; j++; }
        if (j==pattern.length()) { result.add(i-j); j=lps[j-1]; }
        else if (i<text.length() && text.charAt(i)!=pattern.charAt(j)) {
            if (j!=0) j=lps[j-1]; else i++;
        }
    }
    return result;
}
```

---

### 22.2 🟡 Z-Algorithm

**Identify:** "find all occurrences of pattern in text (alternative to KMP)", "longest substring starting at each position that matches a prefix of the string", "string period detection"

**Z[i]** = length of longest string starting at `s[i]` that is also a prefix of `s`.

```java
int[] zFunction(String s) {
    int n = s.length();
    int[] z = new int[n];
    z[0] = n; // Convention: z[0] = length of string
    int l = 0, r = 0;
    for (int i = 1; i < n; i++) {
        if (i < r) z[i] = Math.min(r - i, z[i - l]); // Use previously computed z
        while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
        if (i + z[i] > r) { l = i; r = i + z[i]; }
    }
    return z;
}

// Find all occurrences of pattern in text using Z-function
List<Integer> zSearch(String text, String pattern) {
    String s = pattern + "$" + text; // "$" as separator not in alphabet
    int[] z = zFunction(s);
    int m = pattern.length();
    List<Integer> result = new ArrayList<>();
    for (int i = m + 1; i < s.length(); i++)
        if (z[i] == m) result.add(i - m - 1); // Occurrence in text at position i-m-1
    return result;
}

// Check if string has period p: z[p] == n - p (or z[p] + p >= n)
int smallestPeriod(String s) {
    int n = s.length();
    int[] z = zFunction(s);
    for (int p = 1; p < n; p++)
        if (n % p == 0 && z[p] == n - p) return p;
    return n;
}
```

---

### 22.3 🟡 Rabin-Karp (Rolling Hash)

```java
boolean rabinKarp(String text, String pattern) {
    int n=text.length(), m=pattern.length();
    long BASE=31, MOD=1_000_000_007;
    long patHash=0, textHash=0, power=1;
    for (int i=0;i<m;i++) {
        patHash=(patHash*BASE+(pattern.charAt(i)-'a'+1))%MOD;
        textHash=(textHash*BASE+(text.charAt(i)-'a'+1))%MOD;
        if (i>0) power=power*BASE%MOD;
    }
    if (patHash==textHash && text.substring(0,m).equals(pattern)) return true;
    for (int i=m;i<n;i++) {
        textHash=(textHash-(text.charAt(i-m)-'a'+1)*power%MOD+MOD)%MOD;
        textHash=(textHash*BASE+(text.charAt(i)-'a'+1))%MOD;
        if (patHash==textHash && text.substring(i-m+1,i+1).equals(pattern)) return true;
    }
    return false;
}
```

---

### 22.4 🟡 Manacher's (Longest Palindromic Substring in O(n))

```java
String longestPalindrome(String s) {
    String t="#"+String.join("#",s.split(""))+"#";
    int n=t.length(); int[] p=new int[n]; int c=0,r=0;
    for (int i=0;i<n;i++) {
        if (i<r) p[i]=Math.min(r-i,p[2*c-i]);
        while (i+p[i]+1<n && i-p[i]-1>=0 && t.charAt(i+p[i]+1)==t.charAt(i-p[i]-1)) p[i]++;
        if (i+p[i]>r) { c=i; r=i+p[i]; }
    }
    int maxLen=0,center=0;
    for (int i=0;i<n;i++) if (p[i]>maxLen) { maxLen=p[i]; center=i; }
    return s.substring((center-maxLen)/2,(center+maxLen)/2);
}
```

---

### 22.5 🔴 Aho-Corasick (Multi-Pattern String Matching)

**Identify:** "find all occurrences of multiple patterns in text simultaneously", "virus scanner", "spam filter"

**Concept:** Build trie of patterns + failure links (like KMP's failure function but on the trie). O(n + sum(pattern lengths) + number of matches).

```java
class AhoCorasick {
    int[][] go;           // Goto function: go[state][char]
    int[] fail;           // Failure function
    List<Integer>[] out;  // Output: pattern IDs that end at each state
    int stateCount = 1;
    static final int ALPHA = 26;

    @SuppressWarnings("unchecked")
    AhoCorasick(int maxStates) {
        go = new int[maxStates][ALPHA];
        fail = new int[maxStates];
        out = new List[maxStates];
        for (int[] row : go) Arrays.fill(row, -1);
        for (int i = 0; i < maxStates; i++) out[i] = new ArrayList<>();
    }

    void addPattern(String pattern, int id) {
        int state = 0;
        for (char c : pattern.toCharArray()) {
            int ch = c - 'a';
            if (go[state][ch] == -1) go[state][ch] = stateCount++;
            state = go[state][ch];
        }
        out[state].add(id);
    }

    void build() {
        Queue<Integer> q = new LinkedList<>();
        // Initialize: states reachable from root in 1 step
        for (int c = 0; c < ALPHA; c++) {
            if (go[0][c] == -1) go[0][c] = 0; // Loop back to root
            else { fail[go[0][c]] = 0; q.offer(go[0][c]); }
        }
        while (!q.isEmpty()) {
            int u = q.poll();
            out[u].addAll(out[fail[u]]); // Inherit outputs from failure link
            for (int c = 0; c < ALPHA; c++) {
                if (go[u][c] == -1) {
                    go[u][c] = go[fail[u]][c]; // Follow failure link
                } else {
                    fail[go[u][c]] = go[fail[u]][c];
                    q.offer(go[u][c]);
                }
            }
        }
    }

    // Search: returns map of pattern_id → list of ending positions in text
    Map<Integer, List<Integer>> search(String text) {
        Map<Integer, List<Integer>> result = new HashMap<>();
        int state = 0;
        for (int i = 0; i < text.length(); i++) {
            state = go[state][text.charAt(i) - 'a'];
            for (int patId : out[state])
                result.computeIfAbsent(patId, k -> new ArrayList<>()).add(i);
        }
        return result;
    }
}
```

---

### 22.6 🔴 Suffix Array + LCP Array

**Identify:** "number of distinct substrings", "longest repeated substring", "longest common substring of two strings"

**Suffix Array SA:** SA[i] = starting index of the ith lexicographically smallest suffix.
**LCP Array:** LCP[i] = length of longest common prefix between SA[i-1] and SA[i] (consecutive suffixes in sorted order).

```java
// O(n log n) suffix array construction
int[] buildSuffixArray(String s) {
    int n = s.length();
    Integer[] sa = new Integer[n];
    for (int i = 0; i < n; i++) sa[i] = i;
    int[] rank = new int[n], tmp = new int[n];
    for (int i = 0; i < n; i++) rank[i] = s.charAt(i);

    for (int gap = 1; gap < n; gap <<= 1) {
        final int[] r = rank.clone(), g = new int[]{gap};
        Arrays.sort(sa, (a, b) -> r[a] != r[b] ? r[a]-r[b] : (a+g[0]<n?r[a+g[0]]:-1)-(b+g[0]<n?r[b+g[0]]:-1));
        tmp[sa[0]] = 0;
        for (int i = 1; i < n; i++)
            tmp[sa[i]] = tmp[sa[i-1]] + (r[sa[i]]!=r[sa[i-1]] || (sa[i]+gap<n?r[sa[i]+gap]:-1)!=(sa[i-1]+gap<n?r[sa[i-1]+gap]:-1) ? 1 : 0);
        rank = tmp.clone();
    }
    return Arrays.stream(sa).mapToInt(Integer::intValue).toArray();
}

// Kasai's algorithm: O(n) LCP array from suffix array
int[] buildLCP(String s, int[] sa) {
    int n = s.length();
    int[] lcp = new int[n], rank = new int[n];
    for (int i = 0; i < n; i++) rank[sa[i]] = i;
    int h = 0;
    for (int i = 0; i < n; i++) {
        if (rank[i] > 0) {
            int j = sa[rank[i] - 1];
            while (i + h < n && j + h < n && s.charAt(i+h) == s.charAt(j+h)) h++;
            lcp[rank[i]] = h;
            if (h > 0) h--;
        }
    }
    return lcp;
}

// Count distinct substrings = n*(n+1)/2 - sum(lcp)
long countDistinctSubstrings(String s) {
    int n = s.length();
    int[] sa = buildSuffixArray(s);
    int[] lcp = buildLCP(s, sa);
    long total = (long)n*(n+1)/2;
    for (int x : lcp) total -= x;
    return total;
}
```

---

## 23. Advanced Graph Algorithms

### 🧠 Identify this pattern when you see:
- "strongly connected components" → Tarjan's SCC or Kosaraju's
- "bridge / cut edge" → Tarjan's bridge algorithm
- "articulation point / cut vertex" → Tarjan's AP algorithm
- "all-pairs shortest path (sparse graph)" → Johnson's
- "path queries on tree" → Heavy-Light Decomposition
- "tree distance problems" → Centroid Decomposition

---

### 23.1 🟡 Dijkstra

```java
int[] dijkstra(int src, int n, List<int[]>[] adj) {
    int[] dist = new int[n]; Arrays.fill(dist, Integer.MAX_VALUE); dist[src]=0;
    PriorityQueue<int[]> pq = new PriorityQueue<>((a,b)->a[1]-b[1]);
    pq.offer(new int[]{src,0});
    while (!pq.isEmpty()) {
        int[] cur=pq.poll(); int u=cur[0],d=cur[1];
        if (d>dist[u]) continue;
        for (int[] e : adj[u]) { int v=e[0],w=e[1];
            if (dist[u]+w<dist[v]) { dist[v]=dist[u]+w; pq.offer(new int[]{v,dist[v]}); }
        }
    }
    return dist;
}
```

---

### 23.2 🟡 Bellman-Ford

```java
int[] bellmanFord(int src, int n, int[][] edges) {
    int[] dist=new int[n]; Arrays.fill(dist,Integer.MAX_VALUE); dist[src]=0;
    for (int i=0;i<n-1;i++)
        for (int[] e:edges)
            if (dist[e[0]]!=Integer.MAX_VALUE && dist[e[0]]+e[2]<dist[e[1]]) dist[e[1]]=dist[e[0]]+e[2];
    for (int[] e:edges)
        if (dist[e[0]]!=Integer.MAX_VALUE && dist[e[0]]+e[2]<dist[e[1]]) return null; // Negative cycle
    return dist;
}
```

---

### 23.3 🟡 Floyd-Warshall

```java
int[][] floydWarshall(int[][] graph, int n) {
    int[][] dist=new int[n][n];
    for (int[] row:dist) Arrays.fill(row, Integer.MAX_VALUE/2);
    for (int i=0;i<n;i++) dist[i][i]=0;
    for (int i=0;i<n;i++) for (int j=0;j<n;j++) if (graph[i][j]!=0) dist[i][j]=graph[i][j];
    for (int k=0;k<n;k++) for (int i=0;i<n;i++) for (int j=0;j<n;j++)
        dist[i][j]=Math.min(dist[i][j], dist[i][k]+dist[k][j]);
    return dist;
}
```

---

### 23.4 🔴 Johnson's Algorithm (All-Pairs Shortest Path, Sparse Graphs)

**When to use:** All-pairs shortest path on **sparse** graphs with negative weights (but no negative cycles). O(V²log V + VE) — better than Floyd-Warshall O(V³) for sparse graphs.

**Steps:**
1. Add virtual node q connected to every vertex with weight 0.
2. Run Bellman-Ford from q → get potentials h[v].
3. Reweight edges: w'(u,v) = w(u,v) + h[u] - h[v] (all non-negative now).
4. Run Dijkstra from every vertex on reweighted graph.
5. Adjust results: dist(u,v) = dijkstra(u,v) - h[u] + h[v].

```java
int[][] johnsons(int n, int[][] edges) { // edges = {u, v, w}
    // Step 1-2: Add virtual node n, run Bellman-Ford
    int[] h = new int[n + 1]; Arrays.fill(h, Integer.MAX_VALUE); h[n] = 0;
    // Add edges from virtual node n to all others with weight 0
    int[][] augEdges = Arrays.copyOf(edges, edges.length + n);
    for (int i = 0; i < n; i++) augEdges[edges.length + i] = new int[]{n, i, 0};
    // Bellman-Ford from virtual node
    for (int iter = 0; iter < n; iter++)
        for (int[] e : augEdges)
            if (h[e[0]] != Integer.MAX_VALUE && h[e[0]] + e[2] < h[e[1]]) h[e[1]] = h[e[0]] + e[2];

    // Step 3: Reweight
    List<int[]>[] adj = new List[n];
    for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
    for (int[] e : edges) adj[e[0]].add(new int[]{e[1], e[2] + h[e[0]] - h[e[1]]});

    // Step 4-5: Dijkstra from each vertex
    int[][] result = new int[n][];
    for (int src = 0; src < n; src++) {
        int[] d = dijkstra(src, n, adj);
        result[src] = d;
        for (int v = 0; v < n; v++) if (d[v] != Integer.MAX_VALUE) result[src][v] += h[v] - h[src];
    }
    return result;
}
```

---

### 23.5 🔴 Bridges in Graph (Tarjan's)

**Identify:** "critical connections", "bridge edges"

```java
List<List<Integer>> bridges = new ArrayList<>();
int timer = 0;
int[] disc, low; boolean[] visited;

void findBridges(List<List<Integer>> adj, int n) {
    disc=new int[n]; low=new int[n]; visited=new boolean[n];
    for (int i=0;i<n;i++) if (!visited[i]) dfsBridge(adj,i,-1);
}
void dfsBridge(List<List<Integer>> adj, int u, int parent) {
    visited[u]=true; disc[u]=low[u]=timer++;
    for (int v:adj.get(u)) {
        if (!visited[v]) {
            dfsBridge(adj,v,u); low[u]=Math.min(low[u],low[v]);
            if (low[v]>disc[u]) bridges.add(Arrays.asList(u,v));
        } else if (v!=parent) low[u]=Math.min(low[u],disc[v]);
    }
}
```

---

### 23.6 🔴 Articulation Points (Tarjan's)

**Identify:** "cut vertices", "removing which node disconnects the graph?"

**⚠️ Nuance:** Root is an AP if it has ≥ 2 children in DFS tree. Non-root is AP if low[v] ≥ disc[u] for some child v.

```java
Set<Integer> articulationPoints = new HashSet<>();
int apTimer = 0;
int[] apDisc, apLow; boolean[] apVisited;

void findArticulationPoints(List<List<Integer>> adj, int n) {
    apDisc=new int[n]; apLow=new int[n]; apVisited=new boolean[n];
    Arrays.fill(apDisc,-1);
    for (int i=0;i<n;i++) if (!apVisited[i]) dfsAP(adj,i,-1);
}

void dfsAP(List<List<Integer>> adj, int u, int parent) {
    apVisited[u]=true; apDisc[u]=apLow[u]=apTimer++;
    int children=0;
    for (int v:adj.get(u)) {
        if (!apVisited[v]) {
            children++;
            dfsAP(adj,v,u);
            apLow[u]=Math.min(apLow[u],apLow[v]);
            // u is AP if:
            if (parent==-1 && children>1) articulationPoints.add(u); // Root with 2+ children
            if (parent!=-1 && apLow[v]>=apDisc[u]) articulationPoints.add(u); // Non-root, can't bypass
        } else if (v!=parent) {
            apLow[u]=Math.min(apLow[u],apDisc[v]); // Back edge
        }
    }
}
```

---

### 23.7 🔴 Strongly Connected Components — Kosaraju's

**Identify:** "SCCs", "groups where every node can reach every other node"

**Steps:**
1. DFS on original graph, push to stack in finish order.
2. Transpose graph (reverse all edges).
3. DFS on transposed graph in reverse finish order — each DFS tree = one SCC.

```java
List<List<Integer>> kosarajuSCC(int n, List<List<Integer>> adj) {
    // Step 1: Finish order DFS
    boolean[] visited = new boolean[n];
    Deque<Integer> order = new ArrayDeque<>();
    for (int i = 0; i < n; i++) if (!visited[i]) dfsFinish(adj, i, visited, order);

    // Step 2: Build transpose
    List<List<Integer>> radj = new ArrayList<>();
    for (int i = 0; i < n; i++) radj.add(new ArrayList<>());
    for (int u = 0; u < n; u++) for (int v : adj.get(u)) radj.get(v).add(u);

    // Step 3: DFS on transposed graph in reverse finish order
    Arrays.fill(visited, false);
    List<List<Integer>> sccs = new ArrayList<>();
    while (!order.isEmpty()) {
        int u = order.pop();
        if (!visited[u]) {
            List<Integer> scc = new ArrayList<>();
            dfsCollect(radj, u, visited, scc);
            sccs.add(scc);
        }
    }
    return sccs;
}
void dfsFinish(List<List<Integer>> adj, int u, boolean[] visited, Deque<Integer> order) {
    visited[u] = true;
    for (int v : adj.get(u)) if (!visited[v]) dfsFinish(adj, v, visited, order);
    order.push(u);
}
void dfsCollect(List<List<Integer>> adj, int u, boolean[] visited, List<Integer> scc) {
    visited[u] = true; scc.add(u);
    for (int v : adj.get(u)) if (!visited[v]) dfsCollect(adj, v, visited, scc);
}
```

---

### 23.8 🔴 Strongly Connected Components — Tarjan's (Single Pass)

**⚠️ Nuance:** Uses a stack and `onStack` flag. Node is SCC root when `low[u] == disc[u]`.

```java
int[] tarjanDisc, tarjanLow;
boolean[] onStack;
Deque<Integer> tarjanStack;
List<List<Integer>> tarjanSCCs;
int tarjanTimer = 0;

List<List<Integer>> tarjanSCC(int n, List<List<Integer>> adj) {
    tarjanDisc=new int[n]; tarjanLow=new int[n]; onStack=new boolean[n];
    Arrays.fill(tarjanDisc,-1);
    tarjanStack=new ArrayDeque<>(); tarjanSCCs=new ArrayList<>();
    for (int i=0;i<n;i++) if (tarjanDisc[i]==-1) dfsTarjan(adj,i);
    return tarjanSCCs;
}
void dfsTarjan(List<List<Integer>> adj, int u) {
    tarjanDisc[u]=tarjanLow[u]=tarjanTimer++;
    tarjanStack.push(u); onStack[u]=true;
    for (int v:adj.get(u)) {
        if (tarjanDisc[v]==-1) { dfsTarjan(adj,v); tarjanLow[u]=Math.min(tarjanLow[u],tarjanLow[v]); }
        else if (onStack[v]) tarjanLow[u]=Math.min(tarjanLow[u],tarjanDisc[v]);
    }
    if (tarjanLow[u]==tarjanDisc[u]) { // u is root of SCC
        List<Integer> scc=new ArrayList<>();
        while (true) { int v=tarjanStack.pop(); onStack[v]=false; scc.add(v); if (v==u) break; }
        tarjanSCCs.add(scc);
    }
}
```

---

### 23.9 🔴 Heavy-Light Decomposition (HLD)

**Identify:** "path query on tree" (sum/max/min from node u to v), "update single node, query path"

**Concept:** Decompose tree into chains. Each path u→v crosses O(log n) chains. Use segment tree on flattened array.

```java
class HLD {
    int n, timer = 0;
    int[] parent, depth, heavyChild, chainHead, pos, size;
    int[] segArray; // Values mapped to segment tree positions
    List<Integer>[] adj;

    @SuppressWarnings("unchecked")
    HLD(int n) {
        this.n=n; adj=new List[n];
        for (int i=0;i<n;i++) adj[i]=new ArrayList<>();
        parent=new int[n]; depth=new int[n]; heavyChild=new int[n];
        chainHead=new int[n]; pos=new int[n]; size=new int[n];
        Arrays.fill(heavyChild,-1);
    }

    // Step 1: Compute subtree sizes and heavy children
    int dfsSize(int u, int p, int d) {
        parent[u]=p; depth[u]=d; size[u]=1;
        int maxSize=0;
        for (int v:adj.get(u)) {
            if (v==p) continue;
            size[u]+=dfsSize(v,u,d+1);
            if (size[v]>maxSize) { maxSize=size[v]; heavyChild[u]=v; }
        }
        return size[u];
    }

    // Step 2: Assign positions in HLD order
    void dfsHLD(int u, int head) {
        chainHead[u]=head; pos[u]=timer++;
        if (heavyChild[u]!=-1) dfsHLD(heavyChild[u],head); // Continue chain
        for (int v:adj.get(u)) if (v!=parent[u] && v!=heavyChild[u]) dfsHLD(v,v); // New chain
    }

    void build(int root) { dfsSize(root,-1,0); dfsHLD(root,root); }

    // Query path u→v — O(log²n) with segment tree, O(log n) passes
    int queryPath(int u, int v, SegTree seg) {
        int result = 0;
        while (chainHead[u] != chainHead[v]) {
            if (depth[chainHead[u]] < depth[chainHead[v]]) { int t=u; u=v; v=t; } // u is deeper chain
            result += seg.query(0, 0, n-1, pos[chainHead[u]], pos[u]);
            u = parent[chainHead[u]]; // Move up to parent of chain head
        }
        if (depth[u] > depth[v]) { int t=u; u=v; v=t; }
        result += seg.query(0, 0, n-1, pos[u], pos[v]);
        return result;
    }
}
```

---

### 23.10 🔴 Centroid Decomposition

**Identify:** "count paths in tree with property X", "nearest node with property X from every node"

**Concept:** Find centroid (removing it splits tree into parts of ≤ n/2). Process all paths through centroid. Recurse on parts. Depth O(log n).

```java
int[] centroidSubtreeSize, centroidParent;
boolean[] removed;

int getSize(int u, int p, List<List<Integer>> adj) {
    centroidSubtreeSize[u]=1;
    for (int v:adj.get(u)) if (v!=p && !removed[v]) centroidSubtreeSize[u]+=getSize(v,u,adj);
    return centroidSubtreeSize[u];
}

int getCentroid(int u, int p, int treeSize, List<List<Integer>> adj) {
    for (int v:adj.get(u)) {
        if (v!=p && !removed[v] && centroidSubtreeSize[v]>treeSize/2) return getCentroid(v,u,treeSize,adj);
    }
    return u;
}

void decompose(int u, int p, List<List<Integer>> adj) {
    int sz=getSize(u,-1,adj);
    int centroid=getCentroid(u,-1,sz,adj);
    centroidParent[centroid]=p;
    removed[centroid]=true;
    // Process all paths through centroid here
    for (int v:adj.get(centroid)) if (!removed[v]) decompose(v,centroid,adj);
}
```

---

### 23.11 🔴 Minimum Spanning Tree — Kruskal's

```java
int kruskalMST(int n, int[][] edges) {
    Arrays.sort(edges, (a,b)->a[2]-b[2]);
    DSU dsu=new DSU(n); int totalCost=0;
    for (int[] e:edges) if (dsu.union(e[0],e[1])) totalCost+=e[2];
    return totalCost;
}
```

---

## 24. Java Concurrency

### 🧠 Identify this pattern when you see:
- "thread-safe data structure"
- "CAS, atomic operations, lock-free"
- "producer-consumer"
- "thread pool, executor"

---

### 24.1 🟢 `synchronized` vs `ReentrantLock`

```java
// synchronized — simpler, built-in, auto-release
class Counter {
    private int count = 0;
    public synchronized void increment() { count++; }
    public synchronized int get() { return count; }
}

// ReentrantLock — explicit, more flexible
class FlexCounter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    public void increment() { lock.lock(); try { count++; } finally { lock.unlock(); } }
    public boolean tryIncrement() { // Non-blocking attempt
        if (lock.tryLock()) { try { count++; return true; } finally { lock.unlock(); } }
        return false;
    }
}
// ReentrantLock advantages:
//   - tryLock() with timeout
//   - lockInterruptibly() — can interrupt while waiting
//   - Multiple Condition variables (lock.newCondition())
//   - Fair locking (ReentrantLock(true))
```

---

### 24.2 🟡 `AtomicInteger` and CAS (Compare-And-Swap)

**CAS:** Atomic instruction — update value only if it currently equals expected value. No lock needed.

```java
AtomicInteger counter = new AtomicInteger(0);

// Lock-free increment
counter.incrementAndGet();         // ++counter (atomic)
counter.getAndIncrement();         // counter++ (atomic)
counter.addAndGet(5);              // counter += 5 (atomic)

// CAS — set to newVal only if current value == expected
boolean updated = counter.compareAndSet(5, 10); // If count==5, set to 10

// Lock-free stack (Treiber Stack)
class LockFreeStack<T> {
    private final AtomicReference<Node<T>> top = new AtomicReference<>();

    void push(T val) {
        Node<T> node = new Node<>(val);
        Node<T> curTop;
        do { curTop = top.get(); node.next = curTop; }
        while (!top.compareAndSet(curTop, node)); // Retry if top changed
    }

    T pop() {
        Node<T> curTop;
        do { curTop = top.get(); if (curTop == null) return null; }
        while (!top.compareAndSet(curTop, curTop.next));
        return curTop.val;
    }

    static class Node<T> { T val; Node<T> next; Node(T v) { val=v; } }
}
```

---

### 24.3 🟡 `ConcurrentHashMap` Internals

```java
// Thread-safe map — better than Collections.synchronizedMap
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Atomic operations:
map.putIfAbsent("key", 1);
map.computeIfAbsent("key", k -> expensiveCompute(k));
map.merge("key", 1, Integer::sum);     // Atomic increment
map.compute("key", (k, v) -> v==null ? 1 : v+1); // Atomic read-modify-write

// Java 8+: Segmented locking → 16 segments by default → 16x concurrency vs Hashtable
// Java 8+ implementation: CAS for simple updates, synchronized on bucket head for complex
// DO NOT: iterate and modify — use forEach with parallelism threshold
map.forEach(1, (k, v) -> process(k, v)); // Parallel if size > threshold
```

---

### 24.4 🟡 `BlockingQueue` (Producer-Consumer)

```java
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(100); // Bounded queue

// Producer
void producer() throws InterruptedException {
    queue.put(new Task());      // Blocks if full
    queue.offer(new Task(), 100, TimeUnit.MILLISECONDS); // Times out if full
}

// Consumer
void consumer() throws InterruptedException {
    Task t = queue.take();      // Blocks if empty
    Task t2 = queue.poll(100, TimeUnit.MILLISECONDS); // Times out if empty
}

// BlockingQueue implementations:
// LinkedBlockingQueue:  Unbounded (or bounded), linked nodes, separate locks for head/tail
// ArrayBlockingQueue:   Bounded, array-based, single lock — higher contention
// PriorityBlockingQueue: Unbounded, ordered, no blocking on put
// SynchronousQueue:     No storage — direct handoff, put blocks until take
// DelayQueue:           Elements delivered only after delay expires (scheduling)
```

---

### 24.5 🔴 Thread-Safe LRU Cache

```java
class ThreadSafeLRU<K, V> {
    private final int capacity;
    private final Map<K, V> cache;

    ThreadSafeLRU(int capacity) {
        this.capacity = capacity;
        // LinkedHashMap with access-order=true evicts LRU automatically
        this.cache = Collections.synchronizedMap(
            new LinkedHashMap<K, V>(capacity, 0.75f, true) { // true = access order
                protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                    return size() > capacity;
                }
            }
        );
    }

    V get(K key) { return cache.get(key); }
    void put(K key, V value) { cache.put(key, value); }

    // For high concurrency: use ReadWriteLock
    // Or ConcurrentLinkedHashMap (external library) / Caffeine cache
}

// High-performance version with ReadWriteLock
class HighPerfLRU<K, V> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final LinkedHashMap<K, V> map;
    HighPerfLRU(int cap) { map = new LinkedHashMap<>(cap, 0.75f, true) { protected boolean removeEldestEntry(Map.Entry<K,V> e) { return size()>cap; } }; }

    V get(K key) { lock.readLock().lock(); try { return map.get(key); } finally { lock.readLock().unlock(); } }
    void put(K key, V val) { lock.writeLock().lock(); try { map.put(key, val); } finally { lock.writeLock().unlock(); } }
}
```

---

### 24.6 🟡 `ExecutorService` and Thread Pools

```java
// Fixed thread pool — bounded concurrency
ExecutorService pool = Executors.newFixedThreadPool(4);

// Submit tasks
Future<Integer> future = pool.submit(() -> compute());
pool.execute(() -> fireAndForget());

// Get result (blocks until done)
int result = future.get(5, TimeUnit.SECONDS); // Timeout version

// Graceful shutdown
pool.shutdown();                        // No new tasks; finish existing
pool.awaitTermination(10, TimeUnit.SECONDS);
pool.shutdownNow();                     // Interrupt running tasks

// CompletableFuture — non-blocking async chains
CompletableFuture.supplyAsync(() -> fetchData())
    .thenApply(data -> process(data))
    .thenAccept(result -> save(result))
    .exceptionally(ex -> { handleError(ex); return null; });

// Thread pool types:
// newFixedThreadPool(n):     n threads, unbounded queue — risk of queue overflow
// newCachedThreadPool():     Unlimited threads, 60s keepalive — risk of too many threads
// newSingleThreadExecutor(): 1 thread, sequential execution guarantee
// newScheduledThreadPool(n): Scheduled/delayed execution
```

---

## 25. Interview Meta & Randomized Algorithms

### 🧠 Identify this pattern when you see:
- "random sample from stream without knowing size" → Reservoir Sampling
- "shuffle array uniformly" → Fisher-Yates
- "sort data larger than RAM" → External Sort
- "amortized O(1)" → Potential method analysis

---

### 25.1 🟡 Reservoir Sampling

**Identify:** "randomly sample k items from a stream of unknown size", "pick k from n uniformly at random"

**Key insight:** Item `i` is kept with probability `k/i`. Correct regardless of stream size.

```java
int[] reservoirSample(int[] stream, int k) {
    int[] reservoir = Arrays.copyOf(stream, k);
    Random rand = new Random();
    for (int i = k; i < stream.length; i++) {
        int j = rand.nextInt(i + 1); // Random index [0, i]
        if (j < k) reservoir[j] = stream[i]; // Replace with probability k/(i+1)
    }
    return reservoir;
}

// Proof: Item i (i >= k) is in final sample with probability k/n
// Item i is selected: k/(i+1)
// Item i is NOT replaced by subsequent items j (j > i): product of (1 - k/(j+1)) * (k/i+1)
// Works out to k/n exactly

// Stream version (when you see items one by one)
class StreamSampler {
    int[] reservoir; Random rand = new Random(); int count = 0;
    StreamSampler(int k) { reservoir = new int[k]; }
    void add(int item) {
        count++;
        if (count <= reservoir.length) reservoir[count - 1] = item;
        else {
            int j = rand.nextInt(count);
            if (j < reservoir.length) reservoir[j] = item;
        }
    }
    int[] getSample() { return reservoir; }
}
```

---

### 25.2 🟡 Fisher-Yates Shuffle

**Identify:** "shuffle array uniformly at random (unbiased)", "random permutation"

**⚠️ Nuance:** Naive shuffle (`nums[i] = random index`) has birthday-problem bias. Fisher-Yates is provably uniform.

```java
void shuffle(int[] nums) {
    Random rand = new Random();
    for (int i = nums.length - 1; i > 0; i--) {
        int j = rand.nextInt(i + 1); // Random [0, i] inclusive
        int t = nums[i]; nums[i] = nums[j]; nums[j] = t;
    }
}
// Each of n! permutations is equally likely
// Wrong way (biased): rand.nextInt(nums.length) — can swap same position multiple times
```

---

### 25.3 🟡 External Sorting (Data > RAM)

**Identify:** "sort file larger than memory", "merge sorted chunks"

**Steps:**
1. Read chunks that fit in RAM, sort each (quicksort), write to temp files.
2. K-way merge all sorted temp files using a min-heap.
3. Result: single sorted output file.

```java
// Step 2: K-way merge of K sorted files
void kWayMerge(List<int[]> sortedChunks) {
    // PQ entry: {value, chunkIndex}
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    // Initialize with first element of each chunk
    for (int i = 0; i < sortedChunks.size(); i++)
        pq.offer(new int[]{sortedChunks.get(i)[0], i, 0});

    List<Integer> result = new ArrayList<>();
    while (!pq.isEmpty()) {
        int[] top = pq.poll();
        result.add(top[0]);
        int chunkIdx = top[1], pos = top[2] + 1;
        if (pos < sortedChunks.get(chunkIdx).length)
            pq.offer(new int[]{sortedChunks.get(chunkIdx)[pos], chunkIdx, pos});
    }
}
// Complexity: O(n log k) where k = number of chunks
// Used by: database external sort, MapReduce sort phase, disk-based B-Trees
```

---

### 25.4 🟡 Randomized Algorithms — Las Vegas vs Monte Carlo

| Type | Result | Time | Example |
|------|--------|------|---------|
| **Las Vegas** | Always correct | Expected fast | QuickSort (random pivot), Randomized QuickSelect |
| **Monte Carlo** | Correct with high probability | Always fast | Miller-Rabin primality, Bloom filter |

```java
// Las Vegas: Randomized QuickSelect — always correct, O(n) expected
// Monte Carlo: Miller-Rabin primality — O(k log²n), error prob 4^-k

// Miller-Rabin primality test
boolean millerRabin(long n, int iterations) {
    if (n < 2) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0) return false;
    // Write n-1 as 2^r * d
    long d = n - 1; int r = 0;
    while (d % 2 == 0) { d /= 2; r++; }
    Random rand = new Random();
    for (int i = 0; i < iterations; i++) {
        long a = 2 + (long)(rand.nextDouble() * (n - 4));
        long x = modPow(a, d, n);
        if (x == 1 || x == n - 1) continue;
        boolean composite = true;
        for (int j = 0; j < r - 1; j++) {
            x = mulMod(x, x, n);
            if (x == n - 1) { composite = false; break; }
        }
        if (composite) return false; // Definitely composite
    }
    return true; // Probably prime
}
long modPow(long base, long exp, long mod) {
    long result=1; base%=mod;
    while (exp>0) { if((exp&1)==1) result=mulMod(result,base,mod); base=mulMod(base,base,mod); exp>>=1; }
    return result;
}
long mulMod(long a, long b, long mod) { return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue(); }
```

---

### 25.5 🟡 Amortized Analysis

**Identify:** "prove that a sequence of operations is fast on average even if individual operations are slow"

**Methods:**

**Aggregate Method:** Total cost of n operations / n = amortized cost per operation.
```
Dynamic Array push: occasional resize costs O(n) but amortized O(1)
  - n pushes total: O(1) + O(1) + ... + O(n/2) (resize) + ... + O(n) (resize) = O(n) total → O(1) amortized
```

**Accounting Method:** Assign "credits" to operations. Charge extra for cheap ops; use credits for expensive ops.
```
Stack push: charge 2 tokens (1 for push, 1 saved for future pop)
Stack pop:  use saved token (O(1) actual, 0 amortized)
```

**Potential Method:** Define potential function Φ. Amortized cost = actual cost + ΔΦ.
```
Dynamic array: Φ = 2*(size) - capacity
Push (no resize): actual=1, ΔΦ=2 → amortized=3=O(1)
Push (resize):    actual=n, ΔΦ=-n+2 → amortized=2=O(1)
```

**Common amortized O(1) structures:**
- Dynamic array (ArrayList) append
- Stack operations in histogram problems
- DSU find with path compression
- Splay tree operations

---

### 25.6 🟢 Java Standard Library Quick Reference

```java
// Collections
Collections.sort(list);                    // Stable sort O(n log n)
Collections.sort(list, comparator);
Collections.reverse(list);
Collections.frequency(list, elem);
Collections.unmodifiableList(list);

// Arrays
Arrays.sort(arr);                          // Dual-pivot quicksort for primitives
Arrays.sort(arr, comparator);              // TimSort for objects (stable)
Arrays.fill(arr, value);
Arrays.copyOf(arr, newLen);
Arrays.copyOfRange(arr, from, to);         // [from, to) exclusive
Arrays.binarySearch(arr, key);             // Returns index or -(insertion_point)-1

// String
s.charAt(i); s.substring(l, r);           // [l, r) exclusive
s.toCharArray(); String.valueOf(charArr);
s.split(","); s.trim(); s.toLowerCase();
s.indexOf("x"); s.contains("x"); s.replace("a","b");
String.join(",", list);
new StringBuilder(s).reverse().toString(); // Reverse string

// Math
Math.max(a,b); Math.min(a,b); Math.abs(n);
Math.pow(base, exp); Math.sqrt(n); Math.log(n);
Integer.MAX_VALUE; Integer.MIN_VALUE; Long.MAX_VALUE;
Integer.bitCount(n); Integer.numberOfTrailingZeros(n);
Integer.numberOfLeadingZeros(n); Integer.reverse(n);
Integer.toBinaryString(n); Integer.parseInt("101", 2); // Binary string to int

// Deque (preferred over Stack class)
Deque<Integer> stack = new ArrayDeque<>();   // push/pop/peek — LIFO
Deque<Integer> queue = new ArrayDeque<>();   // offer/poll/peek — FIFO

// Priority Queue
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> a[0]-b[0]);

// Map patterns
map.getOrDefault(key, 0);
map.merge(key, 1, Integer::sum);
map.computeIfAbsent(key, k -> new ArrayList<>());
map.putIfAbsent(key, value);
map.entrySet().stream().sorted(Map.Entry.comparingByValue());

// TreeMap (sorted map)
TreeMap<Integer,Integer> tm = new TreeMap<>();
tm.floorKey(k);   // Greatest key <= k
tm.ceilingKey(k); // Least key >= k
tm.firstKey(); tm.lastKey();
```

---

## 26. Master Cheat Sheet

### 🔑 Problem → Technique

| Problem Signal | Technique |
|---------------|-----------|
| Subarray sum / prefix queries | Prefix Sum + HashMap |
| Range update, point query | Difference Array |
| Range min/max (static) | Sparse Table O(1) |
| Range update + range query | Lazy Seg Tree |
| Subarray with condition (longest/shortest) | Sliding Window |
| Max contiguous subarray | Kadane's |
| Sorted + pair/triple | Two Pointers |
| Find in sorted / binary decision | Binary Search |
| Generate all combinations/permutations | Backtracking |
| Min/Max cost, count ways (overlapping) | Dynamic Programming |
| Count integers in [1,N] with property | Digit DP |
| Fibonacci-like recurrence for large N | Matrix Exponentiation |
| Cycle in linked list | Floyd's Tortoise & Hare |
| Next greater/smaller element | Monotonic Stack |
| O(1) max in sliding window | Monotonic Deque |
| K largest/smallest, top-K, median stream | Heap (PQ) |
| Connected components, islands | BFS/DFS |
| Dependencies, task ordering | Topological Sort |
| Graph with 0/1 edge weights | 0/1 BFS (Deque) |
| Heuristic grid pathfinding | A* Search |
| Traverse all edges once | Eulerian Path (Hierholzer) |
| Max flow, bipartite matching | Dinic's Algorithm |
| Strongly connected components | Tarjan's or Kosaraju's |
| Bridge / cut edge | Tarjan's Bridge |
| Articulation point / cut vertex | Tarjan's AP |
| Prefix search, autocomplete | Trie |
| Multi-pattern search in text | Aho-Corasick |
| Pattern matching (single) | KMP or Z-Algorithm |
| String period / z-function | Z-Algorithm |
| Longest palindrome O(n) | Manacher's |
| Distinct substrings, repeated substrings | Suffix Array + LCP |
| Dynamic connectivity, union check | DSU (Union-Find) |
| Path queries on tree | Heavy-Light Decomposition |
| Count paths in tree | Centroid Decomposition |
| Subtree queries | Euler Tour + BIT |
| O(1) LCA | Euler Tour + Sparse Table |
| XOR, single number, bitmask subsets | Bit Manipulation |
| Interval scheduling | Greedy (sort by end time) |
| Items can be split | Fractional Knapsack (Greedy) |
| Random sample from stream | Reservoir Sampling |
| Shuffle uniformly | Fisher-Yates |
| Approximate membership | Bloom Filter |
| Frequency estimation in streams | Count-Min Sketch |
| Sorted structure with O(log n) ops | Treap / Skip List |
| Thread-safe counter, lock-free | AtomicInteger + CAS |
| Producer-consumer pipeline | BlockingQueue |

---

### ⚡ Complexity Quick Reference

| Algorithm | Time | Space |
|-----------|------|-------|
| Binary Search | O(log n) | O(1) |
| BFS / DFS | O(V + E) | O(V) |
| 0/1 BFS | O(V + E) | O(V) |
| Dijkstra (heap) | O((V+E) log V) | O(V) |
| Bellman-Ford | O(VE) | O(V) |
| Floyd-Warshall | O(V³) | O(V²) |
| Johnson's | O(V²logV + VE) | O(V²) |
| Dinic's Max Flow | O(V²E) | O(V+E) |
| Kosaraju's SCC | O(V+E) | O(V) |
| Tarjan's SCC | O(V+E) | O(V) |
| Tarjan's Bridges | O(V+E) | O(V) |
| HLD path query | O(log²n) | O(n) |
| Centroid decomp | O(n log²n) | O(n) |
| Kruskal MST | O(E log E) | O(V) |
| QuickSelect | O(n) avg | O(1) |
| KMP / Z-Algorithm | O(n + m) | O(n) |
| Aho-Corasick build | O(sum pattern lengths) | — |
| Aho-Corasick search | O(n + matches) | — |
| Suffix Array | O(n log n) | O(n) |
| Manacher's | O(n) | O(n) |
| Segment Tree | O(log n) per op | O(n) |
| Lazy Seg Tree | O(log n) per op | O(n) |
| Sparse Table build | O(n log n) | O(n log n) |
| Sparse Table query | O(1) | — |
| BIT | O(log n) per op | O(n) |
| DSU find/union | O(α(n)) ≈ O(1) | O(n) |
| Trie insert/search | O(m) | O(total chars) |
| Heap push/pop | O(log n) | O(n) |
| Reservoir Sampling | O(n) | O(k) |
| Fisher-Yates | O(n) | O(1) |
| Matrix Exponentiation | O(k³ log n) | O(k²) |
| Karatsuba | O(n^1.585) | O(n) |
| FFT / NTT | O(n log n) | O(n) |
| Skip List | O(log n) expected | O(n) |
| Treap | O(log n) expected | O(n) |

---

### 🔥 Common Edge Cases to Always Check

```
Arrays:     empty [], single element, all duplicates, all negative, all same
Strings:    empty "", single char, all same chars, uppercase vs lowercase
Linked List: null head, single node, cycle, even vs odd length
Trees:      null root, single node, skewed (all left/right), only root
Graphs:     disconnected, self-loops, parallel edges, negative weights
Integers:   Integer.MIN_VALUE, Integer.MAX_VALUE, overflow on multiply
Binary Search: lo > hi, mid overflow (use lo+(hi-lo)/2), infinite loop (lo<hi vs lo<=hi)
Two Pointers: l == r boundary, skip duplicates
Backtracking: already-visited state, duplicate elements (sort + skip)
DP:         base cases (i=0, j=0), index off-by-one, need modulo?
Bit Manipulation: sign bit (use >>> not >>), shift by 32 wraps around, ~0 = -1
CAS/Concurrency: ABA problem, spurious wakeups (always use while not if with wait())
```

---

### 📐 DP Decision Tree

```
Can problem be divided into smaller identical sub-problems?
├── No → Try Greedy or Two Pointers
└── Yes → Do sub-problems overlap?
    ├── No → Divide & Conquer (merge sort, closest pair)
    └── Yes → Dynamic Programming
        ├── 1D state? → Linear array (coin change, LIS, climbing stairs)
        ├── 2D state?
        │   ├── Same string/array [i][j] → Interval DP (palindrome, balloon burst)
        │   ├── Two sequences [i][j] → LCS, edit distance
        │   └── Item + capacity → Knapsack
        ├── Count integers up to N? → Digit DP
        ├── Recurrence computable as matrix? → Matrix Exponentiation
        └── State is subset? → Bitmask DP (TSP, assignment)
```

---

### 🗺️ Graph Problem Decision Tree

```
Weighted graph?
├── No → BFS (shortest), DFS (connectivity, SCC, toposort)
└── Yes → Negative weights?
    ├── No → Dijkstra (single source), Kruskal/Prim (MST), Johnson's (all-pairs sparse)
    └── Yes → Bellman-Ford (single source, detect neg cycle)
               Floyd-Warshall (all-pairs, dense)

Tree queries?
├── Path queries (sum/max u→v) → HLD + Segment Tree
├── Subtree queries → Euler Tour + BIT
├── LCA in O(1) → Euler Tour + Sparse Table
└── Count paths with property → Centroid Decomposition

Special graph?
├── DAG → Topological sort + DP
├── Bipartite → 2-coloring BFS; matching → Dinic's
└── Max flow → Dinic's
```

---

*DSA Complete Playbook — Java (v1.1). Last updated: May 2026.*
*Covers LeetCode Easy–Hard, competitive programming (Codeforces Div 1-2), FAANG system design DS, and Java concurrency interviews.*
