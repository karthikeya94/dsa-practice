# DSA Pattern Identification & Competitive Coding Recap

A practical guide to recognizing **which DSA pattern fits a problem**, how to read a problem statement, how constraints determine the acceptable complexity, and how multiple patterns can be combined.

> **Goal:** Do not memorize solutions. Learn to look at the **data shape + operation + constraints + required output** and derive the pattern.

---

## 0. The Most Important Mental Model

When you see a new DSA problem, do **not** ask:

> “Which LeetCode pattern does this look like?”

Ask these questions in order:

1. **What is the input shape?**
   - Array?
   - String?
   - Linked list?
   - Tree?
   - Graph?
   - Matrix?
   - Intervals?
   - Numbers/bits?

2. **What operation is being requested?**
   - Find/search?
   - Count/frequency?
   - Pair/triple?
   - Longest/shortest subarray?
   - Maximum/minimum?
   - Reachability?
   - Ordering/sorting?
   - All possible combinations?
   - Repeated queries?
   - Optimization/minimum cost?

3. **What property can I exploit?**
   - Sorted order
   - Contiguous range
   - Duplicate/frequency information
   - Monotonic answer
   - Tree hierarchy
   - Graph connectivity
   - State repetition
   - Local optimal choice
   - Bit properties

4. **What do the constraints allow?**
   - `O(n²)`?
   - `O(n log n)`?
   - Must be `O(n)`?
   - Is `O(n log answer)` acceptable?
   - Can we use `O(n)` extra memory?

5. **What is the brute-force solution?**
   Write it mentally first. Then identify the repeated work and remove it.

This is the core skill behind pattern recognition.

---

# 1. Learning Order / Dependency Order

The categories in the screenshots are reorganized into a practical learning sequence. The important dependency is that a later topic should build on ideas from earlier topics.

## Recommended order

| Order | Pattern / Topic | Main prerequisite |
|---:|---|---|
| 1 | Beginner Problems | Basic programming |
| 2 | Sorting | Arrays, loops |
| 3 | Arrays | Basic data manipulation |
| 4 | Hashing | Arrays + basic complexity |
| 5 | Binary Search | Sorted arrays + monotonicity |
| 6 | Recursion | Functions + arrays/stack thinking |
| 7 | Linked List | References/pointers + iteration |
| 8 | Sliding Window / Two Pointers | Arrays + hashing |
| 9 | Stack / Queues | Arrays/Linked lists |
| 10 | Binary Trees | Recursion + queues + linked-node thinking |
| 11 | Binary Search Trees | Binary trees + ordering |
| 12 | Heaps / Priority Queue | Arrays + trees + ordering |
| 13 | Greedy Algorithms | Sorting + heaps + proof thinking |
| 14 | Intervals | Sorting + greedy |
| 15 | Graphs | BFS/DFS + queues/stacks |
| 16 | Advanced Graphs | Graph basics + heaps + DSU concepts |
| 17 | Dynamic Programming – 1D | Recursion + memoization |
| 18 | Dynamic Programming – 2D | 1D DP + state transitions |
| 19 | Backtracking | Recursion + pruning |
| 20 | Tries | Trees + strings + hashing |
| 21 | Bit Manipulation | Integer representation |
| 22 | Strings – Advanced Algorithms | Strings + arrays + hashing |
| 23 | Math & Geometry | Basic mathematics + arrays |

### Why this order works

- **Arrays before Binary Search:** binary search usually operates over an ordered sequence, and understanding indices is essential.
- **Recursion before Trees:** tree DFS is recursion on a hierarchical structure.
- **Linked Lists before Trees:** both teach node/reference-based structures, pointer movement, and structural manipulation.
- **Stack/Queue before Graph BFS/DFS:** BFS naturally uses a queue; iterative DFS uses a stack.
- **Heaps before advanced shortest-path algorithms:** Dijkstra and similar methods commonly use a priority queue.
- **Recursion before DP and Backtracking:** both are often discovered by first writing the recursive state space.

The order is not absolute. In real interviews, patterns overlap heavily.

---

# 2. How to Read Any DSA Problem

## Step 1 — Read the last sentence first

The final sentence often tells you exactly what is expected:

- “Return the maximum…” → optimization
- “Return whether…” → decision problem
- “Count the number of…” → counting / frequency / DP
- “Find the shortest…” → BFS / sliding window / DP / binary search on answer
- “Find all…” → backtracking / combinatorics / graph traversal
- “Can you make…” → feasibility / greedy / DP / binary search on answer

Then read the input description.

---

## Step 2 — Circle the constraints

Examples:

- `n <= 10` → brute force may be fine
- `n <= 100` → `O(n²)` is commonly reasonable
- `n <= 2,000` → `O(n²)` may be intended
- `n <= 100,000` → usually `O(n log n)` or `O(n)`
- `n <= 1,000,000` → usually close to `O(n)`; memory also matters
- `2 <= n <= 10^5`, values huge/negative → think hashing, sorting, two pointers, etc.
- Answer range huge but feasibility is monotonic → consider binary search on answer

Constraints are **not just implementation details**. They are clues to the intended pattern.

---

## Step 3 — Identify whether order matters

Ask:

> “Would sorting the input destroy information I need?”

If no, sorting may unlock:

- Two pointers
- Greedy
- Interval merging
- Binary search
- Duplicate compression

If yes, preserve original positions with:

- `(value, index)` pairs
- Hash maps
- Auxiliary arrays
- Stable processing

---

## Step 4 — Ask whether the answer concerns a contiguous range

Words such as:

- subarray
- substring
- continuous
- consecutive
- window
- segment
- at most `k`
- exactly `k`
- longest / shortest contiguous

should immediately make you consider **Sliding Window / Prefix Sum / Two Pointers**.

But do not blindly choose sliding window. Sliding window depends on whether the condition can be maintained while expanding/shrinking.

---

## Step 5 — Ask whether you repeatedly need “have I seen this?”

This strongly suggests:

- HashSet
- HashMap
- Frequency map
- Prefix-state map

Typical phrases:

- duplicate
- already seen
- frequency
- count occurrences
- first occurrence
- last occurrence
- pair with target
- anagram
- distinct elements

---

## Step 6 — Ask whether there is monotonic behavior

This is a huge clue for binary search.

Suppose you ask:

> “Can I complete the work if the allowed capacity is `X`?”

If:

- `X = 10` → impossible
- `X = 20` → possible
- `X = 30` → possible
- `X = 40` → possible

then feasibility is monotonic:

`false false false true true true`

That means you can binary-search the answer even when the original array is **not sorted**.

---

# 3. Constraint → Complexity Cheat Sheet

These are practical interview/contest guidelines, not mathematical guarantees. Actual runtime depends on constants, language, memory limits, and operation cost.

| Input size / structure | Often acceptable | Usually suspicious / too slow |
|---|---|---|
| `n <= 10` | `O(2^n)`, `O(n!)` | Not much is forbidden |
| `n <= 20` | `O(2^n)`, backtracking with pruning | `O(n!)` often too large |
| `n <= 30-40` | Meet-in-the-middle, clever exponential | Full `O(2^n)` may be too large |
| `n <= 100` | `O(n³)` can be possible | Higher polynomial |
| `n <= 500` | `O(n²)`, some `O(n³)` with tight constants | Exponential |
| `n <= 2,000` | `O(n²)` commonly intended | `O(n³)` often risky |
| `n <= 10^4` | `O(n²)` usually suspicious; `O(n log n)` preferred | `O(n³)` |
| `n <= 10^5` | `O(n log n)`, `O(n)` | `O(n²)` usually too slow |
| `n <= 10^6` | `O(n)` / near-linear | `O(n log n)` may still be okay but memory/constants matter |
| `n <= 10^7+` | Streaming / very tight `O(n)` | Heavy objects / large memory |

## Binary search on answer complexity

If a feasibility check is `O(n)` and answer range is roughly `R`, binary search gives approximately:

`O(n log R)`

For example, if `R` is up to `10^18`, `log2(R)` is only about 60.

---

# 4. Pattern Identification Decision Tree

Use this during interviews.

