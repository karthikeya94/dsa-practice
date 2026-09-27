# DSA Pattern Recognition Guide
### How to identify the right pattern from a problem statement, keywords, and constraints

---

## 0. How to Read Any DSA Problem (Before You Think of a Pattern)

Follow this order every single time — don't jump to coding first.

1. **Read the constraints block first, not last.** `N ≤ 10^5` vs `N ≤ 20` changes the entire approach before you've even understood the story of the problem.
2. **Identify the input shape.** Array? Sorted or unsorted? Linked list? Tree? Graph (adjacency list/matrix)? Grid? String? Stream?
3. **Identify what's being asked (the output shape).** A count, a boolean, the actual subsequence/subarray, the minimum/maximum value, all possible combinations, or a modified structure. "Find if" (boolean) is usually cheaper than "find all" (enumeration).
4. **Scan for keyword signals** (see Section 2) — but treat them as *hints*, not proof. Many keywords overlap across patterns.
5. **Check for hidden ordering.** "Sorted array" in the constraints is a massive hint toward Binary Search / Two Pointers even if the problem text doesn't say so.
6. **Match the constraint size to a complexity budget** (Section 1), then pick the pattern(s) whose typical complexity fits that budget.
7. **When 2+ patterns fit**, pick based on: (a) which one meets the time/space budget, (b) which one is simpler to implement correctly under time pressure, (c) whether the interviewer/problem hints at optimizing a naive solution (usually Brute Force → Hashing/Sorting → Two Pointers/Binary Search → DP/Greedy).

---

## 1. Constraints → Complexity Budget → Candidate Patterns

Competitive/interview problems almost always leak the intended complexity through `N` (or `N, M, Q`). Assume ~10^8 simple operations/sec as the safe budget.

| Constraint on N (or grid size) | Expected complexity | Likely patterns |
|---|---|---|
| N ≤ 10–12 | O(2^N), O(N!) | Brute force permutations, Backtracking, Bitmask DP |
| N ≤ 20–25 | O(2^N · N) | Bitmask DP, Backtracking with pruning |
| N ≤ 100–500 | O(N^3) | Brute-force DP (interval/matrix chain), Floyd–Warshall |
| N ≤ 2,000–5,000 | O(N^2 log N) / O(N^2) | 2-D DP, brute-force pair checking, simple graph algorithms |
| N ≤ 10^5 | O(N log N) | Sorting, Binary Search, Heap, Divide & Conquer, Segment Tree/BIT, most Greedy |
| N ≤ 10^6 – 10^7 | O(N) or O(N log N) | Sliding Window, Two Pointers, Prefix Sum, Hashing, single-pass Greedy, BFS/DFS |
| N ≤ 10^8 | O(N) tight loop, or O(log N)/O(1) per query | Bit Manipulation tricks, Math/Number theory formulas, precomputation + O(1) query |
| N and Q both large (queries on a structure) | O((N + Q) log N) | Segment Tree, Binary Indexed Tree (Fenwick), Binary Search on Answer, Sparse Table |
| Grid of size R×C, R·C ≤ 10^6 | O(R·C) | BFS/DFS on grid, DP on grid |
| String length ≤ 10^6 | O(N) or O(N log N) | KMP, Z-function, Rabin–Karp, Trie, Manacher |

**Rule of thumb:** if the "obvious" nested-loop solution is O(N²) and N > 10^4, the problem is signaling you to drop to O(N log N) or O(N) — look at Sorting+Two Pointers, Hashing, Sliding Window, Binary Search on Answer, or a Heap.

---

## 2. Pattern Catalogue (in prerequisite / learning order)

Order respects dependencies: **Arrays before Binary Search**, **Linked List before Trees**, and generally simpler structures before the ones built on top of them.

---

