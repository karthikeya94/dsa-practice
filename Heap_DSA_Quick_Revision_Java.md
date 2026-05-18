# Heap DSA — Ultimate Quick Revision Guide (Java)

> **How to use:** Each topic follows: *When to use → Nuance/Gotcha → Steps → Code.*  
> Start with the **Master Cheat Sheet** at the bottom before an interview, then drill weak spots.

---

## 📦 Group 1: Heap Fundamentals & Java PriorityQueue

**What is a Heap?**  
A **complete binary tree** stored as an array where every parent satisfies the heap property.

| Type | Property | Java Class |
|---|---|---|
| Min-Heap | Parent ≤ Children | `PriorityQueue<>()` (default) |
| Max-Heap | Parent ≥ Children | `PriorityQueue<>(Comparator.reverseOrder())` |

**Array Index Relationships (0-indexed):**
```
Parent of i     → (i - 1) / 2
Left child of i → 2 * i + 1
Right child of i→ 2 * i + 2
```

**⚠️ Core Nuances:**
- Java's `PriorityQueue` is a **min-heap by default** — for max-heap, always pass `Comparator.reverseOrder()` or `(a, b) -> b - a`.
- `peek()` → O(1). `poll()` → O(log N). `add()` → O(log N). `contains()` → O(N) — never use for fast lookup.
- Heap gives you **min/max in O(1)** but does NOT give sorted order — for sorted output, poll N times = O(N log N) = heap sort.
- Building a heap from N elements using `addAll` is O(N log N), but **heapify** (build in-place) is O(N). Java's `new PriorityQueue<>(list)` uses heapify internally → O(N).

```java
import java.util.*;

// Min-Heap (default)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-Heap
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

// Custom comparator (e.g., by second element of int[])
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);

// Build heap from existing collection — O(N) heapify
PriorityQueue<Integer> heapFromList = new PriorityQueue<>(Arrays.asList(5, 3, 8, 1));

// Common operations
minHeap.add(10);         // insert — O(log N)
minHeap.offer(5);        // same as add, preferred (returns false vs throws)
int top = minHeap.peek();  // view min — O(1), does NOT remove
int min = minHeap.poll();  // remove min — O(log N)
int size = minHeap.size();
boolean empty = minHeap.isEmpty();
```

---

## 🔧 Group 2: Heap Implementation from Scratch

**⚠️ Nuance:** Knowing how to implement a heap shows depth. Two core operations:
- **Sift-up (bubble-up):** After insert — compare with parent, swap if violating heap property. Used in `insert`.
- **Sift-down (bubble-down):** After remove — replace root with last element, sift down. Used in `extractMin/Max`.

---

### 1. Min-Heap from Scratch

```java
static class MinHeap {
    int[] heap;
    int size;

    MinHeap(int capacity) {
        heap = new int[capacity];
        size = 0;
    }

    int parent(int i)    { return (i - 1) / 2; }
    int leftChild(int i) { return 2 * i + 1; }
    int rightChild(int i){ return 2 * i + 2; }

    void swap(int i, int j) {
        int tmp = heap[i]; heap[i] = heap[j]; heap[j] = tmp;
    }

    // Insert — O(log N)
    void insert(int val) {
        heap[size] = val;
        siftUp(size);
        size++;
    }

    void siftUp(int i) {
        while (i > 0 && heap[parent(i)] > heap[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    // Extract min — O(log N)
    int extractMin() {
        if (size == 0) throw new NoSuchElementException();
        int min = heap[0];
        heap[0] = heap[--size];   // move last to root
        siftDown(0);
        return min;
    }

    void siftDown(int i) {
        int smallest = i;
        int l = leftChild(i), r = rightChild(i);
        if (l < size && heap[l] < heap[smallest]) smallest = l;
        if (r < size && heap[r] < heap[smallest]) smallest = r;
        if (smallest != i) {
            swap(i, smallest);
            siftDown(smallest);
        }
    }

    int peek() { return heap[0]; }

    // Build heap from array — O(N) heapify
    static MinHeap buildHeap(int[] arr) {
        MinHeap h = new MinHeap(arr.length);
        h.heap = arr.clone();
        h.size = arr.length;
        // Start from last non-leaf node and sift down
        for (int i = h.size / 2 - 1; i >= 0; i--) h.siftDown(i);
        return h;
    }
}
```

---

### 2. Heap Sort — O(N log N), O(1) Space

**⚠️ Nuance:** Build a **max-heap** in-place, then swap root (max) to end and reduce heap size. This gives ascending order. No extra space needed.

