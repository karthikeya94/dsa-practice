package com.demo.algorithms.recursion;

/**
 * Print numbers from 1 to N using recursion.
 * Two approaches: Forward (incrementing) and Backtracking (decrement then print on unwind).
 */
public class OneToN {

    // ==========================================
    // Approach 1: Forward Recursion (Print then recurse with incrementing counter)
    // ==========================================

    /**
     * Prints 1, 2, ..., N by incrementing from 1 and printing BEFORE recursive call.
     */
    static void printOneToNForward(int current, int n) {
        if (current > n) {
            return;
        }
        System.out.print(current + " ");
        printOneToNForward(current + 1, n);
    }

    // ==========================================
    // Approach 2: Backtracking (Recurse down to 0, then print on unwind)
    // ==========================================

    /**
     * Prints 0, 1, 2, ..., N by decrementing to base case first,
     * then printing on the way back up (backtracking).
     */
    static void printZeroToNUsingBacktracking(int current, int n) {
        if (current < 0) return;
        printZeroToNUsingBacktracking(current - 1, n);
        System.out.print(current + " ");
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        System.out.println("Forward (1 to N):");
        printOneToNForward(0, 4);

        System.out.println("\n=======================");

        System.out.println("Backtracking (0 to N):");
        printZeroToNUsingBacktracking(4, 4);
    }
}