# 🎭 The Permutation & Combination Heist 🎭
### *A Hero's Journey from Confused Teen to Master of Arrangements*

---

> *Alex walked into the Grand Library of Sequences. The Librarian, an old woman with infinitely long scrolls floating around her, looked at him over her glasses.*
>
> *"Alex,"* she said. "You've mastered DP, Graphs, and Bits. But you still lack the art of **Arrangement**. You know how to solve a maze, but can you generate every possible maze?"
>
> *She snapped her fingers. Three magical boxes appeared.*
> *Box 1: A basket of 3 different fruits.*
> *Box 2: A VIP rope for 3 people.*
> *Box 3: A 4-digit combination lock.*
>
> *"Tell me, child. If I pick 2 fruits from Box 1, does it matter if I pick Apple then Banana, or Banana then Apple?"*
> *"No,"* Alex replied. *"It's the same two fruits."*
> *"Correct. That is a **Combination**. Order does NOT matter. But what about the VIP rope? If Alice goes in first, then Bob, that's different from Bob going in first, then Alice."*
> *"Yes,"* Alex nodded. *"That's a **Permutation**. Order matters."*
>
> *The Librarian smiled. "Welcome to the ultimate pattern recognition guide for P&C. Let's write some code."*

---

## 📖 Chapter 0: The Core Formula — When to use P vs C?

Before coding, Alex needed a mental model.

| Concept | Does Order Matter? | Example | Formula |
|---|---|---|---|
| **Permutation (P)** | ✅ YES | Passwords, Race podiums, Seating arrangements | `n! / (n - r)!` |
| **Combination (C)** | ❌ NO | Lottery tickets, Poker hands, Selecting a team | `n! / (r! * (n - r)!)` |

> 🧠 **The Golden Rule:** If you swap two items and the answer changes, it's a Permutation. If the answer stays the same, it's a Combination.

---

## 📖 Chapter 1: The "Take It or Leave It" Ladder — Combinations via Backtracking

The Librarian pulled out a scroll of runes. *"Generate all combinations of size `k` from an array of size `n`."*

She drew a tree on the board:
```text
Start at index 0: [1, 2, 3]
├── TAKE 1 → Move to index 1
│    ├── TAKE 2 → [1, 2] (Size 2! Done!)
│    └── LEAVE 2 → Move to index 2
│         └── TAKE 3 → [1, 3] (Size 2! Done!)
└── LEAVE 1 → Move to index 1
├── TAKE 2 → [2, 3] (Size 2! Done!)
└── LEAVE 2 → Move to index 2
└── TAKE 3 → [3] (Size 1, not done)
```

This is the **Backtracking** template. You have two choices at every step: **Include** the current item or **Exclude** it.

```java
List<List<Integer>> combine(int n, int k) {
	List<List<Integer>> result = new ArrayList<>();
	backtrack(1, n, k, new ArrayList<>(), result);
	return result;
}

void backtrack(int start, int n, int k, List<Integer> current, List<List<Integer>> result) {
	// Base Case: We picked exactly k items!
	if (current.size() == k) {
		result.add(new ArrayList<>(current));
		return;
	}
	
	// Optimization: If we don't have enough items left to reach k, stop!
	// (This is called pruning).
	if (current.size() + (n - start + 1) < k) return;
	
	for (int i = start; i <= n; i++) {
		current.add(i);               // TAKE
		backtrack(i + 1, n, k, current, result); // Move to next item
		current.remove(current.size() - 1); // LEAVE (Undo / Backtrack)
	}
}
```

> 🏷️ **Pattern Recognition:** 
> "Find all subsets", "Choose k elements", "Combination Sum" → **Use a `for` loop starting from `start` index, recurse with `i + 1`.**

---

## 📖 Chapter 2: The VIP Rope — Permutations via Swapping

*"Now,"* the Librarian said, *"arrange all 3 fruits in a line. Order matters!"*

Instead of "Take/Leave", the easiest way to generate permutations is **Swapping**.
1. Fix the first position: swap every element into index 0.
2. Recurse to fix the next position.
3. Swap back (backtrack) to restore the array.

```java
List<List<Integer>> permute(int[] nums) {
	List<List<Integer>> result = new ArrayList<>();
	permuteHelper(0, nums, result);
	return result;
}

void permuteHelper(int index, int[] nums, List<List<Integer>> result) {
	// Base Case: If we fixed every position, we have a permutation!
	if (index == nums.length) {
		List<Integer> current = new ArrayList<>();
		for (int num : nums) current.add(num);
		result.add(current);
		return;
	}
	
	for (int i = index; i < nums.length; i++) {
		swap(nums, index, i);                      // Put nums[i] at the current fixed position
		permuteHelper(index + 1, nums, result);    // Recurse to fix the next position
		swap(nums, index, i);                      // Backtrack (undo the swap)
	}
}

void swap(int[] nums, int i, int j) {
	int temp = nums[i];
	nums[i] = nums[j];
	nums[j] = temp;
}
```

