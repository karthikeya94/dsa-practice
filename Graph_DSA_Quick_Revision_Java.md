# Graph DSA — Ultimate Quick Revision Guide (Java)

> **How to use this guide:** Each algorithm follows the same pattern: *When to use → Nuance/Gotcha → Steps → Code.*  
> Before any interview, scan the **Master Cheat Sheet** at the bottom first, then drill whichever algorithm you are shaky on.

---

## 📦 Group 1: Graph Representation

| Structure | Space | Edge Check | Best For |
|---|---|---|---|
| Adjacency List | O(V + E) | O(degree) | BFS, DFS, Dijkstra, Prim — **default** |
| Adjacency Matrix | O(V²) | O(1) | Dense graphs, Floyd-Warshall |
| Edge List | O(E) | O(E) | Kruskal, Bellman-Ford |

**⚠️ Nuance:** Always default to adjacency list. Use matrix only when you need O(1) edge existence check or the graph is dense (E ≈ V²).

```java
import java.util.*;

// Unweighted adjacency list
static List<List<Integer>> buildGraph(int V) {
    List<List<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    return adj;
}

// Weighted adjacency list
static class Edge {
    int to, wt;
    Edge(int to, int wt) { this.to = to; this.wt = wt; }
}
static List<List<Edge>> buildWeightedGraph(int V) {
    List<List<Edge>> adj = new ArrayList<>();
    for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
    return adj;
}

// Edge list (for Kruskal / Bellman-Ford)
static class WEdge {
    int u, v, w;
    WEdge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
}
```

---

## 🔍 Group 2: Graph Traversal

### 1. BFS (Breadth-First Search)
**Use:** Shortest path in unweighted graph, level-order, connected components.  
**Time:** O(V + E) | **Space:** O(V)

**⚠️ Nuance:** Mark visited **when you enqueue**, not when you dequeue. Marking on dequeue causes duplicate entries in the queue for large graphs.

**Steps:**
1. Enqueue start node, mark visited.
2. While queue not empty: dequeue `u`, process it.
3. For each unvisited neighbor `v`: mark visited, enqueue.

```java
static List<Integer> bfs(List<List<Integer>> adj, int start) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    Queue<Integer> q = new LinkedList<>();
    List<Integer> order = new ArrayList<>();

    q.offer(start);
    vis[start] = true;

    while (!q.isEmpty()) {
        int u = q.poll();
        order.add(u);
        for (int v : adj.get(u)) {
            if (!vis[v]) { vis[v] = true; q.offer(v); }
        }
    }
    return order;
}

// For DISCONNECTED graph — always wrap in a loop
static List<Integer> bfsAll(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    List<Integer> order = new ArrayList<>();
    for (int i = 0; i < V; i++) {
        if (!vis[i]) {
            Queue<Integer> q = new LinkedList<>();
            q.offer(i); vis[i] = true;
            while (!q.isEmpty()) {
                int u = q.poll(); order.add(u);
                for (int v : adj.get(u)) {
                    if (!vis[v]) { vis[v] = true; q.offer(v); }
                }
            }
        }
    }
    return order;
}
```

---

### 2. DFS — Recursive
**Use:** Cycle detection, topological sort, connected components, path finding, backtracking.  
**Time:** O(V + E) | **Space:** O(V) recursion stack

**⚠️ Nuance:** Risk of StackOverflow for large graphs (V ~ 10⁵). Prefer iterative DFS in those cases.

**Steps:**
1. Mark current node visited.
2. For each unvisited neighbor, recurse.
3. Backtracking is automatic on return.

```java
static void dfsRec(int u, List<List<Integer>> adj, boolean[] vis, List<Integer> order) {
    vis[u] = true;
    order.add(u);
    for (int v : adj.get(u)) {
        if (!vis[v]) dfsRec(v, adj, vis, order);
    }
}

// Wrapper for disconnected graph
static List<Integer> dfsAll(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    List<Integer> order = new ArrayList<>();
    for (int i = 0; i < V; i++)
        if (!vis[i]) dfsRec(i, adj, vis, order);
    return order;
}
```

