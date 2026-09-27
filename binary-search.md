# ⏳ The Chrono-Search Heist: Binary Search ⏳
### *A Hero’s Journey from Linear Scanner to Master of Time*

---

> *Alex stood before the Infinite Library of Time. The librarian, a glowing entity named Chronos, floated beside him. "Alex, the book you need is somewhere on this shelf. The shelf holds 1,000,000,000 books, sorted alphabetically by title."*
>
> *Alex sighed. "I guess I'll start at book 1 and read every spine until I find it."*
>
> *Chronos gasped. "Are you mad?! That will take you 40 years! In the realm of DSA, checking every item one-by-one is called **Linear Search (O(N))**. It is the sin of the amateur!"*
>
> *Chronos snapped his fingers. A glowing sword of light appeared in Alex's hand. "I will teach you the art of **Binary Search**. It cuts time itself in half. With this, you will find the book in less than 30 steps. Draw your sword, and let us divide and conquer."*

---

## 📖 Chapter 0: The Mental Model — The Guessing Game

Before writing code, Alex had to understand *why* binary search works.

Imagine playing "Guess the Number" between 1 and 100.
* **You:** "50?"
* **Me:** "Higher."
* **You:** "75?"
* **Me:** "Lower."
* **You:** "62?"

Every time you guess, you eliminate **half** of the remaining possibilities. 
`100 -> 50 -> 25 -> 13 -> 6 -> 3 -> 1`. You find the number in 7 steps max!

### ⚠️ The Golden Rule of Binary Search
*"Master,"* Alex asked, *"Can I use this on any array?"*
*"NO!"* Chronos boomed. *"The array MUST be sorted! If it's not sorted, cutting it in half tells you nothing about where your target went. Binary Search requires **Monotonicity** (a consistent order)."*

---

## 📖 Chapter 1: The Core Spell — Standard Binary Search

Find a `target` in a sorted array. Return its index, or `-1`.

### 🧠 The 3 Pointers
You need 3 pointers: `left` (start), `right` (end), and `mid` (the sword).
1. Calculate `mid`.
2. If `arr[mid] == target`, you found it!
3. If `arr[mid] < target`, the target is on the right. Move `left = mid + 1`.
4. If `arr[mid] > target`, the target is on the left. Move `right = mid - 1`.

```java
int binarySearch(int[] arr, int target) {
	int left = 0;
	int right = arr.length - 1;
	
	while (left <= right) {
		// 🛑 CRITICAL: Avoid integer overflow!
		// DO NOT do (left + right) / 2. 
		// If left and right are near 2 billion, they overflow to negative!
		int mid = left + (right - left) / 2; 
		
		if (arr[mid] == target) {
			return mid; // Found it!
		} else if (arr[mid] < target) {
			left = mid + 1;  // Discard left half
		} else {
			right = mid - 1; // Discard right half
		}
	}
	return -1; // Not found
}
```

> ⚡ **Time Complexity:** `O(log N)`. You cut the array in half every step. 1 billion items take ~30 steps.

---

## 📖 Chapter 2: The Edge of the Blade — Lower & Upper Bound

*"What if there are duplicate numbers?"* Chronos asked. "Array: `[1, 2, 2, 2, 3, 4]`. Find me the FIRST `2`."

Standard binary search will just return *any* `2`. A Master needs to find the **Lower Bound** (first occurrence) and **Upper Bound** (last occurrence).

### 📉 Lower Bound Template (Find First Occurrence)
When you find a `2`, don't stop! Pretend you want to go even lower. Save it as a candidate, but keep searching left.

```java
int lowerBound(int[] arr, int target) {
	int left = 0;
	int right = arr.length - 1;
	int ans = arr.length; // Default if not found
	
	while (left <= right) {
		int mid = left + (right - left) / 2;
		
		if (arr[mid] >= target) {
			ans = mid;         // Candidate found! But maybe there's an earlier one?
			right = mid - 1;   // Keep searching left
		} else {
			left = mid + 1;    // Too small, go right
		}
	}
	return ans;
}
```

### 📈 Upper Bound Template (Find First Element STRICTLY Greater)
Used to find the last occurrence, or where an element would be inserted to keep sorted order.

```java
int upperBound(int[] arr, int target) {
	int left = 0;
	int right = arr.length - 1;
	int ans = arr.length;
	
	while (left <= right) {
		int mid = left + (right - left) / 2;
		
		if (arr[mid] > target) {
			ans = mid;
			right = mid - 1;
		} else {
			left = mid + 1;
		}
	}
	return ans;
}
```
> 🏷️ **Pattern Recognition:** If the problem says "Find the first element >= target" or "Insert position", you use **Lower Bound**. 