```text
START
 |
 |-- Is the input an array/string?
 |      |
 |      |-- Need exact lookup/frequency/duplicate tracking?
 |      |       -> HASHING
 |      |
 |      |-- Is the data sorted, or can I sort it?
 |      |       |
 |      |       |-- Search one value / boundary?
 |      |       |       -> BINARY SEARCH
 |      |       |
 |      |       |-- Pair/triple / closest sum / remove duplicates?
 |      |               -> TWO POINTERS (+ SORTING)
 |      |
 |      |-- Is the required range contiguous?
 |      |       |
 |      |       |-- fixed/variable window?
 |      |       |       -> SLIDING WINDOW
 |      |       |
 |      |       |-- Range sum / exact sum?
 |      |               -> PREFIX SUM (+ HASHING / BINARY SEARCH)
 |      |
 |      |-- Need max/min answer and feasibility is monotonic?
 |              -> BINARY SEARCH ON ANSWER
 |
 |-- Is it a linked list?
 |      -> POINTERS / FAST-SLOW / REVERSAL / MERGING
 |
 |-- Is it a tree?
 |      |
 |      |-- Need hierarchy/traversal?
 |      |       -> DFS / BFS
 |      |-- Need ordering/search in BST?
 |      |       -> BST + BINARY SEARCH IDEA
 |
 |-- Is it a graph?
 |      |
 |      |-- Reachability/components?
 |      |       -> DFS / BFS / DSU
 |      |-- Shortest unweighted path?
 |      |       -> BFS
 |      |-- Shortest weighted path (non-negative)?
 |      |       -> DIJKSTRA + HEAP
 |      |-- DAG dependencies?
 |      |       -> TOPOLOGICAL SORT / DP
 |
 |-- Need all possibilities?
 |      |
 |      |-- Enumerate subsets/permutations/combinations?
 |      |       -> BACKTRACKING
 |      |-- Same recursive states repeat?
 |              -> DP
 |
 |-- Need repeated min/max retrieval?
 |      -> HEAP / PRIORITY QUEUE
 |
 |-- Intervals overlap / merge / scheduling?
 |      -> SORT + INTERVALS / GREEDY
 |
 |-- String prefix-related?
 |      -> TRIE
 |
 |-- Number/bit property?
 |      -> BIT MANIPULATION / MATH
 |
 '-- Geometry / coordinates / area / distance?
         -> MATH & GEOMETRY
```

This tree is a starting heuristic. The actual invariant of the problem matters more than keywords.

---

# 5. Beginner Problems

## What this category teaches

Before patterns, become comfortable with:

- loops
- conditions
- arrays
- strings
- functions
- integer arithmetic
- basic simulation
- time and space complexity

## Typical clues

The problem may simply ask you to:

- process every item
- count something
- find min/max
- reverse data
- simulate operations
- construct a result

## Typical complexity

Most beginner problems are:

- `O(n)`
- `O(n log n)`
- occasionally `O(n²)`

## Questions to ask

> “Can I solve this with one pass?”

> “Do I need extra memory?”

> “Can I maintain the answer while scanning?”

### Example

**Find the maximum value in an array.**

Pattern: simple array traversal.

Not every problem needs a named pattern.

---

# 6. Sorting

Sorting is often not the final pattern. It is an **enabler**.

## Strong clues

- “Arrange…”
- “closest pair”
- “minimum difference”
- “merge overlapping intervals”
- “schedule jobs”
- “find duplicates efficiently”
- “choose the smallest/largest next item”

## Why sorting helps

Sorting creates order:

`1 2 2 4 7 9`

Once ordered, many problems become easier:

- two pointers
- binary search
- greedy
- interval processing
- duplicate detection

## Complexity

Comparison sorting is generally `O(n log n)`.

For `n = 100,000`, sorting is usually practical.

## Common combinations

### Sorting + Two Pointers

**3Sum**, pair sum, closest sum, duplicate removal.

### Sorting + Greedy

Activity selection, scheduling, interval problems, resource allocation.

### Sorting + Hashing

Group/compare information while retaining additional metadata.

### Sorting + Binary Search

For every element, binary-search a partner or valid range.

## Recognition question

> “Would the problem become simpler if I knew everything was in order?”

If yes, try sorting.

---

# 7. Arrays

Arrays are the base for most interview patterns.

## Important array skills

Know:

- index movement
- in-place modification
- prefix/suffix information
- frequency arrays
- Kadane-style running state
- sorting
- subarrays
- rotation
- matrix traversal

## Common questions

- maximum/minimum
- duplicate removal
- prefix/suffix products
- subarray sum
- pair/triple problems
- rearrangement
- rotation
- maximum subarray

## Typical patterns that grow from arrays

`Arrays -> Hashing`

`Arrays -> Two Pointers`

`Arrays -> Sliding Window`

`Arrays -> Binary Search`

`Arrays -> Prefix Sum`

`Arrays -> Greedy`

`Arrays -> DP`

## Important recognition

**Subarray** means contiguous.

Example:

```text
[2, 3, 5, 7]
```

`[3,5]` is a subarray.

`[2,5]` is not a subarray because `3` was skipped.

That distinction is extremely important.

---

# 8. Hashing

Hashing is one of the most useful `O(n)` optimization tools.

## Think HASHING when you hear

- “Have I seen this before?”
- “frequency”
- “duplicate”
- “distinct”
- “count occurrences”
- “first/last occurrence”
- “two values sum to target”
- “anagram”
- “group equal information”

## Main data structures

### HashSet

Use when you care about existence.

```text
Have I already seen x?
```

### HashMap

Use when you care about a mapping.

```text
value -> count
value -> index
state -> frequency
key -> object
```

## Typical complexity

Expected average:

- insert: `O(1)`
- lookup: `O(1)`
- delete: `O(1)`

So a repeated `O(n)` lookup can often turn an `O(n²)` brute force into `O(n)`.

## Canonical example — Two Sum

Brute force:

```text
for i
  for j
    if a[i] + a[j] == target
```

Complexity: `O(n²)`.

Hashing idea:

For each `x`, ask:

> “Have I seen `target - x`?”

Now complexity becomes expected `O(n)`.

## Important overlap

Hashing can combine with almost every pattern:

- HashMap + sliding window
- HashMap + prefix sum
- HashMap + two pointers
- HashMap + DFS
- HashSet + BFS
- HashMap + DP

---

# 9. Binary Search

Binary search is **not only for sorted arrays**.

It is fundamentally about exploiting an ordered or monotonic search space.

## Type A — Search an ordered collection

Clues:

- sorted array
- find target
- lower bound
- upper bound
- first/last position
- smallest index satisfying condition

### Example

```text
[1, 3, 5, 7, 9]
```

Find the first value `>= 6`.

This is a boundary-search problem.

---

## Type B — Binary Search on Answer

This is one of the most important interview patterns.

Ask:

> “If I guess an answer X, can I quickly check whether X is feasible?”

Then ask:

> “As X gets larger/smaller, does feasibility change only once?”

If yes → binary search on answer.

### Typical phrases

- minimum possible maximum
- maximum possible minimum
- smallest capacity
- minimum speed
- minimum time
- maximum distance
- maximize the minimum
- minimize the maximum
- at most / at least with a guessed threshold

### Canonical examples

- Koko Eating Bananas
- Ship Packages Within D Days
- Split Array Largest Sum
- Aggressive Cows / maximize minimum distance
- Allocate Books
- Painter's Partition

## General formula

```text
low = smallest possible answer
high = largest possible answer

while low <= high:
    mid = low + (high - low) / 2
    if feasible(mid):
        answer = mid
        move toward better answer
    else:
        move the other way
```

## Constraint clue

Suppose:

- `n = 10^5`
- answer range = `1 ... 10^18`
- feasibility check = `O(n)`

Then brute-forcing every answer is impossible.

Binary search gives roughly:

`O(n log 10^18)` ≈ `O(60n)`.

That is a classic intended solution.

## Common overlap

### Binary Search + Greedy

The feasibility function often uses greedy counting.

### Binary Search + Prefix Sum

For range feasibility checks.

### Binary Search + Sorting

Sort once, then repeatedly query boundaries.

### Binary Search + Two Pointers

A problem may use sorting + pointer scanning inside a binary-search strategy.

---

# 10. Recursion

Recursion is both a problem-solving technique and a foundation for later patterns.

## Think recursion when

The problem naturally says:

> “Solve the same problem on a smaller part.”

Examples:

- tree traversal
- divide and conquer
- subsets
- permutations
- combinations
- recursive linked-list operations
- DFS

## Recursion recognition checklist

Ask:

1. What is the state?
2. What is the base case?
3. How does the state get smaller?
4. What should the recursive call return?
5. What work happens before/after the recursive call?

## Important recursion families

### Divide and conquer

Split problem into independent subproblems.

Examples:

- Merge Sort
- Quick Sort
- binary-search style recursion