---

### 3. DFS — Iterative (with Stack)
**Use:** Same as recursive DFS; safe for deep graphs; avoids StackOverflow.  
**⚠️ Nuance:** Mark visited **after popping** (not on push), because a node can be pushed multiple times before processing. Push neighbors in **reverse order** to match recursive DFS traversal order.

**Steps:**
1. Push start node.
2. Pop node `u`. If already visited, skip.
3. Mark visited, process.
4. Push unvisited neighbors in reverse order.

```java
static List<Integer> dfsIterative(List<List<Integer>> adj, int start) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    Stack<Integer> st = new Stack<>();
    List<Integer> order = new ArrayList<>();

    st.push(start);
    while (!st.isEmpty()) {
        int u = st.pop();
        if (vis[u]) continue;   // ← key: skip if already processed
        vis[u] = true;
        order.add(u);
        List<Integer> nbrs = adj.get(u);
        for (int i = nbrs.size() - 1; i >= 0; i--) {  // reverse for same order as recursive
            if (!vis[nbrs.get(i)]) st.push(nbrs.get(i));
        }
    }
    return order;
}
```

---

### 4. Connected Components Count
**Steps:** Loop all nodes. Each unvisited node = start of a new component.

```java
static int countComponents(List<List<Integer>> adj) {
    int V = adj.size(), count = 0;
    boolean[] vis = new boolean[V];
    for (int i = 0; i < V; i++) {
        if (!vis[i]) {
            count++;
            // run BFS or DFS from i
            Queue<Integer> q = new LinkedList<>();
            q.offer(i); vis[i] = true;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj.get(u))
                    if (!vis[v]) { vis[v] = true; q.offer(v); }
            }
        }
    }
    return count;
}
```

---

## 🔄 Group 3: Cycle Detection

**Memory trick:**
- Undirected → parent check (DFS/BFS) or DSU
- Directed → path array / recursion stack (DFS), or Kahn's (BFS)

---

### 5. Undirected Cycle — DFS (Parent Check)
**⚠️ Nuance:** The key distinction is between a back-edge and the edge to parent. Always pass parent into recursion. For **multi-edge graphs**, use parent index, not parent value.

**Steps:**
1. DFS with parent.
2. If neighbor is visited AND not parent → cycle found.

```java
static boolean hasCycleUndirectedDFS(int u, int parent, List<List<Integer>> adj, boolean[] vis) {
    vis[u] = true;
    for (int v : adj.get(u)) {
        if (!vis[v]) {
            if (hasCycleUndirectedDFS(v, u, adj, vis)) return true;
        } else if (v != parent) return true;
    }
    return false;
}

static boolean isCycleUndirected(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    for (int i = 0; i < V; i++)
        if (!vis[i] && hasCycleUndirectedDFS(i, -1, adj, vis)) return true;
    return false;
}
```

---

### 6. Undirected Cycle — BFS (Parent Check)
**Steps:**
1. Queue stores (node, parent) pairs.
2. If visited neighbor is not parent → cycle.

```java
static boolean isCycleUndirectedBFS(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    for (int i = 0; i < V; i++) {
        if (vis[i]) continue;
        Queue<int[]> q = new LinkedList<>();  // {node, parent}
        q.offer(new int[]{i, -1});
        vis[i] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int u = cur[0], par = cur[1];
            for (int v : adj.get(u)) {
                if (!vis[v]) { vis[v] = true; q.offer(new int[]{v, u}); }
                else if (v != par) return true;
            }
        }
    }
    return false;
}
```

---

### 7. Directed Cycle — DFS (Path Array / Recursion Stack)
**⚠️ Nuance:** You need TWO boolean arrays: `vis[]` (globally visited) and `pathVis[]` (on current DFS path). A node in `pathVis` but already in `vis` indicates a back-edge = cycle. **Always reset `pathVis[u] = false` on backtrack.**