**Steps:**
1. Build max-heap in-place from array (heapify from N/2-1 to 0).
2. Swap root (max) with last element.
3. Reduce heap size by 1, sift-down root.
4. Repeat until size = 1.

```java
static void heapSort(int[] arr) {
    int n = arr.length;

    // Build max-heap — O(N)
    for (int i = n / 2 - 1; i >= 0; i--) siftDownMax(arr, n, i);

    // Extract max one by one
    for (int i = n - 1; i > 0; i--) {
        int tmp = arr[0]; arr[0] = arr[i]; arr[i] = tmp; // swap max to end
        siftDownMax(arr, i, 0);                           // restore heap on reduced size
    }
}

static void siftDownMax(int[] arr, int n, int i) {
    int largest = i, l = 2 * i + 1, r = 2 * i + 2;
    if (l < n && arr[l] > arr[largest]) largest = l;
    if (r < n && arr[r] > arr[largest]) largest = r;
    if (largest != i) {
        int tmp = arr[i]; arr[i] = arr[largest]; arr[largest] = tmp;
        siftDownMax(arr, n, largest);
    }
}
```

---

## 📊 Group 3: Kth Element Problems

**Memory trick:** "Kth largest → Min-Heap of size K. Kth smallest → Max-Heap of size K."  
The heap maintains only K elements — its top is always the answer.

---

### 3. Kth Largest Element in Array

**⚠️ Nuance:** Use a **min-heap of size K**. If new element > heap top, replace it. At the end, heap top = Kth largest. Do NOT use max-heap and pop K times if you only need one answer — that's O(N log N) vs O(N log K).

**Steps:**
1. Add first K elements to min-heap.
2. For remaining elements: if `num > heap.peek()`, poll and add.
3. `heap.peek()` = Kth largest.

```java
static int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // min-heap of size k
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) minHeap.poll(); // remove smallest
    }
    return minHeap.peek(); // kth largest
}
```

---

### 4. Kth Smallest Element in Array

**⚠️ Nuance:** Max-heap of size K. If `num < heap.peek()`, replace. Top = Kth smallest.

```java
static int findKthSmallest(int[] nums, int k) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    for (int num : nums) {
        maxHeap.offer(num);
        if (maxHeap.size() > k) maxHeap.poll(); // remove largest
    }
    return maxHeap.peek(); // kth smallest
}
```

---

### 5. Kth Largest in a Stream (Online / Dynamic)

**⚠️ Nuance:** Classic design problem — maintain a min-heap of exactly size K at all times. The top is always the Kth largest seen so far.

```java
static class KthLargest {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    int k;

    KthLargest(int k, int[] nums) {
        this.k = k;
        for (int n : nums) add(n);
    }

    int add(int val) {
        minHeap.offer(val);
        if (minHeap.size() > k) minHeap.poll();
        return minHeap.peek();
    }
}
```

---

### 6. K Closest Points to Origin

**⚠️ Nuance:** Use a **max-heap of size K** ordered by distance. If a new point is closer than the farthest so far, replace it. No need to sort all N points.

```java
static int[][] kClosest(int[][] points, int k) {
    // Max-heap by distance
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])
    );
    for (int[] p : points) {
        maxHeap.offer(p);
        if (maxHeap.size() > k) maxHeap.poll(); // remove farthest
    }
    return maxHeap.toArray(new int[k][]);
}
```

---

### 7. Top K Frequent Elements

**⚠️ Nuance:** Count frequencies with HashMap, then use min-heap of size K ordered by frequency. For large N with small K, this beats full sort: O(N log K) vs O(N log N).

```java
static int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);

    // Min-heap by frequency (so we can evict least frequent)
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(Comparator.comparingInt(freq::get));
    for (int num : freq.keySet()) {
        minHeap.offer(num);
        if (minHeap.size() > k) minHeap.poll();
    }

    int[] res = new int[k];
    for (int i = k - 1; i >= 0; i--) res[i] = minHeap.poll();
    return res;
}
```

---

### 8. Sort Characters by Frequency
```java
static String frequencySort(String s) {
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);

    PriorityQueue<Character> maxHeap = new PriorityQueue<>(
        (a, b) -> freq.get(b) - freq.get(a)
    );
    maxHeap.addAll(freq.keySet());

    StringBuilder sb = new StringBuilder();
    while (!maxHeap.isEmpty()) {
        char c = maxHeap.poll();
        sb.append(String.valueOf(c).repeat(freq.get(c)));
    }
    return sb.toString();
}
```

