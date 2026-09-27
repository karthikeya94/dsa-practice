# 🥞 The Time Capsule & The Ticket Line: Stacks & Queues 🥞
### *A Hero’s Journey from Waiting in Line to Bending Time*

---

> *Alex entered the Bureau of Order. It was a massive room split perfectly down the middle. On the left, a chaotic pile of magical plates. On the right, a perfectly straight rope line leading to a rollercoaster.*
>
> *The Bureau Master, a man with a stopwatch named **Keeper Lifo-Fifo**, smiled. "Alex, you’ve conquered the complex stuff: Graphs, DP, Bits. But you’re overthinking the simple stuff. Data isn't always a web or a tree. Sometimes, it’s just a pile. Or a line."*
> 
> *He pointed to the left. "That is a **Stack**. Last In, First Out (LIFO). You put a plate on top, you take a plate off the top."*
> *He pointed to the right. "That is a **Queue**. First In, First Out (FIFO). The first person in line gets on the ride first."*
> *"Seems simple,"* Alex said.
> *"It is,"* the Keeper grinned. "But when you mix these with loops and recursion, they become the most powerful tools for parsing, validating, and searching in tight spaces."*

---

## 📖 Chapter 0: The Java Tools

In Java, you don't build these from scratch (usually). You use the built-in tools.

```java
// STACK (LIFO) - The Plate Pile
Stack<Integer> stack = new Stack<>();
stack.push(5);    // Put on top
stack.pop();      // Take off top
stack.peek();     // Look at top without taking

// QUEUE (FIFO) - The Ticket Line
Queue<Integer> queue = new LinkedList<>();
queue.add(5);     // Get in line
queue.poll();     // First person leaves line
queue.peek();     // Who is next?

// DEQUE (Double-Ended Queue) - The Ultimate Tool
// Fast at adding/removing from BOTH ends. Use this over Stack for performance!
Deque<Integer> deque = new ArrayDeque<>();
deque.addFirst(1); deque.addLast(2);
deque.pollFirst(); deque.pollLast();
```

---

## 📖 Chapter 1: The Bracket Enforcer (Valid Parentheses)

The Keeper threw a string at Alex: `"{ [ ( ] ) }"`. 
*"Is this mathematically valid?"* he asked.

Alex saw that the brackets were mismatched. 
*"How do I check it?"*
*"A Stack!"* the Keeper said. "When you see an opening bracket `({[`, you push it onto the plate pile. When you see a closing bracket `)}]`, the top plate MUST be its matching partner. If it’s not, or if the pile is empty when you need it, it’s invalid!"