**Steps:**
1. Mark both `vis[u]` and `pathVis[u]` true.
2. If neighbor is in `pathVis` → cycle.
3. On backtrack, set `pathVis[u] = false`.

```java
static boolean hasCycleDirectedDFS(int u, List<List<Integer>> adj, boolean[] vis, boolean[] pathVis) {
    vis[u] = true; pathVis[u] = true;
    for (int v : adj.get(u)) {
        if (!vis[v]) {
            if (hasCycleDirectedDFS(v, adj, vis, pathVis)) return true;
        } else if (pathVis[v]) return true;
    }
    pathVis[u] = false;  // ← crucial: unmark on backtrack
    return false;
}

static boolean isCycleDirected(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V], pathVis = new boolean[V];
    for (int i = 0; i < V; i++)
        if (!vis[i] && hasCycleDirectedDFS(i, adj, vis, pathVis)) return true;
    return false;
}
```

---

### 8. Directed Cycle — BFS / Kahn's (Bonus: also detects cycle)
**⚠️ Nuance:** If `processedCount < V` after Kahn's, a cycle exists. This is the cleanest cycle check in directed graphs — no recursion needed.

```java
static boolean isCycleDirectedBFS(List<List<Integer>> adj) {
    int V = adj.size();
    int[] indegree = new int[V];
    for (int u = 0; u < V; u++)
        for (int v : adj.get(u)) indegree[v]++;
    Queue<Integer> q = new LinkedList<>();
    for (int i = 0; i < V; i++) if (indegree[i] == 0) q.offer(i);
    int count = 0;
    while (!q.isEmpty()) {
        int u = q.poll(); count++;
        for (int v : adj.get(u)) if (--indegree[v] == 0) q.offer(v);
    }
    return count != V;  // true = cycle exists
}
```

---

## 📋 Group 4: Topological Sort (DAGs only)

**⚠️ Nuance:** Topological sort is only valid on DAGs (Directed Acyclic Graphs). If a cycle exists, topo sort is undefined. Kahn's also detects cycles as a side effect.

---

### 9. Topological Sort — Kahn's (BFS, Indegree)
**Use:** Task scheduling, course prerequisites, build order, detecting cycles in directed graphs.

**Steps:**
1. Compute in-degree of all nodes.
2. Enqueue all nodes with in-degree 0.
3. Dequeue `u`, add to result, reduce in-degree of neighbors.
4. If neighbor's in-degree hits 0, enqueue it.
5. If result size < V → cycle detected.

```java
static List<Integer> topoKahn(List<List<Integer>> adj) {
    int V = adj.size();
    int[] indegree = new int[V];
    for (int u = 0; u < V; u++)
        for (int v : adj.get(u)) indegree[v]++;
    Queue<Integer> q = new LinkedList<>();
    for (int i = 0; i < V; i++) if (indegree[i] == 0) q.offer(i);
    List<Integer> topo = new ArrayList<>();
    while (!q.isEmpty()) {
        int u = q.poll(); topo.add(u);
        for (int v : adj.get(u)) if (--indegree[v] == 0) q.offer(v);
    }
    return topo;  // size < V means cycle exists
}
```

---

### 10. Topological Sort — DFS (Finish-time Stack)
**⚠️ Nuance:** Push node **after** all neighbors are visited (post-order). Pop the stack for answer (reversed post-order = topo order).

**Steps:**
1. DFS all nodes.
2. After all neighbors done, push node to stack.
3. Pop stack for topo order.

```java
static void topoDfs(int u, List<List<Integer>> adj, boolean[] vis, Stack<Integer> st) {
    vis[u] = true;
    for (int v : adj.get(u)) if (!vis[v]) topoDfs(v, adj, vis, st);
    st.push(u);  // ← push AFTER exploring all neighbors
}

static List<Integer> topoSortDFS(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    Stack<Integer> st = new Stack<>();
    for (int i = 0; i < V; i++) if (!vis[i]) topoDfs(i, adj, vis, st);
    List<Integer> topo = new ArrayList<>();
    while (!st.isEmpty()) topo.add(st.pop());
    return topo;
}
```

