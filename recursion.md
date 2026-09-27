# 🪞 The Mirror Dimension: Recursion & Backtracking 🪞
### *A Hero’s Journey from Confused Teen to Master of the Infinite Loop*

---

> *Alex stood before the Temple of Echoes. The door had no handle, only a sign:*
> **"To enter, you must ask the door to open the door."**
>
> *Alex knocked. The door knocked back. Suddenly, a figure stepped out of the wall itself. It was Master Ouroboros, a man made entirely of shimmering glass.*
>
> *"You know DP, Graphs, and Bits,"* Ouroboros whispered. *"But you are forcing your way through problems. To truly master DSA, you must learn to step outside of time, look at a problem, and ask: 'What if I do one step, and let my clone handle the rest?'"*
>
> *Alex blinked. "A clone?"*
>
> *"We call it Recursion,"* the Master smiled. "And when you realize you made a mistake and need to undo your clone's work? That is Backtracking. Let me show you the Mirror Dimension."*

---

## 📖 Chapter 0: The Mind Bender — What is Recursion?

Recursion is **a function calling itself**. But mentally, it’s much more.

Imagine you are standing in a long line at a food truck. You want to know how many people are in front of you.
You can't see the front. So you ask the person in front of you: *"How many people are in front of you?"*
They ask the person in front of them. This continues until someone is at the very front (0 people in front). 
Then, the answers ripple all the way back: 0 → 1 → 2 → 3...

### 🧠 The 2 Pillars of Recursion
Every recursive function MUST have these, or it will loop forever (Stack Overflow):
1. **Base Case:** When do I stop? (The person at the front of the line).
2. **Recursive Step:** How do I make the problem smaller? (Asking the person in front of me).

---

## 📖 Chapter 1: The Trust Fall — The Recursive Mental Model

The biggest mistake 15-year-olds make with recursion is trying to trace the *entire* call stack in their head. **Stop doing that!** It will break your brain.

Instead, use the **"Clone Delegation"** mental model.

You are a manager. You get a problem: "Find the sum of an array."
You say: *"I'll take the first number. I will create a clone, hand him the REST of the array, and trust he gives me the right answer. I'll just add my number to his answer."*

```java
// Sum of array [1, 2, 3, 4]
int sumArray(int[] arr, int index) {
	// 1. Base Case: If I'm past the end of the array, sum is 0.
	if (index == arr.length) return 0;
	
	// 2. Recursive Step: Take arr[index], TRUST the clone to sum the rest.
	return arr[index] + sumArray(arr, index + 1);
}
```
*Ouroboros nodded. "You don't need to know how the clone does it. You just need to know: IF the clone does his job right, does my math work out? Yes. That is the Leap of Faith."*

---

## 📖 Chapter 2: The Two Trees — Types of Recursion

### 🌱 1. Linear Recursion (The Single Path)
The function calls itself once. (Like the `sumArray` above).
Example: Factorial `n! = n * (n-1)!`

### 🌳 2. Tree Recursion (The Forking Paths)
The function calls itself *multiple* times. This creates a tree of calls.
Example: Fibonacci `fib(n) = fib(n-1) + fib(n-2)`.

```java
int fib(int n) {
	if (n <= 1) return n;      // Base
	return fib(n - 1) + fib(n - 2); // Two clones! One does n-1, one does n-2.
}
```
*Warning:* Without Memoization (DP!), Tree Recursion repeats work. `fib(5)` calculates `fib(2)` three times!

---

## 📖 Chapter 3: The Explorer’s Chalk — What is Backtracking?

*Ouroboros drew a maze on the glass floor.*
*"Recursion is walking into the maze. Backtracking is leaving a chalk mark at every fork so you know which paths you've already tried, and if you hit a dead end, you turn around (backtrack) and try another path."*

Backtracking is just **Recursion + Undoing Choices**.

### 🎒 The Backpacker's Template (The Holy Grail)
Memorize this 4-step template. It solves 90% of all backtracking problems on Earth.

```java
void solve(Parameters) {
	// 1. BASE CASE: Did we reach the end / find a valid answer?
	if (isSolution) {
		saveAnswer();
		return;
	}
	
	// 2. CHOICES: What are the options at this step?
	for (Option option : availableOptions) {
		
		// 3. TAKE: Make a choice (Modify state)
		state.add(option);
		
		// 4. EXPLORE: Recurse to the next step
		solve(nextParameters);
		
		// 5. UNDO: Backtrack (Remove the choice to try the next one!)
		state.remove(option); 
	}
}
```

---

## 📖 Chapter 4: The Rat in the Maze (Grid Backtracking)

The Master placed a rat at the top-left of a grid. It needs to reach the bottom-right. It can only move Right (`R`) or Down (`D`). Some cells are blocked (`0`), open ones are `1`.

Let's apply the template!