### Tree recursion

One node → child subproblems.

### Backtracking recursion

Choose → recurse → undo.

### DP recursion

Recursive states repeat → memoize.

## Complexity warning

A recursion tree can explode:

`T(n) = 2T(n-1) + O(1)`

is exponential.

Do not assume recursion is efficient simply because the code is short.

---

# 11. Linked List

Learn linked lists before trees because they build intuition around **nodes and references**.

## Think linked list when

Input looks like:

```text
1 -> 4 -> 7 -> 9 -> null
```

Typical problems:

- reverse list
- detect cycle
- find middle
- merge two lists
- remove Nth node
- reorder list
- intersection
- palindrome

## Core pointer patterns

### Pattern 1 — Fast and slow pointers

Use when you need:

- middle node
- cycle detection
- cycle entry
- compare halves

### Pattern 2 — Dummy node

Useful when the head may change.

Typical problems:

- remove nodes
- merge lists
- partition lists

### Pattern 3 — Reverse pointers

```text
prev <- current -> next
```

becomes

```text
prev <- current
```

while storing `next`.

### Pattern 4 — Two list pointers

Move two lists together:

- merge sorted lists
- intersection
- synchronized traversal

## Common overlap

Linked list + two pointers is extremely common.

Linked list + recursion is also common.

Linked list + hashing can solve intersection/cycle-related variations, though it may use extra memory when a pointer solution is possible.

---

# 12. Sliding Window / Two Pointers

These are related but not identical.

## Two Pointers

Usually two indices move through a sequence.

Common forms:

- left/right ends
- slow/fast
- read/write pointers
- two sequences merged

## Sliding Window

A **contiguous** region `[left ... right]` is maintained.

```text
left                right
 |                    |
 v                    v
[a, b, c, d, e, f, g]
      <--- window --->
```

## Strong sliding-window clues

- longest substring
- shortest subarray
- maximum/minimum in every window
- at most `k`
- no repeating characters
- exactly `k` distinct
- replace up to `k`
- continuous segment

## Fixed-size window

Example:

> Maximum sum of any subarray of size `k`.

Algorithm:

1. Add `right`.
2. If window size exceeds `k`, remove `left`.
3. Track answer.

Complexity: `O(n)`.

## Variable-size window

Example:

> Smallest subarray with sum >= target.

Use:

```text
expand right
while condition satisfied:
    update answer
    shrink left
```

## Important limitation

Classic sum-based sliding window works naturally when the relevant condition is monotonic under expansion/shrinking, such as positive numbers.

With arbitrary negative numbers, many naive sliding-window approaches fail.

That may push you toward:

- prefix sums
- monotonic deque
- hashing
- binary search
- DP

## Two pointers after sorting

Example:

> Determine whether any pair sums to target.

Sorted array:

```text
left = 0
right = n - 1
```

If sum too small → `left++`.

If sum too large → `right--`.

Complexity: sorting `O(n log n)` + scan `O(n)`.

## Hashing vs Two Pointers

Suppose the problem is Two Sum.

### Hashing

- `O(n)` expected
- `O(n)` memory
- preserves original indices naturally

### Sort + two pointers

- `O(n log n)`
- lower conceptual memory if done in-place
- may complicate original index tracking

Both can solve the same keyword-heavy question.

---

# 13. Stack / Queue

## Stack = “last thing added is processed first”

Think stack when the problem has:

- nested structure
- undo
- matching brackets
- previous greater/smaller
- next greater/smaller
- expression evaluation
- monotonic behavior

## Classic stack problems

- Valid Parentheses
- Min Stack
- Daily Temperatures
- Next Greater Element
- Largest Rectangle in Histogram
- Evaluate Postfix
- Decode String

## Monotonic Stack

A very important advanced pattern.

Think:

> “For each element, I need the nearest previous/next element that is greater/smaller.”

Keywords:

- next greater
- next smaller
- previous greater
- previous smaller
- nearest larger/smaller
- warmer day
- how many until a larger value

## Queue

Think queue for:

- BFS
- first-in-first-out processing
- level-order traversal
- task processing
- moving windows with special structures

## Deque

Useful for:

- sliding-window maximum
- monotonic queue
- BFS variants such as 0-1 BFS

---

# 14. Binary Trees

A binary tree problem is usually a traversal/state problem.

## Recognize from input

```text
        5
       / \
      3   8
     / \   \
    1   4   9
```

## First question

> “Do I need information from the left subtree, right subtree, or both?”

If yes → DFS is often natural.

## DFS traversals

### Preorder

`root -> left -> right`

Useful for:

- copying/serialization ideas
- construction
- root-first processing

### Inorder

`left -> root -> right`

Especially important for BSTs because it gives sorted order.

### Postorder

`left -> right -> root`

Useful when the parent depends on results from children.

Examples:

- subtree height
- balance checking
- diameter
- maximum path sum
- deleting/freeing subtrees

## BFS / Level order

Use a queue.

Strong clues:

- level by level
- nearest node
- minimum number of edges in an unweighted tree
- right side view
- zigzag levels

## Typical tree state

A recursive function may return:

- height
- boolean valid/invalid
- max/min value
- pair of values
- global answer update

## Common overlap

- Tree + recursion
- Tree + stack
- Tree + queue
- Tree + hashing
- Tree + DP
- Tree + BFS
- Tree + DFS

---

# 15. Binary Search Trees (BST)

BST adds a crucial property:

```text
left values < root < right values
```

This means search can often be guided like binary search.

## Think BST when

- insert/search/delete in ordered tree
- predecessor/successor
- kth smallest/largest
- validate BST
- range queries

## Important clue

> “Inorder traversal of a BST is sorted.”

So if asked for kth smallest:

- perform inorder traversal
- stop at kth node

## Complexity

Balanced BST:

- search ≈ `O(log n)`
- insertion ≈ `O(log n)`
- deletion ≈ `O(log n)`

Worst-case unbalanced tree:

- `O(n)`

Never assume a BST is balanced unless guaranteed or implemented as one.

---

# 16. Heaps / Priority Queue

Use a heap when you repeatedly need the current:

- minimum
- maximum
- kth largest/smallest
- next best candidate

## Strong clues

- “top K”
- “kth largest”
- “kth smallest”
- repeatedly remove minimum
- repeatedly choose maximum
- merge K sorted lists
- schedule next available job
- always process the smallest/largest available item

## Top K pattern

For `k` small relative to `n`, a heap can avoid full sorting.

Example:

> Find kth largest element.

Min-heap of size `k`:

- push each element
- if size > `k`, pop smallest
- heap top = kth largest

Complexity:

`O(n log k)`

This can be better than sorting `O(n log n)`.

## Heap vs sorting

### Sorting

`O(n log n)`

### Heap size k

`O(n log k)`

When `k << n`, heap can be attractive.

## Heap + Graph

Dijkstra is a major example:

```text
Graph traversal + shortest distance + repeatedly choose minimum distance
=> Priority Queue
```

---

# 17. Greedy Algorithms

Greedy means making a locally optimal-looking choice while relying on a property/proof that this choice can lead to a global optimum.

Do not use greedy merely because it is fast.

## Clues

- choose earliest finishing interval
- choose smallest/largest available item
- minimum number of resources
- maximize count
- minimize removals
- schedule as many activities as possible

## Typical ingredients

Greedy often combines with:

- sorting
- heap
- intervals
- two pointers

## How to test a greedy idea

Ask:

> “Why can I safely take this choice now?”

Good greedy solutions usually have a proof idea such as:

- exchange argument
- staying ahead
- dominance
- earliest finish leaves maximum future room

## Example

Activity selection:

> Select maximum non-overlapping activities.

Sort by finishing time, then always choose the next activity that finishes earliest.

Pattern:

`Intervals + Sorting + Greedy`

---

# 18. Intervals

Intervals are often a disguised sorting + greedy problem.

Input looks like:

```text
[1,3], [2,6], [8,10], [9,12]
```

## Strong clues

- overlap
- merge
- meeting rooms
- schedule
- start/end
- interval coverage
- minimum rooms/resources
- insert interval

## First instinct

Sort by start time unless the problem clearly requires another ordering.

Then process left to right.

## Common patterns

### Merge Intervals

If current start `<= previous end`, they overlap.

### Meeting Rooms

Can also become a heap problem.

### Minimum Rooms

Common alternatives:

- min-heap of end times
- start/end event sorting
- two pointers on sorted starts and ends

Thus the same problem may have several valid patterns.

---

# 19. Graphs

