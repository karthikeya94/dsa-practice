# 📜 The Archivo-Vault: Hashing & Prefix Sum 📜
### *A Hero’s Journey from Counting Stones to Bending Time*

---

> *Alex pushed open the heavy stone doors of the Archivo-Vault. Inside, millions of glowing ledgers floated in the air. A blindfolded monk named **Archivist Ledger** was spinning around, catching books without looking.*
>
> *"Alex,"* he whispered. "You have mastered Pointers and Windows. But you still suffer from the Curse of Recalculation. If I ask you, 'What is the sum of elements from index 5 to index 1,000,000?', you will start a loop. You will count 1 million items. By the time you finish, I will be dead."
>
> *He pulled off his blindfold. His eyes glowed with a faint, mathematical light. "To beat time, you must trade it for space. I will teach you the art of **Prefix Sum** (seeing the future by pre-calculating the past) and **Hashing** (the magic of instant retrieval)."*

---

## 📖 Chapter 0: The Magic Equation (1D Prefix Sum)

Archivist Ledger threw an array at Alex: `[2, 4, 6, 8, 10]`.
*"I want the sum from index 1 to 3 (which is 4 + 6 + 8 = 18). But I want it in `O(1)` time."*

**The Mental Model:**
Create a new array `prefix[]` where `prefix[i]` stores the sum of everything *from the start up to index i-1*.
`prefix = [0, 2, 6, 12, 20, 30]`
*(Notice the leading 0. It prevents out-of-bounds errors!)*

**The Magic Equation:**
`Sum(L to R) = prefix[R + 1] - prefix[L]`
*Sum(1 to 3) = prefix[4] - prefix[1] = 20 - 2 = 18.*

```java
// Step 1: Build the Prefix Sum array
int[] prefix = new int[nums.length + 1];
for (int i = 0; i < nums.length; i++) {
	prefix[i + 1] = prefix[i] + nums[i];
}

// Step 2: Answer any range sum query in O(1) time!
int rangeSum(int L, int R, int[] prefix) {
	return prefix[R + 1] - prefix[L];
}
```
> 🏷️ **Pattern Recognition:** "Range sum query", "Sum of subarray", "Equal sum partitions" → **Prefix Sum**.

---

## 📖 Chapter 1: The Instant Librarian (Hashing Basics)

Ledger snapped his fingers. A magical cabinet with infinite drawers appeared.
*"This is a HashMap. In Java, it uses `hashCode()` to instantly jump to the exact drawer where your data lives. It gives `O(1)` insertion, deletion, and lookup."*

```java
// HashMap stores Key-Value pairs
Map<String, Integer> map = new HashMap<>();
map.put("Apple", 5);
map.get("Apple"); // Returns 5 in O(1) time!

// HashSet stores unique keys (like a mathematical set)
Set<Integer> set = new HashSet<>();
set.add(10);
set.contains(10); // Returns true in O(1) time!
```

### 🕵️‍♂️ The "Seen It Before" Pattern (Two Sum)
*"Find two numbers that sum to target."*
Instead of nested loops (`O(N^2)`), use a HashSet.
As you walk the array, ask: *"Have I seen `target - current_number` before?"*
If yes, you found your pair! If no, throw the `current_number` into the set.

```java
int[] twoSum(int[] nums, int target) {
	Map<Integer, Integer> map = new HashMap<>(); // value -> index
	
	for (int i = 0; i < nums.length; i++) {
		int complement = target - nums[i];
		if (map.containsKey(complement)) {
			return new int[]{map.get(complement), i}; // Found it!
		}
		map.put(nums[i], i); // Store for later
	}
	return null;
}
```
> 🏷️ **Pattern Recognition:** "Find pairs", "Contains duplicate", "Intersection of arrays" → **HashSet / HashMap (Seen It Before)**.

---

## 📖 Chapter 2: The Frequency Counter (Anagrams & Grouping)

*"I have 10,000 words,"* Ledger said. "Group the anagrams together." (`eat`, `tea`, `tan`, `ate`, `nat`).

Sorting every word takes `O(N * K log K)`. Too slow.
**The Trick:** Use a HashMap where the **Key** is a representation of the letters, and the **Value** is a list of words.
For the key, you can either sort the string, OR (the master move) use a 26-character count array as a string!

```java
List<List<String>> groupAnagrams(String[] strs) {
	Map<String, List<String>> map = new HashMap<>();
	
	for (String s : strs) {
		int[] count = new int[26];
		for (char c : s.toCharArray()) count[c - 'a']++;
		
		// Convert count array to a string key like "1#0#2#..."
		String key = Arrays.toString(count);
		
		map.putIfAbsent(key, new ArrayList<>());
		map.get(key).add(s);
	}
	return new ArrayList<>(map.values());
}
```

---

## 📖 Chapter 3: The Grand Combo (Prefix Sum + HashMap)

This is the most powerful technique in this realm. It separates the apprentices from the Grandmasters.
*"Find the total number of continuous subarrays whose sum equals `k`."*

If you use nested loops, it's `O(N^2)`.
**The Grandmaster's Paradigm Shift:**
1. We know `Sum(L to R) = prefix[R] - prefix[L-1]`.
2. We want `Sum(L to R) == k`.
3. Therefore: `prefix[R] - k == prefix[L-1]`.

As you walk the array, keep a running `currentSum` (which acts as `prefix[R]`).
At every step, ask the HashMap: *"How many times have I seen a previous prefix sum equal to `currentSum - k`?"*
Every time you've seen it, that's a valid subarray!

