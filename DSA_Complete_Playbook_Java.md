# DSA Complete Interview Playbook — Java

> **How to use:** Each topic follows: *Identify (keywords/patterns) → Nuance/Gotcha → Steps → Code.*
> Before any interview, scan the **Quick-Reference Index** first, then drill the sections you are shaky on.
> Difficulty: 🟢 Easy → 🟡 Medium → 🔴 Hard within each group.

---

## 📋 Quick-Reference Index

| # | Topic | Key Signal Words |
|---|-------|-----------------|
| 1 | [Arrays & Strings](#1-arrays--strings) | subarray, prefix, rotation, anagram |
| 2 | [Two Pointers](#2-two-pointers) | sorted, pair sum, palindrome, remove duplicates |
| 3 | [Sliding Window](#3-sliding-window) | subarray/substring of length k, longest, shortest, at most k |
| 4 | [Binary Search](#4-binary-search) | sorted, rotated, find minimum/maximum, first/last position |
| 5 | [Recursion & Backtracking](#5-recursion--backtracking) | all combinations, permutations, subsets, generate all |
| 6 | [Dynamic Programming](#6-dynamic-programming) | optimal, count ways, min/max cost, overlapping subproblems |
| 7 | [Linked Lists](#7-linked-lists) | reverse, cycle, nth from end, merge, middle |
| 8 | [Stacks & Queues](#8-stacks--queues) | next greater, balanced brackets, monotonic, BFS |
| 9 | [Trees & Binary Trees](#9-trees--binary-trees) | path sum, lowest ancestor, diameter, level order |
| 10 | [Binary Search Trees](#10-binary-search-trees) | kth smallest, validate BST, floor/ceil |
| 11 | [Heaps / Priority Queues](#11-heaps--priority-queues) | kth largest, top-k, median, merge k lists |
| 12 | [Graphs](#12-graphs) | connected, shortest path, cycle, topological sort |
| 13 | [Tries](#13-tries) | prefix, search words, autocomplete, word board |
| 14 | [Sorting & Searching](#14-sorting--searching) | sort by custom criteria, k-th element, order statistics |
| 15 | [Greedy](#15-greedy) | minimum steps, scheduling, intervals, locally optimal |
| 16 | [Divide & Conquer](#16-divide--conquer) | split, merge, count inversions, large power |
| 17 | [Bit Manipulation](#17-bit-manipulation) | XOR, single number, power of 2, subset mask |
| 18 | [Math & Number Theory](#18-math--number-theory) | prime, GCD, modular, combinatorics, digits |
| 19 | [Monotonic Stack / Queue](#19-monotonic-stack--queue) | next greater/smaller, largest rectangle, sliding max |
| 20 | [Union-Find (DSU)](#20-union-find-dsu) | connected components, cycle detection, dynamic connectivity |
| 21 | [Segment Trees & BITs](#21-segment-trees--binary-indexed-trees) | range query, range update, point update |
| 22 | [String Algorithms](#22-string-algorithms) | pattern matching, KMP, rolling hash, Z-function |
| 23 | [Advanced Graph Algorithms](#23-advanced-graph-algorithms) | Dijkstra, Bellman-Ford, Floyd, MST, bridges |
| 24 | [Master Cheat Sheet](#24-master-cheat-sheet) | — |

---

## 1. Arrays & Strings

### 🧠 Identify this pattern when you see:
- "subarray", "contiguous", "prefix sum", "rotation"
- "anagram", "permutation of string", "window"
- "find element in O(1)" → HashMap
- "range sum queries" → prefix sum array

---

### 1.1 🟢 Prefix Sum

**Use:** Answer multiple range sum queries in O(1) after O(n) preprocessing.

**⚠️ Nuance:** `prefix[i]` = sum of `nums[0..i-1]` (1-indexed prefix is cleaner). Range `[l, r]` = `prefix[r+1] - prefix[l]`.

```java
// Build prefix sum
int[] buildPrefix(int[] nums) {
    int n = nums.length;
    int[] prefix = new int[n + 1]; // prefix[0] = 0
    for (int i = 0; i < n; i++) {
        prefix[i + 1] = prefix[i] + nums[i];
    }
    return prefix;
}

// Range sum [l, r] inclusive, 0-indexed
int rangeSum(int[] prefix, int l, int r) {
    return prefix[r + 1] - prefix[l];
}
```

**2D Prefix Sum (Matrix):**
```java
int[][] buildPrefix2D(int[][] mat) {
    int m = mat.length, n = mat[0].length;
    int[][] p = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            p[i][j] = mat[i-1][j-1] + p[i-1][j] + p[i][j-1] - p[i-1][j-1];
    return p;
}

// Sub-matrix sum: rows [r1,r2], cols [c1,c2] (0-indexed in original)
int subMatSum(int[][] p, int r1, int c1, int r2, int c2) {
    return p[r2+1][c2+1] - p[r1][c2+1] - p[r2+1][c1] + p[r1][c1];
}
```

---

### 1.2 🟢 Kadane's Algorithm (Maximum Subarray Sum)

**Identify:** "maximum sum contiguous subarray", "largest sum subarray"

**⚠️ Nuance:** Reset `current` to 0 when it goes negative — starting fresh is better. Initialize `maxSum` to `nums[0]` (not 0) to handle all-negative arrays.

```java
int maxSubarraySum(int[] nums) {
    int maxSum = nums[0], current = 0;
    for (int n : nums) {
        current = Math.max(n, current + n); // Extend or start fresh
        maxSum = Math.max(maxSum, current);
    }
    return maxSum;
}
// [-2,1,-3,4,-1,2,1,-5,4] → 6 ([4,-1,2,1])
```

**Variant — max product subarray:** Track both max and min (negatives flip sign).
```java
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

### 1.3 🟢 HashMap for O(1) Lookup

**Identify:** "two sum", "subarray with sum k", "count distinct", "frequency"

```java
// Two Sum — classic
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
    freq.put(0, 1); // Empty prefix
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

### 1.4 🟡 Array Rotation

**Identify:** "rotate array by k", "cyclic shift"

**Steps:** Reverse entire → Reverse first k → Reverse last n-k.

```java
void rotate(int[] nums, int k) {
    int n = nums.length;
    k %= n; // Handle k > n
    reverse(nums, 0, n - 1);
    reverse(nums, 0, k - 1);
    reverse(nums, k, n - 1);
}

void reverse(int[] nums, int l, int r) {
    while (l < r) { int t = nums[l]; nums[l++] = nums[r]; nums[r--] = t; }
}
```

---

### 1.5 🟡 Anagram / Frequency Matching

**Identify:** "anagram", "permutation in string", "rearrange characters"

**⚠️ Nuance:** Use `int[26]` for lowercase letters — faster than HashMap.

```java
boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] freq = new int[26];
    for (char c : s.toCharArray()) freq[c - 'a']++;
    for (char c : t.toCharArray()) freq[c - 'a']--;
    for (int f : freq) if (f != 0) return false;
    return true;
}
```

---

### 1.6 🟡 Dutch National Flag (3-Way Partition)

**Identify:** "sort array with 3 values (0,1,2)", "3-way partition"

```java
void sortColors(int[] nums) {
    int lo = 0, mid = 0, hi = nums.length - 1;
    while (mid <= hi) {
        if      (nums[mid] == 0) swap(nums, lo++, mid++);
        else if (nums[mid] == 1) mid++;
        else                     swap(nums, mid, hi--);
        // Don't increment mid after hi swap — re-examine nums[mid]
    }
}
```

---

### 1.7 🔴 Trapping Rain Water

**Identify:** "trap water", "elevation map", "bars of height"

**Approach:** For each index, water = `min(maxLeft, maxRight) - height[i]`. Use two-pointer O(n)/O(1).

```java
int trap(int[] height) {
    int lo = 0, hi = height.length - 1;
    int maxLeft = 0, maxRight = 0, water = 0;
    while (lo <= hi) {
        if (height[lo] <= height[hi]) {
            if (height[lo] >= maxLeft) maxLeft = height[lo];
            else water += maxLeft - height[lo];
            lo++;
        } else {
            if (height[hi] >= maxRight) maxRight = height[hi];
            else water += maxRight - height[hi];
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
- "pair with sum", "three sum", "four sum"
- "palindrome check", "remove duplicates"
- "squeeze from both ends"
- Two arrays being merged/compared

---

### 2.1 🟢 Opposite-End Pointers (Sorted Array)

**Use:** Find pair with target sum. Move left pointer right to increase sum, right pointer left to decrease.

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

### 2.2 🟡 Three Sum (All Unique Triplets)

**⚠️ Nuances:**
1. Sort first.
2. Fix `i`, use two pointers for `[i+1, n-1]`.
3. Skip duplicates at every level.

```java
List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue; // Skip dup i
        int l = i + 1, r = nums.length - 1;
        while (l < r) {
            int sum = nums[i] + nums[l] + nums[r];
            if (sum == 0) {
                result.add(Arrays.asList(nums[i], nums[l], nums[r]));
                while (l < r && nums[l] == nums[l + 1]) l++; // Skip dup l
                while (l < r && nums[r] == nums[r - 1]) r--; // Skip dup r
                l++; r--;
            } else if (sum < 0) l++;
            else r--;
        }
    }
    return result;
}
```

---

### 2.3 🟢 Palindrome Check (Two Pointers)

```java
boolean isPalindrome(String s) {
    // Clean string to only alphanumerics, lowercase
    int l = 0, r = s.length() - 1;
    while (l < r) {
        while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
        while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
        if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) return false;
        l++; r--;
    }
    return true;
}
```

---

### 2.4 🟡 Remove Duplicates from Sorted Array (In-Place)

**⚠️ Nuance:** `slow` pointer tracks where the next unique element goes.

```java
int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    int slow = 1;
    for (int fast = 1; fast < nums.length; fast++) {
        if (nums[fast] != nums[fast - 1]) {
            nums[slow++] = nums[fast];
        }
    }
    return slow;
}
```

---

### 2.5 🟡 Container with Most Water

**Identify:** "max area between two lines/bars", "most water"

**Greedy insight:** Always move the shorter bar inward — the taller bar can only do better.

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
- "**longest** subarray with condition" → Expanding window
- "**shortest** subarray with condition" → Shrinking window
- "**at most k** distinct", "**exactly k**" → Variable window
- Condition maintains a single monotonic constraint

---

### 3.1 🟢 Fixed-Size Window (max/min/avg of size k)

```java
double maxAvgSubarray(int[] nums, int k) {
    double sum = 0, maxSum = 0;
    for (int i = 0; i < k; i++) sum += nums[i];
    maxSum = sum;
    for (int i = k; i < nums.length; i++) {
        sum += nums[i] - nums[i - k]; // Add new, remove old
        maxSum = Math.max(maxSum, sum);
    }
    return maxSum / k;
}
```

---

### 3.2 🟡 Longest Substring Without Repeating Characters

**⚠️ Nuance:** Use a HashMap to store the **last seen index** of each char. Jump `left` to `map.get(c) + 1` when a repeat is found — don't just `left++`.

```java
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> map = new HashMap<>();
    int maxLen = 0;
    for (int left = 0, right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (map.containsKey(c)) {
            left = Math.max(left, map.get(c) + 1); // Jump left past repeat
        }
        map.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

---

### 3.3 🟡 Minimum Window Substring

**Identify:** "minimum window containing all characters", "smallest substring with all chars"

**Steps:**
1. Frequency map of `t`.
2. Expand `right`; when a needed char is found, decrement `formed`.
3. When `formed == 0` (all chars covered), try shrinking `left`.

```java
String minWindow(String s, String t) {
    int[] need = new int[128];
    for (char c : t.toCharArray()) need[c]++;
    int left = 0, minLen = Integer.MAX_VALUE, minStart = 0;
    int formed = t.length(); // chars still needed

    for (int right = 0; right < s.length(); right++) {
        if (need[s.charAt(right)]-- > 0) formed--; // Satisfy a need
        while (formed == 0) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1;
                minStart = left;
            }
            if (++need[s.charAt(left++)] > 0) formed++; // Un-satisfy a need
        }
    }
    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
```

---

### 3.4 🟡 Longest Subarray with At Most K Distinct Characters

```java
int atMostKDistinct(String s, int k) {
    int[] freq = new int[128];
    int distinct = 0, left = 0, maxLen = 0;
    for (int right = 0; right < s.length(); right++) {
        if (freq[s.charAt(right)]++ == 0) distinct++;
        while (distinct > k) {
            if (--freq[s.charAt(left++)] == 0) distinct--;
        }
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

---

### 3.5 🟡 Longest Subarray with Sum ≤ K (Positive Numbers Only)

**⚠️ Nuance:** Works only for positive numbers. For negative numbers, use prefix sum + deque.

```java
int longestSubarraySum(int[] nums, int k) {
    int left = 0, sum = 0, maxLen = 0;
    for (int right = 0; right < nums.length; right++) {
        sum += nums[right];
        while (sum > k) sum -= nums[left++];
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

---

### 3.6 🟡 Exactly K → At Most K Trick

**Identify:** "exactly k distinct", "subarrays with exactly k odd numbers"

**Formula:** `exactly(k) = atMost(k) - atMost(k-1)`

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
        count += right - left + 1; // All subarrays ending at right
    }
    return count;
}
```

---

## 4. Binary Search

### 🧠 Identify this pattern when you see:
- "sorted array", "rotated sorted array"
- "find first/last position", "first true condition"
- "search in range [lo, hi]", "minimize maximum", "maximize minimum"
- Answer is **monotonic** — if X works, anything larger/smaller also works

---

### 4.1 🟢 Classic Binary Search Template

**⚠️ Nuances:**
- Use `mid = lo + (hi - lo) / 2` to avoid integer overflow.
- Three variants: find exact, find leftmost, find rightmost.

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

// Find leftmost (first occurrence)
int searchLeft(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) { result = mid; hi = mid - 1; } // Keep going left
        else if (nums[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return result;
}

// Find rightmost (last occurrence)
int searchRight(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) { result = mid; lo = mid + 1; } // Keep going right
        else if (nums[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return result;
}
```

---

### 4.2 🟡 Binary Search on Answer ("Predicate" Pattern)

**Identify:** "minimize the maximum", "maximize the minimum", "minimum days to...", "split array largest sum"

**Template:** The answer satisfies a monotonic predicate. Search on the **answer space**, not the array.

```java
// Generic template
int bsOnAnswer(int lo, int hi) { // lo/hi = answer range
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (feasible(mid)) hi = mid;    // mid works, try smaller
        else               lo = mid + 1; // mid doesn't work, need more
    }
    return lo; // Smallest feasible answer
}
```

**Example — Split Array Largest Sum:**
```java
int splitArray(int[] nums, int k) {
    int lo = Arrays.stream(nums).max().getAsInt(); // min possible
    int hi = Arrays.stream(nums).sum();            // max possible
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (canSplit(nums, k, mid)) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}

boolean canSplit(int[] nums, int k, int limit) {
    int parts = 1, sum = 0;
    for (int n : nums) {
        if (sum + n > limit) { parts++; sum = 0; }
        sum += n;
    }
    return parts <= k;
}
```

---

### 4.3 🟡 Search in Rotated Sorted Array

**⚠️ Nuance:** One half is always sorted. Determine which half, then decide where to search.

```java
int searchRotated(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[lo] <= nums[mid]) { // Left half is sorted
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else { // Right half is sorted
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
```

---

### 4.4 🟡 Find Minimum in Rotated Sorted Array

```java
int findMin(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[hi]) lo = mid + 1; // Min is in right half
        else                      hi = mid;      // Mid could be min
    }
    return nums[lo];
}
```

---

### 4.5 🟡 Kth Smallest in Sorted Matrix

**Approach:** Binary search on value range; count elements ≤ mid row by row.

```java
int kthSmallest(int[][] matrix, int k) {
    int n = matrix.length;
    int lo = matrix[0][0], hi = matrix[n-1][n-1];
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        int count = countLessEqual(matrix, mid, n);
        if (count < k) lo = mid + 1;
        else           hi = mid;
    }
    return lo;
}

int countLessEqual(int[][] matrix, int mid, int n) {
    int count = 0, row = n - 1, col = 0;
    while (row >= 0 && col < n) {
        if (matrix[row][col] <= mid) { count += row + 1; col++; }
        else row--;
    }
    return count;
}
```

---

## 5. Recursion & Backtracking

### 🧠 Identify this pattern when you see:
- "generate **all** combinations / permutations / subsets"
- "find **all** paths", "all possible words"
- "solve N-Queens, Sudoku"
- "can we place / choose / assign?"
- Constraint satisfaction problems

---

### 5.1 🟢 Subsets (Power Set)

**⚠️ Nuance:** At each element, two choices: include or exclude.

```java
List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current)); // Add at every node (not just leaves)
    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);
        backtrack(nums, i + 1, current, result); // i+1: move forward, no reuse
        current.remove(current.size() - 1);      // Undo choice
    }
}
```

---

### 5.2 🟡 Combinations (Choose k from n)

```java
List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> result = new ArrayList<>();
    backtrackCombine(1, n, k, new ArrayList<>(), result);
    return result;
}

void backtrackCombine(int start, int n, int k, List<Integer> curr, List<List<Integer>> res) {
    if (curr.size() == k) { res.add(new ArrayList<>(curr)); return; }
    // Pruning: need (k - curr.size()) more items, only go up to n-(k-curr.size())+1
    for (int i = start; i <= n - (k - curr.size()) + 1; i++) {
        curr.add(i);
        backtrackCombine(i + 1, n, k, curr, res);
        curr.remove(curr.size() - 1);
    }
}
```

---

### 5.3 🟡 Permutations

**⚠️ Nuance:** All elements must be used exactly once. Use a `boolean[] used` or swap in-place.

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
        used[i] = true;
        curr.add(nums[i]);
        backtrackPerm(nums, used, curr, res);
        curr.remove(curr.size() - 1);
        used[i] = false;
    }
}
```

**Permutations with Duplicates:** Sort first; skip `nums[i] == nums[i-1] && !used[i-1]`.

---

### 5.4 🟡 Combination Sum (Unlimited Reuse)

**⚠️ Nuance:** Pass `i` (not `i+1`) to allow reuse of same element.

```java
List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<>();
    Arrays.sort(candidates);
    backtrackCombSum(candidates, target, 0, new ArrayList<>(), result);
    return result;
}

void backtrackCombSum(int[] nums, int rem, int start, List<Integer> curr, List<List<Integer>> res) {
    if (rem == 0) { res.add(new ArrayList<>(curr)); return; }
    for (int i = start; i < nums.length; i++) {
        if (nums[i] > rem) break; // Pruning (sorted)
        curr.add(nums[i]);
        backtrackCombSum(nums, rem - nums[i], i, curr, res); // i, not i+1
        curr.remove(curr.size() - 1);
    }
}
```

---

### 5.5 🔴 N-Queens

**Identify:** "place N queens on NxN board, no two attack"

**Trick:** Track `cols`, `diag1` (row-col), `diag2` (row+col) sets for O(1) conflict check.

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
        if (cols[col] || d1[row - col + n] || d2[row + col]) continue;
        queens[row] = col;
        cols[col] = d1[row - col + n] = d2[row + col] = true;
        solveNQ(row + 1, n, queens, cols, d1, d2, res);
        cols[col] = d1[row - col + n] = d2[row + col] = false;
    }
}

List<String> buildBoard(int[] queens, int n) {
    List<String> board = new ArrayList<>();
    for (int row = 0; row < n; row++) {
        char[] line = new char[n];
        Arrays.fill(line, '.');
        line[queens[row]] = 'Q';
        board.add(new String(line));
    }
    return board;
}
```

---

### 5.6 🔴 Word Search in Grid

```java
boolean exist(char[][] board, String word) {
    int m = board.length, n = board[0].length;
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            if (dfsWord(board, word, i, j, 0, m, n)) return true;
    return false;
}

boolean dfsWord(char[][] board, String word, int i, int j, int k, int m, int n) {
    if (k == word.length()) return true;
    if (i < 0 || i >= m || j < 0 || j >= n || board[i][j] != word.charAt(k)) return false;
    char temp = board[i][j];
    board[i][j] = '#'; // Mark visited
    boolean found = dfsWord(board, word, i+1, j, k+1, m, n)
                 || dfsWord(board, word, i-1, j, k+1, m, n)
                 || dfsWord(board, word, i, j+1, k+1, m, n)
                 || dfsWord(board, word, i, j-1, k+1, m, n);
    board[i][j] = temp; // Restore
    return found;
}
```

---

## 6. Dynamic Programming

### 🧠 Identify this pattern when you see:
- "number of ways to...", "count paths/arrangements"
- "minimum cost / maximum profit"
- "can we achieve X?" → Boolean DP
- "overlapping subproblems" (same subproblem computed multiple times in recursion)
- Choices at each step with optimal substructure

**Framework:** Define state → Write recurrence → Choose top-down (memo) or bottom-up (tabulation).

---

### 6.1 🟢 Fibonacci / Climbing Stairs

**Identify:** "reach step n in k hops of size 1 or 2", "decode ways"

```java
int climbStairs(int n) {
    if (n <= 2) return n;
    int prev2 = 1, prev1 = 2;
    for (int i = 3; i <= n; i++) {
        int cur = prev1 + prev2;
        prev2 = prev1; prev1 = cur;
    }
    return prev1;
}
```

---

### 6.2 🟢 0/1 Knapsack

**Identify:** "pick items with weight/value, max capacity W, each item used once"

**State:** `dp[i][w]` = max value using first `i` items with capacity `w`.

```java
int knapsack(int[] weights, int[] values, int W) {
    int n = weights.length;
    int[][] dp = new int[n + 1][W + 1];
    for (int i = 1; i <= n; i++) {
        for (int w = 0; w <= W; w++) {
            dp[i][w] = dp[i-1][w]; // Don't take item i
            if (weights[i-1] <= w)
                dp[i][w] = Math.max(dp[i][w], dp[i-1][w - weights[i-1]] + values[i-1]);
        }
    }
    return dp[n][W];
}

// Space-optimized: iterate w from right to left
int knapsack1D(int[] weights, int[] values, int W) {
    int[] dp = new int[W + 1];
    for (int i = 0; i < weights.length; i++)
        for (int w = W; w >= weights[i]; w--) // Reverse to avoid reuse
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
    return dp[W];
}
```

---

### 6.3 🟡 Coin Change (Unbounded Knapsack)

**Identify:** "minimum coins to reach amount", "ways to reach amount", "unlimited supply"

**⚠️ Nuance:** Iterate `w` forward (left to right) for unbounded (allow reuse); reverse for 0/1.

```java
// Minimum coins
int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1); // Sentinel infinity
    dp[0] = 0;
    for (int w = 1; w <= amount; w++)
        for (int c : coins)
            if (c <= w) dp[w] = Math.min(dp[w], dp[w - c] + 1);
    return dp[amount] > amount ? -1 : dp[amount];
}

// Number of ways (order doesn't matter — iterate coins outer, amount inner)
int countWays(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    dp[0] = 1;
    for (int c : coins)           // Coin outer → combinations (no duplicates)
        for (int w = c; w <= amount; w++)
            dp[w] += dp[w - c];
    return dp[amount];
}
// Note: if order MATTERS (permutations), flip loops: amount outer, coins inner
```

---

### 6.4 🟡 Longest Common Subsequence (LCS)

**Identify:** "longest common subsequence", "edit distance", "shortest common supersequence"

```java
int lcs(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            if (s1.charAt(i-1) == s2.charAt(j-1)) dp[i][j] = dp[i-1][j-1] + 1;
            else                                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
    return dp[m][n];
}
```

**Edit Distance (Levenshtein):**
```java
int editDistance(String s, String t) {
    int m = s.length(), n = t.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            if (s.charAt(i-1) == t.charAt(j-1)) dp[i][j] = dp[i-1][j-1];
            else dp[i][j] = 1 + Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]));
    return dp[m][n];
}
```

---

### 6.5 🟡 Longest Increasing Subsequence (LIS)

**⚠️ Nuance:** O(n²) DP is easy; O(n log n) uses patience sorting (binary search on `tails` array).

```java
// O(n log n)
int lis(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    for (int n : nums) {
        int pos = Collections.binarySearch(tails, n);
        if (pos < 0) pos = -(pos + 1); // Insertion point
        if (pos == tails.size()) tails.add(n);
        else tails.set(pos, n);
    }
    return tails.size();
}
```

---

### 6.6 🟡 Matrix Chain Multiplication / Interval DP

**Identify:** "burst balloons", "minimum cost to merge stones", "optimal BST"

**Pattern:** `dp[i][j]` = optimal solution for subarray `[i, j]`. Try all split points `k`.

```java
// Burst Balloons
int maxCoins(int[] nums) {
    int n = nums.length;
    int[] arr = new int[n + 2];
    arr[0] = arr[n + 1] = 1;
    for (int i = 1; i <= n; i++) arr[i] = nums[i - 1];
    int[][] dp = new int[n + 2][n + 2];
    // dp[i][j] = max coins bursting balloons in open interval (i,j)
    for (int len = 1; len <= n; len++) {
        for (int left = 1; left <= n - len + 1; left++) {
            int right = left + len - 1;
            for (int k = left; k <= right; k++) { // k is last to burst
                dp[left][right] = Math.max(dp[left][right],
                    dp[left][k-1] + arr[left-1] * arr[k] * arr[right+1] + dp[k+1][right]);
            }
        }
    }
    return dp[1][n];
}
```

---

### 6.7 🟡 House Robber / State Machine DP

**Identify:** "can't pick adjacent elements", "state transitions"

```java
int rob(int[] nums) {
    int prev2 = 0, prev1 = 0;
    for (int n : nums) {
        int cur = Math.max(prev1, prev2 + n);
        prev2 = prev1; prev1 = cur;
    }
    return prev1;
}

// Circular (house 0 and n-1 are adjacent): solve twice
int robCircular(int[] nums) {
    if (nums.length == 1) return nums[0];
    return Math.max(robRange(nums, 0, nums.length - 2),
                    robRange(nums, 1, nums.length - 1));
}
int robRange(int[] nums, int lo, int hi) {
    int prev2 = 0, prev1 = 0;
    for (int i = lo; i <= hi; i++) {
        int cur = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1; prev1 = cur;
    }
    return prev1;
}
```

---

### 6.8 🟡 Partition DP (Palindrome Partitioning, Word Break)

```java
// Word Break
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

### 6.9 🔴 DP on Trees (Tree DP)

**Identify:** "maximum path sum in tree", "diameter", "independent set on tree"

```java
// Binary Tree Maximum Path Sum (can start/end anywhere)
int maxPathSum;
int maxPathSum(TreeNode root) {
    maxPathSum = Integer.MIN_VALUE;
    dfsPath(root);
    return maxPathSum;
}
int dfsPath(TreeNode node) {
    if (node == null) return 0;
    int left  = Math.max(0, dfsPath(node.left));  // Ignore negative gain
    int right = Math.max(0, dfsPath(node.right));
    maxPathSum = Math.max(maxPathSum, left + right + node.val); // Update global
    return node.val + Math.max(left, right); // Return best single arm
}
```

---

### 6.10 🔴 Bitmask DP

**Identify:** "assign tasks to people", "visit all nodes exactly once", "TSP"

```java
// Assign tasks — each task to one worker, minimize cost
int assignTasks(int[][] cost) {
    int n = cost.length;
    int[] dp = new int[1 << n];
    Arrays.fill(dp, Integer.MAX_VALUE);
    dp[0] = 0;
    for (int mask = 0; mask < (1 << n); mask++) {
        if (dp[mask] == Integer.MAX_VALUE) continue;
        int worker = Integer.bitCount(mask); // Which worker's turn
        if (worker == n) continue;
        for (int task = 0; task < n; task++) {
            if ((mask & (1 << task)) == 0) { // Task not yet assigned
                dp[mask | (1 << task)] = Math.min(dp[mask | (1 << task)], dp[mask] + cost[worker][task]);
            }
        }
    }
    return dp[(1 << n) - 1];
}
```

---

## 7. Linked Lists

### 🧠 Identify this pattern when you see:
- "reverse", "cycle detection", "find middle"
- "merge k sorted lists", "nth from end"
- "reorder list", "palindrome linked list"

---

### 7.1 🟢 Reverse a Linked List

**⚠️ Nuance:** Use three pointers: `prev`, `curr`, `next`. Don't lose `curr.next` before reassigning.

```java
// Iterative
ListNode reverse(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}

// Recursive
ListNode reverseRec(ListNode head) {
    if (head == null || head.next == null) return head;
    ListNode newHead = reverseRec(head.next);
    head.next.next = head; // Node after head points back to head
    head.next = null;
    return newHead;
}
```

---

### 7.2 🟢 Floyd's Cycle Detection (Tortoise & Hare)

**Identify:** "detect cycle", "find cycle start", "linked list loop"

```java
// Detect cycle
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}

// Find cycle start
ListNode cycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next; fast = fast.next.next;
        if (slow == fast) {
            slow = head; // Reset one pointer to head
            while (slow != fast) { slow = slow.next; fast = fast.next; }
            return slow; // Meeting point = cycle start
        }
    }
    return null;
}
```

---

### 7.3 🟢 Find Middle of Linked List

```java
ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next; fast = fast.next.next;
    }
    return slow; // For even length, returns first of two middles
}
```

---

### 7.4 🟡 Merge K Sorted Lists

**Approach:** Use a min-heap of size k. Always extract minimum and insert next node from that list.

```java
ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
    for (ListNode node : lists) if (node != null) pq.offer(node);
    ListNode dummy = new ListNode(0), cur = dummy;
    while (!pq.isEmpty()) {
        ListNode node = pq.poll();
        cur.next = node;
        cur = cur.next;
        if (node.next != null) pq.offer(node.next);
    }
    return dummy.next;
}
```

---

### 7.5 🟡 Reorder List (L0→Ln→L1→Ln-1→...)

**Steps:** Find middle → Reverse second half → Merge alternately.

```java
void reorderList(ListNode head) {
    ListNode mid = findMiddle(head);
    ListNode second = reverse(mid.next);
    mid.next = null;
    ListNode first = head;
    while (second != null) {
        ListNode tmp1 = first.next, tmp2 = second.next;
        first.next = second; second.next = tmp1;
        first = tmp1; second = tmp2;
    }
}
```

---

### 7.6 🟡 LRU Cache

**Approach:** HashMap (key → node) + Doubly Linked List (order).

```java
class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(), tail = new Node(); // Sentinels

    LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail; tail.prev = head;
    }

    int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        remove(node); addToFront(node);
        return node.val;
    }

    void put(int key, int value) {
        if (map.containsKey(key)) remove(map.get(key));
        if (map.size() == capacity) { Node lru = tail.prev; remove(lru); map.remove(lru.key); }
        Node node = new Node(key, value);
        addToFront(node); map.put(key, node);
    }

    void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; map.remove(n.key); }
    void addToFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; map.put(n.key, n); }

    static class Node { int key, val; Node prev, next; Node() {} Node(int k, int v) { key=k; val=v; } }
}
```

---

## 8. Stacks & Queues

### 🧠 Identify this pattern when you see:
- "next greater/smaller element"
- "valid parentheses / balanced brackets"
- "evaluate expression / calculator"
- "BFS / level-order traversal" → Queue
- "monotonic" order needed

---

### 8.1 🟢 Valid Parentheses

```java
boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '[' || c == '{') stack.push(c);
        else {
            if (stack.isEmpty()) return false;
            char top = stack.pop();
            if (c == ')' && top != '(') return false;
            if (c == ']' && top != '[') return false;
            if (c == '}' && top != '{') return false;
        }
    }
    return stack.isEmpty();
}
```

---

### 8.2 🟡 Next Greater Element

**Approach:** Monotonic stack (decreasing). Process array; for each element, pop all smaller elements — current element is their "next greater".

```java
int[] nextGreater(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>(); // Stores indices
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
            result[stack.pop()] = nums[i]; // i is the answer for popped index
        }
        stack.push(i);
    }
    return result;
}
```

---

### 8.3 🟡 Largest Rectangle in Histogram

**Approach:** Monotonic stack (increasing). For each bar, when we find a shorter bar, pop and compute area.

```java
int largestRectangleArea(int[] heights) {
    int n = heights.length, maxArea = 0;
    int[] h = new int[n + 2]; // Pad with 0s on both sides
    System.arraycopy(heights, 0, h, 1, n);
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(0);
    for (int i = 1; i < h.length; i++) {
        while (h[i] < h[stack.peek()]) {
            int height = h[stack.pop()];
            int width  = i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    return maxArea;
}
```

---

### 8.4 🟡 Min Stack (O(1) getMin)

**Trick:** Maintain a parallel `minStack` that always has the running minimum.

```java
class MinStack {
    Deque<Integer> stack = new ArrayDeque<>();
    Deque<Integer> minStack = new ArrayDeque<>();

    void push(int val) {
        stack.push(val);
        minStack.push(minStack.isEmpty() ? val : Math.min(val, minStack.peek()));
    }
    void pop() { stack.pop(); minStack.pop(); }
    int top() { return stack.peek(); }
    int getMin() { return minStack.peek(); }
}
```

---

### 8.5 🟡 Sliding Window Maximum (Deque)

**Approach:** Monotonic deque (decreasing). Front = max. Remove elements out of window from front; remove smaller elements from back.

```java
int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>(); // Stores indices
    for (int i = 0; i < n; i++) {
        if (!deque.isEmpty() && deque.peekFirst() < i - k + 1) deque.pollFirst(); // Out of window
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast(); // Smaller → remove
        deque.offerLast(i);
        if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
    }
    return result;
}
```

---

### 8.6 🟡 Basic Calculator (Expression Parsing)

```java
int calculate(String s) {
    Deque<Integer> stack = new ArrayDeque<>();
    int result = 0, num = 0, sign = 1;
    for (char c : s.toCharArray()) {
        if (Character.isDigit(c)) num = num * 10 + (c - '0');
        else if (c == '+') { result += sign * num; num = 0; sign = 1; }
        else if (c == '-') { result += sign * num; num = 0; sign = -1; }
        else if (c == '(') { stack.push(result); stack.push(sign); result = 0; sign = 1; }
        else if (c == ')') { result += sign * num; num = 0; result *= stack.pop(); result += stack.pop(); }
    }
    return result + sign * num;
}
```

---

## 9. Trees & Binary Trees

### 🧠 Identify this pattern when you see:
- "level order", "BFS on tree" → Queue
- "inorder/preorder/postorder" → DFS (recursion/stack)
- "path sum", "root to leaf", "lowest common ancestor"
- "diameter", "height", "balanced"
- "serialize/deserialize"

---

### 9.1 🟢 DFS Traversals

```java
// Inorder: Left → Root → Right (gives sorted order for BST)
void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    inorder(root.left, result);
    result.add(root.val);
    inorder(root.right, result);
}

// Iterative Inorder (interview-safe)
List<Integer> inorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode cur = root;
    while (cur != null || !stack.isEmpty()) {
        while (cur != null) { stack.push(cur); cur = cur.left; }
        cur = stack.pop();
        result.add(cur.val);
        cur = cur.right;
    }
    return result;
}
```

---

### 9.2 🟢 BFS Level-Order Traversal

```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left  != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

---

### 9.3 🟡 Lowest Common Ancestor (LCA)

**⚠️ Nuance:** If both p and q are in different subtrees of node, node is LCA. If one equals node, node is LCA.

```java
TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left  = lca(root.left,  p, q);
    TreeNode right = lca(root.right, p, q);
    if (left != null && right != null) return root; // p and q on different sides
    return left != null ? left : right;
}
```

---

### 9.4 🟡 Path Sum Problems

```java
// Has path sum root-to-leaf
boolean hasPathSum(TreeNode root, int target) {
    if (root == null) return false;
    if (root.left == null && root.right == null) return root.val == target;
    return hasPathSum(root.left, target - root.val)
        || hasPathSum(root.right, target - root.val);
}

// Count paths summing to target (any start/end on root-to-leaf path)
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
    map.merge(curr, -1, Integer::sum); // Backtrack
    return count;
}
```

---

### 9.5 🟡 Diameter of Binary Tree

**⚠️ Nuance:** Diameter passes through a node; return height upward but track diameter globally.

```java
int diameter;
int diameterOfBinaryTree(TreeNode root) {
    diameter = 0;
    height(root);
    return diameter;
}
int height(TreeNode node) {
    if (node == null) return 0;
    int left = height(node.left), right = height(node.right);
    diameter = Math.max(diameter, left + right);
    return 1 + Math.max(left, right);
}
```

---

### 9.6 🟡 Serialize / Deserialize Binary Tree

```java
// Preorder with null markers
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
    node.left  = buildTree(q);
    node.right = buildTree(q);
    return node;
}
```

---

### 9.7 🔴 Construct Binary Tree from Traversals

**Preorder + Inorder:**
- Preorder[0] = root. Find root in inorder → left size.

```java
TreeNode buildFromPreIn(int[] pre, int[] in) {
    Map<Integer, Integer> inMap = new HashMap<>();
    for (int i = 0; i < in.length; i++) inMap.put(in[i], i);
    return build(pre, 0, pre.length - 1, in, 0, in.length - 1, inMap);
}
TreeNode build(int[] pre, int ps, int pe, int[] in, int is, int ie, Map<Integer,Integer> map) {
    if (ps > pe) return null;
    TreeNode root = new TreeNode(pre[ps]);
    int inRoot = map.get(pre[ps]);
    int leftSize = inRoot - is;
    root.left  = build(pre, ps+1,          ps+leftSize, in, is,       inRoot-1, map);
    root.right = build(pre, ps+leftSize+1, pe,          in, inRoot+1, ie,       map);
    return root;
}
```

---

## 10. Binary Search Trees

### 🧠 Identify this pattern when you see:
- "kth smallest/largest in BST"
- "validate BST", "BST insert/delete"
- "floor/ceil in BST"
- "BST to sorted linked list"

---

### 10.1 🟢 Validate BST

**⚠️ Nuance:** Pass min/max bounds, not just compare with parent.

```java
boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}
boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) && validate(node.right, node.val, max);
}
```

---

### 10.2 🟡 Kth Smallest Element in BST

**Approach:** Inorder gives sorted order. Stop at kth element.

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

### 10.3 🟡 BST Iterator (next/hasNext in O(1) amortized)

```java
class BSTIterator {
    Deque<TreeNode> stack = new ArrayDeque<>();
    BSTIterator(TreeNode root) { pushLeft(root); }
    int next() { TreeNode n = stack.pop(); pushLeft(n.right); return n.val; }
    boolean hasNext() { return !stack.isEmpty(); }
    void pushLeft(TreeNode n) { while (n != null) { stack.push(n); n = n.left; } }
}
```

---

## 11. Heaps / Priority Queues

### 🧠 Identify this pattern when you see:
- "kth largest/smallest"
- "top K elements", "K closest points"
- "merge K sorted", "median of data stream"
- "task scheduler", "find next optimal"

---

### 11.1 🟢 Kth Largest Element

**⚠️ Nuance:** Use a min-heap of size k. If heap size > k, poll. Top of heap = kth largest.

```java
int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int n : nums) {
        minHeap.offer(n);
        if (minHeap.size() > k) minHeap.poll();
    }
    return minHeap.peek();
}
```

---

### 11.2 🟡 K Closest Points to Origin

```java
int[][] kClosest(int[][] points, int k) {
    // Max-heap of size k; keep the k smallest
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])
    );
    for (int[] p : points) {
        maxHeap.offer(p);
        if (maxHeap.size() > k) maxHeap.poll();
    }
    return maxHeap.toArray(new int[0][]);
}
```

---

### 11.3 🔴 Median Finder (Data Stream)

**Approach:** Two heaps: max-heap (lower half) and min-heap (upper half). Balance sizes.

```java
class MedianFinder {
    PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder()); // Max-heap
    PriorityQueue<Integer> upper = new PriorityQueue<>(); // Min-heap

    void addNum(int n) {
        lower.offer(n);
        upper.offer(lower.poll()); // Balance: always push to lower, move top to upper
        if (lower.size() < upper.size()) lower.offer(upper.poll()); // lower >= upper size
    }

    double findMedian() {
        return lower.size() > upper.size() ? lower.peek() : (lower.peek() + upper.peek()) / 2.0;
    }
}
```

---

### 11.4 🟡 Task Scheduler

**Identify:** "idle time", "minimum time to finish tasks with cooldown n"

```java
int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char c : tasks) freq[c - 'A']++;
    int maxFreq = Arrays.stream(freq).max().getAsInt();
    int maxCount = 0;
    for (int f : freq) if (f == maxFreq) maxCount++;
    // Each "chunk" has (n+1) slots; last chunk only needs maxCount slots
    return Math.max(tasks.length, (maxFreq - 1) * (n + 1) + maxCount);
}
```

---

## 12. Graphs

### 🧠 Identify this pattern when you see:
- "connected components", "islands", "number of provinces"
- "shortest path", "minimum cost to reach"
- "cycle detection", "topological sort"
- "bipartite", "coloring"
- "word ladder", "minimum transformations"

---

### 12.1 🟢 BFS (Shortest Path / Level Traversal)

```java
int bfs(int start, int target, Map<Integer, List<Integer>> graph) {
    Queue<Integer> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    queue.offer(start); visited.add(start);
    int dist = 0;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int node = queue.poll();
            if (node == target) return dist;
            for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
                if (visited.add(neighbor)) queue.offer(neighbor);
            }
        }
        dist++;
    }
    return -1;
}
```

**BFS on Grid:**
```java
int bfsGrid(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    Queue<int[]> queue = new LinkedList<>();
    // Add starting cells to queue; mark visited
    int dist = 0;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int[] cell = queue.poll();
            for (int[] d : dirs) {
                int nr = cell[0] + d[0], nc = cell[1] + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == 0) {
                    grid[nr][nc] = -1; // Mark visited
                    queue.offer(new int[]{nr, nc});
                }
            }
        }
        dist++;
    }
    return dist;
}
```

---

### 12.2 🟢 DFS (Number of Islands / Components)

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
    grid[i][j] = '0'; // Mark visited
    dfsIsland(grid, i+1, j, m, n); dfsIsland(grid, i-1, j, m, n);
    dfsIsland(grid, i, j+1, m, n); dfsIsland(grid, i, j-1, m, n);
}
```

---

### 12.3 🟡 Topological Sort

**Identify:** "course schedule", "task ordering with prerequisites", "dependency resolution"

**Approach A — Kahn's (BFS + in-degree):**
```java
int[] topoSort(int n, int[][] edges) {
    List<List<Integer>> adj = new ArrayList<>();
    int[] inDegree = new int[n];
    for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    for (int[] e : edges) { adj.get(e[1]).add(e[0]); inDegree[e[0]]++; }
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < n; i++) if (inDegree[i] == 0) queue.offer(i);
    int[] order = new int[n]; int idx = 0;
    while (!queue.isEmpty()) {
        int node = queue.poll(); order[idx++] = node;
        for (int next : adj.get(node)) if (--inDegree[next] == 0) queue.offer(next);
    }
    return idx == n ? order : new int[]{}; // Empty = cycle exists
}
```

**Approach B — DFS (detect cycle):**
```java
boolean hasCycleDirected(List<List<Integer>> adj, int n) {
    int[] color = new int[n]; // 0=white, 1=gray (in path), 2=black (done)
    for (int i = 0; i < n; i++) if (color[i] == 0 && dfsCycle(adj, color, i)) return true;
    return false;
}
boolean dfsCycle(List<List<Integer>> adj, int[] color, int u) {
    color[u] = 1;
    for (int v : adj.get(u)) {
        if (color[v] == 1) return true; // Back edge = cycle
        if (color[v] == 0 && dfsCycle(adj, color, v)) return true;
    }
    color[u] = 2; return false;
}
```

---

### 12.4 🟡 Bipartite Check (2-Coloring)

**Identify:** "is graph bipartite", "can you divide nodes into two groups"

```java
boolean isBipartite(int[][] graph) {
    int n = graph.length;
    int[] color = new int[n]; // 0=uncolored, 1, -1
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

### 12.5 🟡 Union-Find for Graph Connectivity

See [Section 20](#20-union-find-dsu) for detailed DSU.

---

### 12.6 🔴 Word Ladder (BFS + All Transformations)

```java
int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> wordSet = new HashSet<>(wordList);
    if (!wordSet.contains(endWord)) return 0;
    Queue<String> queue = new LinkedList<>();
    queue.offer(beginWord); wordSet.remove(beginWord);
    int steps = 1;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            char[] word = queue.poll().toCharArray();
            for (int j = 0; j < word.length; j++) {
                char orig = word[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    word[j] = c;
                    String next = new String(word);
                    if (next.equals(endWord)) return steps + 1;
                    if (wordSet.remove(next)) queue.offer(next);
                }
                word[j] = orig;
            }
        }
        steps++;
    }
    return 0;
}
```

---

## 13. Tries

### 🧠 Identify this pattern when you see:
- "autocomplete", "prefix search", "starts with"
- "word dictionary with wildcards"
- "count words with prefix"
- "longest common prefix"

---

### 13.1 🟢 Trie Implementation

```java
class Trie {
    TrieNode root = new TrieNode();

    void insert(String word) {
        TrieNode cur = root;
        for (char c : word.toCharArray()) {
            cur.children.putIfAbsent(c, new TrieNode());
            cur = cur.children.get(c);
        }
        cur.isEnd = true;
    }

    boolean search(String word) {
        TrieNode node = find(word);
        return node != null && node.isEnd;
    }

    boolean startsWith(String prefix) { return find(prefix) != null; }

    private TrieNode find(String s) {
        TrieNode cur = root;
        for (char c : s.toCharArray()) {
            if (!cur.children.containsKey(c)) return null;
            cur = cur.children.get(c);
        }
        return cur;
    }

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
    }
}
```

---

### 13.2 🟡 Word Search II (Trie + DFS on Board)

**Approach:** Build trie from word list → DFS from each cell, prune using trie.

```java
List<String> findWords(char[][] board, String[] words) {
    Trie trie = new Trie(); // Build trie
    for (String w : words) trie.insert(w);
    Set<String> result = new HashSet<>();
    int m = board.length, n = board[0].length;
    for (int i = 0; i < m; i++)
        for (int j = 0; j < n; j++)
            dfsBoard(board, i, j, m, n, trie.root, new StringBuilder(), result);
    return new ArrayList<>(result);
}

void dfsBoard(char[][] board, int i, int j, int m, int n, Trie.TrieNode node, StringBuilder path, Set<String> result) {
    if (i < 0 || i >= m || j < 0 || j >= n || board[i][j] == '#') return;
    char c = board[i][j];
    if (!node.children.containsKey(c)) return;
    node = node.children.get(c);
    path.append(c);
    if (node.isEnd) result.add(path.toString());
    board[i][j] = '#';
    dfsBoard(board, i+1, j, m, n, node, path, result); dfsBoard(board, i-1, j, m, n, node, path, result);
    dfsBoard(board, i, j+1, m, n, node, path, result); dfsBoard(board, i, j-1, m, n, node, path, result);
    board[i][j] = c;
    path.deleteCharAt(path.length() - 1);
}
```

---

## 14. Sorting & Searching

### 🧠 Identify this pattern when you see:
- "sort by custom comparator", "sort intervals"
- "kth largest without full sort" → QuickSelect
- "order statistics", "find median without sort"

---

### 14.1 🟢 Comparators and Sorting

```java
// Sort intervals by start time
int[][] intervals = {{2,6},{1,3},{8,10},{15,18}};
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

// Sort strings by length, then alphabetically
String[] strs = {"banana", "apple", "fig", "kiwi"};
Arrays.sort(strs, Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));

// Sort by multiple fields
Arrays.sort(people, (a, b) -> a.age != b.age ? a.age - b.age : a.name.compareTo(b.name));
```

---

### 14.2 🟡 QuickSelect (Kth Largest in O(n) Average)

**⚠️ Nuance:** Partition like QuickSort; recurse only on one side.

```java
int quickSelect(int[] nums, int k) {
    // kth largest = (n-k)th smallest (0-indexed)
    return quickSelectHelper(nums, 0, nums.length - 1, nums.length - k);
}
int quickSelectHelper(int[] nums, int lo, int hi, int k) {
    if (lo == hi) return nums[lo];
    int pivot = partition(nums, lo, hi);
    if      (pivot == k) return nums[pivot];
    else if (pivot  < k) return quickSelectHelper(nums, pivot + 1, hi, k);
    else                 return quickSelectHelper(nums, lo, pivot - 1, k);
}
int partition(int[] nums, int lo, int hi) {
    int pivot = nums[hi], i = lo;
    for (int j = lo; j < hi; j++)
        if (nums[j] <= pivot) { int t = nums[i]; nums[i++] = nums[j]; nums[j] = t; }
    int t = nums[i]; nums[i] = nums[hi]; nums[hi] = t;
    return i;
}
```

---

### 14.3 🟡 Merge Intervals

```java
int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    List<int[]> merged = new ArrayList<>();
    for (int[] cur : intervals) {
        if (merged.isEmpty() || merged.get(merged.size()-1)[1] < cur[0]) {
            merged.add(cur);
        } else {
            merged.get(merged.size()-1)[1] = Math.max(merged.get(merged.size()-1)[1], cur[1]);
        }
    }
    return merged.toArray(new int[0][]);
}
```

---

### 14.4 🔴 Merge Sort (Count Inversions)

**Identify:** "count inversions", "how many (i,j) pairs where i < j but nums[i] > nums[j]"

```java
long mergeSort(int[] arr, int lo, int hi) {
    if (lo >= hi) return 0;
    int mid = lo + (hi - lo) / 2;
    long count = mergeSort(arr, lo, mid) + mergeSort(arr, mid + 1, hi);
    int[] temp = new int[hi - lo + 1]; int i = lo, j = mid + 1, k = 0;
    while (i <= mid && j <= hi) {
        if (arr[i] <= arr[j]) temp[k++] = arr[i++];
        else { count += (mid - i + 1); temp[k++] = arr[j++]; } // All elements from i to mid form inversions
    }
    while (i <= mid) temp[k++] = arr[i++];
    while (j <= hi)  temp[k++] = arr[j++];
    System.arraycopy(temp, 0, arr, lo, temp.length);
    return count;
}
```

---

## 15. Greedy

### 🧠 Identify this pattern when you see:
- "minimum number of ...", "maximum ...", "can we finish?"
- "interval scheduling", "meeting rooms"
- "locally optimal → globally optimal"
- Greedy works when: choosing the best option now never invalidates future options

---

### 15.1 🟢 Jump Game (Can You Reach End?)

```java
boolean canJump(int[] nums) {
    int maxReach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false;
        maxReach = Math.max(maxReach, i + nums[i]);
    }
    return true;
}

// Jump Game II — minimum jumps
int jump(int[] nums) {
    int jumps = 0, curEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == curEnd) { jumps++; curEnd = farthest; }
    }
    return jumps;
}
```

---

### 15.2 🟡 Meeting Rooms (Minimum Rooms)

```java
int minMeetingRooms(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    PriorityQueue<Integer> endTimes = new PriorityQueue<>(); // Min-heap of end times
    for (int[] interval : intervals) {
        if (!endTimes.isEmpty() && endTimes.peek() <= interval[0])
            endTimes.poll(); // Reuse room
        endTimes.offer(interval[1]);
    }
    return endTimes.size();
}
```

---

### 15.3 🟡 Activity Selection (Maximum Non-Overlapping Intervals)

**Greedy:** Sort by end time → Always pick earliest-ending compatible interval.

```java
int maxNonOverlapping(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]); // Sort by end time
    int count = 0, lastEnd = Integer.MIN_VALUE;
    for (int[] i : intervals) {
        if (i[0] >= lastEnd) { count++; lastEnd = i[1]; }
    }
    return count;
}
```

---

### 15.4 🟡 Gas Station

**Insight:** If total gas ≥ total cost, a solution exists. The starting point is where running sum first becomes negative + 1.

```java
int canCompleteCircuit(int[] gas, int[] cost) {
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        tank += gas[i] - cost[i];
        total += gas[i] - cost[i];
        if (tank < 0) { start = i + 1; tank = 0; }
    }
    return total >= 0 ? start : -1;
}
```

---

### 15.5 🟡 Huffman / Greedy with Heap

**Identify:** "minimum cost to connect ropes/strings", "encode chars"

```java
int connectRopes(int[] ropes) {
    PriorityQueue<Integer> pq = new PriorityQueue<>();
    for (int r : ropes) pq.offer(r);
    int cost = 0;
    while (pq.size() > 1) {
        int combined = pq.poll() + pq.poll();
        cost += combined;
        pq.offer(combined);
    }
    return cost;
}
```

---

## 16. Divide & Conquer

### 🧠 Identify this pattern when you see:
- "large power modulo"
- "merge/split and process both halves"
- "closest pair of points"
- Base case is trivial; combine is the key

---

### 16.1 🟢 Fast Power (Exponentiation by Squaring)

```java
long power(long base, long exp, long mod) {
    long result = 1;
    base %= mod;
    while (exp > 0) {
        if ((exp & 1) == 1) result = result * base % mod;
        base = base * base % mod;
        exp >>= 1;
    }
    return result;
}
```

---

### 16.2 🟡 Majority Element (Boyer-Moore Voting)

**Identify:** "element appearing > n/2 times"

```java
int majorityElement(int[] nums) {
    int candidate = nums[0], count = 1;
    for (int i = 1; i < nums.length; i++) {
        if (count == 0) candidate = nums[i];
        count += nums[i] == candidate ? 1 : -1;
    }
    return candidate;
}
```

---

## 17. Bit Manipulation

### 🧠 Identify this pattern when you see:
- "XOR", "single number appearing odd times"
- "power of 2", "subset using bitmask"
- "toggle bits", "missing number"

---

### 17.1 Core Bit Operations

```java
boolean getBit(int n, int i)  { return ((n >> i) & 1) == 1; }
int setBit(int n, int i)      { return n | (1 << i); }
int clearBit(int n, int i)    { return n & ~(1 << i); }
int toggleBit(int n, int i)   { return n ^ (1 << i); }
boolean isOdd(int n)          { return (n & 1) == 1; }
boolean isPowerOfTwo(int n)   { return n > 0 && (n & (n - 1)) == 0; }
int countSetBits(int n)       { int c = 0; while (n != 0) { n &= n-1; c++; } return c; }
int lowestSetBit(int n)       { return n & (-n); }
int removeLowestSetBit(int n) { return n & (n - 1); }
```

---

### 17.2 🟢 XOR Tricks

```java
// Single number (all others appear twice)
int singleNumber(int[] nums) { int r = 0; for (int n : nums) r ^= n; return r; }

// Missing number in [0, n]
int missingNumber(int[] nums) {
    int xor = nums.length;
    for (int i = 0; i < nums.length; i++) xor ^= i ^ nums[i];
    return xor;
}

// Swap without temp
void swap(int[] arr, int i, int j) {
    if (i == j) return; // MUST guard!
    arr[i] ^= arr[j]; arr[j] ^= arr[i]; arr[i] ^= arr[j];
}
```

---

### 17.3 🟡 Generate All Subsets via Bitmask

```java
List<List<Integer>> subsetsViabit(int[] nums) {
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

### 17.4 🟡 Two Single Numbers (All Others Appear Twice)

```java
int[] twoSingleNumbers(int[] nums) {
    int xorAll = 0;
    for (int n : nums) xorAll ^= n;
    int diff = xorAll & (-xorAll); // Lowest set bit: distinguishes the two singles
    int x = 0, y = 0;
    for (int n : nums) {
        if ((n & diff) != 0) x ^= n;
        else y ^= n;
    }
    return new int[]{x, y};
}
```

---

### 17.5 🔴 Counting Bits for 0..N (DP)

```java
int[] countBits(int n) {
    int[] dp = new int[n + 1];
    for (int i = 1; i <= n; i++) dp[i] = dp[i >> 1] + (i & 1);
    return dp;
}
```

---

## 18. Math & Number Theory

### 🧠 Identify this pattern when you see:
- "prime numbers", "sieve"
- "GCD", "LCM", "coprime"
- "modular arithmetic", "n choose k mod p"
- "digits", "factorial trailing zeros"

---

### 18.1 🟢 GCD / LCM

```java
int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); } // Euclidean
int lcm(int a, int b) { return a / gcd(a, b) * b; }          // Avoid overflow: divide first
```

---

### 18.2 🟡 Sieve of Eratosthenes (All Primes up to N)

```java
boolean[] sieve(int n) {
    boolean[] isComposite = new boolean[n + 1];
    isComposite[0] = isComposite[1] = true;
    for (int i = 2; i * i <= n; i++)
        if (!isComposite[i])
            for (int j = i * i; j <= n; j += i) isComposite[j] = true;
    return isComposite; // isComposite[i] = false → i is prime
}
```

---

### 18.3 🟡 Modular Arithmetic

```java
// (a + b) % m
long addMod(long a, long b, long m) { return ((a % m) + (b % m)) % m; }

