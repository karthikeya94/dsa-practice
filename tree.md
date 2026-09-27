# 🌳 The Everwood Heist: Tree Data Structures 🌳
### *A Hero’s Journey from Ground Level to the Canopy of Mastery*

---

> *Alex stepped through a glowing archway into the Everwood Forest. The trees here weren't made of wood; they were made of glowing rings of light, branching out into the sky. An ancient treant named **Sylva, the Warden of Roots**, opened his bark-covered eyes.*
>
> *"Ah, the Algorithm Traveler,"* Sylva rumbled. *"You have mastered Arrays, Linked Lists, and Graphs. But look up. A Linked List is just a tree that never branches. A Graph is just a tree that grew a cycle. If you master the Tree, you master the hierarchy of data itself."*
>
> *Sylva touched the ground. A glowing root emerged, splitting into two, then four. "Let us climb."*

---

## 📖 Chapter 0: The Anatomy of the Everwood

A Tree is a collection of **Nodes** connected by **Edges**, starting from a single **Root**.
*   **Root:** The top node.
*   **Leaf:** A node with no children.
*   **Height:** The longest path from a node down to a leaf.
*   **Depth:** The distance from the root down to a node.

In a **Binary Tree**, no node has more than 2 children (`left` and `right`).

```java
class TreeNode {
	int val;
	TreeNode left;
	TreeNode right;
	TreeNode(int x) { val = x; }
}
```

---

## 📖 Chapter 1: The Three Walking Styles (DFS Traversals)

*"To read a tree,"* Sylva said, *"you must walk it. But a tree splits in two. Do you read yourself first, or your children first?"*

This is **Depth-First Search (DFS)**. We use Recursion.

### 1️⃣ Pre-Order (Root, Left, Right)
*When to use:* You want to copy a tree, or find a path from the root. You process the **Root first**.

```java
void preorder(TreeNode node) {
	if (node == null) return;
	System.out.print(node.val + " "); // 1. Process Root
	preorder(node.left);              // 2. Go Left
	preorder(node.right);             // 3. Go Right
}
```

### 2️⃣ In-Order (Left, Root, Right)
*When to use:* In a Binary Search Tree, this visits nodes in **ascending sorted order**!

```java
void inorder(TreeNode node) {
	if (node == null) return;
	inorder(node.left);               // 1. Go Left
	System.out.print(node.val + " "); // 2. Process Root
	inorder(node.right);              // 3. Go Right
}
```

### 3️⃣ Post-Order (Left, Right, Root)
*When to use:* You need to know the answer from the children before you can calculate the parent. Used for **Tree DP**, deleting trees, or calculating sizes.

```java
void postorder(TreeNode node) {
	if (node == null) return;
	postorder(node.left);             // 1. Go Left
	postorder(node.right);            // 2. Go Right
	System.out.print(node.val + " "); // 3. Process Root
}
```

---

## 📖 Chapter 2: The Expanding Wave (BFS / Level-Order Traversal)

*"DFS goes deep. But what if I want to explore the forest layer by layer, from top to bottom?"* Alex asked.

*"That is **Breadth-First Search (BFS)**,"* Sylva nodded. "You need a **Queue**. You put the root in, pop it, and push its children. You process the tree level by level."

```java
List<List<Integer>> levelOrder(TreeNode root) {
	List<List<Integer>> result = new ArrayList<>();
	if (root == null) return result;
	
	Queue<TreeNode> queue = new LinkedList<>();
	queue.add(root);
	
	while (!queue.isEmpty()) {
		int levelSize = queue.size();
		List<Integer> currentLevel = new ArrayList<>();
		
		for (int i = 0; i < levelSize; i++) {
			TreeNode node = queue.poll();
			currentLevel.add(node.val);
			
			if (node.left != null) queue.add(node.left);
			if (node.right != null) queue.add(node.right);
		}
		result.add(currentLevel);
	}
	return result;
}
```
> 🏷️ **Pattern Recognition:** "Right side view of tree", "Average of levels", "Zigzag traversal" → **BFS with a Queue**.

---

## 📖 Chapter 3: The Magic Compass (Binary Search Trees - BST)

Sylva planted a new seed. "In a **Binary Search Tree (BST)**, everything to the left is smaller than the root. Everything to the right is larger. It is perfectly sorted."

*   **Search in BST:** `O(log N)` time! You just go left if target is smaller, right if larger.
*   **Validate BST:** You must pass down a `min` and `max` allowed range. If a node breaks the range, it's not a valid BST.

```java
boolean isValidBST(TreeNode node, long min, long max) {
	if (node == null) return true;
	if (node.val <= min || node.val >= max) return false;
	
	// Left child must be < node.val, Right child must be > node.val
	return isValidBST(node.left, min, node.val) && 
	isValidBST(node.right, node.val, max);
}
```

