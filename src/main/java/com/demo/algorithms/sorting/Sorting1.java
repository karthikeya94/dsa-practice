package com.demo.algorithms.sorting;

import java.util.Arrays;
/*
Algorithm	Best Case	Average Case	Worst Case	Space Complexity	Stable
Bubble Sort | O(n) | O(n²) | O(n²) | O(1) | Yes
Insertion Sort | O(n) | O(n²) | O(n²) | O(1) | Yes
Selection Sort | O(n²) | O(n²) | O(n²) | O(1) | No
Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes
Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) | No
Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No
Counting Sort | O(n+k) | O(n+k) | O(n+k) | O(k) | Yes
 */
public class Sorting1 {
    //unstable
    public static void selection(int[] arr){
        int n = arr.length;
        for(int i=0;i<n;i++){
            int m = i;
            for(int j=i+1;j<n;j++){
                if(arr[j]<arr[m]){
                    m=j;
                }
            }
            int t = arr[m];
            arr[m]=arr[i];
            arr[i]=t;
        }
    }
    //stable
    public static void bubbleSort(int[] arr){
        int n = arr.length;
        for(int i=0;i<n-1;i++){
            boolean swapped=false;
            for(int j=0;j<n-i-1;j++){
                if(arr[j]>arr[j+1]){
                    int t = arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=t;
                    swapped=true;
                }
            }
            if(!swapped) break;
        }
    }

    public static void insertion(int[] arr){
        int n = arr.length;
        for(int i=1;i<n;i++){
            int key = arr[i];
            int j = i-1;
            while(j>=0 && arr[j]>key){
                arr[j+1]=arr[j];
                j--;
            }
            arr[j+1]=key;
        }
    }

    public static void main(String[] args) {
        int[] arr = {8,4,2,7,8,2,5,1,7,8,9,5,4,3,1};
        System.out.println(Arrays.toString(arr));
        insertion(arr);
        System.out.println(Arrays.toString(arr));
    }
}