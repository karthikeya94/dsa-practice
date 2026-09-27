# 🌲 The Arboretum of Infinity: Advanced Trees 🌲
### *A Hero’s Journey from Branches to Binary Lifting*

---

> *Alex walked into a massive glass greenhouse. Trees of pure crystal grew here, but they were twisting, bending, and reshaping themselves. An ancient groundskeeper with mechanical arms named **Keeper Arborium** was pruning a massive tree that had bent entirely to the left.*
> 
> *"Alex,"* Arborium grunted, snapping a branch back into place. "You've mastered the basic Binary Tree. You know DFS and BFS. But out in the real world, data is violent. If you insert sorted data into a standard BST, it turns into a Linked List. Your `O(log N)` becomes `O(N)`. The tree falls over."*
> 
> *He smacked the bent tree, and it shattered. "We need trees that balance themselves. We need trees that can query ranges. We need trees that bend time and space. Welcome to the Arboretum of Advanced Trees."*

---

## 📖 Chapter 0: The Leaning Towers (Self-Balancing BSTs)

*"If I insert `[1, 2, 3, 4]` into a BST, it just keeps going right!"* Alex complained.
*"Exactly,"* Arborium said. "We need **Self-Balancing Trees**."

### 🔄 AVL Trees (The Strict Perfectionist)
An AVL Tree guarantees that the height difference between the left and right child (the Balance Factor) is **never more than 1**.
If you insert a node and break this rule, you must perform **Rotations** (Left-Left, Right-Right, Left-Right, Right-Left) to pivot the tree back into balance.

### 🖤 Red-Black Trees (The Practical Engineer)
AVL trees are great, but rotating them constantly is slow. A Red-Black tree is looser. It paints nodes Red or Black and follows 5 strict rules to ensure the tree is *roughly* balanced. It guarantees `O(log N)` but does fewer rotations.
> 🏷️ **Pattern Recognition:** You RARELY code an AVL or Red-Black tree from scratch in an interview. BUT, you must know they exist! In Java, `TreeMap` and `TreeSet` are implemented using Red-Black Trees. If you need a sorted map with `O(log N)` insertions and deletions, use `TreeMap`.

---

## 📖 Chapter 1: The Range Guardian (Segment Tree)

Arborium pointed to an array of 1,000,000 numbers. 
*"I want to know the sum of elements from index 500 to index 5,000. And I will change index 10. And I will ask again. Millions of times."*

A Prefix Sum array fails here because updating an element is `O(N)`. 
A **Segment Tree** solves this. It breaks the array in half recursively. Each node stores the answer (sum, min, max) for a specific range `[L, R]`.

### 🧠 The Mental Model
*   **Leaves:** Individual array elements.
*   **Parents:** The combination of two children (e.g., `Parent Sum = Left Child Sum + Right Child Sum`).
*   **Array Size:** We use a `4 * N` array to store the tree safely.

```java
class SegmentTree {
	int[] tree;
	int[] arr;
	int n;
	
	public SegmentTree(int[] input) {
		n = input.length;
		arr = input;
		tree = new int[4 * n]; // Safe size
		build(0, 0, n - 1);    // node 0, covers [0, n-1]
	}
	
	// Build the tree: O(N)
	void build(int node, int start, int end) {
		if (start == end) {
			tree[node] = arr[start]; // Leaf node
		} else {
			int mid = (start + end) / 2;
			int leftChild = 2 * node + 1;
			int rightChild = 2 * node + 2;
			
			build(leftChild, start, mid);
			build(rightChild, mid + 1, end);
			
			tree[node] = tree[leftChild] + tree[rightChild]; // Combine!
		}
	}
	
	// Point Update: O(log N)
	void update(int node, int start, int end, int idx, int val) {
		if (start == end) {
			arr[idx] = val;
			tree[node] = val;
		} else {
			int mid = (start + end) / 2;
			if (idx <= mid) update(2 * node + 1, start, mid, idx, val);
			else update(2 * node + 2, mid + 1, end, idx, val);
			tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
		}
	}
	
	// Range Query [L, R]: O(log N)
	int query(int node, int start, int end, int L, int R) {
		// Range completely outside
		if (R < start || end < L) return 0;
		// Range completely inside
		if (L <= start && end <= R) return tree[node];
		// Partial overlap
		int mid = (start + end) / 2;
		int leftSum = query(2 * node + 1, start, mid, L, R);
		int rightSum = query(2 * node + 2, mid + 1, end, L, R);
		return leftSum + rightSum;
	}
}
```

