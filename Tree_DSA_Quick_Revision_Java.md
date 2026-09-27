# Tree DSA — Ultimate Quick Revision Guide (Java)

> **How to use:** Each topic follows: *When to use → Nuance/Gotcha → Steps → Code.*  
> Start with the **Master Cheat Sheet** at the bottom before an interview, then drill weak spots.

---

## 📦 Group 1: Core Node Structure & Tree Types

```java
// Standard Binary Tree Node
static class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}

// N-ary Tree Node (for tries, generic trees)
static class NaryNode {
    int val;
    List<NaryNode> children = new ArrayList<>();
    NaryNode(int val) { this.val = val; }
}
```

**Tree Types at a Glance:**

| Type | Property | Key Operation |
|---|---|---|
| Binary Tree | Each node ≤ 2 children | Traversal, LCA, paths |
| BST | left < root < right | Search O(h), Insert, Delete |
| AVL Tree | BST + \|balanceFactor\| ≤ 1 | Rotations on insert/delete |
| Complete BT | All levels full except last (left-filled) | Heap-based |
| Perfect BT | All levels completely filled | Counting: 2^h - 1 nodes |
| Full BT | Every node has 0 or 2 children | — |

**⚠️ Nuance:** Height vs Depth — Height is measured from **node to deepest leaf** (bottom-up); Depth is from **root to node** (top-down). Height of tree = height of root. Height of leaf = 0.

---

## 🔍 Group 2: Tree Traversals

**Memory trick for DLR/LDR/LRD:**
- Pre-order → **Root first** → copy/serialize a tree
- In-order → **Sorted output** in BST → validate BST
- Post-order → **Root last** → delete tree, evaluate expressions
- Level-order → **BFS** → find height, level sums, zigzag

---

### 1. Pre-order (Root → Left → Right)
**Use:** Clone/copy a tree, serialize, prefix expressions.

```java
static void preorder(TreeNode root, List<Integer> res) {
    if (root == null) return;
    res.add(root.val);          // Root first
    preorder(root.left, res);
    preorder(root.right, res);
}

// Iterative Pre-order
static List<Integer> preorderIterative(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    Stack<TreeNode> st = new Stack<>();
    st.push(root);
    while (!st.isEmpty()) {
        TreeNode node = st.pop();
        res.add(node.val);
        if (node.right != null) st.push(node.right); // push right first
        if (node.left != null) st.push(node.left);   // left processed first
    }
    return res;
}
```

---

### 2. In-order (Left → Root → Right)
**Use:** BST sorted output, validate BST, kth smallest in BST.

**⚠️ Nuance:** In a valid BST, in-order traversal gives strictly increasing values. If any value ≤ previous, BST is invalid.

```java
static void inorder(TreeNode root, List<Integer> res) {
    if (root == null) return;
    inorder(root.left, res);
    res.add(root.val);          // Root in middle
    inorder(root.right, res);
}

// Iterative In-order (Morris or Stack)
static List<Integer> inorderIterative(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    Stack<TreeNode> st = new Stack<>();
    TreeNode curr = root;
    while (curr != null || !st.isEmpty()) {
        while (curr != null) { st.push(curr); curr = curr.left; } // go left
        curr = st.pop();
        res.add(curr.val);
        curr = curr.right;      // move right after visiting
    }
    return res;
}
```

---

### 3. Post-order (Left → Right → Root)
**Use:** Delete tree, evaluate expression trees, subtree problems.

**⚠️ Nuance:** Iterative post-order trick — reverse of (Root → Right → Left) gives (Left → Right → Root).

```java
static void postorder(TreeNode root, List<Integer> res) {
    if (root == null) return;
    postorder(root.left, res);
    postorder(root.right, res);
    res.add(root.val);          // Root last
}

// Iterative Post-order (Two-stack trick)
static List<Integer> postorderIterative(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    Stack<TreeNode> st = new Stack<>();
    st.push(root);
    while (!st.isEmpty()) {
        TreeNode node = st.pop();
        res.add(0, node.val);           // prepend = reverse insertion
        if (node.left != null) st.push(node.left);
        if (node.right != null) st.push(node.right);
    }
    return res;
}
```

---

### 4. Level-order (BFS)
**Use:** Height, level sums, zigzag, right-side view, connect level nodes.

**⚠️ Nuance:** To track levels, snapshot queue size at start of each level (`int size = q.size()`). Process exactly `size` nodes = one level.

```java
static List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();        // snapshot level size
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            level.add(node.val);
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        res.add(level);
    }
    return res;
}

// Zigzag Level Order
static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    boolean leftToRight = true;
    while (!q.isEmpty()) {
        int size = q.size();
        LinkedList<Integer> level = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (leftToRight) level.addLast(node.val);
            else             level.addFirst(node.val); // reverse direction
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        res.add(level);
        leftToRight = !leftToRight;
    }
    return res;
}
```

---

### 5. Morris Traversal (In-order, O(1) Space)
**Use:** In-order traversal with no stack, no recursion — O(1) extra space.

**⚠️ Nuance:** Temporarily modifies tree (threaded links) and restores on second visit. Two visits per node — first creates link, second removes it.

```java
static List<Integer> morrisInorder(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    TreeNode curr = root;
    while (curr != null) {
        if (curr.left == null) {
            res.add(curr.val);  // no left child, visit and go right
            curr = curr.right;
        } else {
            TreeNode prev = curr.left;
            while (prev.right != null && prev.right != curr) prev = prev.right;
            if (prev.right == null) {
                prev.right = curr;  // create thread
                curr = curr.left;
            } else {
                prev.right = null;  // remove thread
                res.add(curr.val);
                curr = curr.right;
            }
        }
    }
    return res;
}
```