---

## 🔀 Group 4: Merge Problems

---

### 9. Merge K Sorted Lists

**⚠️ Nuance:** Classic K-way merge using a min-heap. Push the head of each list. When you pop the min, push its next node. This processes every node exactly once: **O(N log K)** where N = total nodes.

**Steps:**
1. Add head of each non-null list to min-heap.
2. Poll minimum node, add to result.
3. If polled node has a next, push next into heap.
4. Repeat until heap empty.

```java
static class ListNode {
    int val; ListNode next;
    ListNode(int val) { this.val = val; }
}

static ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(n -> n.val));
    for (ListNode head : lists) if (head != null) minHeap.offer(head);

    ListNode dummy = new ListNode(0), curr = dummy;
    while (!minHeap.isEmpty()) {
        ListNode node = minHeap.poll();
        curr.next = node;
        curr = curr.next;
        if (node.next != null) minHeap.offer(node.next); // push next of consumed node
    }
    return dummy.next;
}
```

---

### 10. Merge K Sorted Arrays

```java
static int[] mergeKSortedArrays(int[][] arrays) {
    // {value, arrayIndex, elementIndex}
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    int totalSize = 0;
    for (int i = 0; i < arrays.length; i++) {
        totalSize += arrays[i].length;
        if (arrays[i].length > 0) minHeap.offer(new int[]{arrays[i][0], i, 0});
    }

    int[] res = new int[totalSize];
    int idx = 0;
    while (!minHeap.isEmpty()) {
        int[] cur = minHeap.poll();
        res[idx++] = cur[0];
        int arrIdx = cur[1], elemIdx = cur[2];
        if (elemIdx + 1 < arrays[arrIdx].length)
            minHeap.offer(new int[]{arrays[arrIdx][elemIdx + 1], arrIdx, elemIdx + 1});
    }
    return res;
}
```

---

### 11. Smallest Range Covering Elements from K Lists

**⚠️ Nuance:** Use a min-heap to track current minimum across all lists. Track global max separately (updated on each push). Range = [heapMin, currentMax]. Advance the list that contributed the minimum.

```java
static int[] smallestRange(List<List<Integer>> nums) {
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    int curMax = Integer.MIN_VALUE;

    for (int i = 0; i < nums.size(); i++) {
        minHeap.offer(new int[]{nums.get(i).get(0), i, 0});
        curMax = Math.max(curMax, nums.get(i).get(0));
    }

    int[] res = {0, Integer.MAX_VALUE};
    while (minHeap.size() == nums.size()) {
        int[] cur = minHeap.poll();
        int curMin = cur[0], listIdx = cur[1], elemIdx = cur[2];
        if (curMax - curMin < res[1] - res[0]) res = new int[]{curMin, curMax};
        if (elemIdx + 1 < nums.get(listIdx).size()) {
            int next = nums.get(listIdx).get(elemIdx + 1);
            minHeap.offer(new int[]{next, listIdx, elemIdx + 1});
            curMax = Math.max(curMax, next);
        }
        // If any list is exhausted, we can't cover all → stop
    }
    return res;
}
```

---

## 📅 Group 5: Scheduling & Interval Problems

---

### 12. Task Scheduler (CPU Scheduling with Cooldown)

**⚠️ Nuance:** Greedy — always pick the most frequent task that's available. Use a **max-heap** by frequency + a **queue** to enforce cooldown. Tasks in cooldown queue re-enter the heap only when their cooldown expires.

**Steps:**
1. Count task frequencies. Add all to max-heap.
2. At each time unit: pop most frequent, decrement, push to cooldown queue with re-entry time.
3. If heap empty and cooldown not met → idle (increment time).
4. When cooldown expires, re-add task to heap.

```java
static int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;

    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    for (int f : freq) if (f > 0) maxHeap.offer(f);

    Queue<int[]> cooldown = new LinkedList<>(); // {remaining_freq, available_at_time}
    int time = 0;

    while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
        time++;
        if (!maxHeap.isEmpty()) {
            int f = maxHeap.poll() - 1;
            if (f > 0) cooldown.offer(new int[]{f, time + n});
        }
        if (!cooldown.isEmpty() && cooldown.peek()[1] == time)
            maxHeap.offer(cooldown.poll()[0]);
    }
    return time;
}
```

---

### 13. Meeting Rooms II (Minimum Number of Rooms)

