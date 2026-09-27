package com.demo.algorithms.recursion;

import java.util.*;

public class Recursion {

    // ==========================================
    // 1. Subsets (Combinations without duplicates)
    // ==========================================

    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        
        // Uncomment the approach you want to test
        subsetsBacktracking(nums, 0, cur, ans);
        // subsetsRecursive(nums, 0, cur, ans);
        
        return ans;
    }

    /**
     * Approach 1: Backtracking (Loop based)
     */
    private void subsetsBacktracking(int[] nums, int ind, List<Integer> cur, List<List<Integer>> res) {
        res.add(new ArrayList<>(cur));
        for (int i = ind; i < nums.length; i++) {
            cur.add(nums[i]);
            subsetsBacktracking(nums, i + 1, cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    /**
     * Approach 2: Pick / Not Pick Recursion
     */
    private void subsetsRecursive(int[] nums, int ind, List<Integer> cur, List<List<Integer>> res) {
        if (ind == nums.length) {
            res.add(new ArrayList<>(cur));
            return;
        }
        // Not pick
        subsetsRecursive(nums, ind + 1, cur, res);
        
        // Pick
        cur.add(nums[ind]);
        subsetsRecursive(nums, ind + 1, cur, res);
        cur.remove(cur.size() - 1); // backtrack
    }


    // ==========================================
    // 2. Subsets II (Combinations with duplicates)
    // ==========================================

    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        Arrays.sort(nums); // prerequisite
        
        subsetsWithDupBacktracking(nums, 0, cur, ans);
        // subsetsWithDupRecursive(nums, 0, cur, ans);
        
        return ans;
    }

    /**
     * Approach 1: Backtracking (Loop based with duplicate check)
     */
    private void subsetsWithDupBacktracking(int[] nums, int ind, List<Integer> cur, List<List<Integer>> res) {
        res.add(new ArrayList<>(cur));
        for (int i = ind; i < nums.length; i++) {
            // skip duplicates
            if (i > ind && nums[i] == nums[i - 1]) {
                continue;
            }
            cur.add(nums[i]);
            subsetsWithDupBacktracking(nums, i + 1, cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    /**
     * Approach 2: Pick / Not Pick Recursion (Skip duplicates on not pick)
     */
    private void subsetsWithDupRecursive(int[] nums, int ind, List<Integer> cur, List<List<Integer>> res) {
        if (ind == nums.length) {
            res.add(new ArrayList<>(cur));
            return;
        }
        // Pick
        cur.add(nums[ind]);
        subsetsWithDupRecursive(nums, ind + 1, cur, res);
        cur.remove(cur.size() - 1); // backtrack
        
        // Not pick (skip duplicates)
        while (ind + 1 < nums.length && nums[ind] == nums[ind + 1]) {
            ind++;
        }
        subsetsWithDupRecursive(nums, ind + 1, cur, res);
    }


    // ==========================================
    // 3. Permutations (Without duplicates)
    // ==========================================

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        
        // permuteUsingVisitedArray(nums, new boolean[nums.length], new ArrayList<>(), ans);
        permuteUsingInPlaceSwapping(0, nums, ans);
        
        return ans;
    }

    /**
     * Approach 1: Backtracking with explicit visited array
     */
    private void permuteUsingVisitedArray(int[] nums, boolean[] vis, List<Integer> cur, List<List<Integer>> ans) {
        if (nums.length == cur.size()) {
            ans.add(new ArrayList<>(cur));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (vis[i]) continue;
            
            vis[i] = true;
            cur.add(nums[i]);
            
            permuteUsingVisitedArray(nums, vis, cur, ans);
            
            vis[i] = false;
            cur.remove(cur.size() - 1);
        }
    }

    /**
     * Approach 2: Backtracking with in-place swapping (No extra space)
     */
    private void permuteUsingInPlaceSwapping(int ind, int[] nums, List<List<Integer>> ans) {
        if (ind == nums.length) {
            ans.add(Arrays.stream(nums).boxed().toList());
            return;
        }
        for (int i = ind; i < nums.length; i++) {
            swap(nums, i, ind);
            permuteUsingInPlaceSwapping(ind + 1, nums, ans);
            swap(nums, i, ind); // backtrack
        }
    }


    // ==========================================
    // 4. Permutations II (With duplicates)
    // ==========================================

    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        Arrays.sort(nums); // prerequisite
        
        permuteUniqueUsingVisitedArray(nums, new boolean[nums.length], new ArrayList<>(), ans);
        // permuteUniqueUsingInPlaceSwapping(0, nums, ans);
        
        return ans;
    }

    /**
     * Approach 1: Backtracking with visited array and duplicate condition check
     */
    private void permuteUniqueUsingVisitedArray(int[] nums, boolean[] vis, List<Integer> cur, List<List<Integer>> ans) {
        if (nums.length == cur.size()) {
            ans.add(new ArrayList<>(cur));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            // skip duplicates using previous visited state
            if ((i > 0 && nums[i] == nums[i - 1] && !vis[i - 1]) || vis[i]) {
                continue;
            }
            vis[i] = true;
            cur.add(nums[i]);
            
            permuteUniqueUsingVisitedArray(nums, vis, cur, ans);
            
            vis[i] = false;
            cur.remove(cur.size() - 1);
        }
    }

    /**
     * Approach 2: Backtracking with in-place swapping using a Set for duplicate check at current index
     */
    private void permuteUniqueUsingInPlaceSwapping(int ind, int[] nums, List<List<Integer>> ans) {
        if (ind == nums.length) {
            ans.add(Arrays.stream(nums).boxed().toList());
            return;
        }
        Set<Integer> set = new HashSet<>();
        for (int i = ind; i < nums.length; i++) {
            if (set.contains(nums[i])) continue; // prevent duplicate swaps
            set.add(nums[i]);
            
            swap(nums, i, ind);
            permuteUniqueUsingInPlaceSwapping(ind + 1, nums, ans);
            swap(nums, i, ind); // backtrack
        }
    }

    // ==========================================
    // Utility Methods
    // ==========================================

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    // ==========================================
    // ALTERNATIVE APPROACHES - Combinations
    // ==========================================

    /**
     * Approach 3: Bitmask - Iterative (no recursion)
     * Each subset is represented by a bitmask 0 to 2^n - 1.
     * Bit i set = include nums[i] in subset.
     */
    public List<List<Integer>> subsetsBitmask(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        int n = nums.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> cur = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    cur.add(nums[i]);
                }
            }
            ans.add(cur);
        }
        return ans;
    }

    /**
     * Approach 4: Iterative cascading
     * Start with [[]], for each element add it to all existing subsets.
     */
    public List<List<Integer>> subsetsIterativeCascading(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        ans.add(new ArrayList<>());
        for (int num : nums) {
            int size = ans.size();
            for (int i = 0; i < size; i++) {
                List<Integer> newSubset = new ArrayList<>(ans.get(i));
                newSubset.add(num);
                ans.add(newSubset);
            }
        }
        return ans;
    }

    /**
     * Combinations of length k (n choose k)
     * Backtracking: pick exactly k elements.
     */
    public List<List<Integer>> combine(int[] nums, int k) {
        List<List<Integer>> ans = new ArrayList<>();
        combineBacktrack(nums, 0, k, new ArrayList<>(), ans);
        return ans;
    }

    private void combineBacktrack(int[] nums, int ind, int k, List<Integer> cur, List<List<Integer>> ans) {
        if (cur.size() == k) {
            ans.add(new ArrayList<>(cur));
            return;
        }
        for (int i = ind; i < nums.length; i++) {
            cur.add(nums[i]);
            combineBacktrack(nums, i + 1, k, cur, ans);
            cur.remove(cur.size() - 1);
        }
    }

    // ==========================================
    // ALTERNATIVE APPROACHES - Permutations
    // ==========================================

    /**
     * Approach 3: Heap's algorithm - Iterative, O(1) extra space
     * Generates permutations by swapping elements.
     */
    public List<List<Integer>> permuteHeapsAlgorithm(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        int[] arr = nums.clone();
        int n = arr.length;
        int[] c = new int[n];
        ans.add(Arrays.stream(arr).boxed().toList());

        int i = 0;
        while (i < n) {
            if (c[i] < i) {
                swap(arr, (i % 2 == 0) ? 0 : c[i], i);
                ans.add(Arrays.stream(arr).boxed().toList());
                c[i]++;
                i = 0;
            } else {
                c[i] = 0;
                i++;
            }
        }
        return ans;
    }

    /**
     * Approach 4: Next permutation - Iterative
     * Repeatedly find next lexicographic permutation until no more exist.
     */
    public List<List<Integer>> permuteNextPermutation(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        int[] arr = nums.clone();
        Arrays.sort(arr);
        ans.add(Arrays.stream(arr).boxed().toList());

        while (nextPermutation(arr)) {
            ans.add(Arrays.stream(arr).boxed().toList());
        }
        return ans;
    }

    private boolean nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) i--;
        if (i < 0) return false;

        int j = nums.length - 1;
        while (nums[j] <= nums[i]) j--;
        swap(nums, i, j);
        reverse(nums, i + 1, nums.length - 1);
        return true;
    }

    private void reverse(int[] nums, int left, int right) {
        while (left < right) {
            swap(nums, left, right);
            left++;
            right--;
        }
    }

    /**
     * Approach 5: Insertion-based permutation
     * Build permutation by inserting each element at every possible position.
     */
    public List<List<Integer>> permuteInsertion(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        ans.add(new ArrayList<>());
        for (int num : nums) {
            List<List<Integer>> next = new ArrayList<>();
            for (List<Integer> perm : ans) {
                for (int pos = 0; pos <= perm.size(); pos++) {
                    List<Integer> newPerm = new ArrayList<>(perm);
                    newPerm.add(pos, num);
                    next.add(newPerm);
                }
            }
            ans = next;
        }
        return ans;
    }

    // ==========================================
    // Examples / Demo
    // ==========================================

    public static void main(String[] args) {
        Recursion r = new Recursion();
        int[] arr = {1, 2, 3};

        System.out.println("=== COMBINATIONS (Subsets) ===");
        System.out.println("1. Bitmask:        " + r.subsetsBitmask(arr));
        System.out.println("2. Cascading:      " + r.subsetsIterativeCascading(arr));
        System.out.println("3. Original:       " + r.subsets(arr));

        System.out.println("\n=== COMBINATIONS (n choose k) ===");
        System.out.println("Combine k=2:       " + r.combine(arr, 2));

        System.out.println("\n=== PERMUTATIONS ===");
        System.out.println("1. Heap's algo:    " + r.permuteHeapsAlgorithm(arr));
        System.out.println("2. Next perm:      " + r.permuteNextPermutation(arr));
        System.out.println("3. Insertion:      " + r.permuteInsertion(arr));
        System.out.println("4. Original:       " + r.permute(arr));
    }
}