// (a * b) % m — prevent overflow
long mulMod(long a, long b, long m) { return (a % m) * (b % m) % m; }

// Modular inverse (m must be prime) — Fermat's little theorem: a^(m-2) mod m
long modInverse(long a, long m) { return power(a, m - 2, m); }

// nCr mod p
long nCr(int n, int r, int p) {
    if (r > n) return 0;
    long[] fact = new long[n + 1];
    fact[0] = 1;
    for (int i = 1; i <= n; i++) fact[i] = fact[i-1] * i % p;
    return fact[n] * modInverse(fact[r], p) % p * modInverse(fact[n-r], p) % p;
}
```

---

### 18.4 🟢 Trailing Zeros in Factorial

**Insight:** Count factors of 5 (each pair of 2 and 5 gives a trailing zero; 2s are more abundant).

```java
int trailingZeros(int n) {
    int count = 0;
    while (n >= 5) { n /= 5; count += n; }
    return count;
}
```

---

### 18.5 🟡 Integer Overflow Traps

```java
// Safe mid calculation
int mid = lo + (hi - lo) / 2;        // NOT (lo + hi) / 2 (overflow!)

// Detect overflow before multiply
boolean overflows(long a, long b) { return b != 0 && a > Long.MAX_VALUE / b; }

// Safe absolute value (Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE — BUG)
long safeAbs(int n) { return n == Integer.MIN_VALUE ? (long) Integer.MAX_VALUE + 1 : Math.abs(n); }
```

---

## 19. Monotonic Stack / Queue

### 🧠 Identify this pattern when you see:
- "next greater/smaller element"
- "previous greater/smaller element"
- "largest rectangle in histogram"
- "maximum in sliding window"
- "stock span", "online stock price"

**Rule of thumb:** Monotonic stack maintains a stack where each element is either:
- **Decreasing** → used to find next GREATER element
- **Increasing** → used to find next SMALLER element

---

### 19.1 Previous Greater/Smaller Element

```java
int[] previousSmaller(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Deque<Integer> stack = new ArrayDeque<>(); // Increasing stack
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[stack.peek()] >= nums[i]) stack.pop();
        result[i] = stack.isEmpty() ? -1 : nums[stack.peek()];
        stack.push(i);
    }
    return result;
}
```

---

### 19.2 🔴 Maximal Rectangle in Binary Matrix

**Approach:** Convert to histogram problem row by row; apply largest rectangle.

```java
int maximalRectangle(char[][] matrix) {
    if (matrix.length == 0) return 0;
    int n = matrix[0].length, maxArea = 0;
    int[] heights = new int[n];
    for (char[] row : matrix) {
        for (int j = 0; j < n; j++)
            heights[j] = row[j] == '1' ? heights[j] + 1 : 0;
        maxArea = Math.max(maxArea, largestRectangleArea(heights));
    }
    return maxArea;
}
```

---

### 19.3 🟡 Daily Temperatures (Next Warmer Day)

```java
int[] dailyTemperatures(int[] temps) {
    int n = temps.length;
    int[] result = new int[n];
    Deque<Integer> stack = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temps[i] > temps[stack.peek()])
            result[stack.pop()] = i - stack.peek(); // Distance to warmer day
        stack.push(i);
    }
    return result;
}
```

---

## 20. Union-Find (DSU)

### 🧠 Identify this pattern when you see:
- "dynamic connectivity", "connected components"
- "cycle detection in undirected graph"
- "number of components that merge over time"
- "redundant connection", "accounts merge"

---

### 20.1 DSU with Path Compression + Union by Rank

```java
class DSU {
    int[] parent, rank;