Recognize a graph even if the word “graph” is never used.

## Hidden graph clues

- cities and roads
- people and friendships
- courses and prerequisites
- computers and connections
- cells connected to neighboring cells
- flights between airports
- dependencies

If entities are **nodes** and relationships are **edges**, it is a graph.

## First classification

Ask:

1. Directed or undirected?
2. Weighted or unweighted?
3. Cyclic or acyclic?
4. One source or many?
5. Connected components?
6. Shortest path?
7. Dependency ordering?

---

## BFS

Use BFS for shortest path in an **unweighted** graph when each edge has equal cost.

Strong clues:

- minimum number of steps
- shortest number of moves
- nearest
- level by level

Complexity with adjacency list:

`O(V + E)`

---

## DFS

Use DFS for:

- reachability
- connected components
- cycle detection
- exploring all reachable states
- island counting
- graph/tree traversal

Complexity:

`O(V + E)`

---

## Connected Components

Typical clue:

> “How many groups/networks/islands are there?”

Patterns:

- DFS
- BFS
- DSU / Union-Find

---

## Topological Sort

Think topological sorting when the problem says:

- prerequisite
- dependency
- task ordering
- course schedule
- build order

Only directed acyclic graphs have a valid topological ordering.

Two common approaches:

- DFS with cycle detection
- Kahn's algorithm using indegrees + queue

---

# 20. Advanced Graphs

Once basic BFS/DFS are strong, move to:

- Dijkstra
- 0-1 BFS
- Bellman-Ford
- Floyd-Warshall
- Minimum Spanning Tree
- DSU / Union-Find
- bridges/articulation points
- strongly connected components
- advanced DAG DP

## Decision guide

| Problem property | Pattern |
|---|---|
| Unweighted shortest path | BFS |
| Edge weights are 0 or 1 | 0-1 BFS |
| Non-negative weights | Dijkstra + heap |
| Negative edges | Bellman-Ford / specialized reasoning |
| All-pairs shortest paths, small V | Floyd-Warshall |
| Connect all nodes with minimum total edge cost | MST |
| Dynamic merging of components | DSU |
| Dependencies | Topological Sort |

## Dijkstra recognition

Typical wording:

> “Minimum cost to travel from A to B”

where edge weights are non-negative.

The pattern becomes:

`Graph + shortest path + minimum candidate -> Priority Queue`

---

# 21. Dynamic Programming — 1D

DP often starts from recursion.

The key question is:

> “Am I solving the same smaller state repeatedly?”

If yes, cache it.

## Two conditions for DP

### 1. Overlapping subproblems

Same state appears multiple times.

### 2. Optimal substructure

The solution can be built from solutions to smaller states.

## Strong DP clues

- number of ways
- minimum cost
- maximum profit
- can/cannot reach
- choose or skip
- best answer up to index i
- longest subsequence
- minimum steps

## Typical 1D state

```text
dp[i] = best answer considering first i elements
```

or

```text
dp[i] = answer when currently at position i
```

## Common patterns

### Fibonacci / climbing stairs

`dp[i]` depends on previous states.

### House Robber

At each position:

- skip
- take

Typical relation:

```text
dp[i] = max(dp[i-1], dp[i-2] + value[i])
```

### Coin Change

State often represents minimum coins or number of ways for an amount.

---

# 22. Dynamic Programming — 2D

Use 2D DP when a state needs two dimensions.

Common shapes:

```text
dp[i][j]
```

## Strong clues

- two strings
- grid movement
- two changing quantities
- capacity + item
- position + remaining resource
- rows + columns
- source index + target index

## Major families

### Grid DP

Examples:

- unique paths
- minimum path sum
- obstacles

### Knapsack

Typical state:

`dp[item][capacity]`

### LCS

Two sequences:

`dp[i][j] = answer for prefixes i and j`

### Edit Distance

Two strings + operations.

## Important overlap

2D DP may come from recursion + memoization.

A grid problem may also involve BFS if every move has equal cost.

A weighted grid may use:

- DP when movement is acyclic/right-down
- Dijkstra when movement has cycles or general non-negative edge costs

This is a good example of why the **movement rules** matter more than the words “grid”.

---

# 23. Backtracking

Backtracking is for exploring a search space of choices, usually when you need **all valid possibilities** or need to find a valid configuration.

## The core template

```text
choose
explore
undo
```

## Strong clues

- all subsets
- all permutations
- all combinations
- generate parentheses
- N-Queens
- Sudoku
- word search
- partition into valid groups

## Complexity

Backtracking is often exponential:

- subsets → `O(2^n)` states
- permutations → `O(n!)`

Therefore, **constraints are crucial**.

### Example

If `n = 20`, subset enumeration may be plausible.

If `n = 100,000`, an `O(2^n)` approach is obviously impossible.

## Backtracking vs DP

Ask:

> “Do I need every possibility, or only the best/count/feasibility over repeated states?”

- Every possibility → Backtracking
- Repeated state + optimize/count → DP

Sometimes both appear:

`Backtracking + memoization = DP over a search space`

---

# 24. Tries

A Trie is especially useful when the problem is about **prefixes**.

## Strong clues

- dictionary of words
- starts with
- autocomplete
- prefix search
- word prefixes
- lexicographic word navigation

## Key difference from HashSet

HashSet answers:

> “Does the complete word exist?”

Trie efficiently supports:

> “Does any word start with this prefix?”

## Common combinations

### Trie + DFS

Word Search II.

### Trie + Backtracking

Explore a board while walking a word-prefix structure.

### Trie + Strings

Prefix matching/autocomplete.

---

# 25. Bit Manipulation

Think bits when the problem has:

- XOR
- binary representation
- powers of two
- set/unset/toggle bit
- subset masks
- parity
- unique element where duplicates cancel

## Essential operations

```text
x & 1          -> lowest bit
x << 1         -> multiply by 2 (within overflow limits)
x >> 1         -> shift right
x ^ y           -> XOR
x & (x - 1)    -> remove lowest set bit
```

## Important identities

### XOR cancellation

```text
a ^ a = 0
a ^ 0 = a
```

So in:

```text
[4, 1, 2, 1, 2]
```

XOR of everything gives `4`.

### Check power of two

For positive `x`:

```text
(x & (x - 1)) == 0
```

## Bitmask + Backtracking / DP

For `n <= 20`, a subset can sometimes be represented by an `n`-bit mask.

That combines:

`Bit Manipulation + DP`

or

`Bit Manipulation + Backtracking`

---

# 26. Strings — Advanced Algorithms

Basic string problems often use arrays/hashing. Advanced string problems may require specialized structures.

## Common advanced topics

- KMP / prefix function
- Z algorithm
- rolling hash
- suffix structures
- Manacher's algorithm
- string DP
- Trie

## Recognition examples

### Pattern appears inside text repeatedly

Think KMP / Z / rolling hash.

### Longest palindromic substring

Could be:

- expand around center
- DP
- Manacher for linear time

### Prefix-heavy dictionary problem

Think Trie.

## Constraint clue

If `n <= 2,000`, an `O(n²)` palindrome solution may be fine.

If `n` is very large and a linear-time string algorithm is expected, consider KMP/Z/Manacher depending on the structure.

---

# 27. Math & Geometry

Many problems look like DSA problems but are actually mathematical.

## Strong clues

- coordinates
- distance
- area
- slope
- gcd/lcm
- divisibility
- primes
- modulo arithmetic
- combinatorics
- exponentiation
- number theory

## Common tools

- Euclidean GCD
- fast exponentiation
- sieve of Eratosthenes
- modular arithmetic
- prefix sums
- coordinate compression
- determinant/cross product
- orientation tests

## Geometry recognition

For points `A, B, C`, cross product/orientation can tell whether the turn is:

- clockwise
- counter-clockwise
- collinear

This often solves geometry ordering/intersection problems without floating-point calculations.

---

# 28. Common Pattern Combinations

Real interview questions frequently combine 2–4 patterns.

## Array + Hashing

### Clues

- frequency
- duplicate
- target pair
- first occurrence

Example:

Two Sum.

---

## Sorting + Two Pointers

### Clues

- pair/triple
- closest sum
- duplicates
- sorted relationship needed

Examples:

- 3Sum
- 4Sum
- container-like problems (sometimes)

---

## Hashing + Sliding Window

### Clues

- longest substring with constraints
- at most K distinct
- character frequencies

Examples:

- Longest Substring Without Repeating Characters
- Minimum Window Substring
- Longest Repeating Character Replacement

