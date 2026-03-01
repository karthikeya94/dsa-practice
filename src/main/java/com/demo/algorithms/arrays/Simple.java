package com.demo.algorithms.arrays;

import java.util.*;

public class Simple {
    
    // ==========================================
    // 1. Array Modification & Rotations
    // ==========================================

    public static int removeDuplicates(int[] arr) {
        if (arr.length == 0) return 0;
        int insertIdx = 0;
        for (int j = 1; j < arr.length; j++) {
            if (arr[insertIdx] != arr[j]) {
                insertIdx++;
                arr[insertIdx] = arr[j];
            }
        }
        return insertIdx + 1;
    }

    public static void leftRotate(int[] arr, int k) {
        int n = arr.length;
        k = k % n;
        reverse(arr, 0, n - 1);
        reverse(arr, 0, k - 1);
        reverse(arr, k, n - 1);
    }

    public static void rightRotate(int[] arr, int k) {
        int n = arr.length;
        k = k % n;
        reverse(arr, 0, k - 1);
        reverse(arr, k, n - 1);
        reverse(arr, 0, n - 1);
    }

    private static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }

    public static void moveZerosToEnd(int[] arr) {
        int firstZeroIdx = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {
                firstZeroIdx = i;
                break;
            }
        }
        if (firstZeroIdx == -1) return;

        for (int i = firstZeroIdx + 1; i < arr.length; i++) {
            if (arr[i] != 0) {
                int temp = arr[i];
                arr[i] = arr[firstZeroIdx];
                arr[firstZeroIdx] = temp;
                firstZeroIdx++;
            }
        }
    }

    // ==========================================
    // 2. Searching & Set Logic
    // ==========================================

    public static Deque<Integer> getUnionOfSortedArrays(int[] arr1, int[] arr2) {
        Deque<Integer> union = new ArrayDeque<>();
        int n = arr1.length, m = arr2.length;
        int i = 0, j = 0;
        
        while (i < n && j < m) {
            if (arr1[i] < arr2[j]) {
                if (union.isEmpty() || union.getLast() != arr1[i]) {
                    union.add(arr1[i]);
                }
                i++;
            } else if (arr1[i] > arr2[j]) {
                if (union.isEmpty() || union.getLast() != arr2[j]) {
                    union.add(arr2[j]);
                }
                j++;
            } else { // Elements are equal
                if (union.isEmpty() || union.getLast() != arr1[i]) {
                    union.add(arr1[i]);
                }
                i++;
                j++;
            }
        }
        while (i < n) {
            if (union.isEmpty() || union.getLast() != arr1[i]) {
                union.add(arr1[i]);
            }
            i++;
        }
        while (j < m) {
            if (union.isEmpty() || union.getLast() != arr2[j]) {
                union.add(arr2[j]);
            }
            j++;
        }
        return union;
    }

    public static void findMissingNumberDifferentApproaches(int[] arr) {
        int n = arr.length;
        
        // Approach 1: Using XOR
        int xorArray = 0, xorExpected = 0;
        for (int i = 0; i < n; i++) {
            xorArray ^= arr[i];
            xorExpected ^= i;
        }
        xorExpected ^= n;
        int ans1 = xorArray ^ xorExpected;
        
        // Approach 2: Using Mathematical Formula
        int expectedSum = n * (n + 1) / 2;
        int actualSum = 0;
        for (int num : arr) {
            actualSum += num;
        }
        int ans2 = expectedSum - actualSum;
        
        // Approach 3: Using Cyclic Sort
        int i = 0;
        while (i < n) {
            int correctIdxForCurrentValue = arr[i];
            if (arr[i] < n && arr[i] != arr[correctIdxForCurrentValue]) {
                int temp = arr[i];
                arr[i] = arr[correctIdxForCurrentValue];
                arr[correctIdxForCurrentValue] = temp;
            } else {
                i++;
            }
        }
        int ans3 = 0;
        for (int ind = 0; ind < n; ind++) {
            if (arr[ind] != ind) {
                ans3 = ind;
                break;
            }
        }
        
        System.out.println("Missing Number -> XOR: " + ans1 + ", Formula: " + ans2 + ", Cyclic: " + ans3);
    }

    // ==========================================
    // 3. Subarray Logic
    // ==========================================

    /**
     * Works for arrays with Positive and Negative numbers. (Prefix Sum approach)
     */
    public static void longestSubArraySumUsingPrefixMap(int[] arr, int k) {
        int n = arr.length;
        Map<Integer, Integer> prefixSumMap = new HashMap<>();
        int currentSum = 0, longestLen = 0;
        
        for (int i = 0; i < n; i++) {
            currentSum += arr[i];
            
            if (currentSum == k) {
                longestLen = i + 1;
            }
            
            int remainingSum = currentSum - k;
            if (prefixSumMap.containsKey(remainingSum)) {
                int len = i - prefixSumMap.get(remainingSum);
                longestLen = Math.max(len, longestLen);
            }
            
            // Only insert if missing to keep the leftmost index for maximum length
            if (!prefixSumMap.containsKey(currentSum)) {
                prefixSumMap.put(currentSum, i);
            }
        }
        System.out.println("Longest SubArray Sum (Prefix Map): " + longestLen);
    }

    /**
     * Optimized for POSITIVE numbers ONLY. (Sliding Window approach)
     */
    public static void longestSubArraySumUsingSlidingWindow(int[] arr, int k) {
        int n = arr.length;
        int left = 0, right = 0;
        if (n == 0) return;
        
        int currentSum = arr[0];
        int longestLen = 0;
        
        while (right < n) {
            // Shrink window if sum exceeds K
            while (left <= right && currentSum > k) {
                currentSum -= arr[left];
                left++;
            }
            if (currentSum == k) {
                longestLen = Math.max(longestLen, right - left + 1);
            }
            
            right++;
            if (right < n) {
                currentSum += arr[right];
            }
        }
        System.out.println("Longest SubArray Sum (Two Pointer): " + longestLen);
    }

    // ==========================================
    // 4. Medium & Advanced Arrays
    // ==========================================

    /**
     * Dutch National Flag Algorithm (0s, 1s, and 2s)
     */
    public static void sortColors(int[] arr) {
        int n = arr.length;
        int low = 0, mid = 0, high = n - 1;
        
        while (mid <= high) {
            if (arr[mid] == 0) {
                int temp = arr[low];
                arr[low] = arr[mid];
                arr[mid] = temp;
                low++;
                mid++;
            } else if (arr[mid] == 1) {
                mid++;
            } else {
                int temp = arr[mid];
                arr[mid] = arr[high];
                arr[high] = temp;
                high--;
            }
        }
        System.out.println("Colors Sorted: " + Arrays.toString(arr));
    }

    /**
     * Moore's Voting Algorithm (Find > N/2 occurrences)
     */
    public static void majorityElement(int[] arr) {
        int n = arr.length;
        int count = 0, element = 0;
        
        // Phase 1: Finding Candidate
        for (int i : arr) {
            if (count == 0) {
                count = 1;
                element = i;
            } else if (element == i) {
                count++;
            } else {
                count--;
            }
        }
        
        // Phase 2: Validating Candidate
        int occurrence = 0;
        for (int i : arr) {
            if (i == element) occurrence++;
        }
        if (occurrence > n / 2) {
            System.out.println("Majority Element: " + element);
        } else {
            System.out.println("No majority element");
        }
    }

    /**
     * Kadane's Algorithm
     */
    public static void findAndPrintMaxSumSubArray(int[] arr) {
        int n = arr.length;
        int maxSum = Integer.MIN_VALUE;
        int currentSum = 0;
        int optimalStart = 0, optimalEnd = 0, currentStart = 0;
        
        for (int i = 0; i < n; i++) {
            currentSum += arr[i];
            
            if (currentSum > maxSum) {
                maxSum = currentSum;
                optimalStart = currentStart;
                optimalEnd = i;
            }
            
            if (currentSum < 0) {
                currentSum = 0;
                currentStart = i + 1;
            }
        }
        System.out.println("Max sum: " + maxSum);
        System.out.println("Subarray with max sum: " + Arrays.toString(Arrays.copyOfRange(arr, optimalStart, optimalEnd + 1)));
    }

    /**
     * Find next lexicographical permutation
     */
    public static void nextPermutation(int[] arr) {
        int n = arr.length;
        int dipIndex = -1;
        
        // Find the first decreasing element from the end
        for (int i = n - 2; i >= 0; i--) {
            if (arr[i] < arr[i + 1]) {
                dipIndex = i;
                break;
            }
        }
        
        // If no dip found, reverse the entire array to form smallest permutation
        if (dipIndex == -1) {
            reverse(arr, 0, n - 1);
            return;
        }
        
        // Swap with the next strictly greater element to its right
        for (int i = n - 1; i > dipIndex; i--) {
            if (arr[i] > arr[dipIndex]) {
                int temp = arr[i];
                arr[i] = arr[dipIndex];
                arr[dipIndex] = temp;
                break;
            }
        }
        
        // Final step: Sort descending tail by just reversing it
        reverse(arr, dipIndex + 1, n - 1);
        System.out.println("Next Permutation: " + Arrays.toString(arr));
    }

    public static void longestConsecutiveSequence(int[] arr) {
        Set<Integer> set = new HashSet<>();
        for (int i : arr) {
            set.add(i);
        }
        
        int longestLen = 0;
        for (int i : arr) {
            // Find start of a consecutive sequence
            if (!set.contains(i - 1)) {
                int currentLen = 1;
                while (set.contains(i + currentLen)) {
                    currentLen++;
                }
                longestLen = Math.max(longestLen, currentLen);
            }
        }
        System.out.println("Longest consecutive seq len: " + longestLen);
    }

    public static void setMatrixZerosInPlace(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean firstRowZero = false, firstColZero = false;
        
        // Check if first row needs zeros
        for (int c = 0; c < cols; c++) {
            if (matrix[0][c] == 0) {
                firstRowZero = true;
                break;
            }
        }
        
        // Check if first col needs zeros
        for (int r = 0; r < rows; r++) {
            if (matrix[r][0] == 0) {
                firstColZero = true;
                break;
            }
        }
        
        // Use first row and first col as markers
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (matrix[r][c] == 0) {
                    matrix[r][0] = 0;
                    matrix[0][c] = 0;
                }
            }
        }
        
        // Apply markers
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (matrix[r][0] == 0 || matrix[0][c] == 0) {
                    matrix[r][c] = 0;
                }
            }
        }
        
        // Fix up first row & col constraints
        if (firstRowZero) {
            for (int c = 0; c < cols; c++) {
                matrix[0][c] = 0;
            }
        }
        if (firstColZero) {
            for (int r = 0; r < rows; r++) {
                matrix[r][0] = 0;
            }
        }
        
        System.out.println("Matrix Output:");
        for (int[] r : matrix) {
            System.out.println(Arrays.toString(r));
        }
    }

    // ==========================================
    // Runner Method
    // ==========================================

    public static void main(String[] args) {
        int[] nums = {0, 0, 1, 0, 1, 2, 0, 2, 3, 0, 3, 4};
        System.out.println("Removes Duplicates (count): " + removeDuplicates(nums));
        
        leftRotate(nums, 5);
        System.out.println("After Left Rotate: " + Arrays.toString(nums));
        
        rightRotate(nums, 5);
        
        moveZerosToEnd(nums);
        System.out.println("Moving Zeros to End: " + Arrays.toString(nums));
        
        System.out.println("Union: " + getUnionOfSortedArrays(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, new int[]{2, 3, 4, 4, 5, 10, 11, 12}));
        
        findMissingNumberDifferentApproaches(new int[]{4, 0, 2, 1});
        
        longestSubArraySumUsingPrefixMap(new int[]{ -1, 1, 1 }, 1);
        longestSubArraySumUsingSlidingWindow(new int[]{10, 5, 2, 7, 1, 9}, 15);
        
        setMatrixZerosInPlace(new int[][]{{3, 1, 2, 4}, {3, 0, 5, 2}, {1, 3, 0, 5}});
    }
}