    DSU(int n) {
        parent = new int[n]; rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // Path compression
        return parent[x];
    }

    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false; // Already connected (cycle!)
        if (rank[px] < rank[py]) { int t = px; px = py; py = t; }
        parent[py] = px;
        if (rank[px] == rank[py]) rank[px]++;
        return true;
    }

    boolean connected(int x, int y) { return find(x) == find(y); }
}
```

**Usage — Redundant Connection:**
```java
int[] findRedundantConnection(int[][] edges) {
    DSU dsu = new DSU(edges.length + 1);
    for (int[] e : edges) if (!dsu.union(e[0], e[1])) return e;
    return new int[]{};
}
```

---

### 20.2 🔴 Accounts Merge

**Approach:** Each email is a node. Union emails that belong to the same account.

```java
List<List<String>> accountsMerge(List<List<String>> accounts) {
    Map<String, String> emailToName = new HashMap<>();
    Map<String, String> emailParent = new HashMap<>();

    // Initialize parents
    for (List<String> acc : accounts) {
        String name = acc.get(0);
        for (int i = 1; i < acc.size(); i++) {
            emailParent.put(acc.get(i), acc.get(i));
            emailToName.put(acc.get(i), name);
        }
    }

    // Union emails in same account
    for (List<String> acc : accounts) {
        String root = findEmail(emailParent, acc.get(1));
        for (int i = 2; i < acc.size(); i++)
            emailParent.put(findEmail(emailParent, acc.get(i)), root);
    }

    // Group by root
    Map<String, TreeSet<String>> groups = new HashMap<>();
    for (String email : emailParent.keySet()) {
        String root = findEmail(emailParent, email);
        groups.computeIfAbsent(root, k -> new TreeSet<>()).add(email);
    }

    List<List<String>> result = new ArrayList<>();
    for (Map.Entry<String, TreeSet<String>> e : groups.entrySet()) {
        List<String> account = new ArrayList<>();
        account.add(emailToName.get(e.getKey()));
        account.addAll(e.getValue());
        result.add(account);
    }
    return result;
}