> 🏷️ **Pattern Recognition:** 
> "Generate all arrangements", "All possible orders" → **Use Swapping technique. Loop from `index` to `nums.length`, recurse with `index + 1`.**

---

## 📖 Chapter 3: The Phantom Books — Handling Duplicates

The Librarian slid a dusty array across the table: `[1, 1, 2]`. *"Generate unique permutations."*

If Alex ran the previous code, he'd get duplicate permutations because the two `1`s are treated as distinct. 
**The Trick:** Sort the array! If the current element is the same as the previous one, AND the previous one wasn't used in this cycle, **skip it**.

To do this, we use a `used[]` boolean array instead of swapping.

```java
List<List<Integer>> permuteUnique(int[] nums) {
	List<List<Integer>> result = new ArrayList<>();
	Arrays.sort(nums); // MUST SORT FIRST!
	boolean[] used = new boolean[nums.length];
	backtrackUnique(nums, used, new ArrayList<>(), result);
	return result;
}

void backtrackUnique(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
	if (current.size() == nums.length) {
		result.add(new ArrayList<>(current));
		return;
	}
	
	for (int i = 0; i < nums.length; i++) {
		// If already used in this branch, skip
		if (used[i]) continue;
		
		// THE MAGIC SKIP: If this element is same as previous, 
		// and previous was NOT used in this branch, skip to avoid duplicates.
		if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
		
		used[i] = true;
		current.add(nums[i]);
		backtrackUnique(nums, used, current, result);
		current.remove(current.size() - 1);
		used[i] = false;
	}
}
```
*(This exact same skip logic `if (i > 0 && nums[i] == nums[i-1] && !used[i-1])` applies to Combination Sum II and Subset II! Memorize it!)*

---

## 📖 Chapter 4: The Endless Coin Fountain — Combinations with Replacement

*"What if you can pick the same fruit multiple times?"* asked the Librarian. (This is the classic **Combination Sum** problem).

*Example:* Target = 7, Array = `[2, 3, 6, 7]`. Output = `[[2,2,3], [7]]`.

**The Trick:** When you recurse, don't do `i + 1`. Do `i`! You stay on the same index because you can reuse the item.

```java
List<List<Integer>> combinationSum(int[] candidates, int target) {
	List<List<Integer>> result = new ArrayList<>();
	backtrackSum(candidates, target, 0, new ArrayList<>(), result);
	return result;
}

void backtrackSum(int[] candidates, int target, int start, List<Integer> current, List<List<Integer>> result) {
	if (target == 0) { // We hit the exact sum!
		result.add(new ArrayList<>(current));
		return;
	}
	if (target < 0) { // We overshot
		return;
	}
	
	for (int i = start; i < candidates.length; i++) {
		current.add(candidates[i]);
		// Notice we pass 'i' not 'i + 1' because we can reuse the same element!
		backtrackSum(candidates, target - candidates[i], i, current, result); 
		current.remove(current.size() - 1);
	}
}
```

---

## 📖 Chapter 5: The Phone Keypad — String Permutations

Alex's phone buzzed. An old Nokia appeared. *"Given a string of digits like '23', generate all possible letter combinations (like T9 predictive text)."*

This is a **Multi-Array Permutation**. You aren't picking from one array; you are picking one letter from Digit 2's array, then one from Digit 3's array.

```java
List<String> letterCombinations(String digits) {
	List<String> result = new ArrayList<>();
	if (digits.length() == 0) return result;
	
	String[] mapping = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
	backtrackPhone(digits, 0, mapping, new StringBuilder(), result);
	return result;
}

void backtrackPhone(String digits, int index, String[] mapping, StringBuilder current, List<String> result) {
	// Base case: If we picked a letter for every digit, we're done!
	if (index == digits.length()) {
		result.add(current.toString());
		return;
	}
	
	String letters = mapping[digits.charAt(index) - '0'];
	for (char c : letters.toCharArray()) {
		current.append(c);
		backtrackPhone(digits, index + 1, mapping, current, result);
		current.deleteCharAt(current.length() - 1); // Backtrack
	}
}
```

---

## 📖 Chapter 6: The Next in Line — Next Permutation (In-Place)

The Librarian threw a curveball. *"I don't want ALL permutations. I have one permutation: `[1, 2, 3]`. Give me the very NEXT lexicographical permutation: `[1, 3, 2]`."*

This is a famous interview question. There is a math trick to do it in `O(N)` time without generating anything!

