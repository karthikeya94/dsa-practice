package com.demo.algorithms.heap;


public class MaxHeap {

    void swap(int[] heap, int l, int r) {
        int temp = heap[l];
        heap[l] = heap[r];
        heap[r] = temp;
    }

    void insert(int val, int ind, int[] heap) {
        heap[ind] = val;
        heapUp(ind, heap);
    }

    void heapUp(int ind, int[] heap) {
        while (ind > 0) {
            int pind = (ind - 1) / 2;
            if (heap[ind] > heap[pind]) { // Max Heap: Child > Parent
                swap(heap, ind, pind);
                ind = pind;
            } else {
                break;
            }
        }
    }

    void delete(int ind, int[] heap) {
        int currentHeapSize = heap.length - ind;
        swap(heap, 0, currentHeapSize - 1);
        heapDown(0, heap, currentHeapSize - 1);
    }

    void heapDown(int ind, int[] heap, int len) {
        int left = (2 * ind) + 1;
        int right = (2 * ind) + 2;
        int large = ind;

        if (left < len && heap[left] > heap[large]) {
            large = left;
        }
        if (right < len && heap[right] > heap[large]) {
            large = right;
        }
        if (ind != large) {
            swap(heap, ind, large);
            heapDown(large, heap, len);
        }
    }

    public void heapify(int arr[], int n, int i) {
        heapDown(i, arr, n);
    }
}