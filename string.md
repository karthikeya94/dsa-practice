# 🔤 The Lexicon Cipher: Advanced String Algorithms 🔤
### *A Hero’s Journey from `charAt()` to Pattern Matching Master*

---

> *Alex entered the Hall of Whispers. The walls were covered in billions of glowing letters, shifting and rearranging. A blind sage named **Scribe Lexicon** sat in the center, weaving threads of light.*
>
> *"You have mastered Arrays, Trees, and Graphs, Alex,"* the Scribe said. "But you still treat strings like simple arrays of characters. You use `.substring()` and `.indexOf()`, blindly scanning from left to right. In the real world, a single string can be a billion characters long. If you search for a word inside it using nested loops, the sun will burn out before your code finishes."*
>
> *He handed Alex a glowing needle. "To find a pattern in a sea of text, you must stop re-reading what you have already read. Let me teach you the ancient arts of **Pattern Matching**."*

---

## 📖 Chapter 0: The Naive Trap (Why we need advanced algorithms)

Alex's first instinct to find a pattern `P` in a text `T` was the "Naive Search":
Look at `T[0]`, compare with `P`. If it fails, look at `T[1]`, compare with `P` from the start.

*Text:* `"ABABABABAA"`
*Pattern:* `"ABABAA"`

When Alex hit the final `A` of the pattern and it didn't match, he threw away all his progress and started over from `T[1]`. 
*"Wasteful!"* the Scribe hissed. "You already read those letters! You should know exactly how far to jump forward. Let me show you the true arts."

---

## 📖 Chapter 1: The Rolling Stone (Rabin-Karp Algorithm)

*"Instead of comparing letters,"* the Scribe said, *"compare numbers. Turn the string into a hash!"*

**The Mental Model:**
If your pattern is `"abc"`, its hash might be `1 + 2 + 3 = 6`.
Now, slide a window across the text. `"bcd"` -> `2 + 3 + 4 = 9`. 
Did you recalculate the hash from scratch? No! You subtracted the leaving letter (`1`), and added the entering letter (`4`). This is a **Rolling Hash**.

If the hash of the window matches the hash of the pattern, *then* you check the letters to be sure (to avoid Hash Collisions).

```java
// Rabin-Karp: O(N + M) average, O(N * M) worst-case (due to collisions)
void rabinKarp(String text, String pattern) {
	int n = text.length(), m = pattern.length();
	if (m > n) return;
	
	long patternHash = 0, windowHash = 0;
	long pow = 1; // To remove the leftmost character
	int base = 256; // Number of characters (ASCII)
	long MOD = (long) 1e9 + 7; // Prevent integer overflow
	
	// Compute initial hashes and the highest power
	for (int i = 0; i < m; i++) {
		patternHash = (patternHash * base + pattern.charAt(i)) % MOD;
		windowHash = (windowHash * base + text.charAt(i)) % MOD;
		if (i < m - 1) pow = (pow * base) % MOD;
	}
	
	for (int i = 0; i <= n - m; i++) {
		// If hashes match, verify characters
		if (patternHash == windowHash) {
			if (text.substring(i, i + m).equals(pattern)) {
				System.out.println("Pattern found at index " + i);
			}
		}
		// Compute hash for next window
		if (i < n - m) {
			// Remove leftmost char, add new rightmost char
			windowHash = (windowHash - text.charAt(i) * pow % MOD + MOD) % MOD;
			windowHash = (windowHash * base + text.charAt(i + m)) % MOD;
		}
	}
}
```
> 🏷️ **Pattern Recognition:** Find multiple patterns in text, Plagiarism detection. The key is the math trick of `Hash = (Hash - old_char * pow) * base + new_char`.

---

## 📖 Chapter 2: The Memory of Failures (KMP Algorithm)

The Scribe drew a massive string on the board: `T = "AAAAABAA"`, `P = "AAAAB"`.
Alex noticed that if he fails at the 5th `A` in the pattern, he shouldn't go all the way back. He already has 4 `A`s matched!

**The Mental Model (The LPS Array):**
KMP (Knuth-Morris-Pratt) builds an array called **LPS (Longest Prefix Suffix)** for the pattern.
LPS tells you: *"If you fail at index `j` of the pattern, how many characters from the beginning can you safely keep without re-checking?"*