---

## 🛣️ Group 5: Shortest Paths

**Memory trick → pick algorithm by edge type:**
```
Unweighted     → BFS
+ve weights    → Dijkstra
-ve weights    → Bellman-Ford
All pairs      → Floyd-Warshall
DAG + weights  → Topo sort + relaxation (O(V+E), fastest for DAG)
```

---

### 11. BFS — Shortest Path (Unweighted)
**⚠️ Nuance:** BFS guarantees shortest path ONLY in unweighted graphs. Each level = 1 hop.

```java
static int[] shortestPathBFS(List<List<Integer>> adj, int src) {
    int V = adj.size();
    int[] dist = new int[V];
    Arrays.fill(dist, -1);
    Queue<Integer> q = new LinkedList<>();
    q.offer(src); dist[src] = 0;
    while (!q.isEmpty()) {
        int u = q.poll();
        for (int v : adj.get(u)) {
            if (dist[v] == -1) { dist[v] = dist[u] + 1; q.offer(v); }
        }
    }
    return dist;
}
```

---

### 12. Dijkstra — Shortest Path (Non-negative Weights)
**Time:** O(E log V) | **Space:** O(V)  
**⚠️ Nuance:**
- **Fails with negative edges** — use Bellman-Ford instead.
- Use `if (d > dist[u]) continue;` to skip stale priority queue entries (lazy deletion).
- The PriorityQueue stores `{node, distance}` sorted by distance.

**Steps:**
1. `dist[src] = 0`, all others = INF. Put src in min-heap.
2. Pop min-dist node. If stale, skip.
3. Relax all neighbors: if `dist[u] + w < dist[v]`, update and push.

```java
static int[] dijkstra(List<List<Edge>> adj, int src) {
    int V = adj.size();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{src, 0});

    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int u = cur[0], d = cur[1];
        if (d > dist[u]) continue;  // ← stale entry, skip
        for (Edge e : adj.get(u)) {
            if (dist[u] + e.wt < dist[e.to]) {
                dist[e.to] = dist[u] + e.wt;
                pq.offer(new int[]{e.to, dist[e.to]});
            }
        }
    }
    return dist;
}
```

---

### 13. Bellman-Ford — Shortest Path (Handles Negative Weights)
**Time:** O(V × E) | **Space:** O(V)  
**⚠️ Nuance:**
- Relax ALL edges exactly **V-1 times** (longest shortest path = V-1 edges).
- On the **V-th** pass, if any edge still relaxes → **negative cycle detected**.
- Works on **edge list**, not adjacency list.

**Steps:**
1. `dist[src] = 0`, all others = INF.
2. Repeat V-1 times: relax every edge.
3. Check one more pass for negative cycle.

```java
static int[] bellmanFord(int V, List<WEdge> edges, int src) {
    int[] dist = new int[V];
    Arrays.fill(dist, (int) 1e9);
    dist[src] = 0;

    for (int i = 1; i <= V - 1; i++) {
        for (WEdge e : edges) {
            if (dist[e.u] != (int) 1e9 && dist[e.u] + e.w < dist[e.v])
                dist[e.v] = dist[e.u] + e.w;
        }
    }
    // Negative cycle check
    for (WEdge e : edges) {
        if (dist[e.u] != (int) 1e9 && dist[e.u] + e.w < dist[e.v])
            throw new RuntimeException("Negative cycle detected!");
    }
    return dist;
}
```

---

### 14. Floyd-Warshall — All-Pairs Shortest Path
**Time:** O(V³) | **Space:** O(V²)  
**⚠️ Nuance:**
- Handles negative edges but **not negative cycles**.
- `dist[i][i] < 0` after running → negative cycle exists.
- Initialize: `dist[i][i] = 0`, no edge = INF, existing edge = weight.

