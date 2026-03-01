package com.demo.algorithms.recursion;

/**
 * Check if a string is a palindrome using recursion.
 * Compares characters from both ends moving inward.
 */
public class Palindrome {

    // ==========================================
    // Recursive Palindrome Check
    // ==========================================

    /**
     * Checks if string s is a palindrome by comparing characters
     * from index i (left) and n-1-i (right), moving inward.
     *
     * Base case: i >= n/2 → all pairs matched → true
     * Mismatch: s[i] != s[n-1-i] → false
     *
     * @param i     current index from the left
     * @param s     the string to check
     * @param n     the length of the string
     * @return      true if the string is a palindrome
     */
    public static boolean isPalindrome(int i, String s, int n) {
        if (i >= n / 2) return true;
        if (s.charAt(i) != s.charAt(n - 1 - i)) return false;
        return isPalindrome(i + 1, s, n);
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        System.out.println("aabbaa: " + isPalindrome(0, "aabbaa", 6)); // true
        System.out.println("aabaa:  " + isPalindrome(0, "aabaa", 5));  // true
        System.out.println("main:   " + isPalindrome(0, "main", 4));   // false
    }
}