*Pattern:* `"ABABD"`
*LPS:* `[0, 0, 1, 2, 0]`
*Meaning:* If I fail at `D` (index 4), my LPS at the previous step is `2`. I can safely jump my pattern forward, knowing `"AB"` is already matched!

```java
int strStr(String haystack, String needle) {
	if (needle.isEmpty()) return 0;
	int[] lps = computeLPS(needle);
	
	int i = 0; // pointer for haystack
	int j = 0; // pointer for needle
	
	while (i < haystack.length()) {
		if (haystack.charAt(i) == needle.charAt(j)) {
			i++; j++;
			if (j == needle.length()) return i - j; // Found it!
		} else {
			if (j > 0) {
				j = lps[j - 1]; // THE MAGIC: Don't move i! Just jump j!
			} else {
				i++; // No match at all, move i
			}
		}
	}
	return -1;
}

// Building the LPS array
int[] computeLPS(String p) {
	int[] lps = new int[p.length()];
	int len = 0; // Length of the previous longest prefix suffix
	int i = 1;
	
	while (i < p.length()) {
		if (p.charAt(i) == p.charAt(len)) {
			len++;
			lps[i] = len;
			i++;
		} else {
			if (len > 0) {
				len = lps[len - 1]; // Fall back
			} else {
				lps[i] = 0;
				i++;
			}
		}
	}
	return lps;
}
```
> 🧠 **KMP guarantees `O(N + M)` time.** The text pointer `i` **never moves backwards**. It only goes forward. The pattern pointer `j` jumps back using LPS.

---

## 📖 Chapter 3: The Doppelgänger (Z-Algorithm)

*"There is another way to remember,"* the Scribe said. He merged the pattern and the text together with a magic divider.
`S = Pattern + "$" + Text`
Example: `"aab$baabxaab"`

He then calculated the **Z-Array**.
`Z[i]` = The length of the longest substring starting from `S[i]` that perfectly matches the prefix of `S`.
If `Z[i]` equals the length of the pattern, you found a match!

```java
int[] computeZArray(String s) {
	int n = s.length();
	int[] Z = new int[n];
	int left = 0, right = 0;
	
	for (int i = 1; i < n; i++) {
		if (i > right) {
			// Outside the Z-box, calculate manually
			left = right = i;
			while (right < n && s.charAt(right - left) == s.charAt(right)) right++;
			Z[i] = right - left;
			right--;
		} else {
			// Inside the Z-box! Copy from the mirror position
			int mirror = i - left;
			if (Z[mirror] < right - i + 1) {
				Z[i] = Z[mirror]; // Safe to copy entirely
			} else {
				// Have to extend manually
				left = i;
				while (right < n && s.charAt(right - left) == s.charAt(right)) right++;
				Z[i] = right - left;
				right--;
			}
		}
	}
	return Z;
}
```
> 🏷️ **Pattern Recognition:** Z-Algorithm is heavily used in genome sequencing (DNA matching), finding string periods, and pattern matching.

---

## 📖 Chapter 4: The Mirror Maze (Manacher’s Algorithm)

*"Find the longest palindromic substring in `O(N)` time."*

Alex knew how to do this in `O(N^2)` (expand around center). But Manacher’s does it in `O(N)`.
**The Mental Model:**
A palindrome is symmetrical. If you have a massive palindrome centered at `C`, and you are looking at a point `i` inside it, the palindrome at `i` is *at least* as big as the palindrome at the mirror point on the left side!

To handle even-length palindromes, Manacher's transforms `"aba"` into `"#a#b#a#"`.

```java
String longestPalindrome(String s) {
	// Transform S into T.
	// For example, S = "abba", T = "^#a#b#b#a#$"
	// ^ and $ signs are sentinels appended to each end to avoid bounds checking
	StringBuilder T = new StringBuilder("^#");
	for (char c : s.toCharArray()) {
		T.append(c).append("#");
	}
	T.append("$");
	
	int n = T.length();
	int[] P = new int[n]; // P[i] stores the radius of the palindrome centered at i
	int C = 0, R = 0;    // Center and Right boundary of the current largest palindrome
	
	for (int i = 1; i < n - 1; i++) {
		int mirror = 2 * C - i; // Mirror of i around center C
		
		// If i is within the right boundary, copy from mirror!
		if (R > i) {
			P[i] = Math.min(R - i, P[mirror]);
		}
		
		// Attempt to expand palindrome centered at i
		while (T.charAt(i + 1 + P[i]) == T.charAt(i - 1 - P[i])) {
			P[i]++;
		}
		
		// If the expanded palindrome goes past R, adjust center C and R
		if (i + P[i] > R) {
			C = i;
			R = i + P[i];
		}
	}
	
	// Find the maximum element in P
	int maxLen = 0, centerIndex = 0;
	for (int i = 1; i < n - 1; i++) {
		if (P[i] > maxLen) {
			maxLen = P[i];
			centerIndex = i;
		}
	}
	
	// Extract the palindrome from original string
	int start = (centerIndex - maxLen) / 2;
	return s.substring(start, start + maxLen);
}
```