---

## 📏 Group 3: Height, Depth & Diameter

### 6. Height of a Tree
**⚠️ Nuance:** Height of null = -1 (edge-count convention) or 0 (node-count). Be consistent. Most problems use node-count (height of single node = 1).

```java
static int height(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(height(root.left), height(root.right));
}
```

---

### 7. Diameter of a Binary Tree
**Use:** Longest path between any two nodes (may or may not pass through root).

**⚠️ Nuance:** Diameter at each node = `leftHeight + rightHeight`. You can't just compute at root — the longest path might be entirely in the left subtree. Use a global max updated at every node.

```java
static int diameterResult = 0;

static int diameterDFS(TreeNode root) {
    if (root == null) return 0;
    int left = diameterDFS(root.left);
    int right = diameterDFS(root.right);
    diameterResult = Math.max(diameterResult, left + right); // path through this node
    return 1 + Math.max(left, right);                        // return height to parent
}

static int diameterOfBinaryTree(TreeNode root) {
    diameterResult = 0;
    diameterDFS(root);
    return diameterResult;
}
```

---

### 8. Check Balanced Tree
**⚠️ Nuance:** A naïve O(N²) solution calls `height()` separately for each node. The efficient O(N) approach returns -1 as a sentinel value to signal "already unbalanced" — short-circuit all the way up.

```java
static int checkBalanced(TreeNode root) {
    if (root == null) return 0;
    int left = checkBalanced(root.left);
    if (left == -1) return -1;                      // propagate imbalance
    int right = checkBalanced(root.right);
    if (right == -1) return -1;
    if (Math.abs(left - right) > 1) return -1;      // current node unbalanced
    return 1 + Math.max(left, right);
}

static boolean isBalanced(TreeNode root) {
    return checkBalanced(root) != -1;
}
```

---

## ➕ Group 4: Path Sums

**Key insight:** Path sum problems almost always involve a **post-order DFS** — you need both children's info before deciding at the current node.

---

### 9. Root-to-Leaf Path Sum (Target Check)
**Steps:**
1. At each node, subtract current value from target.
2. At leaf, if remaining == 0 → path found.

```java
static boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    targetSum -= root.val;
    if (root.left == null && root.right == null) return targetSum == 0; // leaf check
    return hasPathSum(root.left, targetSum) || hasPathSum(root.right, targetSum);
}
```

---

### 10. All Root-to-Leaf Paths with Target Sum
```java
static List<List<Integer>> pathSum(TreeNode root, int target) {
    List<List<Integer>> res = new ArrayList<>();
    dfsPath(root, target, new ArrayList<>(), res);
    return res;
}

static void dfsPath(TreeNode node, int rem, List<Integer> path, List<List<Integer>> res) {
    if (node == null) return;
    path.add(node.val);
    rem -= node.val;
    if (node.left == null && node.right == null && rem == 0)
        res.add(new ArrayList<>(path));     // ← copy, not reference!
    dfsPath(node.left, rem, path, res);
    dfsPath(node.right, rem, path, res);
    path.remove(path.size() - 1);          // backtrack
}
```

---

### 11. Maximum Path Sum (Any Node to Any Node)
**⚠️ Nuance:** The path can start and end at any node — it doesn't need to pass through root, and it doesn't need to go root-to-leaf. At each node, you choose the best contribution upward (only one branch), but you update the global max using both branches.

**Steps:**
1. Compute max gain from left and right subtrees (ignore negatives → use `max(gain, 0)`).
2. Update global max = `node.val + leftGain + rightGain`.
3. Return `node.val + max(leftGain, rightGain)` to parent (can only extend one side upward).

```java
static int maxPathSumResult;

static int maxPathDFS(TreeNode root) {
    if (root == null) return 0;
    int left  = Math.max(maxPathDFS(root.left), 0);  // ignore negative paths
    int right = Math.max(maxPathDFS(root.right), 0);
    maxPathSumResult = Math.max(maxPathSumResult, root.val + left + right); // global update
    return root.val + Math.max(left, right);           // return best single branch
}

static int maxPathSum(TreeNode root) {
    maxPathSumResult = Integer.MIN_VALUE;
    maxPathDFS(root);
    return maxPathSumResult;
}
```

---

### 12. Path Sum III (Count Paths Summing to Target — Any Start/End)
**⚠️ Nuance:** Use **prefix sum + HashMap** pattern (same as subarray sum). `prefixSums[currSum - target]` gives count of valid paths ending at current node.

```java
static int pathSumIII(TreeNode root, int targetSum) {
    Map<Long, Integer> prefixSums = new HashMap<>();
    prefixSums.put(0L, 1);  // empty path
    return dfsCount(root, 0L, targetSum, prefixSums);
}

static int dfsCount(TreeNode node, long curr, int target, Map<Long, Integer> map) {
    if (node == null) return 0;
    curr += node.val;
    int count = map.getOrDefault(curr - target, 0);
    map.put(curr, map.getOrDefault(curr, 0) + 1);
    count += dfsCount(node.left, curr, target, map);
    count += dfsCount(node.right, curr, target, map);
    map.put(curr, map.get(curr) - 1);  // backtrack: remove current node's sum
    return count;
}
```

---

## 👁️ Group 5: Tree Views

