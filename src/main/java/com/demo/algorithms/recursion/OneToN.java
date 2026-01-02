package com.demo.algorithms.recursion;

public class OneToN {
    static void forwardRecursion(int c,int n){
        if(c>n){
            return;
        }
        System.out.print(c+" ");
        forwardRecursion(c+1,n);
    }

    static void backTracking(int c,int n){
        if(c<0) return;
        backTracking(c-1,n);
        System.out.print(c+" ");
    }

    public static void main(String[] args) {
        forwardRecursion(0,4);
        System.out.println("\n=======================");
        backTracking(4,4);
    }
}