```java
boolean isValid(String s) {
	Stack<Character> stack = new Stack<>();
	
	for (char c : s.toCharArray()) {
		if (c == '(' || c == '{' || c == '[') {
			stack.push(c); // Open bracket? Put on pile.
		} else {
			if (stack.isEmpty()) return false; // Closing bracket but nothing to match!
			char top = stack.pop();
			if ((c == ')' && top != '(') || 
				(c == '}' && top != '{') || 
					(c == ']' && top != '[')) {
				return false; // Mismatch!
					}
				}
		}
		return stack.isEmpty(); // Pile must be empty at the end!
	}
	```
	> 🏷️ **Pattern Recognition:** "Balanced brackets", "HTML tag matching", "Reverse a string/linked list" → **Use a Stack**.
	
	---
	
	## 📖 Chapter 2: The Time Traveler’s Undo (Min Stack)
	
	*"Design a Stack,"* the Keeper challenged, "that not only pushes and pops, but can tell you the **minimum value** inside it in `O(1)` time."*
	
	Alex couldn't scan the stack—that's `O(N)`. 
	*"You need a second ghost stack,"* the Keeper whispered. *"Every time you push a number, also push the current minimum onto the ghost stack. When you pop, pop the ghost stack too. The top of the ghost stack is ALWAYS the current minimum!"*
	
	```java
	class MinStack {
		Stack<Integer> mainStack;
		Stack<Integer> minStack;
		
		public MinStack() {
			mainStack = new Stack<>();
			minStack = new Stack<>();
		}
		
		public void push(int val) {
			mainStack.push(val);
			// If minStack is empty or val is smaller/equal to current min, push it
			if (minStack.isEmpty() || val <= minStack.peek()) {
				minStack.push(val);
			} else {
				minStack.push(minStack.peek()); // Duplicate the current min
			}
		}
		
		public void pop() {
			mainStack.pop();
			minStack.pop();
		}
		
		public int top() { return mainStack.peek(); }
		public int getMin() { return minStack.peek(); }
	}
	```
	
	---
	
	## 📖 Chapter 3: The Monotonic Mountain (Next Greater Element)
	
	This is the ultimate Stack pattern. 
	*Array:* `[4, 5, 2, 25]`. For every element, find the *next* element to its right that is larger. If none, `-1`.
	*Output:* `[5, 25, 25, -1]`.
	
	You *could* use two nested loops (`O(N^2)`). But the Keeper demanded `O(N)`.
	**The Mental Model:** Walk through the array. Keep a stack of indices waiting for their "Greater Element".
	When you see a new number, check if it is the "Greater Element" for the guys waiting in the stack. If it is, pop them and record the answer! Then, put the new guy on the stack to wait.
	
	```java
	int[] nextGreaterElement(int[] nums) {
		int n = nums.length;
		int[] result = new int[n];
		Arrays.fill(result, -1); // Default to -1
		Stack<Integer> stack = new Stack<>(); // Stores INDICES
		
		for (int i = 0; i < n; i++) {
			// While stack is not empty AND current number is > the number at stack's top index
			while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
				int prevIndex = stack.pop();
				result[prevIndex] = nums[i]; // Found the next greater!
			}
			stack.push(i); // Put current index in line to wait
		}
		return result;
	}
	```
	> 🏷️ **Pattern Recognition:** "Next greater/smaller", "Daily Temperatures", "Largest Rectangle in Histogram" → **Monotonic Stack**.
	
	---
	
	## 📖 Chapter 4: The Queue from Stacks (Bending the Rules)
	
	*"I only have two Stacks,"* the Keeper said. "Build a Queue."
	
	A Stack is LIFO. A Queue is FIFO. How do you get First-In-First-Out using Last-In-First-Out?
	You use an `inStack` and an `outStack`.
	1. `push(x)`: Just push it to `inStack`.
	2. `pop() / peek()`: If `outStack` is empty, **pour everything** from `inStack` into `outStack`. Now the bottom of `inStack` is the top of `outStack`! Pop from `outStack`.
	
	```java
	class MyQueue {
		Stack<Integer> inStack;
		Stack<Integer> outStack;
		
		public MyQueue() {
			inStack = new Stack<>();
			outStack = new Stack<>();
		}
		
		public void push(int x) {
			inStack.push(x);
		}
		
		public int pop() {
			peek(); // Ensure outStack has the reversed elements
			return outStack.pop();
		}
		
		public int peek() {
			if (outStack.isEmpty()) {
				while (!inStack.isEmpty()) {
					outStack.push(inStack.pop()); // Flip the plates!
				}
			}
			return outStack.peek();
		}
	}
	```
	
	---
	
	## 📖 Chapter 5: The Alien Calculator (Evaluate Reverse Polish Notation)
	
	*"In the alien dimension, math is written backwards,"* the Keeper showed Alex a screen.
	`["2", "1", "+", "3", "*"]`  -> This means `((2 + 1) * 3) = 9`.
	
	This is called **Postfix Notation**. Stacks eat this for breakfast.
	* If you see a number, push it.
	* If you see an operator (`+ - * /`), pop two numbers, do the math, and push the result back!
	
	```java
	int evalRPN(String[] tokens) {
		Stack<Integer> stack = new Stack<>();
		
		for (String token : tokens) {
			if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
				int b = stack.pop();
				int a = stack.pop();
				switch (token) {
					case "+": stack.push(a + b); break;
					case "-": stack.push(a - b); break;
					case "*": stack.push(a * b); break;
					case "/": stack.push(a / b); break;
				}
			} else {
				stack.push(Integer.parseInt(token)); // It's a number!
			}
		}
		return stack.pop(); // The final answer
	}
	```
	
	---
	
	## 📖 Final Chapter: The Keeper’s Blueprint
	
	The Keeper put away his stopwatch. "You see, Alex, Stacks and Queues aren't just data structures. They are *behaviors*. Stacks are for backtracking and undoing. Queues are for order and spreading."
	
	### 🗺️ The Grand Stack & Queue Cheat Sheet
	
	| Problem Shape | Key Signal | Technique / Pattern |
	|---|---|---|
	| **Balanced / Matching** | "Valid parentheses", "Tag matching" | **Stack**. Push openers, pop on closers. |
	| **Undo / History** | "Min Stack", "Browser back button", "Baseball points" | **Stack** (or Two Stacks for Min). |
	| **Next Greater / Smaller** | "Daily Temperatures", "Next larger node" | **Monotonic Stack**. Pop while current > stack top. |
	| **Reverse Polish / Postfix** | `["2", "1", "+"]` | **Stack**. Push numbers, pop 2 on operator. |
	| **Level Order / BFS** | "Shortest path in grid", "Level order tree" | **Queue**. FIFO expands layer by layer. |
	| **Sliding Window Max** | "Max in every window of K" | **Monotonic Deque**. (Covered in PQ Heist). |
	
	### 🧠 The Time-Bender's Final Checklist
	1. **Does order matter backward?** (Undo, nesting, reversing) → Stack.
	2. **Does order matter forward?** (Processing in exact sequence, BFS) → Queue.
	3. **Am I looking for the "next" element satisfying a condition?** → Monotonic Stack.
	4. **Am I parsing an expression?** → Stack for operands/operators.
	5. **Need fast add/remove from both ends?** → `ArrayDeque`.
	
	### 🏆 Practice Quests — From Clerk to Keeper
	
	**Level 1 (Apprentice):**
	- Valid Parentheses, Reverse String, Implement Queue using Stacks.
	
	**Level 2 (Squire):**
	- Min Stack, Baseball Game, Backspace String Compare.
	
	**Level 3 (Knight):**
	- Evaluate Reverse Polish Notation, Next Greater Element I, Daily Temperatures.
	
	**Level 4 (Champion):**
	- Online Stock Span, Asteroid Collision, Remove All Adjacent Duplicates In String.
	
	**Level 5 (Grandmaster):**
	- Largest Rectangle in Histogram, Maximal Rectangle, Sliding Window Maximum.
	
	---
	
	> *Alex walked out of the Bureau of Order. He realized that sometimes the simplest tools—a pile of plates or a rope line—could solve the most complex parsing and searching problems. He didn't always need a Graph or a Tree. Sometimes, he just needed to wait his turn.*
	
	**The Time Capsule Heist is Complete.** ⏳
	
	---
	---
	
	# 🗺️ The Grand Atlas: What Remains in the DSA Universe?
	
	Alex stood on a cliff, looking at the vast landscape of the Algorithm Kingdom. He had conquered massive continents. But to be a true Grandmaster, he needed to know what else was out there.
	
	Here is the map of **Remaining DSA Realms** not fully covered yet:
	
	### 1. 🥷 The Sliding Window & Two Pointers Realm (Arrays/Strings)
	*   **What it is:** Instead of nested loops (`O(N^2)`), use two indices (`left` and `right`) that move forward to find subarrays or substrings.
	*   **Sliding Window:** Finding the max sum of a subarray of size `K`. (Fixed window). Or "Longest substring without repeating characters" (Dynamic window).
	*   **Two Pointers:** "Two Sum in sorted array", "Container with most water", "Trapping rain water".
	*   **Why it matters:** It is the ultimate optimization for Array/String interview questions.
	
	### 2. 🧮 The Hashing & Prefix Sum Realm
	*   **What it is:** Using HashMaps/HashSets to trade memory for `O(1)` lookups.
	*   **Prefix Sum:** If you have an array and need to find the sum of any subarray `[L, R]` instantly, precompute a `prefix[]` array. `sum(L, R) = prefix[R] - prefix[L-1]`.
	*   **Pattern:** "Subarray sum equals K", "Continuous subarray sum". You map remainders or prefix sums to a HashMap!
	
	### 3. 🎯 The Greedy Realm
	*   **What it is:** Unlike DP (which tries all paths), Greedy algorithms make the locally optimal choice at every step and *hope* it leads to the global optimum.
	*   **Examples:** "Jump Game", "Assign Cookies", "Gas Station", "Fractional Knapsack".
	*   **Why it matters:** When it works, it’s faster than DP (`O(N log N)` or `O(N)`). Proving it works is the hard part!
	
	### 4. ⚡ The Union-Find / Disjoint Set Realm (DSU)
	*   *Note: We touched this in Kruskal's MST, but it deserves its own mastery.*
	*   **What it is:** A data structure that tracks elements split into sets. It answers: "Are X and Y in the same group?" in nearly `O(1)` time.
	*   **Pattern:** "Number of connected components", "Redundant Connection" (finding cycles), "Accounts Merge". Uses **Path Compression** and **Union by Rank**.
	
	### 5. 🧸 The Advanced String Realm
	*   **What it is:** Beyond simple `.charAt()` and StringBuilder.
	*   **KMP Algorithm:** Pattern matching. Find string `B` inside string `A` in `O(N+M)` time without backtracking. (Build an LPS - Longest Prefix Suffix array).
	*   **Rabin-Karp:** String matching using Rolling Hashes.
	*   **Z-Algorithm:** Finding all occurrences of a pattern in a text.
	
	### 6. 🌲 The Advanced Trees Realm
	*   **AVL Trees / Red-Black Trees:** Self-balancing BSTs. (Java's `TreeMap` uses a Red-Black tree).
	*   **Binary Indexed Trees (Fenwick Tree):** A magical array that can calculate prefix sums and handle point updates in `O(log N)`. Easier to code than a Segment Tree.
	*   **Trie (Prefix Tree):** (Covered briefly, but highly important for word-search problems).
	
	### 7. 📊 The Math & Geometry Realm
	*   **What it is:** The raw math of DSA.
	*   **Number Theory:** Sieve of Eratosthenes (find primes), GCD/LCM (Euclidean algorithm), Modular Exponentiation (Fast power).
	*   **Computational Geometry:** "Convex Hull" (Jarvis March / Graham Scan), detecting if line segments intersect.
	
	### 8. 🏗️ The Design & Architecture Realm
	*   **What it is:** Object-Oriented Design combined with DSA.
	*   **Patterns:** "Design Twitter" (Mix of HashMap, Heap, and Linked List), "LRU Cache" (Covered!), "LFU Cache", "Design Tic-Tac-Toe", "Design Search Autocomplete System".
	
	---
	> *The journey never truly ends, Alex. But with DP, Graphs, Bits, P&C, Recursion, Binary Search, Linked Lists, Trees, Priority Queues, Stacks, and Queues in your arsenal... you are no longer a beginner. You are a dangerous algorithm warrior. Pick your next realm, and conquer it.*