**Steps:**
1. Build distance matrix.
2. For each intermediate node `k`, try `i → k → j`.

```java
static int[][] floydWarshall(int[][] mat) {
    int V = mat.length;
    int INF = (int) 1e9;
    int[][] dist = new int[V][V];
    for (int i = 0; i < V; i++)
        for (int j = 0; j < V; j++)
            dist[i][j] = (i == j) ? 0 : (mat[i][j] == 0 && i != j ? INF : mat[i][j]);

    for (int k = 0; k < V; k++)
        for (int i = 0; i < V; i++)
            for (int j = 0; j < V; j++)
                if (dist[i][k] < INF && dist[k][j] < INF)
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);

    return dist;
}
```

---

## 🌳 Group 6: MST + DSU

### 15. Disjoint Set Union (DSU / Union-Find)
**Use:** Kruskal MST, detecting cycles in undirected graphs, dynamic connectivity.  
**Time:** O(α(V)) ≈ O(1) amortized

**⚠️ Nuance:**
- Always use **path compression** in `find()`.
- Use **union by size** (or rank) to keep tree flat.
- `find(a) == find(b)` means same component → adding edge creates cycle.

```java
static class DSU {
    int[] parent, size, rank;
    DSU(int n) {
        parent = new int[n]; size = new int[n]; rank = new int[n];
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; rank[i] = 0; }
    }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);  // path compression
        return parent[x];
    }

    // Variant 1: Union by size
    boolean unionBySize(int a, int b) {
        int pa = find(a), pb = find(b);
        if (pa == pb) return false;
        if (size[pa] < size[pb]) { parent[pa] = pb; size[pb] += size[pa]; }
        else                     { parent[pb] = pa; size[pa] += size[pb]; }
        return true;
    }

    // Variant 2: Union by rank
    boolean unionByRank(int a, int b) {
        int pa = find(a), pb = find(b);
        if (pa == pb) return false;

        if (rank[pa] < rank[pb]) parent[pa] = pb;
        else if (rank[pb] < rank[pa]) parent[pb] = pa;
        else {
            parent[pb] = pa;
            rank[pa]++;
        }
        return true;
    }
}
```

---

### 16. Kruskal's MST
**Use:** Minimum spanning tree; better for sparse graphs (small E).  
**Time:** O(E log E) | **Space:** O(V)

**⚠️ Nuance:** Uses **edge list**, not adjacency list. Sort edges, then greedily pick safe ones with DSU.

**Steps:**
1. Sort all edges by weight.
2. For each edge (u, v, w): if `find(u) != find(v)`, include edge, union them.
3. Stop after V-1 edges selected.

```java
static int kruskalMST(int V, List<WEdge> edges) {
    edges.sort(Comparator.comparingInt(e -> e.w));
    DSU dsu = new DSU(V);
    int cost = 0, used = 0;
    for (WEdge e : edges) {
        if (dsu.unionBySize(e.u, e.v)) {
            cost += e.w;
            if (++used == V - 1) break;
        }
    }
    return cost;
}
```

---

### 17. Prim's MST
**Use:** MST; better for dense graphs (large E).  
**Time:** O(E log V) | **Space:** O(V)

**⚠️ Nuance:** Uses **adjacency list + min-heap**. Skip if node already in MST. Very similar to Dijkstra — difference is we pick by **edge weight**, not cumulative dist.

**Steps:**
1. Start from node 0, push (node, cost) into min-heap.
2. Pop min edge. If node in MST, skip.
3. Add cost, mark in MST, push all neighbors.

```java
static int primMST(List<List<Edge>> adj) {
    int V = adj.size();
    boolean[] inMST = new boolean[V];
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{0, 0});  // {node, cost}
    int totalCost = 0;
    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int u = cur[0], w = cur[1];
        if (inMST[u]) continue;
        inMST[u] = true;
        totalCost += w;
        for (Edge e : adj.get(u))
            if (!inMST[e.to]) pq.offer(new int[]{e.to, e.wt});
    }
    return totalCost;
}
```