---

## 📖 Chapter 3: The Shifted Timeline — Search in Rotated Sorted Array

The library spun. The sorted array `[0,1,2,3,4,5,6,7]` magically rotated to `[4,5,6,7,0,1,2,3]`. 
*"Find `0`,"* Chronos challenged.

Even though it's rotated, **one half of the array is ALWAYS sorted**. You just have to figure out which half, and ask if your target lives there.

```java
int searchRotated(int[] nums, int target) {
	int left = 0, right = nums.length - 1;
	
	while (left <= right) {
		int mid = left + (right - left) / 2;
		if (nums[mid] == target) return mid;
		
		// Check if the LEFT half is sorted
		if (nums[left] <= nums[mid]) {
			// Is the target inside this sorted left half?
			if (target >= nums[left] && target < nums[mid]) {
				right = mid - 1; // Yes, go left
			} else {
				left = mid + 1;  // No, go right
			}
		} 
		// Otherwise, the RIGHT half must be sorted
		else {
			// Is the target inside this sorted right half?
			if (target > nums[mid] && target <= nums[right]) {
				left = mid + 1;  // Yes, go right
			} else {
				right = mid - 1; // No, go left
			}
		}
	}
	return -1;
}
```

---

## 📖 Chapter 4: The Hidden Matrix — Search in 2D Matrix

Alex stood before a grid of books. Rows sorted left-to-right, and the first book of each row is larger than the last book of the previous row. 
Example:
`[ 1,  3,  5,  7]`
`[10, 11, 16, 20]`
`[23, 30, 34, 60]`

*"Treat it as a 1D array,"* Chronos whispered. "If you flatten it, it's just one long sorted line."

To map a 1D index `mid` back to a 2D grid:
* `row = mid / columns`
* `col = mid % columns`

```java
boolean searchMatrix(int[][] matrix, int target) {
	int rows = matrix.length;
	int cols = matrix[0].length;
	
	int left = 0;
	int right = rows * cols - 1; // Treat as 1D array!
	
	while (left <= right) {
		int mid = left + (right - left) / 2;
		
		// Translate 1D index back to 2D coordinates
		int midValue = matrix[mid / cols][mid % cols];
		
		if (midValue == target) return true;
		else if (midValue < target) left = mid + 1;
		else right = mid - 1;
	}
	return false;
}
```

---

## 📖 Chapter 5: The Peak Finder — Unsorted Binary Search

*"Must the array always be fully sorted?"* Alex asked.
*"No,"* Chronos smiled. "It just needs a **Monotonic Trend**. If it goes up, then down, you can binary search for the peak!"

Example: `[1, 2, 3, 4, 5, 4, 3, 2]`. Find `5` (The Peak).
**The Logic:** Look at `mid`. If `arr[mid] < arr[mid+1]`, you are on the uphill slope! The peak must be to the **right**. If `arr[mid] > arr[mid+1]`, you are on the downhill slope! The peak must be to the **left**.

```java
int findPeakElement(int[] nums) {
	int left = 0, right = nums.length - 1;
	
	while (left < right) {
		int mid = left + (right - left) / 2;
		
		if (nums[mid] < nums[mid + 1]) {
			// Uphill! Peak is to the right.
			left = mid + 1; 
		} else {
			// Downhill! Peak is here or to the left.
			right = mid; 
		}
	}
	return left; // left and right converge on the peak
}
```

---

## 📖 Chapter 6: The Grand Illusion — Binary Search on Answer Space

This is the most powerful technique in the chapter. It separates the apprentices from the Grandmasters.

*"Master,"* Alex said, "I have an array of wood lengths: `[4, 6, 8, 10]`. I need to cut them to get exactly `15` meters of wood. What is the *maximum* height I can set my sawblade to?"

If you set the sawblade to height `H`, you get `0` from the `4`-log, `2` from the `6`-log, `4` from the `8`-log, `6` from the `10`-log. Total = `12`. Too low!

You could write a loop trying `H = 1, 2, 3, 4...` up to 10. `O(N * MaxHeight)`. Too slow!

### 🌟 The Grandmaster's Paradigm Shift
*"Stop searching the array,"* Chronos said. "**Search the answer.**"

What is the range of possible answers? The sawblade height `H` can be anywhere from `0` to `10` (the max tree height). That range is a **sorted number line**!

1. Binary search the height `H` between `0` and `10`.
2. For a given `mid` (sawblade height), write a helper function to calculate how much wood you get.
3. If you get *more* than `15` wood, your sawblade is too low! Move `left = mid + 1`.
4. If you get *less* than `15` wood, your sawblade is too high! Move `right = mid - 1`.

