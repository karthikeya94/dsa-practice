# 💰 The Merchant's Bazaar: Greedy Algorithms 💰
### *A Hero’s Journey from Overthinking to Instant Action*

---

> *Alex walked into the Grand Bazaar of Algorithmia. It was chaotic. Merchants were shouting, trading gold, silk, and spices. A fast-talking merchant with a gleaming eye patch named **Broker Midas** pulled Alex aside.*
>
> *"I hear you're the DP master,"* Midas smirked. *"You spend all day calculating every possible path. Too slow, kid! In the Bazaar, opportunities vanish in seconds. You don't have time to build a 2D memoization table. You see a gold coin on the ground? You grab it. You don't check if grabbing it ruins your future. That's the art of **Greedy Algorithms**."*
>
> *Alex frowned. "But what if grabbing the coin now means you miss out on a treasure chest later?"*
> 
> *Midas laughed. "Ah, the classic trap! Greedy isn't always right. But when the bazaar is structured right, the local best choice *is* the global best choice. Let me teach you when to trust your gut."*

---

## 📖 Chapter 0: The Core Philosophy — What is Greedy?

Dynamic Programming tries **all** choices and picks the best.
Greedy makes **one** choice, commits to it, and never looks back.

### ⚠️ The Golden Rule of Greedy
A greedy algorithm only works if the problem has the **Greedy Choice Property** (making a local optimum choice leads to a global optimum) and **Optimal Substructure**.

> 🧠 **How to know if you should use Greedy?**
> 1. **Sorting** or **Heaps** are almost always involved.
> 2. The problem asks to "minimize the maximum", "maximize the count", or "find the minimum time".
> 3. If you ever think: *"Can I just sort this and pick the best one right now?"* — it's usually Greedy.

---

## 📖 Chapter 1: The Meeting Rooms (Interval Scheduling)

Midas pointed to a board of meeting times: `[[0, 30], [5, 10], [15, 20]]`. 
*"You have one conference room. How many meetings can you fit?"*

Alex thought: "Do I sort by start time? Or by duration?"
*"Neither!"* Midas barked. *"Sort by **END TIME**! The meeting that finishes earliest leaves the most room for the rest."*

```java
int maxMeetings(int[][] intervals) {
	// Sort by end time
	Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
	
	int count = 0;
	int lastEndTime = Integer.MIN_VALUE;
	
	for (int[] interval : intervals) {
		int start = interval[0];
		int end = interval[1];
		
		// If this meeting starts after the last one ended, we can take it!
		if (start >= lastEndTime) {
			count++;
			lastEndTime = end; // Update the end time
		}
	}
	return count;
}
```
> 🏷️ **Pattern Recognition:** "Non-overlapping intervals", "Meeting rooms", "Maximum number of activities" → **Sort by End Time**.

---

## 📖 Chapter 2: The Stepping Stones (Jump Game)

Midas laid out a line of stones: `[2, 3, 1, 1, 4]`. Each number is exactly how many steps you can jump forward. *"Can you reach the end?"*

Instead of trying every jump from every stone (DP), use the **Greedy Reach**.
Start from the back. Ask: *"Can I reach the end from stone `i`?"*
If `i + nums[i] >= lastPos`, then yes! Update `lastPos = i`. If you make it back to the start, you win!

```java
boolean canJump(int[] nums) {
	int lastPos = nums.length - 1; // Start at the end
	
	for (int i = nums.length - 2; i >= 0; i--) {
		// If from stone 'i', I can jump to or past 'lastPos'
		if (i + nums[i] >= lastPos) {
			lastPos = i; // Move the target closer!
		}
	}
	return lastPos == 0; // Did the target reach the start?
}
```
> 🏷️ **Pattern Recognition:** "Jump Game", "Can you reach the end", "H-index" → **Greedy Reach / Track Maximum Reachable Index**.

---

## 📖 Chapter 3: The Sliced Cake (Fractional Knapsack)

*"You have a backpack that holds 50kg,"* Midas said. "There are three items: Gold (10kg, 60$), Silver (20kg, 100$), Copper (30kg, 120$). Maximize profit!"

In the 0/1 Knapsack (DP), you can't break items. But in the Bazaar, you can slice them!
**The Trick:** Calculate value/weight ratio. Sort by ratio descending. Grab as much as you can!

```java
double fractionalKnapsack(int[] weights, int[] values, int capacity) {
	int n = weights.length;
	double[][] ratio = new double[n][2]; // [index, ratio]
	
	for (int i = 0; i < n; i++) {
		ratio[i][0] = i;
		ratio[i][1] = (double) values[i] / weights[i];
	}
	
	// Sort by ratio descending
	Arrays.sort(ratio, (a, b) -> Double.compare(b[1], a[1]));
	
	double totalValue = 0;
	for (int i = 0; i < n; i++) {
		int idx = (int) ratio[i][0];
		
		if (capacity >= weights[idx]) {
			// Take the whole item
			capacity -= weights[idx];
			totalValue += values[idx];
		} else {
			// Take a fraction!
			totalValue += ratio[i][1] * capacity;
			break; // Backpack is full
		}
	}
	return totalValue;
}
```
> 🏷️ **Pattern Recognition:** "Maximize value with limited capacity", "Can break items" → **Sort by Ratio (Value / Weight)**.

---

## 🏆 Practice Quests — From Peddler to Tycoon

**Level 1 (Apprentice):**
- Assign Cookies, Lemonade Change, Maximum Units on a Truck.

**Level 2 (Squire):**
- Array Partition, Maximum Subarray (Kadane's Greedy), Best Time to Buy and Sell Stock.

**Level 3 (Knight):**
- Jump Game, Non-overlapping Intervals, Minimum Number of Arrows to Burst Balloons.

**Level 4 (Champion):**
- Task Scheduler, Gas Station, Candy (Two-Pass Greedy).

**Level 5 (Grandmaster):**
- Queue Reconstruction by Height, Minimum Number of Taps to Water Garden, Employee Free Time.

---

> *Alex strapped on his merchant's satchel. Greedy wasn't about being reckless. It was about finding the perfect sorting key. Once the data was sorted by the right property, the answer was just a single pass away.*

**The Bazaar Heist is Complete.** 💰
