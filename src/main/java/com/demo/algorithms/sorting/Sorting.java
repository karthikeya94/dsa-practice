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
public class Sorting {

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

    public static void mergeSort(int[] arr,int s,int e){
        if(e-s==1){
            return;
        }
        int mid = (s+e)/2;
        mergeSort(arr,s,mid);
        mergeSort(arr,mid,e);
        merge(arr,s,mid,e);
    }

    private static void merge(int[] arr, int s, int mid, int e) {
        int[] temp = new int[e-s];
        int i=s,j=mid,k=0;
        while(i<mid && j<e){
            if(arr[i]<arr[j]){
                temp[k++]=arr[i++];
            }else{
                temp[k++]=arr[j++];
            }
        }
        while(i<mid){
            temp[k++]=arr[i++];
        }
        while(j<e){
            temp[k++]=arr[j++];
        }
        for(int l=0;l<temp.length;l++){
            arr[l+s] = temp[l];
        }
    }

    public static void quickSort(int[] arr, int l, int h){
        if(l>=h){
            return;
        }
        int s=l;
        int e=h;
        int m=(l+h)/2;
        int piv = arr[m];
        System.out.println("Pivot: "+piv+", l: "+l+", h: "+h+", m: "+m+", arr: "+ Arrays.toString(arr));
        while(s<=e){
            while(arr[s]<piv){
                s++;
            }
            while(arr[e]>piv){
                e--;
            }
            if(s<=e){
                int temp = arr[s];
                arr[s] = arr[e];
                arr[e] = temp;
                s++;
                e--;
            }
        }
        quickSort(arr,l,e);
        quickSort(arr,s,h);
    }

    public static void quickSort1(int[] arr,int l,int h){
        if(l<h){
            int p = partition(arr,l,h);
            quickSort1(arr,l,p-1);
            quickSort1(arr,p+1,h);
        }
    }

    private static int partition(int[] arr, int l, int h) {
        int pivot = arr[h];
        int i = l - 1;
        for (int j = l; j < h; j++) {
            if(arr[j]<pivot){
                i++;
                int temp = arr[i];
                arr[i]=arr[j];
                arr[j]=temp;
            }
        }
        int t=arr[i+1];
        arr[i+1]=arr[h];
        arr[h]=t;
        return i+1;
    }

    public static void main(String[] args) {
        int[] arr = {8,4,2,7,8,2,5,1,7,8,9,5,4,3,1};//{7, 4, 1, 5, 3};//{8,4,2,7,8,2,5,1,7,8,9,5,4,3,1};
        int[] arr1 = {8,4,2,7,8,2,5,1,7,8,9,5,4,3,1};//{7, 4, 1, 5, 3};//{8,4,2,7,8,2,5,1,7,8,9,5,4,3,1};
        System.out.println(Arrays.toString(arr));
        quickSort(arr,0,arr.length-1);
        quickSort1(arr1,0,arr.length-1);
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(arr1));
    }
}