---

## 📖 Chapter 2: The Binary Alchemist (Fenwick Tree / BIT)

*"Segment Trees are powerful,"* Arborium said, "but they use a lot of memory and the code is long. What if we just want prefix sums, but with updates?"*

Enter the **Binary Indexed Tree (BIT)**. It uses a brilliant mathematical trick based on binary numbers. 
In a BIT, index `i` is responsible for a range of elements. The length of this range is the **lowest set bit** of `i`!

*   `i = 6` (Binary `110`). Lowest set bit is `10` (2). So index 6 covers 2 elements.
*   To get the next index to update, we add the lowest set bit: `6 + 2 = 8`.
*   To get the prefix sum, we subtract the lowest set bit: `6 - 2 = 4`.

**The Magic Spell:** `i & -i` extracts the lowest set bit.

```java
class FenwickTree {
	int[] bit;
	int n;
	
	public FenwickTree(int n) {
		this.n = n;
		bit = new int[n + 1]; // 1-indexed!
	}
	
	// Update: Add 'delta' to index 'i' and all its ancestors. O(log N)
	public void update(int i, int delta) {
		i++; // Convert 0-indexed to 1-indexed
		while (i <= n) {
			bit[i] += delta;
			i += i & -i; // Jump to next responsible index
		}
	}
	
	// Query: Get prefix sum from [0, i]. O(log N)
	public int query(int i) {
		i++; // Convert 0-indexed to 1-indexed
		int sum = 0;
		while (i > 0) {
			sum += bit[i];
			i -= i & -i; // Strip off the lowest set bit
		}
		return sum;
	}
	
	// Range Sum [L, R] = query(R) - query(L - 1)
}
```
> 🏷️ **Pattern Recognition:** "Range sum with point updates", "Count of smaller numbers after self". If you need a dynamic prefix sum, **Fenwick Tree** is 10x faster and shorter than a Segment Tree.

---

## 📖 Chapter 3: The Napping Dragon (Lazy Propagation)

Arborium warned Alex of the ultimate challenge. 
*"What if I want to add 5 to EVERY element from index 10 to index 10,000?"* 

If you call `update` 10,000 times on a Segment Tree, it's `O(N log N)`. Too slow!
**Lazy Propagation:** Like a dragon napping on its gold. If a node is entirely within the update range, don't go down to its children. Just update the node, mark it with a "Lazy Tag" (meaning "my children need to be updated later"), and go back to sleep.

```java
// Conceptual Lazy Propagation on a Segment Tree
int[] lazy; // Tracks pending updates

// Inside the update or query function:
void pushDown(int node, int start, int end) {
	if (lazy[node] != 0) {
		tree[node] += (end - start + 1) * lazy[node]; // Apply the lazy update
		
		if (start != end) { // If not a leaf, pass laziness to children
			lazy[2 * node + 1] += lazy[node];
			lazy[2 * node + 2] += lazy[node];
		}
		lazy[node] = 0; // Clear my laziness!
	}
}

// Call pushDown(node, start, end) at the very beginning of EVERY query and update!
```
> 🏷️ **Pattern Recognition:** "Range updates (add X to range)", "Range assignments (set all to X)" → **Segment Tree + Lazy Propagation**.

---

## 📖 Chapter 4: The Chrono-Jumper (Binary Lifting / LCA)

Alex stood before a massive, deep tree. Millions of nodes. 
*"Find the Lowest Common Ancestor of Node A and Node B."*

If Alex just walked up the tree one parent at a time, it would take `O(N)` in the worst case.
**Binary Lifting** uses Dynamic Programming to jump in powers of 2! 
Instead of jumping 1 step at a time, you jump 2, 4, 8, 16 steps!

`up[node][j]` = The ancestor of `node` who is $2^j$ steps above them.