---

## Prefix Sum + Hashing

### Clues

- exact subarray sum
- number of subarrays with sum `k`
- negative numbers prevent simple sliding window

Classic pattern:

```text
currentPrefix - oldPrefix = target
```

Store prefix frequencies in a map.

---

## Binary Search + Greedy

### Clues

- minimum possible maximum
- maximum possible minimum
- capacity/speed/time/distance
- feasibility check can be greedy

Examples:

- Ship packages
- Koko bananas
- aggressive cows

---

## Heap + Hashing

### Clues

- frequency + top K
- most frequent elements
- dynamic candidate set

Example:

Top K Frequent Elements.

---

## Graph + BFS + HashSet

### Clues

- shortest number of transformations
- visited states
- implicit graph

Example:

Word Ladder.

The graph may not be explicitly provided. Each word is a node; one-character transformations are edges.

---

## Graph + Heap

### Clues

- shortest weighted path
- repeatedly choose minimum distance

Example:

Dijkstra.

---

## Graph + DFS + DP

### Clues

- directed graph
- DAG
- count/best path under dependency order

Example:

Longest path in a DAG.

---

## Backtracking + Trie

### Clues

- search words on board
- dictionary + path exploration

Example:

Word Search II.

---

## DP + Hashing

### Clues

A DP state is not naturally indexed by a small integer, so use a map.

Example concept:

```text
Map<State, BestAnswer>
```

---

# 29. Same Keywords, Different Patterns

This is one of the most important interview skills.

## Keyword: “Longest”

Could mean:

- longest subarray → Sliding Window / Prefix Sum
- longest substring → Sliding Window / DP / String Algorithms
- longest increasing subsequence → DP / Binary Search optimization
- longest path in tree → Tree DFS / DP
- longest path in DAG → Graph DP

So “longest” tells you the objective, **not the algorithm**.

---

## Keyword: “Minimum”

Could mean:

- minimum in array → simple traversal
- minimum window → Sliding Window
- minimum cost path → BFS / Dijkstra / DP
- minimum capacity → Binary Search on Answer
- minimum number of intervals → Greedy / Heap
- minimum spanning tree → Prim/Kruskal

“Minimum” tells you the goal, not the pattern.

---

## Keyword: “Pair”

Could mean:

- HashMap Two Sum
- sorted Two Pointers
- binary search partner
- sorting + two pointers
- DP over pairs

---

## Keyword: “Closest”

Could mean:

- sorted two pointers
- binary search nearest value
- heap
- tree predecessor/successor
- geometry distance

---

## Keyword: “K”

`k` can suggest very different approaches:

- fixed window size `k` → Sliding Window
- kth largest → Heap / Quickselect
- at most `k` distinct → Sliding Window + HashMap
- choose `k` items → Greedy / Heap / DP / Backtracking
- exactly `k` operations → DP / BFS / Prefix-state techniques

Never treat `k` itself as a pattern.

---

# 30. Constraint-Driven Pattern Selection

## Case 1 — `n <= 20`

Exponential solutions may be intended.

Consider:

- Backtracking
- Bitmasking
- Subsets
- Permutations
- Meet-in-the-middle

---

## Case 2 — `n <= 2,000`

Often:

- `O(n²)`
- occasionally optimized `O(n² log n)`

Possible patterns:

- DP
- two loops + hashing
- quadratic palindrome checks
- graph adjacency matrix algorithms for smaller V

---

## Case 3 — `n <= 100,000`

Usually think:

- `O(n)`
- `O(n log n)`

Common patterns:

- Hashing
- Sorting + two pointers
- Sliding Window
- Binary Search
- Heap
- Greedy
- BFS/DFS

Be suspicious of nested loops unless one pointer only moves forward or the inner loop has a strictly limited total number of operations.

---

## Case 4 — Huge answer range + `n <= 100,000`

A classic signal for:

**Binary Search on Answer**

especially when a linear-time feasibility check exists.

---

## Case 5 — `n <= 20`, “all possible”

Think:

**Backtracking / Bitmask**

---

## Case 6 — `V, E <= 100,000`

Think:

- adjacency list
- BFS/DFS
- topological sorting
- Dijkstra if weighted
- DSU for components/MST variants

Avoid adjacency matrix unless `V` is small enough.

---

# 31. Time Complexity Patterns You Should Recognize Instantly

## `O(n)`

Typical:

- one pass
- HashMap/HashSet
- two pointers
- sliding window
- BFS/DFS over graph with `V+E`

## `O(n log n)`

Typical:

- sorting
- heap operations over n items
- divide-and-conquer
- binary search repeated over n queries

## `O(n²)`

Typical:

- all pairs
- simple DP table
- brute force pair/triple preparation

Only choose this when constraints support it.

## `O(2^n)`

Typical:

- subsets
- choose/not choose
- some backtracking

## `O(n!)`

Typical:

- permutations

Usually only reasonable for very small `n`.

---

# 32. Memory Constraints Also Change the Pattern

Do not only ask:

> “Will it finish in time?”

Also ask:

> “Can I fit the data?”

## Example

Suppose `n = 10^7`.

An `O(n)` algorithm is good in time, but:

- `HashMap<Integer,Integer>` may consume a lot of memory.
- object-heavy structures can be expensive in Java.

A primitive array may be much more memory-efficient.

## Typical memory-aware choices

- array instead of object-heavy map when values are bounded
- in-place two pointers instead of auxiliary arrays
- iterative DFS instead of deep recursion when stack depth is risky
- heap of size `k` instead of storing all candidates

---

# 33. Input Value Range Is a Hidden Clue

The value range can tell you which structure to use.

## Values small, e.g. `0 <= x <= 10^5`

Frequency array may beat HashMap.

```text
int[] freq = new int[100001];
```

## Values huge / negative / arbitrary

HashMap/HashSet is often better.

## Coordinates huge but only relative order matters

Consider **coordinate compression**.

---

# 34. “Can Sort” vs “Must Preserve Order”

This distinction is extremely important.

## Sorting is safe when

The problem only cares about values/relationships.

Example:

> Find whether any pair sums to target.

## Sorting may break the problem when

The original indices/order are important.

Example:

> Return original indices of the two elements.

You can still sort, but you need to retain original indices.

---

# 35. Prefix Sum: The Missing Pattern Between Arrays and Sliding Window

Although not always shown as a separate category in pattern lists, prefix sum is an extremely important technique.

## Recognize it from

- repeated range sum queries
- sum of subarray
- exact sum
- cumulative totals
- many interval sum calculations

For array:

```text
a = [2, 4, 1, 3]
```

Prefix:

```text
p = [0, 2, 6, 7, 10]
```

Then:

```text
sum(l..r) = p[r+1] - p[l]
```

## Prefix sum + HashMap

Very powerful when asked:

> “How many subarrays have sum exactly `k`?”

Store how many times each prefix sum occurred.

This is often the correct solution when negative values exist and simple sliding window fails.

---

# 36. Monotonic Stack vs Sliding Window vs Heap

These can look similar because all maintain a changing set of candidates.

## Use Sliding Window when

The candidates form a **contiguous range** `[left, right]`.

## Use Monotonic Stack when

For every element, you need the nearest candidate satisfying greater/smaller behavior.

## Use Heap when

You need the globally smallest/largest candidate among an active set.

### A useful comparison

| Need | Pattern |
|---|---|
| Contiguous active range | Sliding Window |
| Nearest greater/smaller | Monotonic Stack |
| Global min/max active candidate | Heap |

---

# 37. BFS vs DFS vs DP

These are commonly confused.

## BFS

Think:

> “I want minimum number of edges/steps.”

Best when each transition has equal cost.

## DFS

Think:

> “I want to explore a structure completely.”

Useful for connectivity, components, paths, tree properties.

## DP

Think:

> “Many recursive states repeat, and I need a count/best/feasibility result.”

### Example: Grid

**Question:** Can I reach the destination?

→ DFS/BFS may be enough.

**Question:** Minimum number of moves, all moves equal cost?

→ BFS is natural.

**Question:** Number of ways to reach destination with only right/down moves?

→ DP.

**Question:** Minimum path cost with positive weights and arbitrary movement?

→ Dijkstra may be appropriate.

Same grid. Different pattern because the **question and transition costs differ**.

---

# 38. Greedy vs DP

This is another major interview distinction.

## Greedy

Choose the best current option and never reconsider it.

## DP

Consider multiple choices, but reuse results of states.

### Example idea

Suppose you can take or skip each item.

