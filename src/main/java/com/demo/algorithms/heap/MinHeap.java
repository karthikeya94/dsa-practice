package com.demo.algorithms.heap;

import java.util.Arrays;

public class MinHeap {
    
    // Maintain size internally for active items in current structure
    private int size = 0;

    // ==========================================
    // Core Min Heap Operations
    // ==========================================

    /**
     * Inserts a new value into the min heap.
     */
    public void insert(int[] heap, int val) {
        heap[size] = val;
        heapifyUp(heap, size);
        size++;
    }

    /**
     * Deletes the element at the specified index (typically root at 0).
     */
    public void delete(int[] heap, int ind) {
        if (size <= 0) return;
        
        // Swap element to delete with the last active element
        swap(heap, ind, size - 1);
        size--; // Reduce size before restoring structure
        
        // Restore heap property
        heapifyDown(heap, ind, size);
    }

    // ==========================================
    // Heapify Helpers
    // ==========================================

    /**
     * Moves the element up to maintain min heap property (Parent < Child).
     */
    private void heapifyUp(int[] heap, int ind) {
        while (ind > 0) {
            int parentInd = (ind - 1) / 2;
            if (heap[ind] < heap[parentInd]) {
                swap(heap, ind, parentInd);
                ind = parentInd;
            } else {
                break;
            }
        }
    }

    /**
     * Moves the element down to maintain min heap property (Parent < Child).
     */
    private void heapifyDown(int[] heap, int ind, int currentSize) {
        int smallest = ind;
        int leftChild = (2 * ind) + 1;
        int rightChild = (2 * ind) + 2;

        if (leftChild < currentSize && heap[leftChild] < heap[smallest]) {
            smallest = leftChild;
        }
        if (rightChild < currentSize && heap[rightChild] < heap[smallest]) {
            smallest = rightChild;
        }
        if (smallest != ind) {
            swap(heap, ind, smallest);
            heapifyDown(heap, smallest, currentSize);
        }
    }

    // ==========================================
    // Utilities
    // ==========================================

    private void swap(int[] heap, int l, int r) {
        int temp = heap[l];
        heap[l] = heap[r];
        heap[r] = temp;
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        int[] arr = new int[10]; // Provide array capacity
        int[] initialVals = {10, 5, 30, 2, 8};
        
        System.out.println("Original Set: " + Arrays.toString(initialVals));
        MinHeap minHeap = new MinHeap();
        
        for (int val : initialVals) {
            minHeap.insert(arr, val);
        }
        
        System.out.println("Min Heap built: " + Arrays.toString(Arrays.copyOf(arr, minHeap.size)));

        minHeap.delete(arr, 0);
        System.out.println("After deleting root: " + Arrays.toString(Arrays.copyOf(arr, minHeap.size)));
        
        minHeap.delete(arr, 0);
        System.out.println("After deleting root: " + Arrays.toString(Arrays.copyOf(arr, minHeap.size)));
    }
}