**⚠️ Nuance:** Sort by start time. Use a min-heap of **end times**. If new meeting starts after the earliest ending meeting, reuse that room (poll and replace). Otherwise open new room.

**Steps:**
1. Sort intervals by start time.
2. Min-heap stores end times of active meetings.
3. For each meeting: if `start >= heap.peek()`, reuse room (poll old end time).
4. Push current meeting's end time.
5. Heap size = rooms needed.

```java
static int minMeetingRooms(int[][] intervals) {
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
    PriorityQueue<Integer> endTimes = new PriorityQueue<>(); // min-heap of end times

    for (int[] interval : intervals) {
        if (!endTimes.isEmpty() && endTimes.peek() <= interval[0])
            endTimes.poll(); // reuse room that ended earliest
        endTimes.offer(interval[1]);
    }
    return endTimes.size(); // rooms in use
}
```

---

### 14. Car Pooling (Capacity Check)
**⚠️ Nuance:** Event-based approach. Use a min-heap sorted by drop-off time. Drop passengers off before picking up new ones at the same location.

```java
static boolean carPooling(int[][] trips, int capacity) {
    Arrays.sort(trips, Comparator.comparingInt(a -> a[1]));
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[2])); // by drop-off
    int currPassengers = 0;

    for (int[] trip : trips) {
        int passengers = trip[0], pickup = trip[1], dropoff = trip[2];
        // Drop off anyone whose stop <= current pickup
        while (!minHeap.isEmpty() && minHeap.peek()[2] <= pickup) {
            currPassengers -= minHeap.poll()[0];
        }
        currPassengers += passengers;
        if (currPassengers > capacity) return false;
        minHeap.offer(trip);
    }
    return true;
}
```

---

### 15. Find Median from Data Stream (Two Heaps)

**⚠️ Nuance:** This is the most important heap pattern for interviews. Maintain:
- **Max-heap (left half):** smaller half of numbers.
- **Min-heap (right half):** larger half of numbers.
- Balance rule: `maxHeap.size() == minHeap.size()` or `maxHeap.size() == minHeap.size() + 1`.
- Median: if sizes equal → average of both tops. Otherwise → maxHeap top.

**Steps:**
1. Add to maxHeap first (always).
2. Move maxHeap top to minHeap (balance order).
3. If minHeap is larger, move its top back to maxHeap.

```java
static class MedianFinder {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder()); // lower half
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();                           // upper half

    void addNum(int num) {
        maxHeap.offer(num);                         // always add to maxHeap first
        minHeap.offer(maxHeap.poll());              // push maxHeap's max to minHeap (keep sorted split)
        if (minHeap.size() > maxHeap.size())        // maintain size balance: maxHeap >= minHeap
            maxHeap.offer(minHeap.poll());
    }

    double findMedian() {
        if (maxHeap.size() > minHeap.size()) return maxHeap.peek();
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}
```

---

### 16. Sliding Window Median

**⚠️ Nuance:** Same two-heap idea as MedianFinder, but now you must **remove elements leaving the window**. Java `PriorityQueue.remove(val)` is O(N) — that's acceptable here, but mention it. Advanced: use a lazy-deletion map to mark removed elements.

```java
static double[] medianSlidingWindow(int[] nums, int k) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder()); // lower half
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();                           // upper half
    double[] res = new double[nums.length - k + 1];

    for (int i = 0; i < nums.length; i++) {
        // Add new element
        maxHeap.offer(nums[i]);
        minHeap.offer(maxHeap.poll());
        if (minHeap.size() > maxHeap.size()) maxHeap.offer(minHeap.poll());

        // Remove outgoing element when window is full
        if (i >= k) {
            int outgoing = nums[i - k];
            if (outgoing <= maxHeap.peek()) maxHeap.remove(outgoing);
            else minHeap.remove(outgoing);
            // Rebalance
            if (minHeap.size() > maxHeap.size()) maxHeap.offer(minHeap.poll());
            else if (maxHeap.size() > minHeap.size() + 1) minHeap.offer(maxHeap.poll());
        }

        if (i >= k - 1) {
            res[i - k + 1] = maxHeap.size() > minHeap.size()
                ? maxHeap.peek()
                : (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }
    return res;
}
```

---

## ⬛ Group 6: Greedy + Heap Patterns

---

### 17. Reorganize String (No Two Adjacent Same Characters)

**⚠️ Nuance:** Greedy — always pick the most frequent character that is NOT the previous one. Use a max-heap by frequency. If the most frequent is same as previous, pick second-most frequent. If heap has only one element left and it equals previous → impossible.