If choosing locally largest can block a better future solution:

→ Greedy may fail.

If the problem can be represented as:

```text
dp[i] = best of several previous states
```

→ DP may be needed.

## Recognition rule

Ask:

> “Can I prove that the local decision is always safe?”

If you cannot, do not assume greedy.

---

# 39. Binary Search vs Two Pointers

## Binary Search

Usually discards about half of an ordered/monotonic search space.

Think:

> “I am searching for one boundary or answer.”

## Two Pointers

Usually scans a sequence with pointers moving forward.

Think:

> “I am maintaining two positions in the data.”

### Example

Find pair sum in sorted array:

→ Two pointers.

Find first value >= target:

→ Binary search.

Find minimum feasible capacity:

→ Binary search on answer.

---

# 40. Backtracking vs Brute Force

Both explore possibilities, but backtracking cuts invalid branches early.

Brute force:

```text
generate everything
then validate
```

Backtracking:

```text
choose
if already invalid:
    stop this branch
otherwise:
    continue
undo
```

The pruning can reduce actual work dramatically, even though worst-case complexity can remain exponential.

---

# 41. “All” vs “Any” Changes the Algorithm

Look carefully at the wording.

## “Does there exist…”

You may be able to stop at the first valid solution.

Potential patterns:

- HashSet lookup
- BFS/DFS
- Binary Search
- Greedy feasibility
- Backtracking with early return

## “Return all…”

Usually must enumerate solutions.

Potential patterns:

- Backtracking
- Graph traversal collecting results
- Trie traversal

## “Count all…”

Usually suggests:

- Hashing
- Prefix sum + map
- DP
- combinatorics

---

# 42. “Shortest” Deserves Immediate Attention

When you read:

> shortest / minimum number of steps / minimum operations

ask these questions:

### Is this an unweighted graph?

→ BFS.

### Is it a positive/non-negative weighted graph?

→ Dijkstra or another shortest-path algorithm.

### Is it a linear contiguous range?

→ Sliding Window may apply.

### Is it a numeric answer with monotonic feasibility?

→ Binary Search on Answer.

### Is it a sequence optimization with repeated states?

→ DP.

No single “minimum pattern” exists.

---

# 43. Build the Brute Force First

In an interview, saying:

> “The brute-force approach is `O(n²)` because I try every pair. With `n <= 10^5`, that will not fit. The repeated operation is checking whether the complement exists, so I can use a HashMap.”

shows much stronger problem-solving than jumping straight to a memorized pattern.

## The optimization process

```text
Brute force
   ↓
Find repeated work
   ↓
Identify exploitable property
   ↓
Choose data structure / pattern
   ↓
Prove invariant
   ↓
Implement
   ↓
Validate edge cases
```

This is the workflow to practice.

---

# 44. Interview Explanation Template

When you know the pattern, explain it in this order.

## 1. Clarify

> “Do we need original indices?”

> “Can values be negative?”

> “Are edge weights non-negative?”

> “Can intervals be empty?”

## 2. Brute force

State the obvious solution and complexity.

## 3. Bottleneck

Explain what makes brute force too slow.

## 4. Key observation

State the property that enables optimization.

## 5. Pattern

Example:

> “Because we need the longest contiguous range and can expand/shrink while maintaining the condition, this is a variable-size sliding window.”

## 6. Invariant

Example:

> “The window always contains at most K distinct values.”

## 7. Complexity

State:

- time
- extra space

## 8. Edge cases

Then code.

---

# 45. Competitive Coding Reading Process

During a contest, use this fast process.

## Pass 1 — Read the statement

Identify:

- input
- output
- constraints
- exact objective

## Pass 2 — Ignore the story

Convert the story into data structures.

For example:

> “There are cities connected by roads.”

becomes:

```text
Graph + edges
```

> “Customer visits contiguous days.”

becomes:

```text
Subarray / interval / window
```

## Pass 3 — Estimate brute force

Ask:

```text
n = ?
Brute complexity = ?
Would it fit?
```

## Pass 4 — Find the repeated work

Examples:

- repeated lookup → HashMap
- repeated range sum → Prefix Sum
- repeated min → Heap
- repeated state → DP
- repeated search over ordered domain → Binary Search

## Pass 5 — Identify invariant

This is often the final clue.

---

# 46. Invariants You Should Know

Patterns become much easier when you understand the invariant.

## Two Pointers

After moving a pointer, a known impossible/invalid region is eliminated.

## Sliding Window

The current window always satisfies the maintained condition.

## Binary Search

One side of the search space is known to contain/no longer contain the answer.

## Monotonic Stack

The stack remains increasing or decreasing.

## BFS

Nodes are visited in nondecreasing distance from the source in an unweighted graph.

## DFS

The current recursion path represents the exploration state.

## Heap

The top element is the smallest/largest among elements currently stored.

## DP

`dp[state]` stores the solved answer for that state.

## Backtracking

Current path/state is valid before exploring the next choice.

---

# 47. Common Interview Traps

## Trap 1 — Keyword matching

Seeing “longest” and immediately choosing sliding window.

Wrong because longest subsequence, tree path, and DP problems also exist.

## Trap 2 — Using Binary Search because an array is sorted

Sorted data does not automatically mean binary search.

Maybe the problem needs two pointers.

## Trap 3 — Using Sliding Window with negative numbers

Many standard sum-window assumptions break when negatives are allowed.

## Trap 4 — Using Greedy without proof

A fast algorithm that is incorrect is still incorrect.

## Trap 5 — Ignoring original indexes

Sorting can destroy required positional information.

## Trap 6 — Ignoring duplicates

Two pointers, hashing, intervals, and BST logic can all behave differently with duplicates.

## Trap 7 — Ignoring overflow

A Java `int` can overflow around 2.1 billion.

Use `long` when products/sums can exceed the `int` range.

## Trap 8 — Recursive stack overflow

For a long linked list or highly skewed tree, recursion may become too deep.

---

# 48. Pattern Recognition by Problem Shape

| Problem shape | First patterns to consider |
|---|---|
| One-pass array statistic | Arrays |
| Need seen/frequency | Hashing |
| Sorted lookup | Binary Search |
| Pair in sorted array | Two Pointers |
| Contiguous segment | Sliding Window / Prefix Sum |
| Exact subarray sum | Prefix Sum + HashMap |
| Next greater/smaller | Monotonic Stack |
| Top K | Heap |
| Repeated min/max candidate | Heap |
| Nested/matching structure | Stack |
| Middle/cycle in list | Fast/Slow pointers |
| Tree property | DFS / BFS |
| Ordered tree | BST |
| Unweighted shortest path | BFS |
| Weighted shortest path | Dijkstra / advanced graph |
| Components/connectivity | DFS/BFS/DSU |
| Dependencies | Topological Sort |
| All choices | Backtracking |
| Repeated recursive states | DP |
| Prefix of words | Trie |
| XOR/powers of two/subsets | Bit Manipulation |
| Intervals | Sort + Greedy/Heap |
| Coordinates | Math/Geometry |

---

# 49. A More Precise “Question → Pattern” Table

| If the question asks… | Strong candidates |
|---|---|
| Is X present? | HashSet / Binary Search if sorted |
| Count frequency | HashMap / frequency array |
| Two values sum to target | HashMap / Sorting + Two Pointers |
| Triplets sum to target | Sort + Two Pointers |
| Longest valid substring | Sliding Window |
| Longest subarray with exact sum | Prefix Sum + HashMap; sometimes other methods depending on constraints |
| First index satisfying condition | Binary Search |
| Minimum feasible capacity | Binary Search on Answer |
| Kth largest | Heap / Quickselect |
| Top K frequent | HashMap + Heap / bucket-style methods |
| Next greater element | Monotonic Stack |
| Merge intervals | Sorting + Intervals |
| Maximum non-overlapping intervals | Greedy + Sorting |
| Minimum rooms | Heap / sweep-line sorting |
| Reverse linked list | Pointer manipulation |
| Middle linked-list node | Fast/Slow pointers |
| Detect list cycle | Fast/Slow pointers |
| Tree height | DFS / recursion |
| Tree level order | BFS / queue |
| Validate BST | Inorder / bounds |
| Kth smallest in BST | Inorder |
| Connected components | DFS/BFS/DSU |
| Shortest unweighted path | BFS |
| Shortest weighted path | Dijkstra / other shortest path |
| Course prerequisites | Topological Sort |
| All subsets/permutations | Backtracking / bitmask |
| Number of ways | DP / combinatorics |
| Minimum cost with repeated states | DP |
| Prefix dictionary lookup | Trie |
| Unique number using cancellation | XOR |
| Grid minimum path with right/down only | DP |
| Grid shortest path where each move costs 1 | BFS |
| Min/max answer under a threshold | Binary Search on Answer + feasibility |