```java
int subarraySum(int[] nums, int k) {
	int count = 0;
	int currentSum = 0;
	
	// Map: Prefix Sum -> How many times it has appeared
	Map<Integer, Integer> prefixMap = new HashMap<>();
	// Base case: A sum of 0 has appeared 1 time (before the array starts)
	prefixMap.put(0, 1); 
	
	for (int num : nums) {
		currentSum += num; // This is our prefix[R]
		
		// Have we seen a previous sum that we can subtract to get k?
		if (prefixMap.containsKey(currentSum - k)) {
			count += prefixMap.get(currentSum - k);
		}
		
		// Record the current sum for future elements
		prefixMap.put(currentSum, prefixMap.getOrDefault(currentSum, 0) + 1);
	}
	return count;
}
```
> 🏷️ **Pattern Recognition:** "Subarray sum equals K", "Continuous subarray sum divisible by K", "Count subarrays with exactly K odd numbers" → **Prefix Sum + HashMap**.

---

## 📖 Chapter 4: The Grid of Blocks (2D Prefix Sum)

Ledger unfurled a 2D matrix on the floor. *"I want the sum of the rectangle from `(r1, c1)` to `(r2, c2)` in `O(1)` time."*

It works exactly like 1D, but using the **Inclusion-Exclusion Principle**.
`prefix[r+1][c+1]` = sum of everything from `(0,0)` to `(r,c)`.
To find the sum of a rectangle, you take the big area, subtract the left part, subtract the top part, and add back the overlapping corner (because you subtracted it twice).

```java
// Build 2D Prefix Sum
int[][] prefix = new int[rows + 1][cols + 1];
for (int i = 0; i < rows; i++) {
	for (int j = 0; j < cols; j++) {
		prefix[i + 1][j + 1] = matrix[i][j] 
		+ prefix[i][j + 1] 
		+ prefix[i + 1][j] 
		- prefix[i][j];
	}
}

// Query Rectangle (r1, c1) to (r2, c2) inclusive
int sumRect = prefix[r2 + 1][c2 + 1] 
- prefix[r1][c2 + 1] 
- prefix[r2 + 1][c1] 
+ prefix[r1][c1];
```

---

## 📖 Chapter 5: The Rolling Hash (Rabin-Karp Concept)

*"I need to find a string `pattern` inside a massive text,"* Ledger said. "If I compare every window, it's `O(N * M)`. We need to hash the windows, but re-hashing every window is slow."

**Rolling Hash:** You don't recompute the hash from scratch. You subtract the value of the character leaving the window, and add the value of the character entering the window! (Just like a prefix sum, but for string hashes).

---

## 📖 Final Chapter: The Archivist’s Blueprint

Ledger closed the floating books. "You see now, Alex? Time is rigid, but space is flexible. If you are willing to spend a little memory, you can bypass millions of CPU cycles."

### 🗺️ The Grand Hashing & Prefix Sum Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Range Sum Query** | "Sum of subarray", "Sum from L to R" | **1D Prefix Sum**. `prefix[R+1] - prefix[L]`. |
| **Rectangle Sum Query** | "Sum of matrix block" | **2D Prefix Sum**. Inclusion-Exclusion principle. |
| **Find Pairs / Duplicates** | "Two Sum", "Contains Duplicate" | **HashSet / "Seen It Before"**. |
| **Grouping / Anagrams** | "Group anagrams", "Group shifted strings" | **HashMap**. Key = normalized string/count array. |
| **Count Subarrays = K** | "Subarray sum equals K", "Binary subarrays with sum" | **Prefix Sum + HashMap**. Look for `currentSum - k`. |
| **String Matching** | "Find substring in text" | **Rolling Hash (Rabin-Karp)**. Slide the hash. |

### 🧠 The Archivist's Final Checklist

1. **Am I asked for a range sum?** (If yes, build a Prefix Sum array. Don't loop!).
2. **Am I looking for pairs?** (If yes, use a HashSet. Store what you've seen).
3. **Am I counting subarrays that sum to K?** (If yes, Prefix Sum + HashMap. Remember to `put(0, 1)` as the base case!).
4. **Is my HashMap Key correct?** (For anagrams, the key must be the letter frequency, not the string itself).
5. **Am I dealing with negative numbers?** (Prefix Sum + HashMap handles negatives perfectly! Sliding Window cannot handle negative numbers easily).

### 🏆 Practice Quests — From Clerk to Archivist

**Level 1 (Apprentice):**
- Running Sum of 1D Array, Find Pivot Index, Contains Duplicate, Two Sum.

**Level 2 (Squire):**
- Range Sum Query - Immutable, Majority Element, Valid Anagram, Intersection of Two Arrays.

**Level 3 (Knight):**
- Subarray Sum Equals K, Contiguous Array (Binary array with equal 0s and 1s), Group Anagrams.

**Level 4 (Champion):**
- Range Sum Query 2D - Immutable, Continuous Subarray Sum (Divisible by K), Longest Substring Without Repeating Characters (Hash + Window).

**Level 5 (Grandmaster):**
- Count of Range Sum, Maximum Size Subarray Sum Equals k, Find the Longest Substring Containing Vowels in Even Counts (Bitmask + Prefix Sum), Implement strStr() (Rolling Hash).

---

> *Alex stepped out of the Archivo-Vault. He looked at a massive array of numbers and no longer saw a million items to add up. He saw a single subtraction. He had learned the ultimate trade-off: memory is cheap, time is precious. Spend memory to save time.*

**The Archivo-Vault Heist is Complete.** 📜