```java
static String reorganizeString(String s) {
    int[] freq = new int[26];
    for (char c : s.toCharArray()) freq[c - 'a']++;
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[1] - a[1]); // {char, freq}
    for (int i = 0; i < 26; i++) if (freq[i] > 0) maxHeap.offer(new int[]{i, freq[i]});

    StringBuilder sb = new StringBuilder();
    int[] prev = null; // last placed character

    while (!maxHeap.isEmpty()) {
        int[] cur = maxHeap.poll();
        sb.append((char)('a' + cur[0]));
        if (prev != null && prev[1] > 0) maxHeap.offer(prev); // re-add previous
        cur[1]--;
        prev = cur;
    }
    return sb.length() == s.length() ? sb.toString() : "";
}
```

---

### 18. Maximum Sum Combinations (Sum of Two Elements from Two Arrays)

**⚠️ Nuance:** Sort both arrays. Start with (max of A + max of B). Use a max-heap with a visited set to avoid duplicates. At each step, expand by decrementing one index at a time.

```java
static List<Integer> maxSumCombinations(int[] A, int[] B, int k) {
    Arrays.sort(A); Arrays.sort(B);
    int n = A.length;
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);
    Set<String> visited = new HashSet<>();

    maxHeap.offer(new int[]{A[n-1] + B[n-1], n-1, n-1});
    visited.add((n-1) + "," + (n-1));
    List<Integer> res = new ArrayList<>();

    while (res.size() < k) {
        int[] cur = maxHeap.poll();
        res.add(cur[0]);
        int i = cur[1], j = cur[2];
        if (i - 1 >= 0 && visited.add((i-1) + "," + j))
            maxHeap.offer(new int[]{A[i-1] + B[j], i-1, j});
        if (j - 1 >= 0 && visited.add(i + "," + (j-1)))
            maxHeap.offer(new int[]{A[i] + B[j-1], i, j-1});
    }
    return res;
}
```

---

### 19. IPO / Maximize Capital (Choose At Most K Projects)

**⚠️ Nuance:** Two-phase greedy: among all affordable projects (capital ≤ current wealth), always pick the one with max profit. Use a min-heap to sort by capital (to find affordable ones) and a max-heap to pick most profitable among affordable.

```java
static int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
    int n = profits.length;
    PriorityQueue<int[]> locked = new PriorityQueue<>(Comparator.comparingInt(a -> a[0])); // by capital
    PriorityQueue<int[]> available = new PriorityQueue<>((a, b) -> b[1] - a[1]);           // by profit desc

    for (int i = 0; i < n; i++) locked.offer(new int[]{capital[i], profits[i]});

    for (int i = 0; i < k; i++) {
        // Unlock all projects we can now afford
        while (!locked.isEmpty() && locked.peek()[0] <= w)
            available.offer(locked.poll());
        if (available.isEmpty()) break;       // no affordable project
        w += available.poll()[1];             // pick most profitable
    }
    return w;
}
```

---

### 20. Minimum Cost to Connect Ropes / Sticks

**⚠️ Nuance:** Optimal strategy is to always combine the two **smallest** ropes first (Huffman-style). Min-heap gives the two smallest in O(log N) each step. Total: O(N log N).

```java
static int connectRopes(int[] ropes) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int r : ropes) minHeap.offer(r);
    int totalCost = 0;

    while (minHeap.size() > 1) {
        int combined = minHeap.poll() + minHeap.poll(); // two smallest
        totalCost += combined;
        minHeap.offer(combined);
    }
    return totalCost;
}
```

---

### 21. Last Stone Weight
```java
static int lastStoneWeight(int[] stones) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    for (int s : stones) maxHeap.offer(s);

    while (maxHeap.size() > 1) {
        int y = maxHeap.poll(), x = maxHeap.poll();
        if (y != x) maxHeap.offer(y - x);
    }
    return maxHeap.isEmpty() ? 0 : maxHeap.peek();
}
```

---

## 🛤️ Group 7: Heap in Graph Algorithms

---

### 22. Dijkstra's Shortest Path (Min-Heap)

**⚠️ Nuance:** Min-heap on `{distance, node}`. Always skip stale entries with `if (d > dist[u]) continue`. This "lazy deletion" avoids the need for a decrease-key operation.