```java
List<String> findPaths(int[][] grid) {
	List<String> paths = new ArrayList<>();
	// Start at (0,0) with an empty path
	backtrack(grid, 0, 0, "", paths);
	return paths;
}

void backtrack(int[][] grid, int r, int c, String path, List<String> paths) {
	int n = grid.length, m = grid[0].length;
	
	// 1. BASE CASES (Out of bounds, Blocked, or Reached Destination)
	if (r < 0 || c < 0 || r >= n || c >= m || grid[r][c] == 0) return;
	if (r == n - 1 && c == m - 1) {
		paths.add(path);
		return;
	}
	
	// Mark visited so we don't go in circles! (Modify state)
	grid[r][c] = 0; 
	
	// 2. CHOICES & 3. TAKE & 4. EXPLORE & 5. UNDO
	backtrack(grid, r + 1, c, path + "D", paths); // Down
	backtrack(grid, r, c + 1, path + "R", paths); // Right
	
	// Backtrack: Unmark the cell so other paths can use it!
	grid[r][c] = 1; 
}
```
> 🧠 **Mental Model Check:** Why do we set `grid[r][c] = 0` and then `= 1`? Because when the "Down" clone finishes his journey, we need the grid to look brand new for the "Right" clone! We must clean up our mess.

---

## 📖 Chapter 5: The Knight’s Tour (Constraint Backtracking)