String findEmail(Map<String, String> parent, String email) {
    if (!parent.get(email).equals(email)) parent.put(email, findEmail(parent, parent.get(email)));
    return parent.get(email);
}
```

---

## 21. Segment Trees & Binary Indexed Trees

### 🧠 Identify this pattern when you see:
- "range sum/min/max queries with updates"
- "point update, range query"
- "range update, range query"

---

### 21.1 🟡 Binary Indexed Tree (Fenwick Tree) — Point Update, Range Query

```java
class BIT {
    int[] tree;
    int n;
    BIT(int n) { this.n = n; tree = new int[n + 1]; }

    void update(int i, int delta) { // 1-indexed
        for (; i <= n; i += i & (-i)) tree[i] += delta;
    }

    int query(int i) { // Prefix sum [1..i]
        int sum = 0;
        for (; i > 0; i -= i & (-i)) sum += tree[i];
        return sum;
    }

    int rangeQuery(int l, int r) { return query(r) - query(l - 1); }
}
```

---

### 21.2 🔴 Segment Tree — Range Query, Point Update

```java
class SegmentTree {
    int[] tree;
    int n;
    SegmentTree(int[] nums) {
        n = nums.length;
        tree = new int[4 * n];
        build(nums, 0, 0, n - 1);
    }