---

## 🔗 Group 7: Strongly Connected Components (SCC)

**SCC:** A maximal group of nodes where every node can reach every other node.  
**Only for directed graphs.**

---

### 18. Kosaraju's SCC
**Time:** O(V + E) | Two DFS passes

**⚠️ Nuance:** Two separate DFS passes. First on original graph (push by finish time), second on **transposed (reversed) graph** in reverse finish order.

**Steps:**
1. DFS original graph → push nodes to stack in finish order.
2. Transpose the graph (reverse all edges).
3. Pop stack, DFS on transposed graph → each DFS = one SCC.

```java
static void dfs1(int u, List<List<Integer>> adj, boolean[] vis, Stack<Integer> st) {
    vis[u] = true;
    for (int v : adj.get(u)) if (!vis[v]) dfs1(v, adj, vis, st);
    st.push(u);  // push after all neighbors
}

static void dfs2(int u, List<List<Integer>> rev, boolean[] vis, List<Integer> comp) {
    vis[u] = true; comp.add(u);
    for (int v : rev.get(u)) if (!vis[v]) dfs2(v, rev, vis, comp);
}

static List<List<Integer>> kosaraju(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V];
    Stack<Integer> st = new Stack<>();
    for (int i = 0; i < V; i++) if (!vis[i]) dfs1(i, adj, vis, st);

    // Build transpose
    List<List<Integer>> rev = new ArrayList<>();
    for (int i = 0; i < V; i++) rev.add(new ArrayList<>());
    for (int u = 0; u < V; u++) for (int v : adj.get(u)) rev.get(v).add(u);

    Arrays.fill(vis, false);
    List<List<Integer>> sccs = new ArrayList<>();
    while (!st.isEmpty()) {
        int u = st.pop();
        if (!vis[u]) {
            List<Integer> comp = new ArrayList<>();
            dfs2(u, rev, vis, comp);
            sccs.add(comp);
        }
    }
    return sccs;
}
```

---

### 19. Tarjan's SCC
**Time:** O(V + E) | Single DFS pass

**⚠️ Nuance:** One pass using `disc[]` and `low[]`. A node is an SCC root when `disc[u] == low[u]`. Use `inStack[]` to only update `low` from nodes currently on the stack.

**Steps:**
1. Assign `disc[u]` and `low[u]` on entry. Push to stack, mark `inStack`.
2. DFS children: update `low[u] = min(low[u], low[child])`.
3. For back-edges: `low[u] = min(low[u], disc[v])` only if `inStack[v]`.
4. If `disc[u] == low[u]`, pop one SCC from stack.

```java
static int timer = 0;

static void tarjanDFS(int u, List<List<Integer>> adj, int[] disc, int[] low,
                      boolean[] inStack, Stack<Integer> st, List<List<Integer>> sccs) {
    disc[u] = low[u] = ++timer;
    st.push(u); inStack[u] = true;

    for (int v : adj.get(u)) {
        if (disc[v] == 0) {
            tarjanDFS(v, adj, disc, low, inStack, st, sccs);
            low[u] = Math.min(low[u], low[v]);
        } else if (inStack[v]) {
            low[u] = Math.min(low[u], disc[v]);
        }
    }

    if (disc[u] == low[u]) {  // SCC root found
        List<Integer> comp = new ArrayList<>();
        while (true) {
            int x = st.pop(); inStack[x] = false; comp.add(x);
            if (x == u) break;
        }
        sccs.add(comp);
    }
}

static List<List<Integer>> tarjanSCC(List<List<Integer>> adj) {
    int V = adj.size();
    int[] disc = new int[V], low = new int[V];
    boolean[] inStack = new boolean[V];
    Stack<Integer> st = new Stack<>();
    List<List<Integer>> sccs = new ArrayList<>();
    timer = 0;
    for (int i = 0; i < V; i++) if (disc[i] == 0) tarjanDFS(i, adj, disc, low, inStack, st, sccs);
    return sccs;
}
```

