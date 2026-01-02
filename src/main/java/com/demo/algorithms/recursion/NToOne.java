package com.demo.algorithms.recursion;

public class NToOne {
    static void forward(int n){
        if(n==0) return;
        System.out.print(n+" ");
        forward(n-1);
    }
    static void backtrack(int n){
        if(n==0) return;
        backtrack(n-1);
        System.out.print(n+" ");
    }

    public static void main(String[] args) {
        forward(4);
        System.out.println("\n============================");
        backtrack(4);
    }
}