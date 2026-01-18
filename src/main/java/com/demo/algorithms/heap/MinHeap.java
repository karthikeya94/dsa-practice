package com.demo.algorithms.heap;

import java.util.Arrays;

public class MinHeap {
    static int size =0;
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
            if (heap[ind] < heap[pind]) {
                swap(heap, ind, pind);
                ind = pind;
            } else {
                break;
            }
        }
    }

    void delete(int ind, int[] heap) {
        int currentHeapSize = size;
        if(currentHeapSize<=0) return;
        swap(heap, 0, currentHeapSize - 1);
        heapDown(0, heap, currentHeapSize - 1);
    }

    void heapDown(int ind, int[] heap, int len) {
        int left = (2 * ind) + 1;
        int right = (2 * ind) + 2;
        int small = ind;

        if (left < len && heap[left] < heap[small]) {
            small = left;
        }
        if (right < len && heap[right] < heap[small]) {
            small = right;
        }
        if (ind != small) {
            swap(heap, ind, small);
            heapDown(small, heap, len);
            size--;
        }
    }

    public static void main(String[] args) {
        int[] arr = {10, 5, 30, 2, 8};
        System.out.println("Original: " + Arrays.toString(arr));
        MinHeap heap = new MinHeap();
        for (int i = 0; i < arr.length; i++) {
//             heapUp(i, arr);
            heap.insert(arr[i], i, arr);
        }
        size=arr.length;
        System.out.println("Min Heap: " + Arrays.toString(arr));

        heap.delete(0, arr);
        System.out.println("After deleting root: " + Arrays.toString(arr));
        heap.delete(0, arr);
        System.out.println("After deleting root: " + Arrays.toString(arr));
    }
}