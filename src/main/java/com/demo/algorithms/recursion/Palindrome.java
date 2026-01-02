package com.demo.algorithms.recursion;

public class Palindrome {
    public static boolean palindrome(int i,String s,int n){
        if(i>=n/2) return true;
        if(s.charAt(i)!=s.charAt(n-1-i)) return false;
        return palindrome(i+1,s,n);
    }

    public static void main(String[] args) {
        System.out.println(palindrome(0,"aabbaa",6));
        System.out.println(palindrome(0,"aabaa",5));
        System.out.println(palindrome(0,"main",4));
    }
}