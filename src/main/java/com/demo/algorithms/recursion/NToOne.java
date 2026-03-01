package com.demo.algorithms.recursion;

/**
 * Print numbers from N to 1 using recursion.
 * Two approaches: Forward (direct print) and Backtracking (print on return).
 */
public class NToOne {

    // ==========================================
    // Approach 1: Forward Recursion (Print then recurse)
    // ==========================================

    /**
     * Prints N, N-1, ..., 1 by printing BEFORE the recursive call.
     * Call stack: print(N) → print(N-1) → ... → print(1)
     */
    static void printNToOneForward(int n) {
        if (n == 0) return;
        System.out.print(n + " ");
        printNToOneForward(n - 1);
    }

    // ==========================================
    // Approach 2: Backtracking (Recurse then print)
    // ==========================================

    /**
     * Prints N, N-1, ..., 1 by printing AFTER the recursive call returns.
     * Recurses down to 0 first, then prints on the way back up: 1, 2, ..., N
     * But since we print on unwind: effectively prints 1 to N (ascending).
     *
     * NOTE: This approach actually prints 1 to N (ascending), demonstrating
     * how backtracking reverses the print order compared to forward recursion.
     */
    static void printOneToNUsingBacktracking(int n) {
        if (n == 0) return;
        printOneToNUsingBacktracking(n - 1);
        System.out.print(n + " ");
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        System.out.println("Forward (N to 1):");
        printNToOneForward(4);

        System.out.println("\n============================");

        System.out.println("Backtracking (prints 1 to N):");
        printOneToNUsingBacktracking(4);
    }
}