---

## 📖 Chapter 5: The Word Web (Trie - Prefix Tree)

*"What if I don't need to find one word, but I need to check if a string starts with a prefix, or autocomplete a word?"*
For this, arrays are too slow. We need a Tree made of letters.

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
	
	public boolean startsWith(String prefix) {
		TrieNode node = root;
		for (char c : prefix.toCharArray()) {
			int i = c - 'a';
			if (node.children[i] == null) return false;
			node = node.children[i];
		}
		return true;
	}
}
```
> 🏷️ **Pattern Recognition:** "Autocomplete", "Word search", "Replace words". A Trie takes `O(L)` time to search/insert, where L is the length of the word. It doesn't depend on how many words are in the dictionary!

---

## 📖 Final Chapter: The Scribe’s Blueprint

Scribe Lexicon faded into the letters. Alex stood alone in the Hall of Whispers. He no longer saw strings as simple sentences; he saw hashes, symmetrical mirrors, and prefix trees.

### 🗺️ The Grand Advanced String Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Find a pattern in text** | "Find needle in haystack", "Substring search" | **KMP** (LPS Array) or **Rabin-Karp** (Rolling Hash). |
| **Multiple pattern search** | "Find all occurrences of pattern" | **Z-Algorithm** or **Rabin-Karp**. |
| **Longest Palindrome Substring** | "Longest palindrome in string" (`O(N)` needed) | **Manacher's Algorithm** (Mirror property). |
| **Word Autocomplete / Prefix search** | "Starts with", "Replace words", "Dictionary" | **Trie** (Prefix Tree). |
| **String matching with wildcards** | "Match `*` and `?`" | **DP** or **Two Pointers**. |
| **Anagram of pattern in text** | "Find all anagrams in a string" | **Sliding Window + Frequency Array**. |

### 🧠 The Lexicographer's Final Checklist

1. **Do I need to search a text repeatedly?** (If yes, consider KMP or Z-Array to avoid `O(N*M)`).
2. **Am I doing repeated string hashing?** (Use a Rolling Hash to make it `O(1)` per window).
3. **Am I finding palindromes and it's too slow?** (Use Manacher's. Transform string with `#` and use the mirror property).
4. **Am I searching a dictionary repeatedly?** (Build a Trie once, then every search is `O(L)`).
5. **Am I modifying strings constantly?** (Use `StringBuilder`! `String` in Java is immutable; `s += "a"` creates a whole new object).

### 🏆 Practice Quests — From Scribe to Lexicographer

**Level 1 (Apprentice):**
- Implement strStr() (Naive is okay here to learn), Is Subsequence, Valid Palindrome.

**Level 2 (Squire):**
- Implement Trie (Prefix Tree), Longest Common Prefix, Valid Anagram.

**Level 3 (Knight):**
- Longest Palindromic Substring (Expand Around Center), Find All Anagrams in a String, Group Shifted Strings.

**Level 4 (Champion):**
- Implement strStr() using KMP, Rabin-Karp Fingerprint search, Longest Happy Prefix (KMP LPS Array), Replace Words (Trie).

**Level 5 (Grandmaster):**
- Longest Palindromic Substring (Manacher's Algorithm), Word Search II (Trie + Backtracking), Shortest Palindrome (KMP), Minimum Window Substring.

---

> *Alex closed the book of whispers. The strings settled into place. He realized that strings weren't just text—they were arrays of numbers, trees of letters, and mirrors of symmetry. To master strings was to master the very language of the machine.*

**The Lexicon Cipher Heist is Complete.** 🔤
