# ⚖️ The Triage Heist: Priority Queues & Heaps ⚖️
### *A Hero’s Journey from Waiting in Line to Managing Chaos*

---

> *Alex rushed into the Emergency Room of the Algorithm Hospital. It was pandemonium. Patients with paper cuts were standing in front of patients with dragon bites. The line was moving based on who arrived first.*
>
> *A sharp-eyed nurse named **Warden Triage** blew a whistle. "Stop! First-In-First-Out (FIFO) is for regular queues. Here, we save the most critical patients first!"*
>
> *She pulled out a glowing, floating golden pyramid—a **Heap**. "This is a Priority Queue. It doesn't care who arrived first. It only cares who is the most important. Master this, and you can schedule tasks, find the shortest paths, and tame unsorted chaos in `O(log N)` time."*

---

## 📖 Chapter 0: The Anatomy of the Pyramid (What is a Heap?)

A Priority Queue is an Abstract Data Type. In Java, it is implemented using a **Binary Heap**.
Think of a Binary Heap as a **Complete Binary Tree** packed into an array.
*   **Max-Heap:** The largest element is always at the root.
*   **Min-Heap:** The smallest element is always at the root.

> 🧠 **The Magic Math (1D Array to Tree):**
> If a node is at index `i` in an array:
> *   Its left child is at `2i + 1`
> *   Its right child is at `2i + 2`
> *   Its parent is at `(i - 1) / 2`

### ⚙️ The Java Incantation
```java
// Min-Heap (Smallest at top) - DEFAULT IN JAVA
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-Heap (Largest at top) - Use a Comparator!
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

// Operations
minHeap.add(5);      // Insert: O(log N)
minHeap.poll();      // Remove top priority: O(log N)
minHeap.peek();      // Look at top priority: O(1)
```

---

## 📖 Chapter 1: The "Top K" Pattern (The Bread and Butter)

Warden Triage pointed to a massive crowd of 1 million people. *"Find me the 10 tallest people. But if you sort all 1 million, it will take `O(N log N)`. Too slow!"*

### 🌟 The Grandmaster's Paradigm Shift
Do NOT sort the whole array. Maintain a heap of exactly size `K`.

**Mental Model for Top K:**
*   Want the **K Largest**? Use a **Min-Heap** of size K. The smallest of the K largest gets pushed to the top. When you see a new bigger person, you kick the top (smallest) out.
*   Want the **K Smallest**? Use a **Max-Heap** of size K.

```java
// Find the Kth Largest Element in an Array
int findKthLargest(int[] nums, int k) {
	// MIN-HEAP to find Kth Largest
	PriorityQueue<Integer> heap = new PriorityQueue<>();
	
	for (int num : nums) {
		heap.add(num);
		if (heap.size() > k) {
			heap.poll(); // Kick out the smallest! We only keep the K largest.
		}
	}
	return heap.peek(); // The top is the Kth Largest!
}
```
> 🏷️ **Pattern Recognition:** "Kth largest", "Top K frequent", "K closest points" → **Heap of size K**. Time: `O(N log K)`. Space: `O(K)`.

---

## 📖 Chapter 2: The Custom ID Cards (Priority Queues with Objects)

*"What if patients have both a severity score and a name?"* Alex asked. "How does the Heap know who goes first?"

You must give the Heap a **Comparator**—a rulebook for comparing two objects.

Example: You have `int[][] points = {{x1, y1}, {x2, y2}}`. Find the K closest points to origin `(0,0)`.
Distance squared = `x^2 + y^2`.

```java
int[][] kClosest(int[][] points, int k) {
	// Max-Heap based on distance! We want to kick out the FARTHEST.
	// a[0]^2 + a[1]^2 is distance of point a.
	PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
		(a, b) -> (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])
	);
	
	for (int[] point : points) {
		maxHeap.add(point);
		if (maxHeap.size() > k) {
			maxHeap.poll(); // Kick out the farthest point
		}
	}
	
	// Convert heap back to array
	int[][] result = new int[k][2];
	int i = 0;
	while (!maxHeap.isEmpty()) {
		result[i++] = maxHeap.poll();
	}
	return result;
}
```
> ⚡ **Lambda Trick:** `(a, b) -> ...` If it returns negative, `a` comes first. If positive, `b` comes first.

