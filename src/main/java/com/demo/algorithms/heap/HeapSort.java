package com.demo.algorithms.heap;

import java.util.Arrays;

public class HeapSort {

    void swap(int[] heap, int l, int r) {
        int temp = heap[l];
        heap[l] = heap[r];
        heap[r] = temp;
    }

    void buildHeap(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapDown(i, arr, n);
        }
    }

    void heapDown(int ind, int[] heap, int len) {
        int left = (2 * ind) + 1;
        int right = (2 * ind) + 2;
        int largest = ind;

        if (left < len && heap[left] > heap[largest]) {
            largest = left;
        }
        if (right < len && heap[right] > heap[largest]) {
            largest = right;
        }
        if (ind != largest) {
            swap(heap, ind, largest);
            heapDown(largest, heap, len);
        }
    }

    public void heapSort(int[] arr) {
        int n = arr.length;
        buildHeap(arr);
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            heapDown(0, arr, i);
        }
    }

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7};
        HeapSort sort = new HeapSort();
        System.out.println("Original: " + Arrays.toString(arr));
        sort.heapSort(arr);
        System.out.println("Sorted: " + Arrays.toString(arr));
    }
}