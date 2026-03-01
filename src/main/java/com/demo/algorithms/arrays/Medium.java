package com.demo.algorithms.arrays;

import java.util.ArrayList;
import java.util.List;

public class Medium {

    // ==========================================
    // SIMPLE - LeetCode 118: Generate Pascal's Triangle
    // ==========================================

    /**
     * Generate first numRows of Pascal's triangle.
     * Time: O(numRows²), Space: O(1) excluding output
     */
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            List<Integer> curr = new ArrayList<>();
            for (int col = 0; col <= row; col++) {
                if (col == 0 || col == row) {
                    curr.add(1);
                } else {
                    List<Integer> prev = triangle.get(row - 1);
                    curr.add(prev.get(col - 1) + prev.get(col));
                }
            }
            triangle.add(curr);
        }
        return triangle;
    }

    /**
     * Alternative: Build row by row using the property that each element
     * is sum of element above-left and above.
     */
    public List<List<Integer>> generateInPlace(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();
        List<Integer> prev = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            List<Integer> curr = new ArrayList<>();
            for (int col = 0; col <= row; col++) {
                if (col == 0 || col == row) {
                    curr.add(1);
                } else {
                    curr.add(prev.get(col - 1) + prev.get(col));
                }
            }
            triangle.add(new ArrayList<>(curr));
            prev = curr;
        }
        return triangle;
    }

    // ==========================================
    // SIMPLE - LeetCode 119: Get Nth Row (0-indexed)
    // ==========================================

    /**
     * Return only the rowIndex-th row. O(rowIndex) extra space.
     * Build in-place: each row can be computed from previous.
     */
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        row.add(1);
        for (int i = 1; i <= rowIndex; i++) {
            row.add(0); // expand
            for (int j = i; j >= 1; j--) {
                row.set(j, row.get(j) + row.get(j - 1));
            }
        }
        return row;
    }

    /**
     * Get row using binomial coefficient: C(n,k) = C(n,k-1) * (n-k+1)/k
     * O(rowIndex) time, O(1) extra space (excluding output).
     */
    public List<Integer> getRowBinomial(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        long val = 1;
        for (int k = 0; k <= rowIndex; k++) {
            row.add((int) val);
            val = val * (rowIndex - k) / (k + 1);
        }
        return row;
    }

    // ==========================================
    // MEDIUM - Get element at (row, col)
    // ==========================================

    /**
     * Return element at row r, column c (0-indexed).
     * Value = C(r, c) = r! / (c! * (r-c)!)
     */
    public int getElement(int row, int col) {
        if (col > row || col < 0) return 0;
        return binomialCoeff(row, col);
    }

    /**
     * Binomial coefficient C(n, k) = n! / (k! * (n-k)!)
     * Optimized: C(n,k) = C(n,n-k), use min(k, n-k).
     */
    public int binomialCoeff(int n, int k) {
        if (k > n - k) k = n - k;
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return (int) result;
    }

    // ==========================================
    // SIMPLE - Sum of nth row
    // ==========================================

    /**
     * Sum of nth row = 2^n (each row sums to 2^rowIndex).
     */
    public long sumOfRow(int rowIndex) {
        return 1L << rowIndex;
    }

    // ==========================================
    // HARD - Count odd numbers in nth row
    // ==========================================

    /**
     * Count odd numbers in row n.
     * Pattern: count = 2^(popcount of n) where popcount = number of 1s in binary.
     * E.g. row 5 = 101₂ → popcount=2 → 2^2 = 4 odd numbers.
     */
    public int countOddNumbersInRow(int rowIndex) {
        int popCount = Integer.bitCount(rowIndex);
        return 1 << popCount;
    }

    /**
     * Alternative: Generate row and count odds (for verification).
     * Not efficient for large rowIndex.
     */
    public int countOddNumbersInRowBruteForce(int rowIndex) {
        List<Integer> row = getRow(rowIndex);
        int count = 0;
        for (int x : row) {
            if (x % 2 == 1) count++;
        }
        return count;
    }

    // ==========================================
    // HARD - Pascal with modulo (for large numbers)
    // ==========================================

    /**
     * Generate Pascal's triangle with modulo (e.g. 10^9+7).
     * Used when values can overflow.
     */
    public List<List<Integer>> generateWithModulo(int numRows, int mod) {
        List<List<Integer>> triangle = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            List<Integer> curr = new ArrayList<>();
            for (int col = 0; col <= row; col++) {
                if (col == 0 || col == row) {
                    curr.add(1);
                } else {
                    List<Integer> prev = triangle.get(row - 1);
                    int val = (prev.get(col - 1) + prev.get(col)) % mod;
                    curr.add(val);
                }
            }
            triangle.add(curr);
        }
        return triangle;
    }

    /**
     * Binomial coefficient C(n, k) % mod using modular arithmetic.
     * Precompute factorials for O(1) query (or use this for single query).
     */
    public int binomialCoeffMod(int n, int k, int mod) {
        if (k > n) return 0;
        if (k > n - k) k = n - k;
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = (result * (n - i)) % mod;
            result = (result * modInverse(i + 1, mod)) % mod;
        }
        return (int) result;
    }

    private long modInverse(int a, int mod) {
        return power(a, mod - 2, mod);
    }

    private long power(long base, int exp, int mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if (exp % 2 == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp /= 2;
        }
        return result;
    }

    // ==========================================
    // MEDIUM - Print Pascal's triangle (formatted)
    // ==========================================

    /**
     * Print Pascal's triangle with centered alignment.
     */
    public void printTriangle(int numRows) {
        List<List<Integer>> triangle = generate(numRows);
        int lastRowLen = triangle.get(numRows - 1).size() * 4;
        for (List<Integer> row : triangle) {
            StringBuilder sb = new StringBuilder();
            for (int x : row) sb.append(String.format("%4d", x));
            String line = sb.toString();
            int padding = (lastRowLen - line.length()) / 2;
            System.out.println(" ".repeat(Math.max(0, padding)) + line);
        }
    }

    /*
    find all elements appear more than ⌊n / 3⌋ times
    */
    public void majorityElements(int[] nums){
        int n = nums.length;
        int threshold = n / 3;
        int ele1 = 0, count1 = 0;
        int ele2 = 0, count2 = 0;
        for(int num : nums){
            if(num == ele1){
                count1++;
            }else if(num == ele2){
                count2++;
            }else if(count1 == 0){
                ele1 = num;
                count1 = 1;
            }else if(count2 == 0){
                ele2 = num;
                count2 = 1;
            }else{
                count1--;
                count2--;
            }
        }
        count1 = 0;
        count2 = 0;
        for(int num : nums){
            if(num == ele1){
                count1++;
            }else if(num == ele2){
                count2++;
            }
        }
        if(count1 > threshold){
            System.out.println(ele1);
        }
        if(count2 > threshold){
            System.out.println(ele2);
        }
    }

    /*
    find 3sum which sum equal to zero
    */
    public void threeSum(int[] nums){
        int n = nums.length;
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for(int i = 0; i < n; i++){
            if(i>0 && nums[i]==nums[i-1]){
                continue;
            }
            int left=i+1, right=n-1;
            while(left<right){
                int sum = nums[i]+nums[left]+nums[right];
                if(sum==0){
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while(left<right && nums[left]==nums[left-1]) left++;
                    while(left<right && nums[right]==nums[right+1]) right--;
                }else if(sum<0){
                    left++;
                }else{
                    right--;
                }
            }
        }
        System.out.println(result);
    }


    /*
        Given an array nums of n integers, return an array of all the unique quadruplets [nums[a], nums[b], nums[c], nums[d]]
    */
   public void fourSum(int[] nums, int target){
        List<List<Integer>> ans = new ArrayList<>();
        int n = nums.length;
        Arrays.sort(nums);
        for(int i=0;i<n;i++){
            if(i>0 && nums[i]==nums[i-1]) continue;
            for(int j=i+1;j<n;j++){
                if(j>i+1 && nums[j]==nums[j-1]) continue;
                int left =j+1, right=n-1;
                while(left<right){
                    int sum = nums[i]+nums[j]+nums[left]+nums[right];
                    if(sum==target){
                        ans.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        left++;
                        right--;
                        while(left<right && nums[left]==nums[left-1]) left++;
                        while(left<right && nums[right]==nums[right+1]) right--;
                    }else if(sum<target){
                        left++;
                    }else{
                        right--;
                    }
                }
            }
        }
        System.out.println(ans);
   }

    public static void main(String[] args) {
        Medium pt = new Medium();

        System.out.println("=== 1. Generate (numRows=5) ===");
        System.out.println(pt.generate(5));

        System.out.println("\n=== 2. Get Row 4 ===");
        System.out.println("getRow:        " + pt.getRow(4));
        System.out.println("getRowBinomial: " + pt.getRowBinomial(4));

        System.out.println("\n=== 3. Element at (4,2) = C(4,2) ===");
        System.out.println(pt.getElement(4, 2)); // 6

        System.out.println("\n=== 4. Sum of row 4 ===");
        System.out.println(pt.sumOfRow(4)); // 16

        System.out.println("\n=== 5. Count odd numbers in row 5 ===");
        System.out.println("Formula: " + pt.countOddNumbersInRow(5));
        System.out.println("Brute:   " + pt.countOddNumbersInRowBruteForce(5));

        System.out.println("\n=== 6. Generate with modulo 1000000007 ===");
        System.out.println(pt.generateWithModulo(5, 1000000007));

        System.out.println("\n=== 7. Print formatted triangle ===");
        pt.printTriangle(6);
    }
}