```java
int LOG = 20; // Enough to cover depths up to 1,000,000
int[][] up;   // up[node][j]
int[] depth;

void preprocess(int root, int n) {
	up = new int[n][LOG];
	depth = new int[n];
	
	// Base case: 2^0 = 1 step up (direct parent)
	// dfs sets this up: up[child][0] = parent;
	
	// Fill DP table
	for (int j = 1; j < LOG; j++) {
		for (int node = 0; node < n; node++) {
			if (up[node][j - 1] != -1) {
				// To jump 2^j steps, jump 2^(j-1) steps, then another 2^(j-1) steps!
				up[node][j] = up[up[node][j - 1]][j - 1];
			}
		}
	}
}

int getLCA(int u, int v) {
	// 1. Bring both nodes to the same depth using Binary Lifting
	if (depth[u] < depth[v]) { int temp = u; u = v; v = temp; }
	
	int diff = depth[u] - depth[v];
	for (int j = 0; j < LOG; j++) {
		if ((diff & (1 << j)) != 0) {
			u = up[u][j]; // Jump by 2^j
		}
	}
	
	if (u == v) return u; // One was the ancestor of the other
	
	// 2. Jump both up together until they meet!
	for (int j = LOG - 1; j >= 0; j--) {
		if (up[u][j] != up[v][j]) {
			u = up[u][j];
			v = up[v][j];
		}
	}
	return up[u][0]; // The parent is the LCA
}
```
> 🏷️ **Pattern Recognition:** "Tree queries", "K-th ancestor", "Distance between two nodes". Binary Lifting is the standard for static tree LCA.

---

## 📖 Final Chapter: The Keeper’s Blueprint

Arborium looked at the perfectly balanced, instantly querying crystal trees. "You have built engines that run in `O(log N)`, Alex. You no longer fear large datasets."

### 🗺️ The Grand Advanced Tree Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Sorted Map with dynamic insert/delete** | "Need O(log N) put/get", "Ceiling key", "Floor key" | **TreeMap / TreeSet** (Red-Black Tree). |
| **Range Sum/Min with Point Updates** | "Update index, query range", "Dynamic prefix sum" | **Fenwick Tree (BIT)**. Fastest, shortest code. |
| **Range Queries with complex merges** | "Min/Max on range", "GCD on range" | **Segment Tree**. (Fenwick can't do Min/Max easily). |
| **Range Updates (Add X to range)** | "Add value to all elements in [L, R]" | **Segment Tree + Lazy Propagation**. |
| **Lowest Common Ancestor** | "LCA", "K-th ancestor", "Tree distances" | **Binary Lifting** (DP table `up[node][j]`). |

### 🧠 The Arborist's Final Checklist

1. **Do I need to update elements and query ranges?** (If yes, drop the Prefix Sum array. Use Fenwick or Segment Tree).
2. **Is the operation invertible?** (Sum is invertible: `Sum(L,R) = Sum(R) - Sum(L-1)`. Use Fenwick. Min/Max are NOT invertible. Use Segment Tree).
3. **Am I doing range updates?** (If yes, you MUST use Lazy Propagation, or you will get `TLE` / Time Limit Exceeded).
4. **Are my tree array sizes safe?** (Segment tree needs `4 * N`. Fenwick needs `N + 1`).
5. **Is the tree static?** (Binary Lifting for LCA only works if the tree structure doesn't change. If it changes, you need Link-Cut Trees, which are dark magic beyond even Grandmasters).

### 🏆 Practice Quests — From Pruner to Arborist

**Level 1 (Apprentice):**
- Contains Duplicate III (TreeSet), Kth Largest Element in a Stream (Heap, but good to know TreeMap), Range Sum Query - Mutable (Fenwick/Segment Tree basic).

**Level 2 (Squire):**
- Count of Range Sum (Fenwick/TreeSet), Falling Squares (Segment Tree concept).

**Level 3 (Knight):**
- Range Sum Query 2D - Mutable (2D Fenwick Tree), Minimum Number of Refueling Stops (Priority Queue + TreeMap).

**Level 4 (Champion):**
- Count of Smaller Numbers After Self (Fenwick Tree over coordinates), Range Minimum Query (Segment Tree), Lowest Common Ancestor of a Binary Tree (Standard, then Binary Lifting).

**Level 5 (Grandmaster):**
- Range Addition (Lazy Propagation), My Calendar III (Segment Tree with Lazy/HashMap), Longest Common Ancestor paths with Binary Lifting, Dynamic Subtree queries.

---

> *Alex walked out of the Arboretum. He looked at a billion-row database and no longer saw a wall of data. He saw a perfectly balanced tree, jumping in powers of two, querying ranges in logarithmic time. He had bent the very structure of data to his will.*

**The Arboretum of Infinity Heist is Complete.** 🌲