---

## 📖 Chapter 3: The Twin Pyramids (Median of Data Stream)

Numbers keep flying into Alex's clinic one by one: `[1, 5, 2, 8...]`. At any moment, the Warden asks: *"What is the median right now?"*

Sorting every time takes `O(N log N)`. We need `O(log N)`.
**The Two Heaps Technique:**
1.  **Max-Heap (Left):** Stores the smaller half of the numbers. (Largest of the small half is at the top).
2.  **Min-Heap (Right):** Stores the larger half of the numbers. (Smallest of the large half is at the top).

**The Rule:** Keep the heaps balanced. The median is always the average of the two tops (or just the top of the larger heap).

```java
class MedianFinder {
	PriorityQueue<Integer> leftMax;  // smaller half
	PriorityQueue<Integer> rightMin; // larger half
	
	public MedianFinder() {
		leftMax = new PriorityQueue<>(Collections.reverseOrder());
		rightMin = new PriorityQueue<>();
	}
	
	public void addNum(int num) {
		// 1. Always add to leftMax first
		leftMax.add(num);
		
		// 2. Move the max of leftMax to rightMin to maintain order
		rightMin.add(leftMax.poll());
		
		// 3. Balance sizes: if rightMin becomes bigger, move one back to leftMax
		if (rightMin.size() > leftMax.size()) {
			leftMax.add(rightMin.poll());
		}
	}
	
	public double findMedian() {
		if (leftMax.size() > rightMin.size()) {
			return leftMax.peek();
		} else {
			return (leftMax.peek() + rightMin.peek()) / 2.0;
		}
	}
}
```

---

## 📖 Chapter 4: The Great Conveyor Belt (Merge K Sorted Lists)

Warden Triage rolled out K different sorted conveyor belts of data. *"Merge them into one giant sorted line!"*

If you merge them one by one, it's slow `O(K * N)`. 
Instead, put the **first element of every list** into a Min-Heap.
1. Pop the smallest element from the heap. Add it to the final list.
2. If that element came from list `i`, push the *next* element from list `i` into the heap.
3. Repeat until empty.

```java
ListNode mergeKLists(ListNode[] lists) {
	// Min-Heap comparing node values
	PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> a.val - b.val);
	
	// Push the head of every list into the heap
	for (ListNode list : lists) {
		if (list != null) minHeap.add(list);
	}
	
	ListNode dummy = new ListNode(0); // Ghost Engine from Linked List Heist!
	ListNode tail = dummy;
	
	while (!minHeap.isEmpty()) {
		ListNode smallest = minHeap.poll();
		tail.next = smallest;
		tail = tail.next;
		
		// If there is a next node in that specific list, push it!
		if (smallest.next != null) {
			minHeap.add(smallest.next);
		}
	}
	return dummy.next;
}
```

---

## 📖 Chapter 5: The ER Scheduling (Task Scheduler & Meeting Rooms)

*"I have tasks: A, A, A, B, B, B. After doing task A, I must wait `n=2` intervals before doing A again. Find minimum time to finish all tasks."*

This is a greedy + heap problem.
1. Count frequencies of each task.
2. Push all frequencies into a **Max-Heap**.
3. Use a Queue to act as a "cooldown" waiting room.
4. Every time-cycle: Pop from Max-Heap, execute (decrement count). If count > 0, put it in the cooldown queue with `time + n`. When the cooldown timer hits, move it back to the Max-Heap!

```java
int leastInterval(char[] tasks, int n) {
	int[] freq = new int[26];
	for (char c : tasks) freq[c - 'A']++;
	
	PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
	for (int f : freq) if (f > 0) maxHeap.add(f);
	
	int time = 0;
	Queue<int[]> cooldown = new LinkedList<>(); // {count, availableTime}
	
	while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
		time++;
		
		if (!maxHeap.isEmpty()) {
			int count = maxHeap.poll() - 1; // Execute task
			if (count > 0) {
				cooldown.add(new int[]{count, time + n}); // Wait n cycles
			}
		}
		
		if (!cooldown.isEmpty() && cooldown.peek()[1] == time) {
			maxHeap.add(cooldown.poll()[0]); // Cooldown over! Back to heap.
		}
	}
	return time;
}
```

---