---

## 🌉 Group 8: Bridges & Articulation Points (Tarjan's Low-Link)

**Bridge:** An edge whose removal disconnects the graph.  
**Articulation Point (AP):** A node whose removal disconnects the graph.

**⚠️ Nuance:**
- Bridge condition: `low[child] > disc[parent]`
- AP condition (non-root): `low[child] >= disc[parent]`  
- AP condition (root): root is AP if it has **more than 1 DFS child**.
- Note the subtle difference: bridge uses `>`, AP uses `>=`.

**Steps:**
1. DFS with `disc[]` and `low[]`. Track parent and child count.
2. On return from child: update `low[u]`.
3. Check bridge/AP conditions.

```java
static int timer2 = 0;

static void bridgeAPDFS(int u, int parent, List<List<Integer>> adj, boolean[] vis,
                        int[] disc, int[] low, boolean[] isAP, List<int[]> bridges) {
    vis[u] = true;
    disc[u] = low[u] = ++timer2;
    int children = 0;

    for (int v : adj.get(u)) {
        if (v == parent) continue;
        if (!vis[v]) {
            children++;
            bridgeAPDFS(v, u, adj, vis, disc, low, isAP, bridges);
            low[u] = Math.min(low[u], low[v]);
            if (low[v] > disc[u])  bridges.add(new int[]{u, v});           // Bridge
            if (parent != -1 && low[v] >= disc[u]) isAP[u] = true;         // AP (non-root)
        } else {
            low[u] = Math.min(low[u], disc[v]);
        }
    }
    if (parent == -1 && children > 1) isAP[u] = true;  // AP (root)
}

static void findBridgesAndAPs(List<List<Integer>> adj) {
    int V = adj.size();
    boolean[] vis = new boolean[V], isAP = new boolean[V];
    int[] disc = new int[V], low = new int[V];
    List<int[]> bridges = new ArrayList<>();
    timer2 = 0;
    for (int i = 0; i < V; i++)
        if (!vis[i]) bridgeAPDFS(i, -1, adj, vis, disc, low, isAP, bridges);
    // use isAP[] and bridges list
}
```

---

## 🌊 Group 9: Maximum Flow

### 20. Edmonds-Karp (Ford-Fulkerson + BFS)
**Use:** Max flow in a network, bipartite matching, min-cut problems.  
**Time:** O(V × E²)

**⚠️ Nuance:**
- Uses **residual graph**: forward capacity decreases, backward (reverse) capacity increases.
- BFS finds the **shortest augmenting path** each time.
- Termination: when BFS fails to find any path from source to sink.

**Steps:**
1. Build residual graph (copy of capacity matrix).
2. BFS: find path s → t in residual.
3. Find bottleneck (min capacity on path).
4. Update residual: forward `-= flow`, backward `+= flow`.
5. Repeat until no path found.

```java
static boolean bfsFlow(int[][] residual, int s, int t, int[] parent) {
    int V = residual.length;
    Arrays.fill(parent, -1);
    boolean[] vis = new boolean[V];
    Queue<Integer> q = new LinkedList<>();
    q.offer(s); vis[s] = true; parent[s] = s;
    while (!q.isEmpty()) {
        int u = q.poll();
        for (int v = 0; v < V; v++) {
            if (!vis[v] && residual[u][v] > 0) {
                vis[v] = true; parent[v] = u;
                if (v == t) return true;
                q.offer(v);
            }
        }
    }
    return false;
}

static int edmondsKarp(int[][] graph, int s, int t) {
    int V = graph.length;
    int[][] residual = new int[V][V];
    for (int i = 0; i < V; i++) residual[i] = Arrays.copyOf(graph[i], V);
    int[] parent = new int[V];
    int maxFlow = 0;

    while (bfsFlow(residual, s, t, parent)) {
        int flow = Integer.MAX_VALUE;
        for (int v = t; v != s; v = parent[v])
            flow = Math.min(flow, residual[parent[v]][v]);
        for (int v = t; v != s; v = parent[v]) {
            residual[parent[v]][v] -= flow;
            residual[v][parent[v]] += flow;
        }
        maxFlow += flow;
    }
    return maxFlow;
}
```

