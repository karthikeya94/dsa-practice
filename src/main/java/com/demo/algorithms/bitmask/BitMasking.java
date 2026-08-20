package com.demo.algorithms.bitmask;

public class BitMasking {
    public static void main(String[] args) {
        updateBit(10,0,1);
    }
    // Example: n=10 (1010), i=1 → (1010 >> 1)=0101 → 0101 & 1 = 1 → true ✓
    static void getBit(int n, int i){
        System.out.println(((n>>i)&1)==1);
        System.out.println((n&(1<<i))!=0);
    }
    static void setbit(int n,int i){
        System.out.println((int)(n|(1<<i)));
    }
    static void clearBit(int n, int i){
        System.out.println((n&~(1<<i)));
    }
    static void toggleBit(int n, int i){
        System.out.println((n^(1<<i)));
    }
    static void updateBit(int n, int i, int v){
        System.out.println((n&~(1<<i))|(v<<i));
    }
    private static void basics() {
        System.out.println(Integer.toBinaryString(0));
        System.out.println(Integer.toBinaryString(~(0)));
        int i=4;
        while(i<5){
            i=1<<i;//2^i
            System.out.println(i);
        }
        System.out.println("==========");
        while(i>0){
            i=i>>1;//divide by 2
            System.out.println(i);
        }
    }
}