### 🧭 Lowest Common Ancestor (LCA) in BST
*"Find the shared ancestor of two nodes,"* Sylva commanded.
*Trick:* Start at root. If both nodes are smaller, go left. If both are larger, go right. The moment they split paths (one goes left, one goes right), **that node is the LCA!**

```java
TreeNode lowestCommonAncestorBST(TreeNode root, TreeNode p, TreeNode q) {
	while (root != null) {
		if (p.val < root.val && q.val < root.val) root = root.left;
		else if (p.val > root.val && q.val > root.val) root = root.right;
		else return root; // Split point!
	}
	return null;
}
```

---

## 📖 Chapter 4: The Grandmaster’s Flow (Core Tree Problem Patterns)

This is where Alex leveled up. Sylva showed him the 4 mental models that solve 90% of tree problems.

### 🧠 Pattern 1: The Bottom-Up Trust (Tree DP)
*"Calculate the maximum depth of the tree."*
You don't count from the top. You ask your left child: *"How deep are you?"* You ask your right child: *"How deep are you?"* You take their answers, add 1 (for yourself), and pass it to YOUR parent.

```java
int maxDepth(TreeNode node) {
	if (node == null) return 0; // Base case
	
	int leftDepth = maxDepth(node.left);   // Trust left clone
	int rightDepth = maxDepth(node.right); // Trust right clone
	
	return 1 + Math.max(leftDepth, rightDepth); // Combine!
}
```

### 🧠 Pattern 2: The Global Variable Side-Effect (Diameter of Tree)
*"Find the longest path between any two nodes."*
The path might not go through the root! It could be entirely in the left subtree. You need a global variable to keep track of the maximum path seen so far, while the recursion just returns the "longest single branch".

```java
int diameter = 0;

int diameterOfBinaryTree(TreeNode root) {
	dfs(root);
	return diameter;
}

int dfs(TreeNode node) {
	if (node == null) return 0;
	
	int left = dfs(node.left);
	int right = dfs(node.right);
	
	// The path passing through THIS node connects left and right.
	// Update the global max if this is the longest path we've seen.
	diameter = Math.max(diameter, left + right);
	
	// But to the parent, we can only return ONE branch (the longest).
	return 1 + Math.max(left, right); 
}
```

### 🧠 Pattern 3: The Ancestor Backtracker (Path Sum)
*"Does a root-to-leaf path sum to `target`?"*
You subtract your value from the target. If you reach a leaf and the remaining target is 0, you found it! If not, you backtrack. (Often combined with adding/removing from a List to save the actual path).

```java
boolean hasPathSum(TreeNode root, int targetSum) {
	if (root == null) return false;
	
	// If it's a leaf, check if the remaining sum matches the leaf's value.
	if (root.left == null && root.right == null) {
		return targetSum == root.val;
	}
	
	// Subtract current value and ask the children.
	boolean left = hasPathSum(root.left, targetSum - root.val);
	boolean right = hasPathSum(root.right, targetSum - root.val);
	
	return left || right;
}
```

### 🧠 Pattern 4: Lowest Common Ancestor (Normal Binary Tree)
*"Find LCA in a normal tree (not a BST)."*
If the current node is `p` or `q`, you found one! Ask the left and right subtrees. If `p` is in the left and `q` is in the right, YOU are the LCA. If both are in the left, the LCA is in the left.

```java
TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
	if (root == null || root == p || root == q) return root;
	
	TreeNode left = lowestCommonAncestor(root.left, p, q);
	TreeNode right = lowestCommonAncestor(root.right, p, q);
	
	if (left != null && right != null) return root; // Found the split!
	return (left != null) ? left : right; // Both were on one side
}
```

---

## 📖 Chapter 5: The Whispering Woods (N-ary Trees & Tries)

*"Not all trees have two children,"* Sylva waved his branch. A massive tree with 10 branches per node appeared.

### 🌿 N-ary Tree
Instead of `.left` and `.right`, you have a `List<TreeNode> children`. You traverse it with a simple for-loop.
```java
void traverseNary(TreeNode node) {
	if (node == null) return;
	System.out.println(node.val);
	for (TreeNode child : node.children) {
		traverseNary(child);
	}
}
```

### 📖 The Trie (Prefix Tree)
A Trie is a special N-ary tree (usually 26 children for letters 'a'-'z') used for **autocompleting words**.
*   Insert: Walk down the tree, creating nodes for each letter. Mark the last node as `isEndOfWord = true`.
*   Search: Walk down. If you hit `null`, the word doesn't exist. If you reach the end, check `isEndOfWord`.

