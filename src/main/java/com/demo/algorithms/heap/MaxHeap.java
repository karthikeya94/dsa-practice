package com.demo.algorithms.heap;

public class MaxHeap {

    // ==========================================
    // Core Max Heap Operations
    // ==========================================

    /**
     * Inserts a new value into the max heap at the given index.
     */
    public void insert(int[] heap, int val, int currentSize) {
        heap[currentSize] = val;
        heapifyUp(heap, currentSize);
    }

    /**
     * Deletes the element at the specified index.
     * Often used to delete the root (index 0).
     */
    public void delete(int[] heap, int ind, int currentSize) {
        if (currentSize <= 0) return;
        
        // Swap with the last element
        swap(heap, ind, currentSize - 1);
        
        // Heapify down the new element at the current index
        heapifyDown(heap, ind, currentSize - 1);
    }

    // ==========================================
    // Heapify Helpers
    // ==========================================

    /**
     * Moves the element up to maintain the max heap property (Parent > Child).
     */
    private void heapifyUp(int[] heap, int ind) {
        while (ind > 0) {
            int parentInd = (ind - 1) / 2;
            if (heap[ind] > heap[parentInd]) {
                swap(heap, ind, parentInd);
                ind = parentInd;
            } else {
                break;
            }
        }
    }

    /**
     * Moves the element down to maintain the max heap property (Parent > Child).
     */
    public void heapifyDown(int[] heap, int ind, int heapSize) {
        int largest = ind;
        int leftChild = (2 * ind) + 1;
        int rightChild = (2 * ind) + 2;

        if (leftChild < heapSize && heap[leftChild] > heap[largest]) {
            largest = leftChild;
        }
        if (rightChild < heapSize && heap[rightChild] > heap[largest]) {
            largest = rightChild;
        }
        if (largest != ind) {
            swap(heap, ind, largest);
            heapifyDown(heap, largest, heapSize); // Recursively heapify the affected subtree
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
}