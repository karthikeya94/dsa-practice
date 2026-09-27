package com.demo.algorithms.stack;

import java.util.Arrays;
import java.util.Stack;

public class MonotonicStack {
    public int[] nextGreaterElement(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        int[] result = new int[nums.length];
        Arrays.fill(result, -1);

        for (int i = 0; i < nums.length; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        return result;
    }
    public int[] nextSmallerElement(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        int[] result = new int[nums.length];
        Arrays.fill(result, -1);

        for (int i = 0; i < nums.length; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] > nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        return result;
    }
    public int[] previousGreaterElement(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        int[] result = new int[nums.length];
        Arrays.fill(result, -1);

        for (int i = nums.length - 1; i >= 0; i--) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        return result;
    }
    public int[] previousSmallerElement(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        int[] result = new int[nums.length];
        Arrays.fill(result, -1);

        for (int i = nums.length - 1; i >= 0; i--) {
            while (!stack.isEmpty() && nums[stack.peek()] > nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        return result;
    }


    public int[] nextGreater(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        Stack<Integer> st = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && st.peek() <= arr[i]) {
                st.pop();
            }
            res[i] = st.isEmpty() ? -1 : st.peek();
            st.push(arr[i]);
        }
        return res;
    }

    public int[] prevGreater(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        Stack<Integer> st = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && st.peek() <= arr[i]) {
                st.pop();
            }
            res[i] = st.isEmpty() ? -1 : st.peek();
            st.push(arr[i]);
        }
        return res;
    }

    public int[] nextSmaller(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        Stack<Integer> st = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && st.peek() >= arr[i]) {
                st.pop();
            }
            res[i] = st.isEmpty() ? -1 : st.peek();
            st.push(arr[i]);
        }
        return res;
    }

    public int[] prevSmaller(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        Stack<Integer> st = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && st.peek() >= arr[i]) {
                st.pop();
            }
            res[i] = st.isEmpty() ? -1 : st.peek();
            st.push(arr[i]);
        }
        return res;
    }

    public static void main(String[] args) {
        int[] test1 = {4, 5, 2, 10, 8};
        runTest("Test 1 (Mixed Values)", test1);

        // Test Case 2: Strictly increasing array
        int[] test2 = {1, 2, 3, 4, 5};
        runTest("Test 2 (Strictly Increasing)", test2);

        // Test Case 3: Strictly decreasing array
        int[] test3 = {5, 4, 3, 2, 1};
        runTest("Test 3 (Strictly Decreasing)", test3);

        // Test Case 4: Array containing duplicates
        int[] test4 = {3, 3, 1, 3, 4};
        runTest("Test 4 (With Duplicates)", test4);

        // Test Case 5: Single element array
        int[] test5 = {42};
        runTest("Test 5 (Single Element)", test5);
    }

    // Helper method to neatly print all results for a given test case
    private static void runTest(String testName, int[] arr) {
        MonotonicStack mon = new MonotonicStack();
        System.out.println("--- " + testName + " ---");
        System.out.println("Input: " + java.util.Arrays.toString(arr));
        System.out.println("NGE  : " + java.util.Arrays.equals(mon.nextGreater(arr),mon.nextGreaterElement(arr)));
        System.out.println("PGE  : " + java.util.Arrays.equals(mon.prevGreater(arr),mon.previousGreaterElement(arr)));
        System.out.println("NSE  : " + java.util.Arrays.equals(mon.nextSmaller(arr),mon.nextSmallerElement(arr)));
        System.out.println("PSE  : " + java.util.Arrays.equals(mon.prevSmaller(arr),mon.previousSmallerElement(arr)));
        System.out.println();
    }

}