    void build(int[] nums, int node, int start, int end) {
        if (start == end) { tree[node] = nums[start]; return; }
        int mid = (start + end) / 2;
        build(nums, 2*node+1, start, mid);
        build(nums, 2*node+2, mid+1, end);
        tree[node] = tree[2*node+1] + tree[2*node+2]; // Sum; change for min/max
    }

    void update(int node, int start, int end, int idx, int val) {
        if (start == end) { tree[node] = val; return; }
        int mid = (start + end) / 2;
        if (idx <= mid) update(2*node+1, start, mid, idx, val);
        else            update(2*node+2, mid+1, end, idx, val);
        tree[node] = tree[2*node+1] + tree[2*node+2];
    }

    int query(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return 0; // Out of range
        if (l <= start && end <= r) return tree[node]; // Fully within
        int mid = (start + end) / 2;
        return query(2*node+1, start, mid, l, r) + query(2*node+2, mid+1, end, l, r);
    }
}
```

---

## 22. String Algorithms

### 🧠 Identify this pattern when you see:
- "pattern matching", "find all occurrences of pattern in text"
- "longest palindromic substring", "palindromic substrings count"
- "rolling hash", "repeated substring"
- "Z-function", "string periods"

---

### 22.1 🟡 KMP (Knuth-Morris-Pratt) Pattern Matching

**Use:** Find all occurrences of pattern in text in O(n + m).

```java
int[] computeLPS(String pattern) { // Longest Proper Prefix = Suffix
    int m = pattern.length();
    int[] lps = new int[m];
    int len = 0, i = 1;
    while (i < m) {
        if (pattern.charAt(i) == pattern.charAt(len)) { lps[i++] = ++len; }
        else if (len != 0) { len = lps[len - 1]; } // Don't increment i
        else { lps[i++] = 0; }
    }
    return lps;
}