A chess knight must visit every square on an 8x8 board exactly once.
This is where backtracking shines. We try a move, recurse. If we get stuck (no valid moves left and we haven't visited all 64 squares), we **backtrack** and undo that move.

```java
boolean solveKnightTour(int[][] board) {
	// 8 possible moves of a knight (L-shapes)
	int[] xMove = {2, 1, -1, -2, -2, -1, 1, 2};
	int[] yMove = {1, 2, 2, 1, -1, -2, -2, -1};
	
	board[0][0] = 0; // Start at top-left, mark as visited (step 0)
	
	return knightBacktrack(board, 0, 0, 1, xMove, yMove);
}

boolean knightBacktrack(int[][] board, int r, int c, int moveCount, int[] xMove, int[] yMove) {
	// 1. BASE CASE: Visited all 64 squares!
	if (moveCount == 64) return true;
	
	// 2. CHOICES: 8 possible knight moves
	for (int i = 0; i < 8; i++) {
		int nextR = r + xMove[i];
		int nextC = c + yMove[i];
		
		if (isSafe(board, nextR, nextC)) {
			// 3. TAKE
			board[nextR][nextC] = moveCount; 
			
			// 4. EXPLORE
			if (knightBacktrack(board, nextR, nextC, moveCount + 1, xMove, yMove)) {
				return true; // Found a solution! Stop searching.
			}
			
			// 5. UNDO (Backtrack)
			board[nextR][nextC] = -1; // Unmark the square
		}
	}
	return false; // Dead end, trigger backtracking
}

boolean isSafe(int[][] board, int r, int c) {
	return r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == -1;
}
```
> 🏷️ **Pattern Recognition:** If a problem asks "Find a path", "Solve the puzzle", "Is it possible?", you can return `true` early. If it asks "Find ALL paths", you must explore everything and not return early.

---

## 📖 Chapter 6: The Word Hunter — DFS + Backtracking

*"I have a grid of letters,"* the Master said. *"Find the word 'HELLO'."*

This combines Graph DFS with Backtracking. We explore neighbors, and if the path doesn't spell the word, we back up.

```java
boolean exist(char[][] board, String word) {
	for (int r = 0; r < board.length; r++) {
		for (int c = 0; c < board[0].length; c++) {
			if (board[r][c] == word.charAt(0)) {
				if (backtrackWord(board, word, r, c, 0)) return true;
			}
		}
	}
	return false;
}

boolean backtrackWord(char[][] board, String word, int r, int c, int index) {
	// 1. BASE CASES
	if (index == word.length()) return true; // Found it!
	if (r < 0 || c < 0 || r >= board.length || c >= board[0].length) return false;
	if (board[r][c] != word.charAt(index)) return false; // Mismatch
	
	// 3. TAKE: Mark the cell so we don't reuse it in the same word
	char temp = board[r][c];
	board[r][c] = '#'; 
	
	// 4. EXPLORE 4 directions
	boolean found = backtrackWord(board, word, r + 1, c, index + 1) ||
	backtrackWord(board, word, r - 1, c, index + 1) ||
	backtrackWord(board, word, r, c + 1, index + 1) ||
	backtrackWord(board, word, r, c - 1, index + 1);
	
	// 5. UNDO: Restore the letter for other search paths!
	board[r][c] = temp; 
	
	return found;
}
```

---

## 📖 Chapter 7: The Queen’s Gambit — N-Queens

The ultimate test of a Backtracking Master. Place `N` queens on an `N x N` chessboard so no two attack each other.

**The Trick:** We place queens row by row. For each row, we try placing a queen in every column. If it's safe, we place it and move to the next row. If we reach a row where no column is safe, we backtrack!

```java
List<List<String>> solveNQueens(int n) {
	List<List<String>> results = new ArrayList<>();
	char[][] board = new char[n][n];
	for (int i = 0; i < n; i++) Arrays.fill(board[i], '.');
	
	placeQueens(board, 0, results);
	return results;
}

void placeQueens(char[][] board, int row, List<List<String>> results) {
	// 1. BASE CASE: Placed queens in all rows!
	if (row == board.length) {
		results.add(constructBoard(board));
		return;
	}
	
	// 2. CHOICES: Try every column in the current row
	for (int col = 0; col < board.length; col++) {
		if (isSafe(board, row, col)) {
			// 3. TAKE
			board[row][col] = 'Q';
			// 4. EXPLORE (Move to next row)
			placeQueens(board, row + 1, results);
			// 5. UNDO
			board[row][col] = '.';
		}
	}
}

boolean isSafe(char[][] board, int row, int col) {
	// Check straight up
	for (int i = 0; i < row; i++) if (board[i][col] == 'Q') return false;
	
	// Check diagonal up-left
	for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) 
		if (board[i][j] == 'Q') return false;
		
		// Check diagonal up-right
		for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) 
			if (board[i][j] == 'Q') return false;
			
			return true;
}
```

---

## 📖 Chapter 8: Pruning the Timelines (Optimization)

*Ouroboros shattered a piece of glass.* *"If you explore every timeline, you will die of old age before finding the answer. A Master knows when to cut a timeline short. This is called **Pruning**."*

Imagine you are generating subsets that sum to a `target`. 
If your current sum is `50`, the target is `10`, and you still have 10 more numbers to add... **STOP**. Don't recurse further. Undo immediately!

```java
void backtrackSum(int[] nums, int index, int currentSum, int target, List<Integer> path) {
	// PRUNING: If currentSum > target, this timeline is useless.
	if (currentSum > target) return; 
	
	// BASE CASE
	if (currentSum == target) {
		// save path
		return;
	}
	
	for (int i = index; i < nums.length; i++) {
		// take, explore, undo...
	}
}
```
Pruning is what separates an algorithm that runs in 10 seconds from one that runs in 0.01 seconds.

---

## 📖 Final Chapter: The Master’s Mind Map

*Ouroboros faded, leaving Alex alone in the Mirror Dimension. Alex looked down and saw a tattoo on his arm—a map of recursive rules.*

### 🗺️ The Grand Recursion & Backtracking Cheat Sheet

| Problem Shape | Key Signal | Technique / Pattern |
|---|---|---|
| **Basic Counting / Math** | Factorial, Fibonacci, Powers | Linear Recursion. Base case + 1 recursive call. |
| **Generate All Subsets** | "Find all combinations" | Loop `0` to `n`, Take `i`, Recurse `i+1`, Un-take `i`. |
| **Generate Permutations** | "All arrangements" | Loop `0` to `n`, Swap, Recurse `index+1`, Swap back. |
| **Grid Pathfinding** | "Find path in maze", "Word Search" | DFS + Backtrack. Mark cell visited (`#`), explore 4 dirs, unmark. |
| **Constraint Satisfaction** | N-Queens, Sudoku, Coloring | Loop through choices, check `isSafe()`, take, recurse, undo. |
| **Count all paths** | "How many ways to do X" | Backtracking, but just increment a counter instead of saving paths. |

### 🧠 The Mirror Master’s Final Checklist

Before writing any recursive code, ask yourself:
1. **What is the Base Case?** (When do I stop?)
2. **What is the Smaller Subproblem?** (How do I make the input smaller?)
3. **Do I need to make a choice?** (If yes, loop over the choices).
4. **What state am I modifying?** (An array? A grid? A sum?)
5. **Am I undoing my choice?** (If you `add()`, you must `remove()`. If you mark `visited`, you must mark `unvisited`).
6. **Can I prune?** (If the state is already invalid, `return` immediately).

### 🏆 Practice Quests — From Apprentice to Mirror Master

**Level 1 (Apprentice - Build the Faith):**
- Fibonacci, Factorial, Power of Three, Reverse a String.

**Level 2 (Squire - Basic Backtracking):**
- Subsets, Permutations, Combinations, Letter Combinations of a Phone Number.

**Level 3 (Knight - Grids & Words):**
- Word Search, Rat in a Maze, Unique Paths III, Flood Fill.

**Level 4 (Champion - Constraints):**
- N-Queens, Sudoku Solver, Generate Parentheses, Palindrome Partitioning.

**Level 5 (Grandmaster - Optimization & Pruning):**
- Matchsticks to Square, Word Search II (Backtracking + Trie), Crossword Puzzle Solver.

---

> *Alex looked at the glass walls. They were no longer mirrors. They were doorways. He realized that Recursion wasn't just a coding trick; it was a way of thinking. Break the problem down. Trust the smaller steps. Clean up your mess. The infinite loop was no longer a trap—it was a tool.*
>
> *He stepped out of the Temple of Echoes, ready to face any problem, knowing he could always just ask his clones for help.*

**The Mirror Dimension is Mastered.** 🪞
