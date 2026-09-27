package com.demo.algorithms.recursion;

public class Subsequence {
    static boolean checkSubsequenceSum(int[] arr, int target){
        int n = arr.length;
        return solveSubseqSum(0,n,arr,target);
    }

    private static boolean solveSubseqSum(int i, int n, int[] arr, int k) {
        if(k==0) return true;
        if(k<0) return false;
        if(i==n) return k==0;
        return solveSubseqSum(i+1,n,arr,k-arr[i]) || solveSubseqSum(i+1,n,arr,k);
    }

    static int countSumseqWithSumK(int[] arr, int target){
        return solveSubseqSumCount(0,arr.length,arr,target);
    }

    private static int solveSubseqSumCount(int i, int length, int[] arr, int k) {
        if(k==0) return 1;
        if(k<0 || i==length) return 0;
        return solveSubseqSumCount(i+1,length,arr,k-arr[i]) + solveSubseqSumCount(i+1,length,arr,k);
    }

    public static void main(String[] args) {
        int[] arr = {2,3,9,4,5,7};
        System.out.println(countSumseqWithSumK(arr,6));
    }
}