**Memory trick:**
- Left/Right view → **BFS level-order** (first/last of each level), or DFS with level tracking
- Top view → **BFS + horizontal distance (HD)**
- Bottom view → **BFS + horizontal distance** (last node at each HD wins)

---

### 13. Right View of Binary Tree
**Use:** First node visible from the right side at each level.

**⚠️ Nuance:** Right view = last node of each level in BFS. In DFS, visit right child first, and only add if it's the first visit at that depth.

```java
// BFS approach (cleaner)
static List<Integer> rightSideView(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (i == size - 1) res.add(node.val);   // last node of level
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
    }
    return res;
}

// Left view: same but take first node (i == 0)
```

---

### 14. Top View of Binary Tree
**Use:** Nodes visible when tree is viewed from top. Each horizontal distance (HD) shows only the first node.

**⚠️ Nuance:** HD of root = 0, left child = HD-1, right child = HD+1. Use BFS to ensure first node at each HD is captured. Store results in a sorted TreeMap.

```java
static List<Integer> topView(TreeNode root) {
    if (root == null) return new ArrayList<>();
    TreeMap<Integer, Integer> hdMap = new TreeMap<>();  // HD → node val
    Queue<int[]> q = new LinkedList<>();                // {node_index_in_bfs, HD}
    // Store node + HD together
    Queue<Object[]> queue = new LinkedList<>();
    queue.offer(new Object[]{root, 0});
    while (!queue.isEmpty()) {
        Object[] cur = queue.poll();
        TreeNode node = (TreeNode) cur[0];
        int hd = (int) cur[1];
        hdMap.putIfAbsent(hd, node.val);  // only first node at each HD
        if (node.left != null) queue.offer(new Object[]{node.left, hd - 1});
        if (node.right != null) queue.offer(new Object[]{node.right, hd + 1});
    }
    return new ArrayList<>(hdMap.values());
}
```

---

### 15. Bottom View of Binary Tree
**⚠️ Nuance:** Same as top view but **last** node at each HD wins (no `putIfAbsent` — always overwrite).

```java
static List<Integer> bottomView(TreeNode root) {
    if (root == null) return new ArrayList<>();
    TreeMap<Integer, Integer> hdMap = new TreeMap<>();
    Queue<Object[]> queue = new LinkedList<>();
    queue.offer(new Object[]{root, 0});
    while (!queue.isEmpty()) {
        Object[] cur = queue.poll();
        TreeNode node = (TreeNode) cur[0];
        int hd = (int) cur[1];
        hdMap.put(hd, node.val);          // overwrite = last node at each HD wins
        if (node.left != null) queue.offer(new Object[]{node.left, hd - 1});
        if (node.right != null) queue.offer(new Object[]{node.right, hd + 1});
    }
    return new ArrayList<>(hdMap.values());
}
```

---

### 16. Left View of Binary Tree
```java
static List<Integer> leftView(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (i == 0) res.add(node.val);  // first node of each level
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
    }
    return res;
}
```

---

### 17. Vertical Order Traversal
**⚠️ Nuance:** Differs from top/bottom view — if two nodes share the same (row, col), sort them by value. Use `TreeMap<col, TreeMap<row, PriorityQueue<val>>>`.

```java
static List<List<Integer>> verticalTraversal(TreeNode root) {
    TreeMap<Integer, TreeMap<Integer, PriorityQueue<Integer>>> map = new TreeMap<>();
    Queue<Object[]> q = new LinkedList<>();
    q.offer(new Object[]{root, 0, 0}); // {node, col, row}
    while (!q.isEmpty()) {
        Object[] cur = q.poll();
        TreeNode node = (TreeNode) cur[0];
        int col = (int) cur[1], row = (int) cur[2];
        map.computeIfAbsent(col, k -> new TreeMap<>())
           .computeIfAbsent(row, k -> new PriorityQueue<>())
           .offer(node.val);
        if (node.left != null) q.offer(new Object[]{node.left, col - 1, row + 1});
        if (node.right != null) q.offer(new Object[]{node.right, col + 1, row + 1});
    }
    List<List<Integer>> res = new ArrayList<>();
    for (var colMap : map.values()) {
        List<Integer> colList = new ArrayList<>();
        for (var pq : colMap.values()) while (!pq.isEmpty()) colList.add(pq.poll());
        res.add(colList);
    }
    return res;
}
```

---

## 🌲 Group 6: Binary Search Tree (BST)

**BST invariant:** `left.val < node.val < right.val` for **ALL** descendants, not just immediate children.

---

### 18. Search in BST
**Time:** O(h) — O(log N) balanced, O(N) skewed.

```java
static TreeNode searchBST(TreeNode root, int val) {
    if (root == null || root.val == val) return root;
    return val < root.val ? searchBST(root.left, val) : searchBST(root.right, val);
}
```

---

### 19. Insert in BST
**⚠️ Nuance:** Always insert as a **leaf**. Return the node to reconnect the tree after recursion.

```java
static TreeNode insertBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);  // new leaf
    if (val < root.val) root.left = insertBST(root.left, val);
    else if (val > root.val) root.right = insertBST(root.right, val);
    return root;
}
```

---

### 20. Delete in BST
**⚠️ Nuance:** Three cases:
1. No children → return null.
2. One child → return that child.
3. Two children → replace with **in-order successor** (leftmost in right subtree), then delete that successor.

