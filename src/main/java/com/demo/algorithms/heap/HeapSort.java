package com.demo.algorithms.heap;

import java.util.Arrays;

public class HeapSort {

    // ==========================================
    // Heap Sort Main Logic
    // ==========================================

    public void sort(int[] arr) {
        int n = arr.length;
        
        // Step 1: Build Max Heap
        buildMaxHeap(arr, n);
        
        // Step 2: Extract elements one by one from the heap
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i); // Move current root to the end
            heapifyDown(arr, 0, i); // Call max heapify on the reduced heap
        }
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    /**
     * Builds a max heap from the given array.
     */
    private void buildMaxHeap(int[] arr, int n) {
        // Start from the last non-leaf node and heapify down
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapifyDown(arr, i, n);
        }
    }

    /**
     * Restores max heap property starting from given index.
     * @param arr The array representing the heap
     * @param ind The index to start heapify from
     * @param heapSize The current size of the heap bounds
     */
    private void heapifyDown(int[] arr, int ind, int heapSize) {
        int largest = ind;
        int leftChild = (2 * ind) + 1;
        int rightChild = (2 * ind) + 2;

        if (leftChild < heapSize && arr[leftChild] > arr[largest]) {
            largest = leftChild;
        }
        if (rightChild < heapSize && arr[rightChild] > arr[largest]) {
            largest = rightChild;
        }
        if (largest != ind) {
            swap(arr, ind, largest);
            heapifyDown(arr, largest, heapSize);
        }
    }

    private void swap(int[] arr, int l, int r) {
        int temp = arr[l];
        arr[l] = arr[r];
        arr[r] = temp;
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7};
        HeapSort heapSort = new HeapSort();
        
        System.out.println("Original: " + Arrays.toString(arr));
        heapSort.sort(arr);
        System.out.println("Sorted: " + Arrays.toString(arr));
    }
}