---

# 50. Advanced Constraint Signals

Certain constraints strongly suggest particular strategies.

## `n <= 20` + “all subsets”

→ `O(2^n)`.

## `n <= 20` + “all permutations”

→ `O(n!)`.

## `n <= 10^5` + “all pairs”

Do **not** literally inspect all pairs.

Look for:

- sorting + two pointers
- hashing
- binary search
- frequency counting

## `n <= 10^5` + “number of subarrays”

Likely:

- sliding window
- prefix sum + HashMap
- monotonic stack/deque depending on property

## `n <= 10^5` + “minimum possible X”

Immediately test for:

**Binary Search on Answer.**

## `V,E <= 10^5` + “shortest path”

Classify weight type first.

- equal weights → BFS
- non-negative arbitrary weights → Dijkstra
- DAG → topological DP may work

---

# 51. Recognizing Hidden Graph Problems

Some of the hardest interview questions are graphs disguised as something else.

## Grid

A cell can be a node.

Neighbors are edges.

Then:

```text
Grid + movement rules
=> Graph
```

## Word transformations

Each word is a node.

One-character change is an edge.

```text
Word Ladder => implicit graph + BFS
```

## Courses

Each course is a node.

Prerequisite is a directed edge.

```text
Course Schedule => directed graph + topological sort
```

## Social network

People = nodes.

Friend relationships = edges.

```text
Mutual connection / groups => Graph traversal
```

This translation from **story → graph** is a core competitive-programming skill.

---

# 52. Recognizing Hidden DP Problems

DP can also be disguised.

Suppose the statement says:

> “At every index, you can either take the item or skip it.”

Ask:

> “If I arrive at the same index with the same remaining state twice, am I recomputing the same problem?”

If yes:

```text
Recursion
   ↓
Memoization
   ↓
DP
```

## Typical hidden states

- index
- index + previous value
- index + remaining capacity
- position + remaining steps
- row + column
- two string indices
- node + previous state
- mask + position

The hardest part of DP is often not coding. It is finding the correct **state**.

---

# 53. How to Derive a DP State

Ask:

> “What information completely determines the remaining problem?”

Example:

House Robber.

At index `i`, the future only depends on:

- current index
- previous decision constraints

So a 1D state is enough.

For two-string comparison:

```text
(i, j)
```

may be required because both positions affect the remaining problem.

For knapsack:

```text
(i, capacity)
```

is a natural state.

---

# 54. How to Identify Binary Search on Answer Precisely

Use this four-question test:

### Q1
Is the answer numeric or can it be represented by a scalar threshold?

### Q2
Can I guess a candidate answer `X`?

### Q3
Can I write a feasibility function:

```text
possible(X)
```

in much less than the total answer-range size?

### Q4
Is `possible(X)` monotonic?

For example:

```text
X:        1 2 3 4 5 6 7 8
possible: F F F F T T T T
```

or:

```text
T T T T F F F F
```

If yes, Binary Search on Answer is a serious candidate.

---

# 55. Why Constraints Often Reveal the Pattern

Consider the exact same requirement:

> Find whether two numbers sum to target.

### `n <= 500`

`O(n²)` may be acceptable.

### `n <= 100,000`

Try:

- HashMap `O(n)`
- Sort + Two Pointers `O(n log n)`

The **problem statement did not change**. Only the constraints changed.

Therefore:

> **Constraints determine how much brute force you can afford.**

---

# 56. A Practical Pattern-Selection Matrix

Use this whenever you get stuck.

| Question | Ask yourself | Likely pattern |
|---|---|---|
| Search | Is the domain ordered? | Binary Search |
| Search | Just need existence? | HashSet |
| Pair | Can I sort? | Two Pointers |
| Pair | Must preserve original positions? | HashMap |
| Range | Is it contiguous? | Sliding Window / Prefix Sum |
| Range | Exact sum? | Prefix Sum + HashMap |
| Range | Fixed length? | Fixed Sliding Window |
| Range | Validity can be maintained? | Variable Sliding Window |
| K | Need top/bottom K dynamically? | Heap |
| Nearest greater | Need nearest side element? | Monotonic Stack |
| Minimum answer | Is feasibility monotonic? | Binary Search on Answer |
| Tree | Parent depends on children? | Postorder DFS |
| Tree | Level/nearest? | BFS |
| Graph | Reachability? | DFS/BFS |
| Graph | Shortest unweighted? | BFS |
| Graph | Weighted non-negative? | Dijkstra |
| Choices | Need every valid combination? | Backtracking |
| Choices | Repeated state? | DP |
| Prefix strings | Prefix relationships? | Trie |
| Intervals | Overlap/schedule? | Sort + Intervals/Greedy/Heap |
| Bits | XOR/power of two/mask? | Bit Manipulation |

---

# 57. Java-Specific Interview Considerations

Since DSA interviews are often done in Java, also check implementation costs.

## Use `long` when needed

Examples:

```java
long sum = 0L;
long product = 1L;
```

Do not wait for overflow to appear in a test case.

## Prefer `ArrayDeque` over legacy `Stack`

For queue/stack behavior:

```java
Deque<Integer> deque = new ArrayDeque<>();
```

Use:

```java
deque.push(x); // stack behavior
d = deque.pop();
```

or

```java
deque.offer(x); // queue
d = deque.poll();
```

## Priority Queue

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
```

## HashMap

Useful but object-heavy for very large data.

When values are tightly bounded, a primitive `int[]` frequency array can be much faster and smaller.

---

# 58. Complexity of Common Java Operations

| Operation | Typical complexity |
|---|---:|
| Array access `a[i]` | `O(1)` |
| Array scan | `O(n)` |
| HashMap get/put | Expected `O(1)` |
| TreeMap get/put | `O(log n)` |
| PriorityQueue add/poll | `O(log n)` |
| PriorityQueue peek | `O(1)` |
| ArrayList append (amortized) | `O(1)` |
| ArrayList insert middle | `O(n)` |
| LinkedList indexed get | `O(n)` |
| Deque add/remove ends | `O(1)` |
| Arrays.sort primitive array | Typically `O(n log n)` |

Know these before choosing a data structure.

---

# 59. Edge Cases by Pattern

## Arrays

- empty array
- one element
- all equal
- already sorted
- reverse sorted
- negatives
- integer overflow

## Hashing

- duplicate keys
- key absent
- frequency becomes zero
- negative values

## Binary Search

- one element
- answer at low/high boundary
- duplicates
- no valid answer
- overflow in `mid`

Use:

```java
int mid = left + (right - left) / 2;
```

## Sliding Window

- empty string/array
- window size 1
- all identical
- condition never satisfied
- condition always satisfied

## Linked List

- null head
- one node
- two nodes
- deleting head
- cycle

## Trees

- null root
- single node
- highly skewed tree
- duplicate values where relevant

## Graphs

- disconnected graph
- isolated node
- cycles
- self-loop
- duplicate edges

## DP

- base case
- impossible state
- zero capacity
- negative values
- large answer / modulo

---

# 60. The “One Sentence Pattern Explanation” Habit

After identifying a pattern, force yourself to explain it in one sentence.

Examples:

### Hashing

> “I need fast existence/frequency lookup, so I can replace repeated linear searches with a HashMap/HashSet.”

### Two Pointers

> “After sorting, the relationship between the two ends lets me eliminate many pairs at once.”

### Sliding Window

> “The answer is a valid contiguous range, so I maintain the smallest/largest valid window while moving the right and left boundaries.”

### Binary Search

> “The search space is ordered, so each comparison lets me discard half.”

### Binary Search on Answer

> “Feasibility is monotonic in the candidate answer, so I can binary-search the boundary.”

### Heap

> “I repeatedly need the current smallest/largest candidate, so a priority queue maintains it efficiently.”

### BFS

> “Every edge has equal cost, so level-order traversal reaches states in increasing number of steps.”

### DP

> “The recursive subproblems overlap, so I cache the answer for each state.”

### Backtracking

> “I must explore combinations of choices, and I prune branches as soon as the partial solution becomes invalid.”

---

# 61. Pattern Recognition Practice Method

Do not practice only by topic.

Use a **mixed problem set** after learning the individual topics.

For every problem, before writing code, record:

```text
Input shape:
Objective:
Important constraint:
Brute force:
Bottleneck:
Key observation:
Pattern:
Invariant:
Time:
Space:
Why not another pattern?
```

The final line is especially useful.

Example:

```text
Why not HashMap?
Because the array is sorted and we can eliminate pairs using two pointers without extra lookup memory.
```

This develops actual pattern recognition rather than answer memorization.

---

# 62. A 60-Second Interview Decision Process

When the interviewer gives the problem:

### 0–10 seconds: classify the data

```text
array / string / list / tree / graph / matrix / intervals
```

### 10–20 seconds: classify the objective

```text
find / count / shortest / longest / minimum / maximum / all
```

### 20–30 seconds: read constraints

```text
n = ?
values = ?
V,E = ?
```

### 30–40 seconds: brute force

What would the obvious solution cost?

### 40–50 seconds: find the repeated work

```text
lookup?
window?
ordered search?
repeated state?
minimum candidate?
connectivity?
```

### 50–60 seconds: choose pattern and state invariant

Then begin implementation.

---

# 63. Final “What Should I Think?” Cheat Sheet

```text
ARRAY
 |
 +-- Have I seen this? ---------> HASHING
 |
 +-- Sorted / can sort? --------> TWO POINTERS / BINARY SEARCH
 |
 +-- Contiguous range? ---------> SLIDING WINDOW / PREFIX SUM
 |
 +-- Need repeated min/max? ----> HEAP
 |
 +-- Next greater/smaller? -----> MONOTONIC STACK
 |
 +-- Min/max numeric answer? ----> BINARY SEARCH ON ANSWER
 |
 +-- Repeated state? ------------> DP
 |
 '-- All combinations? ----------> BACKTRACKING