---

## 🧠 Master Cheat Sheet — Algorithm Selection Guide

| Problem | Graph Type | Algorithm | Time | Key Data Structure |
|---|---|---|---|---|
| Shortest path (unweighted) | Any | **BFS** | O(V+E) | Queue |
| Shortest path (+ve weights) | Weighted | **Dijkstra** | O(E log V) | Min-Heap |
| Shortest path (-ve weights) | Weighted | **Bellman-Ford** | O(V·E) | Edge List |
| All-pairs shortest path | Any | **Floyd-Warshall** | O(V³) | Matrix |
| Shortest path on DAG | Weighted DAG | **Topo + Relax** | O(V+E) | Stack |
| Task ordering / dependencies | DAG | **Topo Sort (Kahn)** | O(V+E) | Queue + Indegree |
| Detect cycle (undirected) | Undirected | **DFS parent / BFS / DSU** | O(V+E) | — |
| Detect cycle (directed) | Directed | **DFS path array / Kahn** | O(V+E) | — |
| MST (sparse graph) | Undirected Weighted | **Kruskal** | O(E log E) | Edge List + DSU |
| MST (dense graph) | Undirected Weighted | **Prim** | O(E log V) | Adj List + Min-Heap |
| Dynamic connectivity | Undirected | **DSU** | O(α(V)) | Parent Array |
| Strongly connected components | Directed | **Kosaraju / Tarjan** | O(V+E) | Stack |
| Critical edges (bridges) | Undirected | **Tarjan low-link** | O(V+E) | disc[] + low[] |
| Critical nodes (AP) | Undirected | **Tarjan low-link** | O(V+E) | disc[] + low[] |
| Maximum flow / min-cut | Directed + capacity | **Edmonds-Karp** | O(V·E²) | Residual Matrix |
| Bipartite matching | Bipartite | **BFS + Edmonds-Karp** | O(V·E) | — |
| Count connected components | Undirected | **BFS/DFS outer loop** | O(V+E) | vis[] |
| Check bipartite | Any | **BFS 2-coloring** | O(V+E) | color[] |

---

## ⚡ Key Nuances to Remember in Interviews

**Traversal**
- Always handle **disconnected graphs** with an outer for-loop over all nodes.
- Iterative DFS: mark visited on **pop**, not on push.
- BFS: mark visited on **enqueue**, not on dequeue.

**Cycle Detection**
- Undirected: parent-check (DFS/BFS), or DSU (`find(u)==find(v)` before union).
- Directed: need `pathVis[]` array; don't forget to reset on backtrack.
- Kahn's topo sort: if `result.size() < V`, there's a cycle.

**Shortest Paths**
- Dijkstra breaks with negative edges → use Bellman-Ford.
- Dijkstra's "stale entry" skip: `if (d > dist[u]) continue;`
- Floyd-Warshall: `dist[i][i] < 0` after running = negative cycle.

**MST**
- Kruskal uses edge list; Prim uses adjacency list.
- Both produce the same MST cost, but different structure.
- Prim is almost identical to Dijkstra — the only difference is picking by **edge weight** vs **total distance**.

**Bridges vs Articulation Points**
- Bridge: `low[v] > disc[u]` (strict greater)
- AP: `low[v] >= disc[u]` (greater or equal) for non-root; root needs `children > 1`

**SCC**
- Kosaraju: 2 DFS + graph transpose = easier to code, remember in interview.
- Tarjan: 1 DFS, `disc[u] == low[u]` = SCC root = more efficient, harder to code.

**DSU**
- Always use both **path compression** + **union by size/rank** together.
- `dsu.union()` returns `false` if already in same set → edge creates a cycle.

---

*Guide synthesized from your notes — covers all standard interview graph topics. Revise the Master Cheat Sheet last.*