```java
int maxSawbladeHeight(int[] wood, int requiredWood) {
	int left = 0;
	int right = Arrays.stream(wood).max().getAsInt(); // Max possible height (10)
	int ans = 0;
	
	while (left <= right) {
		int mid = left + (right - left) / 2; // Guess a sawblade height
		
		// Calculate wood collected at this height
		long collected = 0;
		for (int w : wood) {
			if (w > mid) collected += (w - mid);
		}
		
		if (collected >= requiredWood) {
			// We got enough wood! Let's try a higher sawblade to get exactly 15.
			ans = mid; 
			left = mid + 1; 
		} else {
			// Not enough wood. Sawblade is too high. Lower it.
			right = mid - 1;
		}
	}
	return ans;
}
```

> 🏷️ **Pattern Recognition (Binary Search on Answer):**
> If a problem asks for:
> - "Find the **minimum** maximum capacity..."
> - "Find the **maximum** minimum distance..."
> - "What is the **smallest** size that satisfies X..."
>
> ...and you can write a `boolean isPossible(mid)` function, **you are looking at Binary Search on Answer Space.**

### 🚢 Classic Example: Ship Packages Within D Days
Array of weights: `[1,2,3,4,5,6,7,8,9,10]`. Ship them in `D` days in order. Find the minimum capacity of the ship.
* Search space: `Max(weights)` to `Sum(weights)`.
* `isPossible(capacity)`: Greedily fill the ship until capacity is exceeded, count the days. If days <= D, it's possible!

---

## 📖 Final Chapter: The Timekeeper’s Map

Chronos stopped the clock. Alex stood before the final door of the library. 

*"You have mastered time, Alex. You no longer scan the universe one step at a time. You slice it in half."*

### 🗺️ The Grand Binary Search Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Exact Match** | "Find target in sorted array" | Standard BS. `left <= right`, return `mid`. |
| **Insert Position** | "Find first >= target" | Lower Bound BS. `ans = mid; right = mid - 1`. |
| **Rotated Array** | Array was sorted, then shifted | Check which half is sorted, see if target lives there. |
| **2D Matrix** | Rows sorted, first element of row > last of prev | Treat as 1D array. `row = mid / cols`. |
| **Find Peak** | Array goes up, then down | Trend BS. If `arr[mid] < arr[mid+1]`, go right. |
| **Minimize Max / Maximize Min** | "Find minimum capacity", "Maximum sawblade" | **BS on Answer Space.** Search `0` to `Max`, use `isPossible(mid)`. |

### 🧠 The Timekeeper's Final Checklist

Before writing any Binary Search code, ask yourself:
1. **Is the search space sorted/monotonic?** (If not, you cannot BS it, unless you are searching the *answer space*).
2. **Am I avoiding overflow?** (Always use `mid = left + (right - left) / 2`).
3. **Am I updating pointers correctly?** 
- Exact match: `left = mid + 1`, `right = mid - 1`.
- Boundaries: `right = mid` (not `mid - 1`) to avoid skipping candidates.
4. **Am I using `<` or `<=`?** 
- Use `while (left <= right)` when you want to stop when pointers cross.
- Use `while (left < right)` when you want them to converge on a single index (like finding a peak or answer space).
5. **Did I write the `isPossible()` function?** (If doing BS on Answer, test your helper function first!).

### 🏆 Practice Quests — From Scanner to Time Master

**Level 1 (Apprentice):**
- Binary Search (Exact Match), Search Insert Position, Square Root of X, Guess Number Higher or Lower.

**Level 2 (Squire):**
- Find First and Last Position of Element (Lower/Upper bound), Peak Index in a Mountain Array, Valid Perfect Square.

**Level 3 (Knight):**
- Search in Rotated Sorted Array, Find Minimum in Rotated Sorted Array, Search a 2D Matrix, Koko Eating Bananas (BS on Answer).

**Level 4 (Champion):**
- Find Peak Element, Capacity To Ship Packages Within D Days, Split Array Largest Sum, Minimum Size Subarray Sum.

**Level 5 (Grandmaster):**
- Median of Two Sorted Arrays (O(log(min(m,n)))), Aggressive Cows (Maximize minimum distance), Allocate Books, Find Kth Smallest Pair Distance.

---

> *Chronos faded into the shelves. Alex opened the final book. Inside, there was only one sentence:*
>
> *"To master algorithms is to master time. You started as a boy scanning the shelves. Today, you are a Timekeeper."*
> 
> *Alex smiled, closed the book, and walked out into the world. He knew exactly where to look.*

**The Chrono-Search Heist is Complete.** ⏳