**The 3-Step Algorithm:**
1. **Find the Dip:** Scan from right to left. Find the first element smaller than its right neighbor. Call it `i`. (If no dip, the array is strictly descending, so reverse the whole thing).
2. **Find the Swap:** Scan from right to left. Find the first element larger than `nums[i]`. Call it `j`. Swap `i` and `j`.
3. **Reverse the Tail:** Reverse everything from `i + 1` to the end.

```java
void nextPermutation(int[] nums) {
	int n = nums.length;
	int i = n - 2;
	
	// Step 1: Find the dip
	while (i >= 0 && nums[i] >= nums[i + 1]) i--;
	
	if (i >= 0) {
		// Step 2: Find the swap point
		int j = n - 1;
		while (nums[j] <= nums[i]) j--;
		swap(nums, i, j);
	}
	
	// Step 3: Reverse the tail (if i = -1, this reverses the whole array)
	reverse(nums, i + 1, n - 1);
}

void reverse(int[] nums, int left, int right) {
	while (left < right) {
		swap(nums, left, right);
		left++; right--;
	}
}
```

---

## 📖 Chapter 7: The Mathematician’s Shortcut — Counting without Generating

*"Generating arrays takes memory,"* the Librarian warned. *"Sometimes I just ask: 'How many combinations are there?' You must calculate it using Math, not loops."*

### 🧮 Pascal’s Triangle (Dynamic Programming for nCr)
To find "n choose r" (e.g., 5 choose 2), use the formula: `C(n, r) = C(n-1, r-1) + C(n-1, r)`.

```java
int nCr(int n, int r) {
	int[][] dp = new int[n + 1][r + 1];
	
	for (int i = 0; i <= n; i++) {
		for (int j = 0; j <= Math.min(i, r); j++) {
			if (j == 0 || j == i) dp[i][j] = 1;
			else dp[i][j] = dp[i - 1][j - 1] + dp[i - 1][j];
		}
	}
	return dp[n][r];
}
```
*(Fun fact: The math formula for unique grid paths in an M x N grid is simply `(M+N-2) choose (M-1)`!)*

---

## 📖 Final Chapter: The Librarian’s Master Index

Alex closed the final book. The scrolls floated gently back to their shelves. The Librarian handed him an index card.

*"Keep this with you. When a problem asks you to arrange, select, or group, consult this index."*

### 🗺️ The Grand P&C Pattern Recognition Table

| Problem Shape | Key Signal | Technique / Code Pattern |
|---|---|---|
| **All Subsets / Combinations** | "Find all subsets", "Choose K" | `for` loop from `start` to `n`. Recurse with `i + 1`. |
| **All Permutations** | "All arrangements", "Rearrange" | `for` loop from `0` to `n`. Swap `index` and `i`. Recurse `index + 1`. |
| **Combination Sum (Reuse)** | "Target sum, reuse elements" | `for` loop from `start` to `n`. Recurse with `i` (not `i+1`). |
| **Unique Subsets/Permutations** | Array has duplicates | Sort array. Skip if `nums[i] == nums[i-1]` (and prev not used). |
| **String Permutations** | Phone keypads, Scrabble tiles | Track an `index`. Iterate over a mapping array. |
| **Next Permutation** | "Next lexicographical order" | Find dip, find swap, reverse tail. `O(N)` in-place. |
| **Counting P&C** | "How many ways?" (No array generation) | Math: Pascal's Triangle (DP) or Factorials. |

### 🧠 The Arranger's Final Checklist

1. **Does order matter?** (If no → Combinations. If yes → Permutations).
2. **Can items be reused?** (If yes → recurse with `i`. If no → recurse with `i + 1`).
3. **Are there duplicates in the input?** (If yes → Sort + Skip logic).
4. **Do I need to generate the arrays, or just count them?** (If just count → Use Math/DP, don't backtrack!).
5. **Am I copying the list correctly?** (Always do `result.add(new ArrayList<>(current))`, never `result.add(current)` or you'll just add empty lists!).

### 🏆 Practice Quests — From Apprentice to Puzzle Master

**Level 1 (Apprentice):**
- Subsets, Permutations, Letter Combinations of a Phone Number

**Level 2 (Squire):**
- Combinations (Pick K), Combination Sum, Generate Parentheses

**Level 3 (Knight):**
- Subsets II, Permutations II, Combination Sum II, Combination Sum III

**Level 4 (Champion):**
- Next Permutation, Permutation Sequence (K-th Permutation), Palindrome Partitioning

**Level 5 (Grandmaster):**
- N-Queens (Permutations on a board), Word Search II (Backtracking + Trie), Matchsticks to Square (Partition DP/Backtracking)

---

> *The Librarian smiled warmly. "You've done it, Alex. You can now manipulate space and order. You know when to pick, when to swap, and when to calculate."*
>
> *She faded into the dust of the library, leaving Alex alone with his terminal. He cracked his knuckles, opened his IDE, and prepared to generate the infinite.* 

**The Arrangement Heist is Complete.** 🎭