```java
static TreeNode deleteBST(TreeNode root, int key) {
    if (root == null) return null;
    if (key < root.val) { root.left = deleteBST(root.left, key); }
    else if (key > root.val) { root.right = deleteBST(root.right, key); }
    else {
        if (root.left == null) return root.right;   // case 1 & 2
        if (root.right == null) return root.left;   // case 2
        // case 3: find in-order successor (leftmost in right subtree)
        TreeNode successor = root.right;
        while (successor.left != null) successor = successor.left;
        root.val = successor.val;                   // copy value
        root.right = deleteBST(root.right, successor.val); // delete successor
    }
    return root;
}
```

---

### 21. Validate BST
**⚠️ Nuance:** Don't just check `left.val < root.val < right.val` locally — this misses cases like a right subtree node being smaller than an ancestor. Pass `min` and `max` bounds down.

```java
static boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

static boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) &&   // left must be < current
           validate(node.right, node.val, max);    // right must be > current
}
```

---

### 22. Kth Smallest in BST
**⚠️ Nuance:** In-order traversal of BST gives sorted order. Stop early at k-th element.

```java
static int kthSmallest(TreeNode root, int k) {
    Stack<TreeNode> st = new Stack<>();
    TreeNode curr = root;
    while (curr != null || !st.isEmpty()) {
        while (curr != null) { st.push(curr); curr = curr.left; }
        curr = st.pop();
        if (--k == 0) return curr.val;
        curr = curr.right;
    }
    return -1;
}
```

---

### 23. Lowest Common Ancestor (LCA) — BST
**⚠️ Nuance:** In BST, LCA is the first node where p and q split to different sides (or one of them equals the node).

```java
static TreeNode lcaBST(TreeNode root, TreeNode p, TreeNode q) {
    if (root.val > p.val && root.val > q.val) return lcaBST(root.left, p, q);
    if (root.val < p.val && root.val < q.val) return lcaBST(root.right, p, q);
    return root;  // split point = LCA
}
```

---

### 24. Lowest Common Ancestor (LCA) — Binary Tree (General)
**⚠️ Nuance:** No BST property to exploit. Post-order DFS: if left and right both return non-null, current node is LCA. If only one side returns non-null, propagate that up.

```java
static TreeNode lcaBinaryTree(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left = lcaBinaryTree(root.left, p, q);
    TreeNode right = lcaBinaryTree(root.right, p, q);
    if (left != null && right != null) return root;  // found on both sides
    return left != null ? left : right;              // propagate found side
}
```

---

## ⚖️ Group 7: AVL Tree (Self-Balancing BST)

**AVL invariant:** For every node, `|height(left) - height(right)| ≤ 1`.  
**Balance Factor (BF)** = `height(left) - height(right)`.  
`BF > 1` → left heavy → rotate right. `BF < -1` → right heavy → rotate left.

**⚠️ Nuance: 4 rotation cases — memorize by the "zig-zag" pattern:**
```
Left-Left (LL)   → Right Rotation
Right-Right (RR) → Left Rotation
Left-Right (LR)  → Left rotate left child, then Right rotate root
Right-Left (RL)  → Right rotate right child, then Left rotate root
```

```java
static class AVLNode {
    int val, height;
    AVLNode left, right;
    AVLNode(int val) { this.val = val; this.height = 1; }
}

static int avlHeight(AVLNode node) {
    return node == null ? 0 : node.height;
}

static int getBalance(AVLNode node) {
    return node == null ? 0 : avlHeight(node.left) - avlHeight(node.right);
}

static void updateHeight(AVLNode node) {
    if (node != null)
        node.height = 1 + Math.max(avlHeight(node.left), avlHeight(node.right));
}

// Right Rotation (LL case)
static AVLNode rotateRight(AVLNode y) {
    AVLNode x = y.left;
    AVLNode T2 = x.right;
    x.right = y;
    y.left = T2;
    updateHeight(y);
    updateHeight(x);
    return x;  // new root
}

// Left Rotation (RR case)
static AVLNode rotateLeft(AVLNode x) {
    AVLNode y = x.right;
    AVLNode T2 = y.left;
    y.left = x;
    x.right = T2;
    updateHeight(x);
    updateHeight(y);
    return y;  // new root
}

static AVLNode avlInsert(AVLNode node, int val) {
    // 1. Normal BST insert
    if (node == null) return new AVLNode(val);
    if (val < node.val) node.left = avlInsert(node.left, val);
    else if (val > node.val) node.right = avlInsert(node.right, val);
    else return node;  // duplicate

    // 2. Update height
    updateHeight(node);

    // 3. Get balance factor
    int bf = getBalance(node);

    // 4. Four rotation cases
    if (bf > 1 && val < node.left.val)           return rotateRight(node);          // LL
    if (bf < -1 && val > node.right.val)         return rotateLeft(node);           // RR
    if (bf > 1 && val > node.left.val) {         // LR
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }
    if (bf < -1 && val < node.right.val) {       // RL
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }
    return node;
}
```

---

## 📝 Group 8: Serialization & Deserialization

**Use:** Convert tree to string (for storage/transmission), reconstruct from string.

**⚠️ Nuance:**
- Pre-order is easiest to serialize/deserialize.
- Use a sentinel like `"#"` for null nodes so structure is preserved.
- In-order alone is **not enough** to reconstruct a tree (need pre-order or post-order too).
- Using a global index or `ArrayDeque` as pointer avoids index-passing complexity.

---

### 25. Serialize & Deserialize (Pre-order + Null markers)