List<Integer> kmpSearch(String text, String pattern) {
    int[] lps = computeLPS(pattern);
    List<Integer> result = new ArrayList<>();
    int i = 0, j = 0;
    while (i < text.length()) {
        if (text.charAt(i) == pattern.charAt(j)) { i++; j++; }
        if (j == pattern.length()) { result.add(i - j); j = lps[j - 1]; }
        else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
            if (j != 0) j = lps[j - 1];
            else i++;
        }
    }
    return result;
}
```

---

### 22.2 🟡 Rabin-Karp (Rolling Hash)

**Use:** Multiple pattern search, repeated substring detection.

```java
boolean rabinKarp(String text, String pattern) {
    int n = text.length(), m = pattern.length();
    long BASE = 31, MOD = 1_000_000_007;
    long patHash = 0, textHash = 0, power = 1;
    for (int i = 0; i < m; i++) {
        patHash  = (patHash  * BASE + (pattern.charAt(i) - 'a' + 1)) % MOD;
        textHash = (textHash * BASE + (text.charAt(i)    - 'a' + 1)) % MOD;
        if (i > 0) power = power * BASE % MOD;
    }
    if (patHash == textHash && text.substring(0, m).equals(pattern)) return true;
    for (int i = m; i < n; i++) {
        textHash = (textHash - (text.charAt(i - m) - 'a' + 1) * power % MOD + MOD) % MOD;
        textHash = (textHash * BASE + (text.charAt(i) - 'a' + 1)) % MOD;
        if (patHash == textHash && text.substring(i - m + 1, i + 1).equals(pattern)) return true;
    }
    return false;
}
```

---

### 22.3 🟡 Manacher's Algorithm (Longest Palindromic Substring in O(n))

```java
String longestPalindrome(String s) {
    String t = "#" + String.join("#", s.split("")) + "#";
    int n = t.length();
    int[] p = new int[n];
    int c = 0, r = 0;
    for (int i = 0; i < n; i++) {
        int mirror = 2 * c - i;
        if (i < r) p[i] = Math.min(r - i, p[mirror]);
        while (i + p[i] + 1 < n && i - p[i] - 1 >= 0 && t.charAt(i + p[i] + 1) == t.charAt(i - p[i] - 1)) p[i]++;
        if (i + p[i] > r) { c = i; r = i + p[i]; }
    }
    int maxLen = 0, center = 0;
    for (int i = 0; i < n; i++) if (p[i] > maxLen) { maxLen = p[i]; center = i; }
    return s.substring((center - maxLen) / 2, (center + maxLen) / 2);
}
```

---

## 23. Advanced Graph Algorithms

### 🧠 Identify this pattern when you see:
- "shortest path with weights" → Dijkstra / Bellman-Ford
- "negative weight cycles" → Bellman-Ford
- "all-pairs shortest paths" → Floyd-Warshall
- "minimum spanning tree" → Kruskal / Prim
- "bridge/articulation points" → Tarjan's DFS
- "strongly connected components" → Tarjan / Kosaraju

---

### 23.1 🟡 Dijkstra's Algorithm (Single Source Shortest Path, Non-negative Weights)

**⚠️ Nuance:** Use a min-heap. Update distance only when shorter path found.

```java
int[] dijkstra(int src, int n, List<int[]>[] adj) { // adj[u] = {v, weight}
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]); // {node, dist}
    pq.offer(new int[]{src, 0});
    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int u = cur[0], d = cur[1];
        if (d > dist[u]) continue; // Outdated entry
        for (int[] edge : adj[u]) {
            int v = edge[0], w = edge[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.offer(new int[]{v, dist[v]});
            }
        }
    }
    return dist;
}
```

---

### 23.2 🟡 Bellman-Ford (Handles Negative Weights / Detect Negative Cycle)

```java
int[] bellmanFord(int src, int n, int[][] edges) { // edges = {u, v, w}
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    for (int i = 0; i < n - 1; i++) // Relax n-1 times
        for (int[] e : edges)
            if (dist[e[0]] != Integer.MAX_VALUE && dist[e[0]] + e[2] < dist[e[1]])
                dist[e[1]] = dist[e[0]] + e[2];
    // Check for negative cycle: if still relaxable on nth iteration
    for (int[] e : edges)
        if (dist[e[0]] != Integer.MAX_VALUE && dist[e[0]] + e[2] < dist[e[1]])
            return null; // Negative cycle exists
    return dist;
}
```

---

### 23.3 🟡 Floyd-Warshall (All-Pairs Shortest Path)

```java
int[][] floydWarshall(int[][] graph, int n) { // graph[i][j] = weight or INF
    int[][] dist = new int[n][n];
    for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE / 2);
    for (int i = 0; i < n; i++) dist[i][i] = 0;
    for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (graph[i][j] != 0) dist[i][j] = graph[i][j];
    for (int k = 0; k < n; k++) // Intermediate node
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
    return dist;
}
```

---

### 23.4 🔴 Minimum Spanning Tree — Kruskal's

```java
int kruskalMST(int n, int[][] edges) { // edges = {u, v, w}, sorted by w
    Arrays.sort(edges, (a, b) -> a[2] - b[2]);
    DSU dsu = new DSU(n);
    int totalCost = 0;
    for (int[] e : edges) {
        if (dsu.union(e[0], e[1])) totalCost += e[2];
    }
    return totalCost;
}
```

---

### 23.5 🔴 Bridges in Graph (Tarjan's Algorithm)

**Identify:** "critical connections", "bridge edges", "cut vertices"

```java
List<List<Integer>> bridges = new ArrayList<>();
int timer = 0;
int[] disc, low;
boolean[] visited;