```java
static class Edge { int to, wt; Edge(int t, int w) { to=t; wt=w; } }

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
        if (d > dist[u]) continue; // stale entry — skip
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

### 23. Prim's MST (Min-Heap)

**⚠️ Nuance:** Very similar to Dijkstra but pick by **edge weight**, not total distance. Use `inMST[]` instead of distance check.

```java
static int primMST(List<List<Edge>> adj) {
    int V = adj.size();
    boolean[] inMST = new boolean[V];
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{0, 0}); // {node, weight}
    int cost = 0;

    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int u = cur[0], w = cur[1];
        if (inMST[u]) continue;
        inMST[u] = true;
        cost += w;
        for (Edge e : adj.get(u))
            if (!inMST[e.to]) pq.offer(new int[]{e.to, e.wt});
    }
    return cost;
}
```

---

### 24. K-th Shortest Path (Modified Dijkstra)

**⚠️ Nuance:** Allow visiting nodes multiple times (up to K times). Count how many times a node is popped — the K-th pop = K-th shortest path to that node.

```java
static int kthShortestPath(List<List<Edge>> adj, int src, int dst, int k) {
    int V = adj.size();
    int[] count = new int[V];  // how many times each node has been popped
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{src, 0});

    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int u = cur[0], d = cur[1];
        count[u]++;
        if (u == dst && count[u] == k) return d;  // k-th time reaching dst
        if (count[u] > k) continue;               // no need to explore further
        for (Edge e : adj.get(u))
            pq.offer(new int[]{e.to, d + e.wt});
    }
    return -1;
}
```

---

## 🏗️ Group 8: Advanced Heap Patterns

---

### 25. Trapping Rain Water (Using Heap / Two Pointer)

**⚠️ Nuance:** The heap approach generalises to 3D (Trapping Rain Water II). For 1D, two-pointer is simpler but knowing the heap variant is a bonus. Push border cells into min-heap. Always process the minimum height cell — water level = max height seen so far from borders.

```java
// 3D - Trapping Rain Water II
static int trapRainWaterII(int[][] heightMap) {
    int m = heightMap.length, n = heightMap[0].length;
    boolean[][] vis = new boolean[m][n];
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

    // Push all border cells
    for (int i = 0; i < m; i++) for (int j = 0; j < n; j++)
        if (i == 0 || i == m-1 || j == 0 || j == n-1) {
            minHeap.offer(new int[]{i, j, heightMap[i][j]});
            vis[i][j] = true;
        }

    int water = 0, maxH = 0;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!minHeap.isEmpty()) {
        int[] cur = minHeap.poll();
        int r = cur[0], c = cur[1], h = cur[2];
        maxH = Math.max(maxH, h);
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || vis[nr][nc]) continue;
            vis[nr][nc] = true;
            water += Math.max(0, maxH - heightMap[nr][nc]);
            minHeap.offer(new int[]{nr, nc, heightMap[nr][nc]});
        }
    }
    return water;
}
```

---

### 26. Find K Pairs with Smallest Sums

**⚠️ Nuance:** Start with (nums1[0], nums2[0]). Use min-heap on sum. Only push `(i, j+1)` when `(i, j)` is popped, and push `(i+1, 0)` only for i=0 to avoid duplicates.

```java
static List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
    List<List<Integer>> res = new ArrayList<>();
    if (nums1.length == 0 || nums2.length == 0) return res;

    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> nums1[a[0]] + nums2[a[1]]));
    for (int i = 0; i < Math.min(nums1.length, k); i++) minHeap.offer(new int[]{i, 0});

    while (!minHeap.isEmpty() && res.size() < k) {
        int[] cur = minHeap.poll();
        int i = cur[0], j = cur[1];
        res.add(Arrays.asList(nums1[i], nums2[j]));
        if (j + 1 < nums2.length) minHeap.offer(new int[]{i, j + 1});
    }
    return res;
}
```

---

### 27. Swim in Rising Water (Min-Cost Path)

**⚠️ Nuance:** Similar to Dijkstra but the "cost" to reach a cell is `max(time_to_reach, cell_value)`. Use min-heap on this max cost.

```java
static int swimInWater(int[][] grid) {
    int n = grid.length;
    boolean[][] vis = new boolean[n][n];
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    minHeap.offer(new int[]{grid[0][0], 0, 0}); // {cost, row, col}

    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!minHeap.isEmpty()) {
        int[] cur = minHeap.poll();
        int t = cur[0], r = cur[1], c = cur[2];
        if (r == n-1 && c == n-1) return t;
        if (vis[r][c]) continue;
        vis[r][c] = true;
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nr >= n || nc < 0 || nc >= n || vis[nr][nc]) continue;
            minHeap.offer(new int[]{Math.max(t, grid[nr][nc]), nr, nc});
        }
    }
    return -1;
}
```

---

### 28. Ugly Numbers (Min-Heap Approach)

**⚠️ Nuance:** Ugly numbers have only 2, 3, 5 as prime factors. Always generate next by multiplying current min by {2, 3, 5}. Use a `HashSet` to prevent duplicates from being added.

```java
static int nthUglyNumber(int n) {
    PriorityQueue<Long> minHeap = new PriorityQueue<>();
    Set<Long> seen = new HashSet<>();
    minHeap.offer(1L); seen.add(1L);
    int[] factors = {2, 3, 5};
    long curr = 1L;

    for (int i = 0; i < n; i++) {
        curr = minHeap.poll();
        for (int f : factors) {
            long next = curr * f;
            if (seen.add(next)) minHeap.offer(next);
        }
    }
    return (int) curr;
}
```

---

### 29. Design Twitter (Top 10 Recent Tweets — K-way Merge)

**⚠️ Nuance:** Each user has a list of tweets in chronological order. Fetch top 10 tweets from the current user + all followees = K-way merge problem. Min-heap of size 10 by tweet timestamp.

```java
static class Twitter {
    Map<Integer, List<int[]>> tweets = new HashMap<>();  // userId → [{tweetId, time}]
    Map<Integer, Set<Integer>> follows = new HashMap<>();
    int time = 0;