```java
// Serialize: pre-order DFS, "#" for null
static String serialize(TreeNode root) {
    StringBuilder sb = new StringBuilder();
    serializeDFS(root, sb);
    return sb.toString();
}

static void serializeDFS(TreeNode node, StringBuilder sb) {
    if (node == null) { sb.append("#,"); return; }
    sb.append(node.val).append(",");
    serializeDFS(node.left, sb);
    serializeDFS(node.right, sb);
}

// Deserialize: consume from deque in same pre-order
static TreeNode deserialize(String data) {
    Deque<String> q = new ArrayDeque<>(Arrays.asList(data.split(",")));
    return deserializeDFS(q);
}

static TreeNode deserializeDFS(Deque<String> q) {
    String val = q.poll();
    if (val.equals("#")) return null;
    TreeNode node = new TreeNode(Integer.parseInt(val));
    node.left = deserializeDFS(q);
    node.right = deserializeDFS(q);
    return node;
}
```

---

### 26. Serialize & Deserialize using Level-order (BFS)

```java
static String serializeBFS(TreeNode root) {
    if (root == null) return "";
    StringBuilder sb = new StringBuilder();
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        TreeNode node = q.poll();
        if (node == null) { sb.append("#,"); continue; }
        sb.append(node.val).append(",");
        q.offer(node.left);
        q.offer(node.right);
    }
    return sb.toString();
}

static TreeNode deserializeBFS(String data) {
    if (data.isEmpty()) return null;
    String[] vals = data.split(",");
    TreeNode root = new TreeNode(Integer.parseInt(vals[0]));
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    int i = 1;
    while (!q.isEmpty() && i < vals.length) {
        TreeNode node = q.poll();
        if (!vals[i].equals("#")) {
            node.left = new TreeNode(Integer.parseInt(vals[i]));
            q.offer(node.left);
        }
        i++;
        if (i < vals.length && !vals[i].equals("#")) {
            node.right = new TreeNode(Integer.parseInt(vals[i]));
            q.offer(node.right);
        }
        i++;
    }
    return root;
}
```

---

## 🔗 Group 9: Flattening a Binary Tree

**Use:** Convert binary tree to linked list in-place (in pre-order).

---

### 27. Flatten Binary Tree to Linked List (In-place, Pre-order)
**⚠️ Nuance:** The trick is to find the **rightmost node of the left subtree**, attach the right subtree there, then move the entire left subtree to the right and clear left. Iterative, O(N) time, O(1) space (Morris-like).

```java
// Iterative O(1) space (Morris-like approach)
static void flatten(TreeNode root) {
    TreeNode curr = root;
    while (curr != null) {
        if (curr.left != null) {
            // Find rightmost of left subtree
            TreeNode rightmost = curr.left;
            while (rightmost.right != null) rightmost = rightmost.right;
            // Attach right subtree to rightmost
            rightmost.right = curr.right;
            // Move left subtree to right
            curr.right = curr.left;
            curr.left = null;
        }
        curr = curr.right;
    }
}

// Recursive approach (reverse post-order: right → left → root)
static TreeNode prev = null;
static void flattenRecursive(TreeNode root) {
    if (root == null) return;
    flattenRecursive(root.right);
    flattenRecursive(root.left);
    root.right = prev;
    root.left = null;
    prev = root;
}
```

---

## 🔁 Group 10: Tree Construction

---

### 28. Build Tree from Pre-order + In-order
**⚠️ Nuance:** Pre-order gives root (first element). In-order tells how many nodes are in left vs right subtree. Use a HashMap for O(1) index lookup in in-order array.

```java
static int[] preorder, inorder;
static Map<Integer, Integer> inMap;

static TreeNode buildTree(int[] preorder, int[] inorder) {
    inMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) inMap.put(inorder[i], i);
    TreeNode.preorder = preorder;
    return build(0, 0, inorder.length - 1);
}

static int preIdx = 0;
static TreeNode build(int preStart, int inStart, int inEnd) {
    if (inStart > inEnd) return null;
    TreeNode root = new TreeNode(preorder[preIdx++]);
    int inMid = inMap.get(root.val);
    root.left = build(preStart, inStart, inMid - 1);
    root.right = build(preStart, inMid + 1, inEnd);
    return root;
}
```

---

### 29. Build Tree from Post-order + In-order
**⚠️ Nuance:** Post-order root is the **last** element. Process post-order from right to left (build right subtree first).

```java
static int postIdx;

static TreeNode buildFromPostIn(int[] inorder, int[] postorder) {
    inMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) inMap.put(inorder[i], i);
    postIdx = postorder.length - 1;
    return buildPost(postorder, 0, inorder.length - 1);
}

static TreeNode buildPost(int[] postorder, int inStart, int inEnd) {
    if (inStart > inEnd) return null;
    TreeNode root = new TreeNode(postorder[postIdx--]);
    int inMid = inMap.get(root.val);
    root.right = buildPost(postorder, inMid + 1, inEnd);   // right first!
    root.left = buildPost(postorder, inStart, inMid - 1);
    return root;
}
```

---

## 🏷️ Group 11: Lowest Common Ancestor & Distance

### 30. LCA with Distance Between Two Nodes
```java
static int distanceBetweenNodes(TreeNode root, int p, int q) {
    TreeNode lca = lcaBinaryTree(root, new TreeNode(p), new TreeNode(q));
    return depth(lca, p, 0) + depth(lca, q, 0);
}

static int depth(TreeNode node, int target, int d) {
    if (node == null) return -1;
    if (node.val == target) return d;
    int left = depth(node.left, target, d + 1);
    return left != -1 ? left : depth(node.right, target, d + 1);
}
```

---

## 🔄 Group 12: Important BST Conversions

### 31. Convert Sorted Array to Balanced BST
**⚠️ Nuance:** Always pick the **middle element** as root to ensure balance.