```java
class TrieNode {
	TrieNode[] children = new TrieNode[26];
	boolean isEnd = false;
}

class Trie {
	TrieNode root = new TrieNode();
	
	public void insert(String word) {
		TrieNode node = root;
		for (char c : word.toCharArray()) {
			int i = c - 'a';
			if (node.children[i] == null) node.children[i] = new TrieNode();
			node = node.children[i];
		}
		node.isEnd = true;
	}
	
	public boolean search(String word) {
		TrieNode node = root;
		for (char c : word.toCharArray()) {
			int i = c - 'a';
			if (node.children[i] == null) return false;
			node = node.children[i];
		}
		return node.isEnd;
	}
}
```

---

## 📖 Chapter 6: The Time-Space Bender (Morris Traversal & Segment Trees)

### 🐾 Morris Traversal (O(1) Space DFS)
*"Recursion uses memory on the stack,"* Sylva warned. "Can you traverse a tree without a stack or queue?"
**Morris Traversal:** You temporarily modify the tree! You find the rightmost node of the left subtree, tie its `right` pointer to the current node (creating a temporary thread), go left, and when you're done, you untie the thread. `O(1)` space!

### 📏 Segment Tree (Range Queries)
You have an array. You want to find the `sum` or `min` between index `L` and `R`... and the array keeps changing!
A **Segment Tree** breaks the array in half recursively. Each node stores the answer for a range `[L, R]`. Updating a value or querying a range takes `O(log N)` time!

---

## 📖 Final Chapter: The Warden’s Canopy

Sylva looked up at the shimmering canopy. *"You have climbed well, Alex. You now see that trees are not just data structures; they are the ultimate expression of hierarchical problem solving."*

### 🗺️ The Grand Tree Pattern Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Print / Collect values** | "Inorder", "Preorder", "Postorder" | **DFS Recursion**. `root`, `left`, `right` order matters. |
| **Level by Level** | "Level order", "Right side view", "Average" | **BFS Queue**. Process `queue.size()` per loop. |
| **Tree Height / Balance** | "Max depth", "Is balanced?" | **Bottom-Up Tree DP**. `1 + Math.max(left, right)`. |
| **Diameter / Max Path** | "Longest path between nodes" | **Global Variable + DFS**. Path = `left + right`. Update global, return `1 + max`. |
| **Path Sums** | "Root to leaf sum", "All paths" | **Backtracking DFS**. Subtract from target, add to path, undo. |
| **LCA (Normal Tree)** | "Common ancestor" | **DFS Return Pattern**. If left and right both find a target, YOU are LCA. |
| **Word Dictionary** | "Autocomplete", "Starts with prefix" | **Trie**. Array of 26 children, `isEnd` boolean. |
| **Range Sum / Min Query** | "Array updates, range queries" | **Segment Tree**. Node covers `[L, R]`. |

### 🧠 The Tree-Climber's Final Checklist

1. **What is my Base Case?** (`if (node == null) return 0;`)
2. **Do I need the children's answers to calculate mine?** (If yes → Bottom-Up Post-Order DP).
3. **Am I searching for a path?** (If yes → Backtracking DFS, subtract from target).
4. **Do I need to process levels?** (If yes → BFS Queue).
5. **Does the path split at the answer?** (If yes → LCA pattern).
6. **Am I dealing with strings/prefixes?** (If yes → Trie).

### 🏆 Practice Quests — From Seedling to Canopy Master

**Level 1 (Apprentice):**
- Maximum Depth, Invert Binary Tree, Same Tree, Symmetric Tree, Binary Tree Inorder Traversal.

**Level 2 (Squire):**
- Level Order Traversal, Path Sum, Merge Two Binary Trees, Subtree of Another Tree.

**Level 3 (Knight):**
- Lowest Common Ancestor (BST & Normal), Validate BST, Construct Binary Tree from Preorder and Inorder, Kth Smallest Element in BST.

**Level 4 (Champion):**
- Diameter of Binary Tree, Binary Tree Maximum Path Sum, Implement Trie, Binary Tree Right Side View.

**Level 5 (Grandmaster):**
- Word Search II (Trie + Backtracking), Serialize and Deserialize Binary Tree, Segment Tree (Range Sum Query), Red-Black Tree concepts.

---

> *Alex stood at the top of the canopy. The forest of nodes spread out beneath him, glowing with recursive light. He realized that the tree was the most natural structure in the universe—branching out, growing from the bottom up, and always connecting back to the root.*
>
> *Sylva faded into the bark. Alex took a deep breath and leaped into the air, gliding toward his final algorithm challenge.*

**The Everwood Heist is Complete.** 🌳