LINKED LIST
 |
 +-- Middle/cycle? --------------> FAST/SLOW POINTERS
 +-- Reverse? --------------------> POINTER REVERSAL
 '-- Merge? ----------------------> TWO LIST POINTERS

TREE
 |
 +-- Explore subtree ------------> DFS
 +-- Level/nearest --------------> BFS
 +-- Ordered search  ------------> BST
 '-- Parent needs children ------> POSTORDER / TREE DP

GRAPH
 |
 +-- Reachability ---------------> DFS/BFS
 +-- Components -----------------> DFS/BFS/DSU
 +-- Shortest unweighted --------> BFS
 +-- Weighted non-negative ------> DIJKSTRA + HEAP
 +-- Dependencies ---------------> TOPOLOGICAL SORT
 '-- Advanced connectivity ------> DSU / SCC / Bridges / etc.

STRING
 |
 +-- Frequency ------------------> HASHING
 +-- Contiguous substring -------> SLIDING WINDOW
 +-- Prefix ---------------------> TRIE
 '-- Pattern matching -----------> KMP / Z / rolling hash

NUMBER / BITS
 |
 +-- XOR / masks ----------------> BIT MANIPULATION
 +-- GCD / primes / modulo ------> MATH
 '-- Coordinates / orientation --> GEOMETRY
```

---

# 64. The Most Important Rule: Pattern Is the Consequence, Not the Starting Point

A mature DSA solver does not think:

> “This keyword reminds me of sliding window, so I will code sliding window.”

Instead:

```text
Problem
  ↓
Input structure
  ↓
Objective
  ↓
Constraints
  ↓
Brute force
  ↓
Repeated work / special property
  ↓
Pattern
  ↓
Invariant
  ↓
Complexity
  ↓
Code
```

This is how pattern recognition becomes reliable.

---

# 65. Final Revision Checklist Before an Interview

Be able to answer these without looking up syntax.

## Arrays / Hashing

- When does HashMap turn `O(n²)` into `O(n)`?
- When can I use a frequency array instead of a HashMap?
- What is a subarray?
- Prefix sum + HashMap?

## Sorting / Two Pointers

- When is sorting safe?
- When does sorting + two pointers work?
- When must I preserve original indices?

## Binary Search

- Can I identify a monotonic predicate?
- Can I write lower-bound / upper-bound binary search?
- Can I binary-search an answer rather than an array?

## Linked Lists

- Reverse list?
- Fast/slow pointer?
- Cycle entry?
- Merge lists?

## Stack / Queue

- Parentheses?
- Next greater?
- Monotonic stack?
- BFS queue?
- Sliding-window deque?

## Trees

- DFS vs BFS?
- Pre/in/postorder?
- Tree DP?
- BST inorder property?

## Heaps

- Top K?
- Kth largest?
- Dynamic min/max?
- Dijkstra?

## Graphs

- BFS vs DFS?
- Components?
- Topological sort?
- Dijkstra?
- DSU?

## DP

- What is the state?
- What is the transition?
- What is the base case?
- Is recursion repeating states?
- Can memory be optimized?

## Backtracking

- What is the choice?
- What is the stopping condition?
- What can be pruned?
- Do I need all results or only one/count/best?

---

# 66. Final Rule of Thumb

When you are completely stuck, ask these **10 questions**:

1. **Can I solve it by scanning once?** → `O(n)`
2. **Do I need fast lookup?** → Hashing
3. **Can sorting create useful order?** → Sort + Two Pointers / Greedy / Binary Search
4. **Is the answer about a contiguous range?** → Sliding Window / Prefix Sum
5. **Is there a nearest greater/smaller relationship?** → Monotonic Stack
6. **Do I repeatedly need min/max?** → Heap
7. **Can I binary-search the answer because feasibility is monotonic?** → Binary Search on Answer
8. **Is this really a tree/graph traversal problem?** → DFS/BFS
9. **Am I exploring choices and need all valid possibilities?** → Backtracking
10. **Am I repeatedly solving the same state?** → DP

And always return to:

> **What do the constraints permit?**

That question often separates the intended solution from brute force.

---

# Quick Reference: Pattern → Typical Complexity

| Pattern | Typical time | Typical extra space |
|---|---:|---:|
| Array scan | `O(n)` | `O(1)` |
| HashSet / HashMap pass | Expected `O(n)` | `O(n)` |
| Sorting | `O(n log n)` | depends |
| Two Pointers | `O(n)` after sorted | `O(1)` to `O(n)` |
| Sliding Window | `O(n)` | often `O(k)` / `O(charset)` |
| Prefix Sum | `O(n)` preprocessing | `O(n)` |
| Binary Search | `O(log n)` | `O(1)` |
| Binary Search on Answer | `O(check(n) * log R)` | check-dependent |
| Linked List pointer patterns | `O(n)` | `O(1)` |
| Stack / Monotonic Stack | `O(n)` amortized | `O(n)` |
| Heap | `O(log n)` per update | `O(k)` or `O(n)` |
| Tree DFS/BFS | `O(n)` | `O(h)` or `O(n)` |
| Graph BFS/DFS | `O(V + E)` | `O(V)` |
| Dijkstra | roughly `O((V+E) log V)` with binary heap | `O(V+E)` graph + auxiliary |
| DP 1D | often `O(n)` | `O(n)` or optimized to `O(1)` |
| DP 2D | often `O(nm)` | `O(nm)` or optimized |
| Backtracking | often exponential | recursion/output dependent |
| Trie operation | `O(L)` per word/query | `O(total characters)` |
| Bit operations | often `O(1)` per integer word | `O(1)` |

---

# Final Takeaway

The goal is not to memorize:

> “Problem X = pattern Y.”

The goal is to memorize **properties**:

- **Need fast lookup?** → Hashing
- **Need ordered boundary?** → Binary Search
- **Need contiguous range?** → Sliding Window / Prefix Sum
- **Need pair movement in ordered data?** → Two Pointers
- **Need nearest greater/smaller?** → Monotonic Stack
- **Need current minimum/maximum candidate?** → Heap
- **Need hierarchy traversal?** → Tree DFS/BFS
- **Need graph reachability?** → BFS/DFS
- **Need weighted shortest path?** → Dijkstra/advanced graph
- **Need dependency ordering?** → Topological Sort
- **Need repeated subproblems?** → DP
- **Need all choices?** → Backtracking
- **Need prefix matching?** → Trie
- **Need bit-level properties?** → Bit Manipulation
- **Need interval scheduling/overlap?** → Sorting + Intervals/Greedy/Heap
- **Need mathematical structure?** → Math/Geometry

Then validate the choice using the three strongest signals:

```text
1. INPUT SHAPE
2. KEY PROPERTY / INVARIANT
3. CONSTRAINTS
```

That combination is far more reliable than keywords alone.