```java
static TreeNode sortedArrayToBST(int[] nums) {
    return arrayToBST(nums, 0, nums.length - 1);
}

static TreeNode arrayToBST(int[] nums, int lo, int hi) {
    if (lo > hi) return null;
    int mid = lo + (hi - lo) / 2;
    TreeNode root = new TreeNode(nums[mid]);
    root.left = arrayToBST(nums, lo, mid - 1);
    root.right = arrayToBST(nums, mid + 1, hi);
    return root;
}
```

---

### 32. BST to Greater Sum Tree (GST)
**⚠️ Nuance:** Reverse in-order (Right → Root → Left) gives descending order. Accumulate running sum.

```java
static int runningSum = 0;

static TreeNode bstToGst(TreeNode root) {
    if (root == null) return null;
    bstToGst(root.right);          // visit right first (larger values)
    runningSum += root.val;
    root.val = runningSum;
    bstToGst(root.left);
    return root;
}
```

---

### 33. Inorder Successor & Predecessor in BST
```java
// Successor: smallest node greater than target
static TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
    TreeNode successor = null;
    while (root != null) {
        if (root.val > p.val) { successor = root; root = root.left; }
        else root = root.right;
    }
    return successor;
}

// Predecessor: largest node smaller than target
static TreeNode inorderPredecessor(TreeNode root, TreeNode p) {
    TreeNode predecessor = null;
    while (root != null) {
        if (root.val < p.val) { predecessor = root; root = root.right; }
        else root = root.left;
    }
    return predecessor;
}
```

---

## 🔤 Group 13: Trie (Prefix Tree)

**Use:** Word search, autocomplete, spell check, prefix matching, IP routing.  
**Time:** O(L) for insert/search/delete where L = word length.

**⚠️ Nuance:**
- Each node represents a **character**, not a word.
- `isEnd` flag marks end of a valid word — a node can be both mid-word and end-of-word.
- For `startsWith`, you just need to reach the last prefix character (no `isEnd` check).

---

### 34. Standard Trie

```java
static class Trie {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }

    TrieNode root = new TrieNode();

    // Insert word
    void insert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null)
                curr.children[idx] = new TrieNode();
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }

    // Exact word search
    boolean search(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) return false;
            curr = curr.children[idx];
        }
        return curr.isEnd;  // must be end of a word
    }

    // Prefix check (startsWith)
    boolean startsWith(String prefix) {
        TrieNode curr = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) return false;
            curr = curr.children[idx];
        }
        return true;  // no isEnd check needed
    }

    // Delete word
    boolean delete(String word) {
        return deleteDFS(root, word, 0);
    }

    boolean deleteDFS(TrieNode curr, String word, int i) {
        if (i == word.length()) {
            if (!curr.isEnd) return false;
            curr.isEnd = false;
            return isEmptyNode(curr);   // true if node can be deleted
        }
        int idx = word.charAt(i) - 'a';
        if (curr.children[idx] == null) return false;
        boolean shouldDelete = deleteDFS(curr.children[idx], word, i + 1);
        if (shouldDelete) curr.children[idx] = null;
        return !curr.isEnd && isEmptyNode(curr);
    }

    boolean isEmptyNode(TrieNode node) {
        for (TrieNode child : node.children) if (child != null) return false;
        return true;
    }
}
```

---

### 35. Trie with Wildcard Search ('.' matches any character)
**Use:** Word Dictionary with addWord / search supporting `.` wildcard.

```java
static class WildcardTrie {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }

    TrieNode root = new TrieNode();

    void addWord(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) curr.children[idx] = new TrieNode();
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }

    boolean search(String word) {
        return searchDFS(word, 0, root);
    }

    boolean searchDFS(String word, int i, TrieNode curr) {
        if (i == word.length()) return curr.isEnd;
        char c = word.charAt(i);
        if (c == '.') {
            for (TrieNode child : curr.children)
                if (child != null && searchDFS(word, i + 1, child)) return true;
            return false;
        }
        int idx = c - 'a';
        if (curr.children[idx] == null) return false;
        return searchDFS(word, i + 1, curr.children[idx]);
    }
}
```

---

### 36. Count Words with Given Prefix
```java
static int countWordsWithPrefix(Trie trie, String prefix) {
    TrieNode curr = trie.root;
    for (char c : prefix.toCharArray()) {
        int idx = c - 'a';
        if (curr.children[idx] == null) return 0;
        curr = curr.children[idx];
    }
    return countAllWords(curr);
}

static int countAllWords(TrieNode node) {
    int count = node.isEnd ? 1 : 0;
    for (TrieNode child : node.children)
        if (child != null) count += countAllWords(child);
    return count;
}
```

---

## 🧩 Group 14: More Important Tree Patterns

---

### 37. Check if Two Trees are Identical
```java
static boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    return p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```

---

### 38. Check Subtree
**⚠️ Nuance:** A subtree must match **exactly**, including null leaves. Don't confuse with just finding the value — the entire structure must match.

```java
static boolean isSubtree(TreeNode root, TreeNode subRoot) {
    if (root == null) return false;
    if (isSameTree(root, subRoot)) return true;
    return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
}
```

---

### 39. Mirror / Invert Binary Tree
```java
static TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode tmp = root.left;
    root.left = invertTree(root.right);
    root.right = invertTree(tmp);
    return root;
}
```

---

### 40. Check Symmetric Tree
**⚠️ Nuance:** Compare mirror positions — left's left with right's right, and left's right with right's left.