void findBridges(List<List<Integer>> adj, int n) {
    disc = new int[n]; low = new int[n]; visited = new boolean[n];
    for (int i = 0; i < n; i++) if (!visited[i]) dfsBridge(adj, i, -1);
}

void dfsBridge(List<List<Integer>> adj, int u, int parent) {
    visited[u] = true; disc[u] = low[u] = timer++;
    for (int v : adj.get(u)) {
        if (!visited[v]) {
            dfsBridge(adj, v, u);
            low[u] = Math.min(low[u], low[v]);
            if (low[v] > disc[u]) bridges.add(Arrays.asList(u, v)); // Bridge found
        } else if (v != parent) {
            low[u] = Math.min(low[u], disc[v]);
        }
    }
}
```

---

## 24. Master Cheat Sheet

### 🔑 Problem → Technique

| Problem Signal | Technique |
|---------------|-----------|
| Subarray sum / prefix queries | Prefix Sum + HashMap |
| Max contiguous subarray | Kadane's |
| Sorted array + pair/triple | Two Pointers |
| Subarray with condition (longest/shortest) | Sliding Window |
| "Find in sorted" or binary decision | Binary Search |
| Generate all combinations/permutations | Backtracking |
| Min/Max cost, count ways (overlapping) | Dynamic Programming |
| Cycle in linked list | Floyd's (Tortoise & Hare) |
| Next greater/smaller element | Monotonic Stack |
| K largest/smallest, top-K, median stream | Heap (PQ) |
| Connected components, islands | BFS/DFS on graph |
| Dependencies, task ordering | Topological Sort (Kahn's / DFS) |
| Prefix search, autocomplete | Trie |
| Dynamic connectivity, union check | DSU (Union-Find) |
| Range queries with updates | Segment Tree / BIT |
| Pattern matching in text | KMP / Rabin-Karp |
| Shortest path (weighted) | Dijkstra / Bellman-Ford |
| XOR, single number, bitmask subsets | Bit Manipulation |
| Interval scheduling | Greedy (sort by end time) |

---

### ⚡ Complexity Quick Reference

| Algorithm | Time | Space |
|-----------|------|-------|
| Binary Search | O(log n) | O(1) |
| BFS / DFS | O(V + E) | O(V) |
| Dijkstra (heap) | O((V+E) log V) | O(V) |
| Bellman-Ford | O(VE) | O(V) |
| Floyd-Warshall | O(V³) | O(V²) |
| Kruskal MST | O(E log E) | O(V) |
| Heap operations | O(log n) | O(n) |
| QuickSelect | O(n) avg | O(1) |
| KMP | O(n + m) | O(m) |
| Segment Tree | O(log n) per query | O(n) |
| BIT | O(log n) per query | O(n) |
| DSU find/union | O(α(n)) ≈ O(1) | O(n) |
| Trie insert/search | O(m) where m = word length | O(total chars) |

---

### 🔥 Common Edge Cases to Always Check

```
Arrays:     empty [], single element, all duplicates, all negative
Strings:    empty "", single char, all same chars, case sensitivity
Linked List: null head, single node, cycle, even vs odd length
Trees:      null root, single node, skewed (all left/right), complete
Graphs:     disconnected, self-loops, parallel edges, negative weights
Integer:    Integer.MIN_VALUE, Integer.MAX_VALUE, overflow on multiply
Binary Search: lo > hi boundary, mid overflow (use lo + (hi-lo)/2)
Two Pointers: l == r boundary, skip duplicates
DP:         base cases (i=0, j=0), index off-by-one, modulo needed?
Backtracking: already-visited state, duplicate elements (sort + skip)
```

---

### 📌 Java Standard Library Quick Reference

```java
// Collections
Collections.sort(list);                     // Stable sort O(n log n)
Collections.sort(list, comparator);
Collections.reverse(list);
Collections.frequency(list, elem);