## 📖 Chapter 6: The Sliding Window maximum (Deque vs Heap)

*"Find the maximum in every sliding window of size K."*
You *could* use a Max-Heap, but removing elements that aren't at the top of the heap is annoying and slow in Java.

### 🚂 The Monotonic Deque (The True Master Technique)
Instead of a Heap, use a **Deque (Double-ended queue)** that stores *indices*. Maintain it so it's always sorted from largest to smallest!

1. Before adding a new element, pop from the back of the deque any element smaller than the new one (they are useless).
2. Add the new element's index to the back.
3. Pop from the front if the index is out of the window.
4. The front of the deque is ALWAYS the maximum for the window!

```java
int[] maxSlidingWindow(int[] nums, int k) {
	int n = nums.length;
	int[] result = new int[n - k + 1];
	Deque<Integer> dq = new LinkedList<>(); // Stores INDICES
	
	for (int i = 0; i < n; i++) {
		// Remove indices that are out of this window
		while (!dq.isEmpty() && dq.peekFirst() <= i - k) dq.pollFirst();
		
		// Remove from back all elements smaller than nums[i]
		while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();
		
		dq.addLast(i);
		
		// Start recording answers once the first window is formed
		if (i >= k - 1) {
			result[i - k + 1] = nums[dq.peekFirst()]; // Front is the max!
		}
	}
	return result;
}
```
> 🏷️ **Pattern Recognition:** "Maximum/Minimum of every window" → **Monotonic Deque**. (Faster and cleaner than a Heap here!)

---

## 📖 Final Chapter: The Triage Master’s Blueprint

Warden Triage pinned a golden badge to Alex's coat. "You no longer wait in lines. You manage the chaos."

### 🗺️ The Grand Priority Queue Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Kth Largest / Smallest** | "Find Kth...", "Top K..." | **Heap of size K**. K Largest = Min-Heap. K Smallest = Max-Heap. |
| **Sort K sorted arrays** | "Merge K lists", "Kth smallest in matrix" | **Min-Heap**. Push first element of all K arrays. Poll & push next. |
| **Median / Middle value** | "Running median", "Middle of stream" | **Two Heaps**. Max-Heap for lower half, Min-Heap for upper half. |
| **Scheduling / Cooldown** | "Task scheduler", "Meeting rooms II" | **Heap + Queue**. Heap executes, Queue tracks cooldown/time. |
| **Sliding Window Max/Min** | "Max in every window of size K" | **Monotonic Deque**. Store indices, maintain descending order. |
| **Graph Shortest Path** | "Dijkstra", "Minimum cost path" | **Min-Heap**. Always expand the cheapest node. |

### 🧠 The Triage Master's Final Checklist

1. **Do I need the max or min repeatedly?** (If yes, use a Heap).
2. **Am I looking for Top K?** (If yes, don't sort the whole array! Keep a heap of size K).
3. **What is my comparator?** (For custom objects, write a lambda `(a, b) -> ...`).
4. **Am I using the right heap?** (K largest needs a Min-Heap to kick out the smallest).
5. **Is a heap overkill?** (For sliding window max, a Deque is better than a Heap).

### 🏆 Practice Quests — From Intern to Chief of Triage

**Level 1 (Apprentice):**
- Kth Largest Element in an Array, Last Stone Weight, Relative Ranks.

**Level 2 (Squire):**
- Top K Frequent Elements, K Closest Points to Origin, Assign Cookies.

**Level 3 (Knight):**
- Merge K Sorted Lists, Find Median from Data Stream, Reorganize String.

**Level 4 (Champion):**
- Task Scheduler, Meeting Rooms II, Sliding Window Maximum (Monotonic Deque), Find K Pairs with Smallest Sums.

**Level 5 (Grandmaster):**
- Smallest Range Covering Elements from K Lists, Minimum Cost to Connect Sticks, Rearrange String k Distance Apart, IPO (Maximize Capital with Two Heaps).

---

> *Alex looked at the chaotic Emergency Room. It was now perfectly organized. The most critical patients were seen instantly, the conveyor belts merged flawlessly, and the sliding windows calculated their maximums without breaking a sweat. He had mastered the art of Triage.*
>
> *He pocketed the golden pyramid. There was only one realm left to conquer.*

**The Triage Heist is Complete.** ⚖️