```java
static boolean isSymmetric(TreeNode root) {
    return isMirror(root.left, root.right);
}

static boolean isMirror(TreeNode l, TreeNode r) {
    if (l == null && r == null) return true;
    if (l == null || r == null) return false;
    return l.val == r.val && isMirror(l.left, r.right) && isMirror(l.right, r.left);
}
```

---

### 41. Connect All Level-order Siblings (next pointer)
**⚠️ Nuance:** Use a dummy node as head of each level to avoid null checks.

```java
// Assumes: class Node { int val; Node left, right, next; }
static Node connect(Node root) {
    if (root == null) return null;
    Queue<Node> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        Node dummy = new Node(-1);
        Node curr = dummy;
        for (int i = 0; i < size; i++) {
            Node node = q.poll();
            curr.next = node;
            curr = curr.next;
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        curr.next = null;
    }
    return root;
}
```

---

### 42. Maximum Width of Binary Tree
**⚠️ Nuance:** Assign indices to nodes like a heap array: root = 1, left child = 2i, right child = 2i+1. Width at each level = `lastIndex - firstIndex + 1`. Use `long` to avoid overflow on deep trees.

```java
static int widthOfBinaryTree(TreeNode root) {
    if (root == null) return 0;
    int maxWidth = 0;
    Queue<Object[]> q = new LinkedList<>();
    q.offer(new Object[]{root, 1L});
    while (!q.isEmpty()) {
        int size = q.size();
        long first = 0, last = 0;
        for (int i = 0; i < size; i++) {
            Object[] cur = q.poll();
            TreeNode node = (TreeNode) cur[0];
            long idx = (long) cur[1];
            if (i == 0) first = idx;
            if (i == size - 1) last = idx;
            if (node.left != null) q.offer(new Object[]{node.left, 2 * idx});
            if (node.right != null) q.offer(new Object[]{node.right, 2 * idx + 1});
        }
        maxWidth = Math.max(maxWidth, (int)(last - first + 1));
    }
    return maxWidth;
}
```

---

### 43. Count Good Nodes
**⚠️ Nuance:** A node is "good" if no ancestor has a greater value. Pass `maxSoFar` down.

```java
static int goodNodes(TreeNode root) {
    return countGood(root, Integer.MIN_VALUE);
}

static int countGood(TreeNode node, int maxSoFar) {
    if (node == null) return 0;
    int count = node.val >= maxSoFar ? 1 : 0;
    int newMax = Math.max(maxSoFar, node.val);
    return count + countGood(node.left, newMax) + countGood(node.right, newMax);
}
```

---

### 44. Boundary Traversal of Binary Tree
**Steps:** Left boundary (top-down, excluding leaves) + All leaves (left to right) + Right boundary (bottom-up, excluding leaves).

```java
static List<Integer> boundaryTraversal(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    if (!isLeaf(root)) res.add(root.val);
    addLeftBoundary(root.left, res);
    addLeaves(root, res);
    addRightBoundary(root.right, res);
    return res;
}

static boolean isLeaf(TreeNode n) { return n.left == null && n.right == null; }

static void addLeftBoundary(TreeNode node, List<Integer> res) {
    if (node == null || isLeaf(node)) return;
    res.add(node.val);
    addLeftBoundary(node.left != null ? node.left : node.right, res);
}

static void addLeaves(TreeNode node, List<Integer> res) {
    if (node == null) return;
    if (isLeaf(node)) { res.add(node.val); return; }
    addLeaves(node.left, res);
    addLeaves(node.right, res);
}

static void addRightBoundary(TreeNode node, List<Integer> res) {
    if (node == null || isLeaf(node)) return;
    addRightBoundary(node.right != null ? node.right : node.left, res);
    res.add(node.val);  // add after recursion = bottom-up
}
```

---

## 🧠 Master Cheat Sheet — Tree Algorithm Selection

| Problem | Technique | Time | Key Insight |
|---|---|---|---|
| Traversal (pre/in/post) | Recursion or Stack | O(N) | DLR / LDR / LRD |
| Level-order / height | BFS (Queue) | O(N) | Snapshot queue size per level |
| Zigzag traversal | BFS + flag | O(N) | addFirst vs addLast |
| Diameter | Post-order DFS + global max | O(N) | Update max at each node |
| Balanced check | Post-order, return -1 sentinel | O(N) | Avoid recomputing heights |
| Max path sum | Post-order + global max | O(N) | `max(gain, 0)` ignores negatives |
| Path sum (any node) | Prefix sum + HashMap | O(N) | Like subarray sum |
| Root-to-leaf path | DFS + backtrack | O(N) | Copy list when adding result |
| LCA (general tree) | Post-order, return found node | O(N) | Both sides non-null = LCA |
| LCA (BST) | Compare val to p and q | O(h) | Split point = LCA |
| BST validate | DFS with min/max bounds | O(N) | Pass range, not just parent |
| Kth smallest (BST) | Iterative in-order | O(h+k) | Stop early at k |
| Serialize/Deserialize | Pre-order + null markers | O(N) | Deque as pointer |
| Flatten to list | Morris-like iterative | O(N) | Find rightmost of left subtree |
| Build from traversals | Pre/Post + In-order + HashMap | O(N) | In-order splits left/right |
| Top/Bottom view | BFS + horizontal distance map | O(N log N) | TreeMap for HD ordering |
| Vertical traversal | BFS + (col, row) + PriorityQueue | O(N log N) | Sort by col, row, then val |
| AVL insert | BST insert + balance + rotate | O(log N) | 4 cases: LL RR LR RL |
| Trie insert/search | Character-by-character | O(L) | children[c - 'a'] |
| Trie wildcard | DFS with '.' → try all children | O(N·L) | Backtracking on '.' |
| Inorder successor (BST) | Walk BST with last-seen | O(h) | First node > target |
| Symmetric tree | Compare mirror positions | O(N) | l.left↔r.right, l.right↔r.left |
| Max width | BFS + heap indexing (2i, 2i+1) | O(N) | Use `long` for indices |
| Boundary traversal | Left boundary + leaves + right | O(N) | Right boundary is bottom-up |