// Arrays
Arrays.sort(arr);
Arrays.fill(arr, value);
Arrays.copyOf(arr, newLen);
Arrays.copyOfRange(arr, from, to);          // [from, to) exclusive

// String
s.charAt(i);   s.substring(l, r);          // [l, r) exclusive
s.toCharArray(); String.valueOf(charArr);
s.split(",");   s.trim();   s.toLowerCase();
s.indexOf("x"); s.contains("x"); s.replace("a","b");
String.join(",", list);

// Math
Math.max(a,b); Math.min(a,b); Math.abs(n);
Math.pow(base, exp);  Math.sqrt(n);  Math.log(n);
Integer.MAX_VALUE; Integer.MIN_VALUE; Long.MAX_VALUE;
Integer.bitCount(n); Integer.numberOfTrailingZeros(n);
Integer.numberOfLeadingZeros(n); Integer.reverse(n);

// Deque (preferred over Stack)
Deque<Integer> stack = new ArrayDeque<>();  // Use push/pop/peek
Deque<Integer> queue = new ArrayDeque<>();  // Use offer/poll/peek

// Priority Queue
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> a[0] - b[0]);

// Map patterns
map.getOrDefault(key, 0);
map.merge(key, 1, Integer::sum);            // Increment count
map.computeIfAbsent(key, k -> new ArrayList<>());
map.putIfAbsent(key, value);
```

---

### 🗺️ DP Decision Tree

```
Can problem be divided into smaller identical sub-problems?
│
├── No → Try Greedy or Two Pointers
│
└── Yes → Do sub-problems overlap?
    │
    ├── No → Divide & Conquer (merge sort, quick select)
    │
    └── Yes → Dynamic Programming
        │
        ├── State has 1 dimension?
        │   ├── Linear array (climbing stairs, coin change)
        │   └── Prefix + suffix combination
        │
        ├── State has 2 dimensions?
        │   ├── i,j both from same string/array → interval DP
        │   ├── i from string1, j from string2 → LCS, edit distance
        │   └── i = item, j = capacity → knapsack
        │
        └── State involves bitmask?
            └── Subset/assignment DP (TSP, task assignment)
```

---

### 📐 Graph Problem Decision Tree

```
Is the graph weighted?
│
├── No → BFS (shortest path), DFS (connectivity, cycle)
│
└── Yes → Positive weights only?
    │
    ├── Yes → Dijkstra (single source)
    │         Kruskal/Prim (MST)
    │
    └── No (negative weights) → Bellman-Ford (single source)
                                 Floyd-Warshall (all-pairs)
                                 Check for negative cycle!
```

---

*Last updated: May 2026. Covers LeetCode Easy–Hard, common system design DS, and competitive programming foundations.*