    void postTweet(int userId, int tweetId) {
        tweets.computeIfAbsent(userId, k -> new ArrayList<>()).add(new int[]{tweetId, time++});
    }

    List<Integer> getNewsFeed(int userId) {
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[2] - a[2]);
        // {listIdx_in_user_tweets, elemIdx, time, tweetId, userId}
        // Simpler: collect all relevant tweets, take top 10
        List<int[]> all = new ArrayList<>();
        if (tweets.containsKey(userId)) all.addAll(tweets.get(userId));
        Set<Integer> followees = follows.getOrDefault(userId, Collections.emptySet());
        for (int fId : followees)
            if (tweets.containsKey(fId)) all.addAll(tweets.get(fId));
        all.sort((a, b) -> b[1] - a[1]);
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < Math.min(10, all.size()); i++) res.add(all.get(i)[0]);
        return res;
    }

    void follow(int followerId, int followeeId) {
        follows.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
    }

    void unfollow(int followerId, int followeeId) {
        follows.getOrDefault(followerId, Collections.emptySet()).remove(followeeId);
    }
}
```

---

## 📚 Group 9: Special Heap Variants

---

### 30. Indexed Priority Queue (Decrease-Key Support)

**⚠️ Nuance:** Java's `PriorityQueue` has no `decreaseKey` operation. For problems requiring it (like proper Dijkstra, Prim), either: (a) use lazy deletion (push duplicates, skip stale), or (b) implement an indexed PQ manually. Lazy deletion is always fine for interviews.

```java
// Lazy deletion pattern — O(E log E) instead of O(E log V) but simpler
static int[] dijkstraLazy(List<List<Edge>> adj, int src) {
    int V = adj.size();
    int[] dist = new int[V];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{src, 0});
    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        if (cur[1] > dist[cur[0]]) continue;  // lazy deletion: stale entry
        for (Edge e : adj.get(cur[0]))
            if (dist[cur[0]] + e.wt < dist[e.to]) {
                dist[e.to] = dist[cur[0]] + e.wt;
                pq.offer(new int[]{e.to, dist[e.to]});
            }
    }
    return dist;
}
```

---

### 31. Double-Ended Priority Queue (Min + Max simultaneously)

**⚠️ Nuance:** When you need both min and max quickly. Use two heaps (like MedianFinder) or `TreeMap`. `TreeMap.firstKey()` = min, `TreeMap.lastKey()` = max, both O(log N).

```java
// Using TreeMap as double-ended PQ
static class DoubleEndedPQ {
    TreeMap<Integer, Integer> map = new TreeMap<>();

    void add(int val) { map.merge(val, 1, Integer::sum); }

    int removeMin() {
        int min = map.firstKey();
        if (map.merge(min, -1, Integer::sum) == 0) map.remove(min);
        return min;
    }

    int removeMax() {
        int max = map.lastKey();
        if (map.merge(max, -1, Integer::sum) == 0) map.remove(max);
        return max;
    }