---

## ⚡ Key Nuances to Remember in Interviews

**Traversal**
- Post-order is king for subtree problems — you need both children's results before current node.
- Iterative in-order: push while going left, pop + visit + go right.
- Morris traversal modifies and restores — mention it for O(1) space bonus points.

**Path Problems**
- "Any node to any node" paths → global max updated at each node, return only one branch upward.
- Negative values in tree → use `max(gain, 0)` to optionally discard a branch.
- "Count paths summing to target" → prefix sum + HashMap (same trick as subarray).
- When collecting path lists, always `new ArrayList<>(path)` to copy, not reference.

**BST Specifics**
- Validate BST with **range bounds** (min/max), not just comparing to parent.
- LCA in BST: first node where p and q split sides.
- Delete with 2 children: replace with **in-order successor** (leftmost of right subtree).
- Kth smallest: iterative in-order, count down, stop early.

**AVL Tree**
- 4 rotation cases, always re-check balance factor **after every insert/delete**.
- LL → 1 right rotation. RR → 1 left rotation. LR / RL → 2 rotations.
- Balance Factor = `height(left) - height(right)`. BF ∈ {-1, 0, 1} = balanced.

**Views**
- Top view: first node at each HD (putIfAbsent). Bottom: last node (always put).
- Left/Right view = first/last node at each level in BFS.
- Vertical traversal ≠ top/bottom view — same position nodes sorted by value.

**Trie**
- `search` needs `isEnd = true` at last char. `startsWith` does not.
- Use HashMap instead of array `children[26]` when alphabet is large or unknown.
- Deletion is tricky — only remove nodes that are no longer needed (check `isEmptyNode`).

**Serialization**
- Pre-order + null markers is the cleanest. Null markers preserve structure.
- In-order alone cannot reconstruct a tree.
- Use `Deque<String>` as a shared pointer — cleaner than passing index arrays.

**Construction**
- Pre-order + In-order: pre-order[0] is root, find it in in-order to split left/right.
- Post-order + In-order: post-order[last] is root, process right subtree **before** left.

---

*Covers all standard interview tree topics: traversal, BST, AVL, paths, views, serialization, flattening, LCA, Trie, and construction patterns.*

---

## ⭐ Important Variations Added

### A) Iterative Post-order (One Stack)
**Why important:** Common follow-up when interviewer asks to avoid recursion and also avoid reverse-insert tricks.

```java
static List<Integer> postorderIterativeOneStack(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    Stack<TreeNode> st = new Stack<>();
    TreeNode curr = root, lastVisited = null;

    while (curr != null || !st.isEmpty()) {
        if (curr != null) {
            st.push(curr);
            curr = curr.left;
        } else {
            TreeNode peek = st.peek();
            if (peek.right != null && lastVisited != peek.right) {
                curr = peek.right;
            } else {
                res.add(peek.val);
                lastVisited = st.pop();
            }
        }
    }
    return res;
}
```

### B) AVL Delete (Rebalance After Deletion)
**Why important:** AVL insert is frequently asked, but delete is a common “advanced variation” because rebalancing must also happen after removal.

```java
static AVLNode avlDelete(AVLNode root, int key) {
    if (root == null) return null;

    // 1) Standard BST delete
    if (key < root.val) root.left = avlDelete(root.left, key);
    else if (key > root.val) root.right = avlDelete(root.right, key);
    else {
        if (root.left == null || root.right == null) {
            root = (root.left != null) ? root.left : root.right;
        } else {
            AVLNode succ = root.right;
            while (succ.left != null) succ = succ.left;
            root.val = succ.val;
            root.right = avlDelete(root.right, succ.val);
        }
    }

    if (root == null) return null;

    // 2) Update height
    updateHeight(root);

    // 3) Rebalance
    int bf = getBalance(root);

    if (bf > 1 && getBalance(root.left) >= 0) return rotateRight(root);  // LL
    if (bf > 1 && getBalance(root.left) < 0) {                            // LR
        root.left = rotateLeft(root.left);
        return rotateRight(root);
    }
    if (bf < -1 && getBalance(root.right) <= 0) return rotateLeft(root);  // RR
    if (bf < -1 && getBalance(root.right) > 0) {                           // RL
        root.right = rotateRight(root.right);
        return rotateLeft(root);
    }

    return root;
}
```

### C) BST Iterator (In-order Iterator)
**Why important:** Very common design/problem-solving question (lazy in-order traversal in O(h) space).

```java
static class BSTIterator {
    Stack<TreeNode> st = new Stack<>();

    BSTIterator(TreeNode root) {
        pushLeft(root);
    }

    boolean hasNext() {
        return !st.isEmpty();
    }

    int next() {
        TreeNode node = st.pop();
        if (node.right != null) pushLeft(node.right);
        return node.val;
    }

    void pushLeft(TreeNode node) {
        while (node != null) {
            st.push(node);
            node = node.left;
        }
    }
}
```