### 2.1 Arrays & Hashing
**Prerequisite:** none — this is the foundation.
**Core idea:** Use direct indexing, prefix sums, or a hash map/set for O(1) average lookup to avoid a nested loop.
**Keyword triggers:** "pair with sum", "duplicate", "frequency", "count of", "contains", "distinct", "anagram", "subarray sum equals K", "first unique".
**Constraint signal:** N up to ~10^6, need O(N) or O(N log N).
**Overlaps with:** Sliding Window (contiguous subarray + hashing for prefix sums), Two Pointers (if array is sorted, hashing becomes unnecessary), Sorting (if order doesn't matter and hashing memory is a concern).
**Complexity:** O(N) time, O(N) space typically.
**Examples:** Two Sum, Group Anagrams, Longest Consecutive Sequence, Subarray Sum Equals K.

---

### 2.2 Sorting
**Prerequisite:** Arrays & Hashing.
**Core idea:** Reordering data to expose structure (adjacent duplicates, monotonic property) that enables Two Pointers, Greedy, or Binary Search afterward.
**Keyword triggers:** "kth smallest/largest", "merge intervals", "non-overlapping", "closest pair", "median", anything where relative order (not original index) matters.
**Constraint signal:** N ≤ 10^6 → O(N log N) sort is safe; if you need better than O(N log N) look at Counting Sort/Bucket Sort (only when value range is bounded) or a Heap for "kth" problems (avoids full sort).
**Overlaps with:** Two Pointers (almost always paired with sorting), Greedy (sorting by one attribute is the classic first step), Heap (kth largest can be O(N log K) via heap instead of O(N log N) full sort).
**Complexity:** O(N log N) time, O(1)–O(N) space depending on algorithm/stability needs.
**Examples:** Merge Intervals, Kth Largest Element, Meeting Rooms.

---

### 2.3 Two Pointers
**Prerequisite:** Arrays & Hashing, Sorting.
**Core idea:** Two indices moving toward each other (or in the same direction) over a **sorted** or otherwise structured sequence to avoid O(N²) comparisons.
**Keyword triggers:** "sorted array", "pair sum", "palindrome check", "remove duplicates in-place", "container with most water", "trapping rain water", "3Sum/4Sum".
**Constraint signal:** N ≤ 10^5–10^6, need O(N) or O(N log N) after an O(N log N) sort.
**Overlaps with:** Sliding Window (both use two indices, but Sliding Window tracks a *variable-size contiguous range* with a running condition, while Two Pointers usually converge from opposite ends or track a fixed relationship); Binary Search (searching for a pair sum in a sorted array can be done with Two Pointers O(N) or Binary Search per element O(N log N) — Two Pointers is strictly better here).
**Complexity:** O(N) after sorting, O(1) extra space.
**Examples:** Two Sum II (sorted), 3Sum, Container With Most Water, Trapping Rain Water.

---

### 2.4 Sliding Window
**Prerequisite:** Two Pointers.
**Core idea:** Maintain a window `[left, right]` over a sequence and expand/shrink it based on a running condition (sum, count of distinct chars, etc.) instead of recomputing from scratch.
**Keyword triggers:** "longest/shortest substring/subarray", "at most K distinct", "maximum sum subarray of size K", "without repeating characters", "minimum window substring".
**Constraint signal:** N ≤ 10^6, strictly need O(N) — nested loop (O(N²)) will TLE.
**Overlaps with:** Two Pointers (a sliding window IS a form of two pointers where both move forward, never backward); Prefix Sum + Hashing (fixed-size or "exact sum K" problems, especially with negative numbers, often need Hashing instead of a pure window because the window can't shrink monotonically with negatives).
**Complexity:** O(N) time, O(1)–O(K) space (e.g., a frequency map of window contents).
**Examples:** Longest Substring Without Repeating Characters, Minimum Window Substring, Max Sum Subarray of Size K, Longest Repeating Character Replacement.

---

### 2.5 Binary Search
**Prerequisite:** Arrays (must understand sorted order), Sorting.
**Core idea:** Repeatedly halve a search space that has a **monotonic property** (sorted array, or "if X works then everything easier than X also works" — Binary Search on Answer).
**Keyword triggers:** "sorted array", "find target/insertion point", "minimum/maximum value such that condition holds", "kth element in two sorted arrays", "search in rotated array", "capacity to ship packages within D days".
**Constraint signal:** N ≤ 10^9 or higher (since Binary Search is O(log N), even huge N is fine) — a strong sign is when brute force enumeration of the answer is too slow but *checking* a candidate answer is cheap (O(N) or O(N log N)) — that's "Binary Search on the Answer," not on an array.
**Overlaps with:** Heap (finding kth smallest can be Binary Search on value + counting, or a Heap of size K — Binary Search wins when the value range is large but N is small, Heap wins when N is small and values are arbitrary); Two Pointers (searching pairs in sorted data — Two Pointers is O(N), Binary Search per element is O(N log N), so prefer Two Pointers unless only one side is iterated).
**Complexity:** O(log N) per search, O(N log N) if combined with a feasibility check over N elements.
**Examples:** Binary Search, Search in Rotated Sorted Array, Koko Eating Bananas, Split Array Largest Sum, Median of Two Sorted Arrays.

---

### 2.6 Linked List
**Prerequisite:** Arrays & Hashing (for contrast — understand why O(1) insert/delete matters), Recursion helps but isn't mandatory yet.
**Core idea:** Pointer manipulation without random access; classic tricks are the **fast/slow (tortoise-hare) pointer** and **dummy head node**.
**Keyword triggers:** "reverse a linked list", "detect cycle", "merge two sorted lists", "middle of linked list", "remove nth node from end", "clone a list with random pointers".
**Constraint signal:** N ≤ 10^5–10^6, need O(N) time and ideally O(1) extra space (in-place reversal) vs O(N) space (recursive/stack-based).
**Overlaps with:** Two Pointers (fast/slow pointer is literally a Two-Pointer technique applied to a list); Hashing (cycle detection or clone-with-random-pointer can also be solved with a hash map trading O(N) space for simplicity, vs O(1) space Floyd's algorithm).
**Complexity:** O(N) time; O(1) space for pointer tricks, O(N) if using extra hashmap/stack.
**Examples:** Reverse Linked List, Linked List Cycle, Merge K Sorted Lists (also uses Heap), Copy List with Random Pointer.

---

### 2.7 Recursion
**Prerequisite:** Arrays, Linked List (to see recursive vs iterative tradeoffs on real structures).
**Core idea:** Break a problem into a smaller identical sub-problem plus a base case. Foundation for Trees, Backtracking, Divide & Conquer, and DP.
**Keyword triggers:** "tree traversal", "generate all", "divide and conquer", any problem defined in terms of itself ("f(n) in terms of f(n-1)").
**Constraint signal:** Watch recursion depth — N > ~10^4–10^5 with plain recursion risks stack overflow; convert to iterative or increase recursion limit / use an explicit stack.
**Overlaps with:** Backtracking (backtracking = recursion + explicit "undo" step + pruning); Dynamic Programming (DP = recursion + memoization to avoid recomputation).
**Complexity:** Varies wildly — O(N) for simple linear recursion, exponential for unpruned branching recursion.
**Examples:** Merge Sort, Quick Sort, Fibonacci (naive), tree height/depth calculations.

---

### 2.8 Stack & Queues
**Prerequisite:** Arrays, Recursion (a stack often *replaces* recursion iteratively).
**Core idea:** LIFO (stack) for "most recent unmatched" problems; FIFO (queue) for "process in arrival order"/level-order/BFS.
**Keyword triggers:** "valid parentheses", "next greater element", "daily temperatures", "monotonic stack", "sliding window maximum" (monotonic deque), "implement queue using stacks", "evaluate expression".
**Constraint signal:** N ≤ 10^6, need O(N) via monotonic stack/queue instead of an O(N²) brute force scan for "next greater/smaller" style problems.
**Overlaps with:** Sliding Window (Sliding Window Maximum uses a monotonic deque — technically both patterns at once); Graphs (BFS is a queue-driven traversal; DFS is stack-driven, whether via recursion or an explicit stack).
**Complexity:** O(N) time since each element is pushed/popped at most once; O(N) space.
**Examples:** Valid Parentheses, Next Greater Element, Daily Temperatures, Min Stack, Sliding Window Maximum.

---

### 2.9 Binary Trees
**Prerequisite:** Linked List (pointer-based nodes), Recursion, Stack/Queue (for iterative traversal).
**Core idea:** Hierarchical recursive structure; most problems are DFS (preorder/inorder/postorder, often recursive) or BFS (level order, queue-based).
**Keyword triggers:** "tree traversal", "diameter of tree", "lowest common ancestor", "path sum", "serialize/deserialize", "level order", "invert tree", "balanced tree check".
**Constraint signal:** N (number of nodes) ≤ 10^5–10^6, target O(N) — one traversal, not one per node (which would be O(N²)).
**Overlaps with:** Recursion (nearly all tree problems are recursion in disguise); Graphs (a tree is a special connected acyclic graph — LCA, path problems, and traversal logic transfer directly); Stack/Queue (iterative traversal alternatives to recursion, useful when depth is large).
**Complexity:** O(N) time for a full traversal, O(H) space for recursion stack where H = height (O(log N) balanced, O(N) skewed).
**Examples:** Maximum Depth of Binary Tree, Diameter of Binary Tree, Lowest Common Ancestor, Path Sum, Serialize and Deserialize Binary Tree.

---

### 2.10 Binary Search Trees (BST)
**Prerequisite:** Binary Trees, Binary Search (to appreciate the ordering property).
**Core idea:** Left subtree < node < right subtree, so search/insert/delete are O(log N) on a balanced tree, and inorder traversal yields sorted order for free.
**Keyword triggers:** "validate BST", "kth smallest in BST", "insert/delete in BST", "convert sorted array to BST", "closest value in BST", "range sum of BST".
**Constraint signal:** N ≤ 10^5–10^6; O(log N) per operation assumes reasonably balanced tree — worst case (skewed) degrades to O(N), which some problems intentionally test.
**Overlaps with:** Binary Search (BST search is literally binary search on a tree instead of an array); Recursion (inorder traversal for validation/kth-smallest); Two Pointers-style (BST iterator problems combine with stack-based DFS).
**Complexity:** O(log N) average, O(N) worst case per operation; O(H) space for recursion.
**Examples:** Validate Binary Search Tree, Kth Smallest Element in a BST, Convert Sorted Array to BST, Lowest Common Ancestor of a BST.

---

### 2.11 Tries (Prefix Trees)
**Prerequisite:** Trees, Recursion, Hashing (as the alternative you're improving on).
**Core idea:** A tree where each path from root represents a string prefix — enables O(L) prefix/word lookup where L = word length, independent of how many words are stored.
**Keyword triggers:** "prefix", "autocomplete", "word search II", "longest common prefix", "search suggestions", "word dictionary with wildcard".
**Constraint signal:** Many words (10^4–10^5) each queried by prefix repeatedly — a hash set would need to check every stored word (O(N·L)); a Trie does it in O(L) per query.
**Overlaps with:** Hashing (a Trie is often "the upgrade" when you need prefix-based queries that plain hashing can't do efficiently); Backtracking (Word Search II combines Trie + grid DFS/backtracking).
**Complexity:** O(L) per insert/search (L = word length); O(total characters) space.
**Examples:** Implement Trie, Word Search II, Longest Word in Dictionary, Design Add and Search Words.

---

### 2.12 Backtracking
**Prerequisite:** Recursion, Trees (thinking in terms of a decision tree), Arrays.
**Core idea:** Explore all candidate solutions via recursion, but **undo (backtrack)** a choice and prune branches early once they can't lead to a valid/optimal answer.
**Keyword triggers:** "generate all subsets/permutations/combinations", "N-Queens", "Sudoku solver", "word search", "partition to K equal subsets", "combination sum".
**Constraint signal:** N ≤ 10–20 typically (exponential complexity O(2^N) or O(N!)) — if N is larger than ~25, backtracking alone will TLE and you likely need DP (if overlapping subproblems exist) or Greedy instead.
**Overlaps with:** Dynamic Programming (if the same sub-state recurs, memoize it — Backtracking + memo = DP); Trie (grid/dictionary word search); Bit Manipulation (bitmask can represent the "chosen so far" state instead of a visited array, common in N-Queens/subset problems).
**Complexity:** O(2^N), O(N!), or O(K^N) depending on branching factor; space O(N) for recursion depth + current path.
**Examples:** Subsets, Permutations, Combination Sum, N-Queens, Word Search, Sudoku Solver.

---

### 2.13 Heaps / Priority Queues
**Prerequisite:** Trees (a heap is a complete binary tree, usually array-backed), Sorting (as the naive alternative).
**Core idea:** Always access the min/max in O(1), insert/remove in O(log N) — ideal for "top K" or "keep processing the smallest/largest so far" problems without fully sorting.
**Keyword triggers:** "kth largest/smallest", "top K frequent", "merge K sorted lists", "median of a data stream", "task scheduler", "meeting rooms II" (min heap of end times).
**Constraint signal:** N ≤ 10^5–10^6 and K << N — full sort is O(N log N), but a heap of size K gives O(N log K), meaningfully better when K is small; also essential when data is a **stream** (can't sort what you haven't seen yet).
**Overlaps with:** Sorting (heap-based "kth" solutions beat full sort when K is small); Binary Search (median-of-stream / kth-element problems can sometimes use Binary Search on value instead of two heaps); Greedy (many greedy scheduling problems use a heap internally to always pick the best available next item).
**Complexity:** O(log N) insert/extract, O(N log K) for top-K style problems.
**Examples:** Kth Largest Element in a Stream, Top K Frequent Elements, Merge K Sorted Lists, Find Median from Data Stream, Task Scheduler.

---

### 2.14 Graphs
**Prerequisite:** Trees (a graph generalizes a tree), Stack/Queue (DFS/BFS), Heaps (for weighted shortest path).
**Core idea:** Model entities as nodes and relationships as edges; traverse with BFS (shortest path in unweighted graphs, level-by-level) or DFS (connectivity, cycle detection, topological sort).
**Keyword triggers:** "number of islands", "shortest path", "connected components", "course schedule" (topological sort), "clone graph", "bipartite", "network delay time".
**Constraint signal:** V (vertices) and E (edges) ≤ 10^5–10^6, target O(V + E) for BFS/DFS/Union-Find; for weighted shortest path, O((V+E) log V) with a heap (Dijkstra).
**Overlaps with:** Stack/Queue (implementation vehicles for DFS/BFS); Trees (a tree is just an acyclic connected graph — many "tree" problems generalize to graph problems); Dynamic Programming (shortest path DP, DAG-based problems); Union-Find (an alternative to DFS for connectivity/cycle detection, often faster in practice for disjoint-set-style problems).
**Complexity:** O(V + E) for BFS/DFS; O((V + E) log V) for Dijkstra with a heap; O(V·E) for Bellman-Ford.
**Examples:** Number of Islands, Course Schedule, Clone Graph, Rotting Oranges, Network Delay Time.

---

### 2.15 Advanced Graphs
**Prerequisite:** Graphs, Heaps, Dynamic Programming (basic).
**Core idea:** Beyond plain BFS/DFS — shortest paths with negative weights (Bellman-Ford), all-pairs shortest paths (Floyd-Warshall), minimum spanning tree (Prim's/Kruskal's + Union-Find), and Dijkstra's algorithm with a priority queue.
**Keyword triggers:** "cheapest flights within K stops", "minimum spanning tree", "negative edge weights", "all pairs shortest path", "critical connections" (bridges), "reconstruct itinerary" (Eulerian path).
**Constraint signal:** V ≤ 500–1000 → Floyd-Warshall O(V^3) is fine; V, E ≤ 10^5 → Dijkstra O((V+E) log V) or Union-Find-based Kruskal's O(E log E); presence of negative weights forces Bellman-Ford O(V·E) since Dijkstra breaks with negative edges.
**Overlaps with:** Heaps (Dijkstra/Prim's both use a priority queue); Dynamic Programming (Bellman-Ford is essentially DP over "number of edges used"); Union-Find (Kruskal's MST relies entirely on it).
**Complexity:** Varies by algorithm — see triggers above.
**Examples:** Network Delay Time, Cheapest Flights Within K Stops, Min Cost to Connect All Points, Swim in Rising Water.

---

### 2.16 Greedy Algorithms
**Prerequisite:** Sorting, Heaps (many greedy solutions sort first or use a heap to always grab the locally-best choice).
**Core idea:** Make the locally optimal choice at each step and prove (or trust, in interviews) that it leads to a globally optimal solution — works only when the problem has the "greedy choice property."
**Keyword triggers:** "maximum/minimum number of", "activity selection", "jump game", "gas station", "assign cookies", "interval scheduling", "minimum number of platforms/rooms".
**Constraint signal:** N ≤ 10^5–10^6, need O(N log N) (sort) or O(N); if a greedy choice doesn't obviously work or you can construct a counterexample, the problem is actually DP in disguise — this is the most common Greedy-vs-DP trap.
**Overlaps with:** Sorting (step 1 of most greedy solutions); Heaps (greedy + "always pick current best" often needs a heap, e.g., task scheduling); Dynamic Programming (when greedy fails because a locally optimal choice can block a better global solution — classic tell: "0/1 Knapsack" is DP, "Fractional Knapsack" is Greedy).
**Complexity:** Usually O(N log N) (dominated by the sort).
**Examples:** Jump Game, Gas Station, Non-overlapping Intervals, Task Scheduler, Minimum Number of Arrows to Burst Balloons.

---

### 2.17 Intervals
**Prerequisite:** Sorting, Greedy, Heaps.
**Core idea:** Sort intervals by start (or end) time, then sweep through with a pointer or heap to merge, count overlaps, or schedule.
**Keyword triggers:** "merge intervals", "insert interval", "meeting rooms", "non-overlapping intervals", "employee free time".
**Constraint signal:** N ≤ 10^5, target O(N log N) via sort + single linear sweep.
**Overlaps with:** Greedy (interval scheduling is a textbook greedy problem); Heaps (Meeting Rooms II needs a min-heap of end times to count concurrent meetings).
**Complexity:** O(N log N) time (sorting dominates), O(N) space.
**Examples:** Merge Intervals, Meeting Rooms II, Non-overlapping Intervals, Insert Interval.

---

### 2.18 Dynamic Programming — 1-D
**Prerequisite:** Recursion, Backtracking (to see the exponential version first), Arrays.
**Core idea:** Break a problem into overlapping subproblems along a single dimension (e.g., "best answer up to index i") and cache results (memoization or a bottom-up array) to avoid recomputation.
**Keyword triggers:** "maximum/minimum number of ways", "climbing stairs", "house robber", "longest increasing subsequence", "coin change", "can you reach/partition".
**Constraint signal:** N ≤ 10^4–10^6 for O(N) or O(N log N) DP (e.g., LIS with binary search); N ≤ 10^3–10^4 for O(N^2) DP; if a brute-force recursive solution is exponential due to **overlapping subproblems**, that's the strongest signal for DP over plain Backtracking.
**Overlaps with:** Backtracking (same recursion tree, DP just adds a memo table); Greedy (some 1-D DP problems have a greedy shortcut — always double check); Binary Search (LIS can be O(N log N) using binary search on a patience-sorting array instead of O(N^2) DP).
**Complexity:** O(N) to O(N^2) depending on state/transition cost; O(N) space (sometimes reducible to O(1) with rolling variables).
**Examples:** Climbing Stairs, House Robber, Coin Change, Longest Increasing Subsequence, Word Break.

---

### 2.19 Dynamic Programming — 2-D
**Prerequisite:** Dynamic Programming (1-D), Arrays/Grids.
**Core idea:** State depends on two indices (e.g., two strings, or a grid position) — `dp[i][j]` typically represents "best answer using first i of one thing and first j of another."
**Keyword triggers:** "longest common subsequence", "edit distance", "unique paths", "0/1 knapsack", "matrix path sum", "interleaving string", "regular expression matching".
**Constraint signal:** Both dimensions ≤ ~10^3–5×10^3 → O(N·M) is fine (up to ~10^7 cells); if N·M exceeds ~10^8, you need to compress the DP table to O(N) or O(M) rolling rows, or find a smarter recurrence.
**Overlaps with:** Backtracking (0/1 Knapsack and subset-sum problems have a natural backtracking form before optimizing to DP); Greedy (Fractional Knapsack looks similar but is solved greedily, not with 2-D DP — a classic trap); Graphs (grid-based DP problems are structurally similar to BFS/DFS on a grid graph).
**Complexity:** O(N·M) time and space typically; space often optimizable to O(min(N,M)).
**Examples:** Longest Common Subsequence, Edit Distance, 0/1 Knapsack, Unique Paths, Interleaving String.

---

### 2.20 Bit Manipulation
**Prerequisite:** Dynamic Programming basics (for bitmask DP), Arrays.
**Core idea:** Use bitwise operations (AND, OR, XOR, shifts) to represent sets, toggle states, or do arithmetic tricks in O(1) per operation — especially powerful when N is small enough to fit a state into an integer's bits (N ≤ ~20–25).
**Keyword triggers:** "single number" (XOR), "subsets" (bitmask enumeration), "count set bits", "power of two", "minimum XOR", "traveling salesman on small N" (bitmask DP).
**Constraint signal:** N ≤ 20–25 is the classic bitmask-DP signal (state space 2^N fits in an int/long and 2^N × N is computationally feasible); pure bit-trick problems (XOR, set bits) work for much larger N since each operation is O(1).
**Overlaps with:** Backtracking (bitmask can replace a `visited[]` boolean array for O(1) state representation and copying); Dynamic Programming (Bitmask DP = DP where the state includes a bitmask of "used/visited" elements, e.g., Traveling Salesman Problem, Assign Tasks to Workers).
**Complexity:** O(1) per bitwise op; O(2^N · N) for bitmask DP.
**Examples:** Single Number, Counting Bits, Subsets (bitmask enumeration), Traveling Salesman Problem (bitmask DP), Minimum XOR Sum of Two Arrays.

---

### 2.21 Math & Geometry
**Prerequisite:** Bit Manipulation, Dynamic Programming (2-D), general arithmetic comfort.
**Core idea:** Number theory (GCD/LCM, primes, modular arithmetic), combinatorics, and geometric reasoning (rotate matrix, points/lines, area) that often have a closed-form or precomputed-table solution faster than any traversal-based pattern.
**Keyword triggers:** "GCD/LCM", "prime factorization", "modulo", "rotate image/matrix", "spiral matrix", "points on a line", "convex hull", "probability/combinatorics".
**Constraint signal:** N can be very large (10^9, 10^18) if the answer is a closed-form formula (O(1) or O(log N) via fast exponentiation); if N is bounded by array size instead, it may combine with DP or simulation.
**Overlaps with:** Bit Manipulation (fast exponentiation uses bit-shifting); Dynamic Programming (counting problems with modular constraints often need DP + modular arithmetic together); Backtracking (geometry/grid simulation problems with small N).
**Complexity:** O(1) to O(log N) for pure math formulas; O(N) or O(N^2) for simulation-heavy geometry (e.g., rotate matrix in place).
**Examples:** Pow(x, n), Rotate Image, Spiral Matrix, Happy Number, Excel Sheet Column Number.

---

### 2.22 Strings (Advanced Algorithms)
**Prerequisite:** Arrays & Hashing, Tries, Dynamic Programming (2-D), Math (modular arithmetic for hashing).
**Core idea:** Specialized linear/near-linear algorithms for pattern matching and string structure that beat the naive O(N·M) substring search: KMP (prefix function), Z-function, Rabin-Karp (rolling hash), Manacher's algorithm (palindromes).
**Keyword triggers:** "find all occurrences of pattern", "longest palindromic substring", "repeated substring pattern", "shortest palindrome", "string matching in O(N)".
**Constraint signal:** String length ≤ 10^5–10^6, and naive substring search O(N·M) would TLE — that's the signal for KMP/Z-function (O(N+M)) or Manacher's (O(N) for all palindromic substrings, vs O(N^2) DP).
**Overlaps with:** Dynamic Programming (Longest Palindromic Substring has an O(N^2) DP solution and an O(N) Manacher's solution — DP is easier to write, Manacher's is needed only if N is large); Tries (multi-pattern matching combines Tries with Aho-Corasick, an extension of KMP); Two Pointers (basic palindrome checks without needing full Manacher's).
**Complexity:** O(N) to O(N+M) for the specialized algorithms vs O(N·M) or O(N^2) naive/DP versions.
**Examples:** Implement strStr() (KMP), Longest Palindromic Substring (DP or Manacher's), Repeated Substring Pattern, Shortest Palindrome.

---

## 3. Same Keywords, Multiple Valid Patterns (Disambiguation Table)

These are the phrasings that trip people up because more than one pattern can technically solve them — use the constraints to break the tie.

| Keyword / phrase in problem | Patterns that could apply | How to decide |
|---|---|---|
| "find a pair that sums to K" | Hashing (unsorted, O(N)) · Two Pointers (sorted, O(N)) · Binary Search (sorted, O(N log N)) | If array isn't sorted and sorting isn't required elsewhere → Hashing. If already sorted → Two Pointers (strictly better than per-element Binary Search). |
| "kth largest/smallest element" | Sorting O(N log N) · Heap O(N log K) · Quickselect O(N) avg | K small relative to N → Heap. Need it once, K unknown ahead → Quickselect. Need many order statistics → just sort. |
| "longest substring/subarray with condition X" | Sliding Window (monotonic condition) · Prefix Sum + Hashing (exact sum, especially with negatives) | If shrinking the window when the condition breaks is always valid → Sliding Window. If negative numbers break monotonicity → Prefix Sum + Hashing. |
| "shortest path" | BFS (unweighted) · Dijkstra/Heap (positive weights) · Bellman-Ford (negative weights) · DP (DAG) | Check edge weights: none → BFS; positive → Dijkstra; negative → Bellman-Ford; graph is a DAG → topological-order DP. |
| "count subsets/subsequences with property" | Backtracking (small N, enumerate) · DP (overlapping subproblems) · Bitmask DP (N ≤ ~20) | N ≤ 20 and need exact enumeration or optimal subset → Bitmask. N larger but overlapping subproblems exist → classic DP. Otherwise plain Backtracking. |
| "minimum number of intervals/rooms/resources" | Greedy + Sorting · Heap (track concurrent usage) | If you only need a count of overlaps at any time → Heap of end times. If it's a scheduling/selection decision → Greedy sort by end time. |
| "all possible combinations/permutations" | Backtracking · Recursion | Always Backtracking if there's any pruning opportunity (early exit on invalid state); pure Recursion if you must generate everything with no way to prune. |
| "detect a cycle" | DFS with visited/recursion-stack (graph) · Fast/Slow pointers (linked list) · Union-Find (undirected graph, especially with edge additions) | Linked list → Fast/Slow pointer. General graph, static → DFS. Graph built incrementally (edges added one by one) → Union-Find. |
| "0/1 choose or skip each item" | Backtracking (small N) · 2-D DP (Knapsack-style) · Greedy (only if divisible/fractional, e.g. Fractional Knapsack) | If items can be split fractionally → Greedy. If items are atomic and N is large → 2-D DP. If N ≤ ~20-25 and you need all combos → Backtracking/Bitmask. |
| "palindrome" related | Two Pointers (simple check) · DP O(N^2) (count/longest) · Manacher's O(N) (longest, large N) | Just checking one string → Two Pointers. Counting all palindromic substrings/longest with moderate N → DP. N > ~10^5 → Manacher's. |
| "median" / "middle element" | Two Heaps (data stream) · Sorting (static array) · Binary Search on value/index (sorted or partitionable data) | Streaming input → Two Heaps. Static array, one-time query → Sort or Quickselect. Two sorted arrays → Binary Search (classic "Median of Two Sorted Arrays"). |

---

## 4. Quick Keyword Index

Fast lookup — jump straight to a pattern from a single word/phrase in the problem statement.

- **"subarray" / "substring" (contiguous)** → Sliding Window, Prefix Sum + Hashing
- **"subsequence" (not contiguous)** → Dynamic Programming, Backtracking
- **"in-place"** → Two Pointers, Bit Manipulation, Cyclic Sort
- **"sorted array/list"** → Binary Search, Two Pointers
- **"top K" / "kth"** → Heap, Quickselect, Sorting
- **"all combinations/permutations/subsets"** → Backtracking
- **"minimum/maximum number of ways"** → Dynamic Programming
- **"minimum/maximum value achievable"** → Greedy or Dynamic Programming (check greedy-choice property)
- **"connected components/islands"** → Graph BFS/DFS, Union-Find
- **"shortest path"** → BFS/Dijkstra/Bellman-Ford
- **"prefix" (of words)** → Trie
- **"parentheses/brackets matching"** → Stack
- **"next greater/smaller element"** → Monotonic Stack
- **"sliding maximum/minimum"** → Monotonic Deque
- **"cycle" (linked list)** → Fast/Slow Pointers
- **"cycle" (graph)** → DFS / Union-Find
- **"merge intervals/meetings"** → Sorting + Greedy, Heap
- **"generate parentheses/Sudoku/N-Queens"** → Backtracking
- **"XOR / single number / set bits"** → Bit Manipulation
- **"GCD/LCM/prime/modulo"** → Math

---

## 5. Recommended Learning Order (Summary)

```
Arrays & Hashing
      │
   Sorting
      │
  Two Pointers ──────► Stack & Queues
      │
 ┌────┼────────┐
 ▼    ▼        ▼
Binary   Sliding   Linked List
Search   Window        │
                    Recursion
                        │
                 ┌──────┴──────┐
                 ▼             ▼
            Binary Trees   Backtracking
                 │             │
          Binary Search    (shares
              Trees          recursion
                 │            base)
              Tries
                 │
        Heaps / Priority Queue
                 │
             Graphs ──────► Advanced Graphs
                 │
        ┌────────┼────────┐
        ▼        ▼        ▼
    Greedy   Intervals   DP (1-D)
                              │
                          DP (2-D)
                              │
                     Bit Manipulation
                              │
                       Math & Geometry
                              │
                    Strings (Advanced Algo)
```

**Why this order:** each layer reuses the mental model of the one above it — Two Pointers is Arrays with structure; Sliding Window is Two Pointers with a running condition; Trees need Linked List's pointer discipline plus Recursion; Backtracking is Recursion with pruning; DP is Backtracking with memoization; Bitmask DP needs both DP and Bit Manipulation fluency; and Advanced Strings lean on Tries, DP, and Math all at once.