    int peekMin() { return map.firstKey(); }
    int peekMax() { return map.lastKey(); }
}
```

---

## 🧠 Master Cheat Sheet — Heap Algorithm Selection

| Problem | Heap Type | Pattern | Time |
|---|---|---|---|
| Kth largest element | Min-Heap size K | Keep smallest K, top = answer | O(N log K) |
| Kth smallest element | Max-Heap size K | Keep largest K, top = answer | O(N log K) |
| Top K frequent | Min-Heap size K by freq | Frequency map + K-heap | O(N log K) |
| K closest points | Max-Heap size K by dist | Evict farthest | O(N log K) |
| Merge K sorted lists/arrays | Min-Heap | K-way merge, push next on pop | O(N log K) |
| Median from stream | Max-Heap + Min-Heap | Two-heap balance | O(log N) per add |
| Sliding window median | Two-Heap + remove | Two heaps + lazy/direct remove | O(N log K) |
| Meeting rooms (min rooms) | Min-Heap of end times | Sort by start, reuse ended rooms | O(N log N) |
| Task scheduler (cooldown) | Max-Heap + cooldown queue | Greedy: most frequent first | O(N log N) |
| Reorganize string | Max-Heap by freq | Place most frequent, skip prev | O(N log N) |
| Connect ropes (min cost) | Min-Heap | Huffman: combine 2 smallest | O(N log N) |
| IPO / maximize capital | Min-Heap + Max-Heap | Unlock affordable, pick max profit | O(N log N) |
| Dijkstra shortest path | Min-Heap | `{node, dist}`, skip stale | O(E log V) |
| Prim's MST | Min-Heap | `{node, edgeWeight}`, skip inMST | O(E log V) |
| Smallest range in K lists | Min-Heap + global max | Track min via heap, max manually | O(N log K) |
| Find K pairs smallest sum | Min-Heap | Expand (i, j) → (i, j+1) | O(K log K) |
| Ugly numbers (Kth) | Min-Heap + HashSet | Multiply by factors, dedup | O(N log N) |
| Swim in rising water | Min-Heap (cost = max seen) | Dijkstra variant | O(N² log N) |
| Heap sort | Max-Heap in-place | Build heap O(N), extract O(N log N) | O(N log N) |

---

## ⚡ Key Nuances to Remember in Interviews

**Choosing Min vs Max Heap**
- "Kth **largest**" → **Min**-Heap of size K (top = Kth largest, discard smaller).
- "Kth **smallest**" → **Max**-Heap of size K (top = Kth smallest, discard larger).
- "Always need the **minimum**" → Min-Heap. "Always need the **maximum**" → Max-Heap.

**Java PriorityQueue Traps**
- Default is **min-heap**. For max-heap: `new PriorityQueue<>(Comparator.reverseOrder())`.
- `(a, b) -> a - b` = min-heap. `(a, b) -> b - a` = max-heap. **Use only for int** — causes overflow with large negatives. Prefer `Integer.compare(a, b)`.
- `contains()` and `remove(Object)` are **O(N)** — mention this if used in a loop.
- Iterating a `PriorityQueue` does NOT give sorted order — use `poll()` in a loop.

**Heap Build Time**
- Insert N elements one by one = O(N log N).
- `new PriorityQueue<>(collection)` = **O(N) heapify** — always prefer this when all elements are known upfront.

**Two-Heap Pattern (Median)**
- Always add to maxHeap first, then rebalance.
- Invariant: `maxHeap.size() == minHeap.size()` OR `maxHeap.size() == minHeap.size() + 1`.
- Median: equal sizes → average of both tops; maxHeap larger → maxHeap top.

**K-Way Merge**
- Push only 1 element per list initially.
- On each pop, push the **next element from that same list**.
- Heap never grows beyond K → O(N log K) total.

**Lazy Deletion**
- When you can't efficiently remove from a heap, push the updated value and skip the old one when popped (`if stale, continue`).
- Valid when the same node can be in the heap multiple times (Dijkstra, sliding window).

**Comparator Pitfalls**
- For `int[]` arrays: `(a, b) -> a[0] - b[0]` only safe if values fit in int. Use `Integer.compare(a[0], b[0])` to be safe.
- For objects with multiple fields: chain comparisons — `Comparator.comparingInt(...).thenComparingInt(...)`.

---

*Covers all standard interview heap topics: implementation, K-th element patterns, two-heap pattern, K-way merge, scheduling, greedy+heap, graph algorithms, and advanced patterns.*
